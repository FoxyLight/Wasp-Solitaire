package org.example.waspsolitaire.game

import kotlinx.coroutines.runBlocking
import org.example.waspsolitaire.engine.*
import org.junit.Assert.*
import org.junit.Test

class GameResetTest {
    @Test fun restartRequiresConfirmationAndUsesActualInitialSnapshot() = runBlocking<Unit> {
        val generated = ClassicWasp.newGame(12).state
        val columns = generated.tableau.map { it.reversed().map { c -> c.copy(faceUp = true) } }
        val initial = GameState.restore(columns, generated.reserve, 12)
        val repo = InMemorySessionRepository(GameSession.restore(initial))
        val controller = GameController.open(repo)
        controller.dispatch(GameIntent.DealReserve)
        controller.dispatch(GameIntent.Select(0, 7))
        val before = controller.uiState.value
        controller.dispatch(GameIntent.RequestRestart)
        assertEquals(PendingReset(ResetKind.RESTART, 1), controller.uiState.value.pendingReset)
        assertSame(before.session, repo.load().session)
        controller.dispatch(GameIntent.CancelReset)
        assertEquals(before, controller.uiState.value)
        controller.dispatch(GameIntent.RequestRestart)
        assertEquals(IntentResult.Committed(2), controller.dispatch(GameIntent.ConfirmReset(1)))
        val restarted = controller.uiState.value
        assertEquals(initial, restarted.board)
        assertNotEquals(ClassicWasp.newGame(12).state, restarted.board)
        assertNull(restarted.selection)
        assertNull(restarted.hintedAction)
        assertNull(restarted.pendingReset)
        assertTrue(restarted.session.history.isEmpty())
        assertEquals(IntentResult.EngineRejected(Rejection.NOTHING_TO_UNDO), controller.dispatch(GameIntent.Undo))
    }

    @Test fun newGameOnlyGeneratesOnConfirmationAndClearsOldDealHistory() = runBlocking<Unit> {
        val repo = InMemorySessionRepository(123)
        var generated = 0
        val controller = GameController.open(repo) { generated++; 456L }
        controller.dispatch(GameIntent.DealReserve)
        val old = controller.uiState.value.session
        controller.dispatch(GameIntent.RequestNewGame)
        controller.dispatch(GameIntent.CancelReset)
        assertSame(old, controller.uiState.value.session)
        assertEquals(0, generated)
        controller.dispatch(GameIntent.RequestNewGame)
        controller.dispatch(GameIntent.ConfirmReset(1))
        assertEquals(1, generated)
        assertEquals(ClassicWasp.newGame(456).state, controller.uiState.value.board)
        assertEquals(2L, controller.uiState.value.revision)
        assertTrue(controller.uiState.value.session.history.isEmpty())
        assertEquals(0L, controller.uiState.value.board.actionCount)
        controller.dispatch(GameIntent.RequestRestart)
        assertEquals(456L, controller.uiState.value.board.seed)
    }

    @Test fun confirmationIsRequiredEvenForAnUntouchedInProgressNewGameAndSeedsDiffer() = runBlocking<Unit> {
        val controller = GameController.open(InMemorySessionRepository(123)) { 123L }
        controller.dispatch(GameIntent.RequestNewGame)
        assertEquals(123L, controller.uiState.value.board.seed)
        assertEquals(PendingReset(ResetKind.NEW_GAME, 0), controller.uiState.value.pendingReset)
        controller.dispatch(GameIntent.ConfirmReset(0))
        assertNotEquals(123L, controller.uiState.value.board.seed)
    }

    @Test fun staleDragAndConfirmationNeverCommit() = runBlocking<Unit> {
        val repo = InMemorySessionRepository(123)
        val controller = GameController.open(repo)
        val move = ClassicWasp.legalMoves(controller.uiState.value.board).first()
        controller.dispatch(GameIntent.RequestNewGame)
        assertEquals(IntentResult.StaleRevision, controller.dispatch(GameIntent.ConfirmReset(9)))
        assertEquals(0L, repo.load().revision)
        controller.dispatch(GameIntent.DealReserve)
        val before = controller.uiState.value
        assertEquals(IntentResult.InvalidInput(InputError.NO_CONFIRMATION), controller.dispatch(GameIntent.ConfirmReset(0)))
        assertEquals(IntentResult.StaleRevision, controller.dispatch(GameIntent.Move(move.source, move.cardIndex, move.destination, 0)))
        assertSame(before, controller.uiState.value)
        assertEquals(1L, repo.load().revision)
    }

    @Test fun failedResetKeepsOriginalSessionHistorySelectionAndPendingConfirmation() = runBlocking<Unit> {
        val delegate = InMemorySessionRepository(123)
        var fail = false
        val repository = object : SessionRepository {
            override suspend fun load() = delegate.load()
            override suspend fun commit(expectedRevision: Long, next: SessionRecord) =
                if (fail) CommitResult.Failure("Injected failure") else delegate.commit(expectedRevision, next)
        }
        val controller = GameController.open(repository) { 456L }
        controller.dispatch(GameIntent.DealReserve)
        for (request in listOf(GameIntent.RequestRestart, GameIntent.RequestNewGame)) {
            controller.dispatch(request)
            val before = controller.uiState.value
            fail = true
            assertEquals(IntentResult.StorageFailure("Injected failure"), controller.dispatch(GameIntent.ConfirmReset(1)))
            assertSame(before, controller.uiState.value)
            assertSame(before.session, delegate.load().session)
            fail = false
            controller.dispatch(GameIntent.CancelReset)
        }
    }

    @Test fun resetUsesCompareAndSwapAcrossControllers() = runBlocking<Unit> {
        val repo = InMemorySessionRepository(1)
        val first = GameController.open(repo)
        val second = GameController.open(repo)
        second.dispatch(GameIntent.RequestNewGame)
        first.dispatch(GameIntent.DealReserve)
        val before = second.uiState.value
        assertEquals(IntentResult.StaleRevision, second.dispatch(GameIntent.ConfirmReset(0)))
        assertSame(before, second.uiState.value)
        assertEquals(1L, repo.load().revision)
    }

    @Test fun tapAndDragIntentsProduceEquivalentStatesForEveryEnumeratedSeededMove() = runBlocking<Unit> {
        for (seed in 0L..9L) {
            val initial = ClassicWasp.newGame(seed)
            for (move in ClassicWasp.legalMoves(initial.state)) {
                val tap = GameController.open(InMemorySessionRepository(initial))
                val drag = GameController.open(InMemorySessionRepository(initial))
                tap.dispatch(GameIntent.Select(move.source, move.cardIndex))
                tap.dispatch(GameIntent.MoveTo(move.destination))
                drag.dispatch(GameIntent.Move(move.source, move.cardIndex, move.destination, 0))
                assertEquals(tap.uiState.value.board, drag.uiState.value.board)
                assertEquals(tap.uiState.value.session.history, drag.uiState.value.session.history)
                assertEquals(1L, tap.uiState.value.revision)
                assertEquals(1L, drag.uiState.value.revision)
            }
        }
    }
}