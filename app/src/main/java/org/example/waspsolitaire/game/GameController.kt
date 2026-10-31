package org.example.waspsolitaire.game

import java.util.Collections
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.example.waspsolitaire.engine.ClassicWasp
import org.example.waspsolitaire.engine.GameAction
import org.example.waspsolitaire.engine.GameSession
import org.example.waspsolitaire.engine.GameStatus
import org.example.waspsolitaire.engine.Rejection
import org.example.waspsolitaire.engine.TableauCard
import org.example.waspsolitaire.engine.Transition

data class CardSelection(val column: Int, val cardIndex: Int)

/** Immutable UI projection. Selected cards and hints are NOT game actions. */
data class GameUiState(
    val session: GameSession,
    val revision: Long,
    val selection: CardSelection? = null,
    val legalDestinations: Set<Int> = emptySet(),
    val hintedAction: GameAction? = null,
) {
    val board get() = session.state
    val selectedTail: List<TableauCard>
        get() = selection?.let { chosen ->
            Collections.unmodifiableList(ArrayList(board.tableau[chosen.column].drop(chosen.cardIndex)))
        } ?: emptyList()
}

sealed interface GameIntent {
    data class Select(val column: Int, val cardIndex: Int) : GameIntent
    data object CancelSelection : GameIntent
    data class MoveTo(val destination: Int) : GameIntent
    /** Also used by drag/drop. Still validated by the authoritative engine. */
    data class Move(val source: Int, val cardIndex: Int, val destination: Int) : GameIntent
    data object DealReserve : GameIntent
    data object Undo : GameIntent
    data object Hint : GameIntent
    /** Explicit retry after another controller commits the shared session. */
    data object Refresh : GameIntent
}

enum class InputError { INVALID_SELECTION, NO_SELECTION }

sealed interface IntentResult {
    data object NoChange : IntentResult
    data object UiChanged : IntentResult
    data class Committed(val revision: Long) : IntentResult
    data class InvalidInput(val reason: InputError) : IntentResult
    data class EngineRejected(val reason: Rejection) : IntentResult
    data object StaleRevision : IntentResult
    data class StorageFailure(val message: String) : IntentResult
}

/**
 * Serializes all user intents, validates via ClassicWasp and never publishes an
 * accepted game state until the repository confirms its atomic revision write.
 * UI-only state (selection, destinations, Hint) is not written to the repository.
 */
class GameController private constructor(
    private val repository: SessionRepository,
    initial: SessionRecord,
) {
    private val lock = Mutex()
    private val mutableState = MutableStateFlow(GameUiState(initial.session, initial.revision))
    val uiState: StateFlow<GameUiState> = mutableState.asStateFlow()

    suspend fun dispatch(intent: GameIntent): IntentResult = lock.withLock {
        val screen = mutableState.value
        when (intent) {
            is GameIntent.Select -> {
                val card = screen.board.tableau.getOrNull(intent.column)
                    ?.getOrNull(intent.cardIndex)
                if (screen.board.status == GameStatus.WON || card?.faceUp != true) {
                    IntentResult.InvalidInput(InputError.INVALID_SELECTION)
                } else {
                    val nextSelection = CardSelection(intent.column, intent.cardIndex)
                    if (screen.selection == nextSelection) {
                        mutableState.value = screen.copy(
                            selection = null, legalDestinations = emptySet(), hintedAction = null,
                        )
                    } else {
                        val destinations = ClassicWasp.legalMoves(screen.board)
                            .asSequence()
                            .filter { it.source == intent.column && it.cardIndex == intent.cardIndex }
                            .map { it.destination }
                            .toCollection(linkedSetOf())
                        mutableState.value = screen.copy(
                            selection = nextSelection,
                            legalDestinations = Collections.unmodifiableSet(destinations),
                            hintedAction = null,
                        )
                    }
                    IntentResult.UiChanged
                }
            }
            GameIntent.CancelSelection -> {
                if (screen.selection == null && screen.hintedAction == null) {
                    IntentResult.NoChange
                } else {
                    mutableState.value = screen.copy(
                        selection = null, legalDestinations = emptySet(), hintedAction = null,
                    )
                    IntentResult.UiChanged
                }
            }
            is GameIntent.MoveTo -> {
                val chosen = screen.selection
                if (chosen == null) IntentResult.InvalidInput(InputError.NO_SELECTION)
                else commit(GameAction.Move(chosen.column, chosen.cardIndex, intent.destination))
            }
            is GameIntent.Move -> commit(
                GameAction.Move(intent.source, intent.cardIndex, intent.destination),
            )
            GameIntent.DealReserve -> commit(GameAction.DealReserve)
            GameIntent.Undo -> commit(GameAction.Undo)
            GameIntent.Hint -> {
                val hint = ClassicWasp.hint(screen.board)
                if (screen.hintedAction == hint) IntentResult.NoChange
                else {
                    mutableState.value = screen.copy(hintedAction = hint)
                    IntentResult.UiChanged
                }
            }
            GameIntent.Refresh -> {
                try {
                    val latest = repository.load()
                    if (latest.revision == screen.revision) IntentResult.NoChange
                    else {
                        check(latest.schemaVersion == SESSION_SCHEMA_VERSION)
                        mutableState.value = GameUiState(latest.session, latest.revision)
                        IntentResult.UiChanged
                    }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (error: Exception) {
                    IntentResult.StorageFailure(error.message ?: "Unable to load saved session")
                }
            }
        }
    }

    private suspend fun commit(action: GameAction): IntentResult {
        val current = mutableState.value
        return when (val transition = ClassicWasp.apply(current.session, action)) {
            is Transition.Rejected -> IntentResult.EngineRejected(transition.reason)
            is Transition.Accepted -> {
                if (current.revision == Long.MAX_VALUE) {
                    return IntentResult.StorageFailure("Session revision overflow")
                }
                val next = SessionRecord(
                    revision = current.revision + 1L,
                    session = transition.session,
                )
                val stored = try {
                    repository.commit(current.revision, next)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (error: Exception) {
                    CommitResult.Failure(error.message ?: "Unable to commit session")
                }
                when (stored) {
                    CommitResult.Stored -> {
                        mutableState.value = GameUiState(next.session, next.revision)
                        IntentResult.Committed(next.revision)
                    }
                    CommitResult.Conflict -> IntentResult.StaleRevision
                    is CommitResult.Failure -> IntentResult.StorageFailure(stored.message)
                }
            }
        }
    }

    companion object {
        /** Caller performs the suspending initial load before creating the ViewModel. */
        suspend fun open(repository: SessionRepository): GameController {
            val initial = repository.load()
            check(initial.schemaVersion == SESSION_SCHEMA_VERSION)
            return GameController(repository, initial)
        }
    }
}
