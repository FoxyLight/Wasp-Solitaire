package org.example.waspsolitaire.game

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.waspsolitaire.engine.ClassicWasp
import org.example.waspsolitaire.engine.GameAction
import org.example.waspsolitaire.engine.Rejection
import org.junit.Assert.*
import org.junit.Test

class GameControllerTest {
    private suspend fun create(seed: Long = 17L): Pair<GameController, InMemorySessionRepository> {
        val repo = InMemorySessionRepository(seed)
        return GameController.open(repo) to repo
    }

    private fun firstMove(controller: GameController): GameAction.Move =
        ClassicWasp.legalMoves(controller.uiState.value.board).first()

    @Test fun initialStateExposesImmutableEngineSessionAndNoEphemeralSelection() = runBlocking<Unit> {
        val (controller, repo) = create(123)
        val ui = controller.uiState.value
        assertEquals(ClassicWasp.newGame(123).state, ui.board)
        assertEquals(7, ui.board.tableau.size)
        assertEquals(12, ui.board.tableau.flatten().count { !it.faceUp })
        assertEquals(0L, ui.revision)
        assertNull(ui.selection)
        assertTrue(ui.legalDestinations.isEmpty())
        assertNull(ui.hintedAction)
        assertEquals(SESSION_SCHEMA_VERSION, repo.load().schemaVersion)
        assertEquals(0, repo.load().session.history.size)
    }

    @Test fun selectingAnExposedCardPublishesOnlyUiStateWithExactlyLegalTargets() = runBlocking<Unit> {
        val (controller, repo) = create()
        val original = controller.uiState.value
        val move = firstMove(controller)
        val expected = ClassicWasp.legalMoves(original.board)
            .filter { it.source == move.source && it.cardIndex == move.cardIndex }
            .map { it.destination }.toSet()
        val emissions = mutableListOf<GameUiState>()
        val collector = launch(start = CoroutineStart.UNDISPATCHED) {
            controller.uiState.take(2).toList(emissions)
        }
        assertEquals(IntentResult.UiChanged, controller.dispatch(GameIntent.Select(move.source, move.cardIndex)))
        collector.join()
        val selected = controller.uiState.value
        assertEquals(2, emissions.size)
        assertSame(original, emissions[0])
        assertSame(selected, emissions[1])
        assertEquals(CardSelection(move.source, move.cardIndex), selected.selection)
        assertEquals(expected, selected.legalDestinations)
        assertEquals(original.board.tableau[move.source].drop(move.cardIndex), selected.selectedTail)
        assertSame(original.session, selected.session)
        assertSame(original.session, repo.load().session)
        assertEquals(0L, repo.load().revision)
        assertThrows(UnsupportedOperationException::class.java) {
            (selected.legalDestinations as MutableSet<Int>).clear()
        }
    }

    @Test fun selectionSwitchCancelAndToggleDoNotCommitActions() = runBlocking<Unit> {
        val (controller, repo) = create()
        val original = controller.uiState.value
        val left = GameIntent.Select(0, 3)
        val right = GameIntent.Select(1, 3)
        assertEquals(IntentResult.UiChanged, controller.dispatch(left))
        assertEquals(IntentResult.UiChanged, controller.dispatch(right))
        assertEquals(CardSelection(1, 3), controller.uiState.value.selection)
        assertEquals(IntentResult.UiChanged, controller.dispatch(right))
        assertNull(controller.uiState.value.selection)
        assertEquals(IntentResult.NoChange, controller.dispatch(GameIntent.CancelSelection))
        assertEquals(IntentResult.UiChanged, controller.dispatch(left))
        assertEquals(IntentResult.UiChanged, controller.dispatch(GameIntent.CancelSelection))
        assertSame(original.session, controller.uiState.value.session)
        assertEquals(0L, repo.load().revision)
    }

    @Test fun hiddenAndOutOfBoundsSelectionAreRejectedWithoutEmission() = runBlocking<Unit> {
        val (controller, repo) = create()
        val before = controller.uiState.value
        for (selection in listOf(
            GameIntent.Select(0, 0), GameIntent.Select(-1, 0),
            GameIntent.Select(7, 0), GameIntent.Select(0, 99),
        )) {
            assertEquals(
                IntentResult.InvalidInput(InputError.INVALID_SELECTION),
                controller.dispatch(selection),
            )
            assertSame(before, controller.uiState.value)
        }
        assertEquals(0L, repo.load().revision)
    }

    @Test fun tapDestinationCommitsExactlyOneMoveAndClearsSelection() = runBlocking<Unit> {
        val (controller, repo) = create()
        val move = firstMove(controller)
        val original = controller.uiState.value
        controller.dispatch(GameIntent.Select(move.source, move.cardIndex))
        assertEquals(IntentResult.Committed(1), controller.dispatch(GameIntent.MoveTo(move.destination)))
        val updated = controller.uiState.value
        assertNull(updated.selection)
        assertTrue(updated.legalDestinations.isEmpty())
        assertNull(updated.hintedAction)
        assertEquals(1L, updated.board.actionCount)
        assertEquals(1L, updated.revision)
        assertEquals(1, updated.session.history.size)
        assertEquals(original.board, updated.session.history.single())
        assertSame(updated.session, repo.load().session)
    }

    @Test fun invalidExplicitDestinationPreservesSelectionAndUndoHistory() = runBlocking<Unit> {
        val (controller, repo) = create()
        val move = firstMove(controller)
        controller.dispatch(GameIntent.Select(move.source, move.cardIndex))
        val before = controller.uiState.value
        assertEquals(
            IntentResult.EngineRejected(Rejection.INVALID_MOVE),
            controller.dispatch(GameIntent.MoveTo(move.source)),
        )
        assertSame(before, controller.uiState.value)
        assertEquals(0L, repo.load().revision)
        assertEquals(0, repo.load().session.history.size)
        assertEquals(
            IntentResult.InvalidInput(InputError.NO_SELECTION),
            GameController.open(InMemorySessionRepository(12)).dispatch(GameIntent.MoveTo(0)),
        )
    }

    @Test fun dragMoveWorksWithoutSelectionAndIsEngineValidated() = runBlocking<Unit> {
        val (controller, _) = create()
        val move = firstMove(controller)
        assertEquals(
            IntentResult.Committed(1),
            controller.dispatch(GameIntent.Move(move.source, move.cardIndex, move.destination)),
        )
        val beforeRejected = controller.uiState.value
        assertEquals(
            IntentResult.EngineRejected(Rejection.INVALID_MOVE),
            controller.dispatch(GameIntent.Move(8, 0, 1)),
        )
        assertSame(beforeRejected, controller.uiState.value)
    }

    @Test fun reserveDealAndUndoRestoreExactBoardWhileRevisionKeepsIncreasing() = runBlocking<Unit> {
        val (controller, repo) = create()
        val original = controller.uiState.value.board
        assertEquals(IntentResult.Committed(1), controller.dispatch(GameIntent.DealReserve))
        assertEquals(0, controller.uiState.value.board.reserve.size)
        assertEquals(1, controller.uiState.value.session.history.size)
        assertEquals(IntentResult.Committed(2), controller.dispatch(GameIntent.Undo))
        assertEquals(original, controller.uiState.value.board)
        assertEquals(3, controller.uiState.value.board.reserve.size)
        assertEquals(0L, controller.uiState.value.board.actionCount)
        assertEquals(2L, controller.uiState.value.revision)
        assertEquals(0, repo.load().session.history.size)
        assertEquals(IntentResult.Committed(3), controller.dispatch(GameIntent.DealReserve))
        assertEquals(
            IntentResult.EngineRejected(Rejection.RESERVE_EMPTY),
            controller.dispatch(GameIntent.DealReserve),
        )
    }

    @Test fun hintsNeverCommitAndRepeatedHintHasNoExtraPublication() = runBlocking<Unit> {
        val (controller, repo) = create()
        val before = controller.uiState.value
        val expected = ClassicWasp.hint(before.board)
        assertNotNull(expected)
        assertEquals(IntentResult.UiChanged, controller.dispatch(GameIntent.Hint))
        assertEquals(expected, controller.uiState.value.hintedAction)
        assertEquals(IntentResult.NoChange, controller.dispatch(GameIntent.Hint))
        assertSame(before.session, controller.uiState.value.session)
        assertEquals(0L, repo.load().revision)
        assertEquals(IntentResult.UiChanged, controller.dispatch(GameIntent.CancelSelection))
        assertNull(controller.uiState.value.hintedAction)
    }

    @Test fun storeFailureDoesNotPublishAnAcceptedButUnsavedMove() = runBlocking<Unit> {
        val delegate = InMemorySessionRepository(55)
        val failing = object : SessionRepository {
            override suspend fun load() = delegate.load()
            override suspend fun commit(expectedRevision: Long, next: SessionRecord) =
                CommitResult.Failure("Injected write failure")
        }
        val controller = GameController.open(failing)
        val before = controller.uiState.value
        assertEquals(
            IntentResult.StorageFailure("Injected write failure"),
            controller.dispatch(GameIntent.DealReserve),
        )
        assertSame(before, controller.uiState.value)
        assertSame(before.session, delegate.load().session)
        assertEquals(0L, delegate.load().revision)
    }

    @Test fun concurrentControllersRejectStaleCommitsUntilExplicitRefresh() = runBlocking<Unit> {
        val repo = InMemorySessionRepository(91)
        val first = GameController.open(repo)
        val second = GameController.open(repo)
        val oldSecond = second.uiState.value
        assertEquals(IntentResult.Committed(1), first.dispatch(GameIntent.DealReserve))
        assertEquals(IntentResult.StaleRevision, second.dispatch(GameIntent.DealReserve))
        assertSame(oldSecond, second.uiState.value)
        assertEquals(1L, repo.load().revision)
        assertEquals(IntentResult.UiChanged, second.dispatch(GameIntent.Refresh))
        assertEquals(1L, second.uiState.value.revision)
        assertNull(second.uiState.value.selection)
        assertEquals(IntentResult.NoChange, second.dispatch(GameIntent.Refresh))
        assertEquals(IntentResult.Committed(2), second.dispatch(GameIntent.Undo))
        assertEquals(3, second.uiState.value.board.reserve.size)
        assertEquals(2L, repo.load().revision)
    }

    @Test fun controllerCanOpenExistingSessionWithFullUndoHistory() = runBlocking<Unit> {
        val repo = InMemorySessionRepository(2)
        val before = GameController.open(repo)
        val original = before.uiState.value.board
        before.dispatch(GameIntent.DealReserve)
        val reopened = GameController.open(repo)
        assertEquals(1, reopened.uiState.value.session.history.size)
        assertEquals(1L, reopened.uiState.value.revision)
        assertEquals(IntentResult.Committed(2), reopened.dispatch(GameIntent.Undo))
        assertEquals(original, reopened.uiState.value.board)
    }

    @Test fun viewModelExposesSameStateFlowAndDeliversTypedResults() = runBlocking<Unit> {
        val (controller, _) = create()
        val viewModel = GameViewModel(controller)
        assertSame(controller.uiState, viewModel.uiState)
        assertNull(viewModel.lastResult.value)
        val move = firstMove(controller)
        assertEquals(
            IntentResult.Committed(1),
            viewModel.dispatch(GameIntent.Move(move.source, move.cardIndex, move.destination)),
        )
        assertEquals(IntentResult.Committed(1), viewModel.lastResult.value)
        assertEquals(1L, viewModel.uiState.value.revision)
        assertEquals(
            IntentResult.EngineRejected(Rejection.INVALID_MOVE),
            viewModel.dispatch(GameIntent.Move(-1, 0, 0)),
        )
        assertEquals(1L, viewModel.uiState.value.revision)
    }
}
