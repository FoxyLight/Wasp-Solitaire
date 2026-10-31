package org.example.waspsolitaire.game

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.example.waspsolitaire.engine.ClassicWasp
import org.example.waspsolitaire.engine.GameSession

/** Version is for the future Room encoding, NOT an implementation of durable persistence. */
const val SESSION_SCHEMA_VERSION: Int = 1

/**
 * A versioned logical snapshot. GameSession includes the actual 52-card state,
 * reserve, seed, action count AND every previous-state Undo snapshot.
 * revision tracks accepted writes; it is separate from net actionCount (which Undo decreases).
 */
data class SessionRecord(
    val revision: Long,
    val session: GameSession,
    val schemaVersion: Int = SESSION_SCHEMA_VERSION,
) {
    init {
        require(revision >= 0) { "Revision must not be negative" }
        require(schemaVersion == SESSION_SCHEMA_VERSION) { "Unsupported session schema" }
    }
}

sealed interface CommitResult {
    data object Stored : CommitResult
    data object Conflict : CommitResult
    data class Failure(val message: String) : CommitResult
}

/**
 * Room will implement these suspending calls and atomically commit the whole
 * current session + Undo history. A failed write MUST NOT be published as state.
 * No seed-only restoration, history truncation or implicit reset is permitted.
 */
interface SessionRepository {
    suspend fun load(): SessionRecord
    suspend fun commit(expectedRevision: Long, next: SessionRecord): CommitResult
}

/**
 * Phase 4A ONLY: loses the game on process death; it is NOT an autosave.
 * A Mutex provides compare-and-swap semantics across multiple controllers.
 */
class InMemorySessionRepository(initial: GameSession) : SessionRepository {
    constructor(seed: Long) : this(ClassicWasp.newGame(seed))

    private val mutex = Mutex()
    private var current = SessionRecord(revision = 0, session = initial)

    override suspend fun load(): SessionRecord = mutex.withLock { current }

    override suspend fun commit(expectedRevision: Long, next: SessionRecord): CommitResult =
        mutex.withLock {
            when {
                current.revision != expectedRevision -> CommitResult.Conflict
                expectedRevision == Long.MAX_VALUE || next.revision != expectedRevision + 1L ->
                    CommitResult.Failure("Invalid revision progression")
                else -> {
                    current = next
                    CommitResult.Stored
                }
            }
        }
}
