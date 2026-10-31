package org.example.waspsolitaire.engine

import org.junit.Assert.*
import org.junit.Test

class ClassicWaspTest {
    private val fullDeck = Suit.entries.flatMap { suit -> Rank.entries.map { Card(suit, it) } }
    private fun card(rank: Rank, suit: Suit = Suit.HEARTS) = TableauCard(Card(suit, rank))

    /** Fill unused cards into column seven so every scenario is a real 52-card state. */
    private fun session(
        specified: Map<Int, List<TableauCard>>,
        reserve: List<Card> = emptyList(),
        count: Long = 0,
    ): GameSession {
        val used = specified.values.flatten().map { it.card } + reserve
        val columns = (0..6).map { specified[it].orEmpty().toMutableList() }
        columns[6].addAll(fullDeck.filterNot { it in used }.map { TableauCard(it) })
        return GameSession.restore(GameState.restore(columns, reserve, 42, count))
    }

    private fun accepted(session: GameSession, action: GameAction): GameSession =
        (ClassicWasp.apply(session, action) as Transition.Accepted).session

    private fun rejected(session: GameSession, action: GameAction, reason: Rejection) {
        val result = ClassicWasp.apply(session, action) as Transition.Rejected
        assertSame(session, result.session)
        assertEquals(reason, result.reason)
    }

    @Test fun seededDeckAndInitialDealAreReproducible() {
        val first = ClassicWasp.newGame(2026)
        assertEquals(first.state, ClassicWasp.newGame(2026).state)
        assertNotEquals(first.state.tableau, ClassicWasp.newGame(2027).state.tableau)
        assertEquals(52, ClassicWasp.deck(2026).toSet().size)
        assertEquals(ClassicWasp.deck(2026), first.state.tableau.flatten().map { it.card } + first.state.reserve)
        assertEquals(3, first.state.reserve.size)
        assertEquals(12, first.state.tableau.flatten().count { !it.faceUp })
        assertEquals(37, first.state.tableau.flatten().count { it.faceUp })
        first.state.tableau.forEachIndexed { col, cards ->
            assertEquals(7, cards.size)
            cards.forEachIndexed { row, item -> assertEquals(col >= 4 || row >= 3, item.faceUp) }
        }
        assertEquals(0L, first.state.actionCount)
        assertTrue(first.history.isEmpty())
    }

    @Test fun shuffleHasStableGoldenOrder() {
        // Independently checked with Collections.shuffle(indices, java.util.Random(0)):
        // first indices 47, 24, 25 in suit-major, Ace-first order.
        assertEquals(
            listOf(Card(Suit.SPADES, Rank.NINE), Card(Suit.DIAMONDS, Rank.QUEEN), Card(Suit.DIAMONDS, Rank.KING)),
            ClassicWasp.deck(0).take(3),
        )
    }

    @Test fun sameSuitOneRankDownMovesEntireUnorderedTail() {
        val tail = listOf(card(Rank.EIGHT), card(Rank.TWO, Suit.CLUBS), card(Rank.KING, Suit.SPADES))
        val initial = session(mapOf(0 to tail, 1 to listOf(card(Rank.NINE))))
        val next = accepted(initial, GameAction.Move(0, 0, 1))
        assertTrue(next.state.tableau[0].isEmpty())
        assertEquals(listOf(card(Rank.NINE)) + tail, next.state.tableau[1])
        assertEquals(tail, initial.state.tableau[0])
        assertEquals(1L, next.state.actionCount)
        assertSame(initial.state, next.history.single())
    }

    @Test fun wrongSuitRankAndGapAreRejectedWithoutHistory() {
        for (target in listOf(card(Rank.NINE, Suit.SPADES), card(Rank.EIGHT, Suit.CLUBS), card(Rank.TEN))) {
            val initial = session(mapOf(0 to listOf(card(Rank.EIGHT)), 1 to listOf(target)))
            rejected(initial, GameAction.Move(0, 0, 1), Rejection.INVALID_MOVE)
        }
        val equalRank = session(mapOf(0 to listOf(card(Rank.EIGHT), card(Rank.TWO))))
        // Selecting an earlier card cannot place it on its own column.
        rejected(equalRank, GameAction.Move(0, 0, 0), Rejection.INVALID_MOVE)
    }

    @Test fun middleSelectionKeepsPrefixAndCanMoveAnyRankToEmpty() {
        val prefix = card(Rank.FOUR)
        val tail = listOf(card(Rank.THREE), card(Rank.JACK, Suit.CLUBS))
        val initial = session(mapOf(0 to listOf(prefix) + tail))
        val next = accepted(initial, GameAction.Move(0, 1, 1))
        assertEquals(listOf(prefix), next.state.tableau[0])
        assertEquals(tail, next.state.tableau[1])
    }

    @Test fun ranksDoNotWrapBetweenAceAndKing() {
        val initial = session(mapOf(0 to listOf(card(Rank.ACE)), 1 to listOf(card(Rank.KING))))
        rejected(initial, GameAction.Move(0, 0, 1), Rejection.INVALID_MOVE)
        rejected(initial, GameAction.Move(1, 0, 0), Rejection.INVALID_MOVE)
    }

    @Test fun historyHasNoFixedMoveLimit() {
        val initial = session(mapOf(0 to listOf(card(Rank.FOUR))))
        var game = initial
        repeat(512) { index ->
            game = accepted(game, GameAction.Move(index % 2, 0, 1 - index % 2))
        }
        assertEquals(512, game.history.size)
        assertEquals(512L, game.state.actionCount)
        repeat(512) { game = accepted(game, GameAction.Undo) }
        assertEquals(initial.state, game.state)
        assertTrue(game.history.isEmpty())
    }

    @Test fun hiddenSelectionAndOutOfBoundsMovesAreRejected() {
        val initial = session(mapOf(0 to listOf(card(Rank.FIVE).copy(faceUp = false), card(Rank.FOUR))))
        for (move in listOf(GameAction.Move(0, 0, 1), GameAction.Move(-1, 0, 1),
            GameAction.Move(7, 0, 1), GameAction.Move(0, -1, 1), GameAction.Move(0, 2, 1),
            GameAction.Move(0, 1, 7), GameAction.Move(1, 0, 2))) {
            rejected(initial, move, Rejection.INVALID_MOVE)
            assertFalse(ClassicWasp.isLegalMove(initial.state, move))
        }
    }

    @Test fun uncoveringRevealsOnlyNewlyExposedCardAndUndoRestoresIt() {
        val initial = session(mapOf(0 to listOf(card(Rank.SIX).copy(faceUp = false),
            card(Rank.FIVE).copy(faceUp = false), card(Rank.FOUR))))
        val next = accepted(initial, GameAction.Move(0, 2, 1))
        assertFalse(next.state.tableau[0][0].faceUp)
        assertTrue(next.state.tableau[0][1].faceUp)
        assertSame(initial.state, accepted(next, GameAction.Undo).state)
    }

    @Test fun reserveDealsOnceToFirstThreeColumnsEvenWithMovesAndEmptyColumns() {
        val reserve = listOf(Card(Suit.CLUBS, Rank.ACE), Card(Suit.DIAMONDS, Rank.TWO), Card(Suit.SPADES, Rank.THREE))
        val initial = session(mapOf(0 to listOf(card(Rank.FOUR))), reserve)
        assertTrue(ClassicWasp.legalMoves(initial.state).isNotEmpty())
        val next = accepted(initial, GameAction.DealReserve)
        assertTrue(next.state.reserve.isEmpty())
        for (index in 0..2) assertEquals(TableauCard(reserve[index]), next.state.tableau[index].last())
        for (index in 3..6) assertEquals(initial.state.tableau[index], next.state.tableau[index])
        assertEquals(1L, next.state.actionCount)
        rejected(next, GameAction.DealReserve, Rejection.RESERVE_EMPTY)
        val undo = accepted(next, GameAction.Undo)
        assertSame(initial.state, undo.state)
        assertEquals(next.state, accepted(undo, GameAction.DealReserve).state)
    }

    private fun run(suit: Suit) = Rank.entries.reversed().map { card(it, suit) }

    @Test fun fourRunsWinInPlaceAndUndoCanReverseVictory() {
        val columns = Suit.entries.map { run(it).toMutableList() }.toMutableList()
        val ace = columns[3].removeAt(12)
        columns.add(mutableListOf(ace))
        columns.add(mutableListOf())
        columns.add(mutableListOf())
        val initial = GameSession.restore(GameState.restore(columns, emptyList(), 9))
        assertEquals(3, initial.state.completedRuns.size)
        val next = accepted(initial, GameAction.Move(4, 0, 3))
        assertEquals(GameStatus.WON, next.state.status)
        assertEquals(Suit.entries.toList(), next.state.completedRuns)
        assertEquals(52, next.state.tableau.flatten().size)
        assertEquals(3, next.state.tableau.count { it.isEmpty() })
        assertNull(ClassicWasp.hint(next.state))
        assertTrue(ClassicWasp.legalMoves(next.state).isEmpty())
        rejected(next, GameAction.Move(0, 0, 4), Rejection.GAME_WON)
        rejected(next, GameAction.DealReserve, Rejection.GAME_WON)
        val undo = accepted(next, GameAction.Undo)
        assertEquals(initial.state, undo.state)
        assertEquals(GameStatus.WON, accepted(undo, GameAction.Move(4, 0, 3)).state.status)
    }

    @Test fun completedRunsStaySelectableAndRequireExposedOrderedFaceUpSuffix() {
        val initial = session(mapOf(0 to run(Suit.HEARTS)))
        assertEquals(listOf(Suit.HEARTS), initial.state.completedRuns)
        val next = accepted(initial, GameAction.Move(0, 7, 1))
        assertTrue(next.state.completedRuns.isEmpty())
        assertEquals(52, next.state.tableau.flatten().size)
        assertTrue(session(mapOf(0 to run(Suit.HEARTS).reversed())).state.completedRuns.isEmpty())
        assertTrue(session(mapOf(0 to run(Suit.HEARTS).drop(1))).state.completedRuns.isEmpty())
        assertTrue(session(mapOf(0 to (run(Suit.HEARTS) + card(Rank.TWO, Suit.CLUBS)))).state.completedRuns.isEmpty())
        assertTrue(session(mapOf(0 to (listOf(card(Rank.KING).copy(faceUp = false)) + run(Suit.HEARTS).drop(1)))).state.completedRuns.isEmpty())
        val mixed = run(Suit.HEARTS).toMutableList()
        mixed[4] = card(Rank.NINE, Suit.CLUBS)
        assertTrue(session(mapOf(0 to mixed)).state.completedRuns.isEmpty())
        val buriedPrefix = session(mapOf(0 to (listOf(card(Rank.TWO, Suit.CLUBS)) + run(Suit.HEARTS))))
        assertEquals(listOf(Suit.HEARTS), buriedPrefix.state.completedRuns)
        assertEquals(GameStatus.IN_PROGRESS, buriedPrefix.state.status)
    }

    private fun noMoves(withReserve: Boolean): GameSession {
        val reserve = if (withReserve) Suit.entries.take(3).map { Card(it, Rank.KING) } else emptyList()
        val tips = Suit.entries.map { Card(it, Rank.ACE) } +
            Suit.entries.take(3).map { Card(it, if (withReserve) Rank.QUEEN else Rank.KING) }
        val buried = fullDeck.filterNot { it in reserve || it in tips }.map { TableauCard(it, false) }
        val columns = tips.mapIndexed { index, tip ->
            (if (index == 0) buried else emptyList()) + TableauCard(tip)
        }
        return GameSession.restore(GameState.restore(columns, reserve, 5))
    }

    @Test fun noMovesNeverDeclaresDefeatAndHintFallsBackToReserve() {
        for (withReserve in listOf(false, true)) {
            val initial = noMoves(withReserve)
            assertTrue(ClassicWasp.legalMoves(initial.state).isEmpty())
            assertEquals(GameStatus.IN_PROGRESS, initial.state.status)
            assertEquals(if (withReserve) GameAction.DealReserve else null, ClassicWasp.hint(initial.state))
            if (withReserve) accepted(initial, GameAction.DealReserve)
        }
    }

    @Test fun legalEnumerationMatchesValidationAndHintsNeverMutateOrSuggestIllegalActions() {
        var game = ClassicWasp.newGame(18)
        repeat(60) {
            val before = game.state
            val moves = ClassicWasp.legalMoves(before)
            val bruteForce = buildList {
                for (source in -1..7) for (index in -1..before.tableau.getOrNull(source).orEmpty().size)
                    for (destination in -1..7) {
                        val move = GameAction.Move(source, index, destination)
                        if (ClassicWasp.isLegalMove(before, move)) add(move)
                    }
            }
            assertEquals(bruteForce, moves)
            val hint = ClassicWasp.hint(before)
            assertSame(before, game.state)
            if (hint != null) game = accepted(game, hint)
        }
    }

    @Test fun randomizedActionsConserveCardsAndEntireUndoHistory() {
        val random = java.util.Random(713)
        repeat(20) { seed ->
            var game = ClassicWasp.newGame(seed.toLong())
            val initial = game.state
            repeat(160) {
                val moves = ClassicWasp.legalMoves(game.state)
                val action = if (game.state.reserve.isNotEmpty() && random.nextInt(8) == 0)
                    GameAction.DealReserve else moves.getOrNull(if (moves.isEmpty()) 0 else random.nextInt(moves.size))
                if (action != null) {
                    val before = game
                    game = accepted(game, action)
                    assertEquals(before.state, accepted(game, GameAction.Undo).state)
                }
                val cards = game.state.tableau.flatten().map { it.card } + game.state.reserve
                assertEquals(fullDeck.toSet(), cards.toSet())
                assertEquals(52, cards.size)
                rejected(game, GameAction.Move(-1, 0, 0), Rejection.INVALID_MOVE)
            }
            val restored = GameSession.restore(game.state, game.history)
            assertEquals(game.history, restored.history)
            game = restored
            while (game.history.isNotEmpty()) game = accepted(game, GameAction.Undo)
            assertEquals(initial, game.state)
            rejected(game, GameAction.Undo, Rejection.NOTHING_TO_UNDO)
        }
    }

    @Test fun collectionInputsAndOutputsCannotMutateSnapshotsOrHistory() {
        val initial = ClassicWasp.newGame(3)
        val columns = initial.state.tableau.map { it.toMutableList() }.toMutableList()
        val reserve = initial.state.reserve.toMutableList()
        val state = GameState.restore(columns, reserve, 3)
        columns[0].clear()
        reserve.clear()
        assertEquals(initial.state, state)
        assertThrows(UnsupportedOperationException::class.java) { (state.tableau as MutableList).clear() }
        assertThrows(UnsupportedOperationException::class.java) { (state.tableau[0] as MutableList).clear() }
        assertThrows(UnsupportedOperationException::class.java) { (state.reserve as MutableList).clear() }
        val next = accepted(initial, GameAction.DealReserve)
        val history = next.history.toMutableList()
        val restored = GameSession.restore(next.state, history)
        history.clear()
        assertEquals(1, restored.history.size)
        assertThrows(UnsupportedOperationException::class.java) { (restored.history as MutableList).clear() }
    }

    @Test fun malformedRestoresAndActionCountOverflowAreRejected() {
        val state = ClassicWasp.newGame(1).state
        assertThrows(IllegalArgumentException::class.java) { GameState.restore(state.tableau.drop(1), state.reserve, 1) }
        assertThrows(IllegalArgumentException::class.java) { GameState.restore(state.tableau, state.reserve.take(2), 1) }
        assertThrows(IllegalArgumentException::class.java) { GameState.restore(state.tableau, List(3) { state.reserve[0] }, 1) }
        assertThrows(IllegalArgumentException::class.java) { GameState.restore(state.tableau, state.reserve, 1, -1) }
        val hiddenTip = state.tableau.map { it.toMutableList() }
        hiddenTip[0][6] = hiddenTip[0][6].copy(faceUp = false)
        assertThrows(IllegalArgumentException::class.java) { GameState.restore(hiddenTip, state.reserve, 1) }
        val hiddenMiddle = state.tableau.map { it.toMutableList() }
        hiddenMiddle[4][2] = hiddenMiddle[4][2].copy(faceUp = false)
        assertThrows(IllegalArgumentException::class.java) { GameState.restore(hiddenMiddle, state.reserve, 1) }
        assertThrows(IllegalArgumentException::class.java) { GameSession.restore(state, listOf(state)) }
        val overflow = GameSession.restore(GameState.restore(state.tableau, state.reserve, 1, Long.MAX_VALUE))
        rejected(overflow, GameAction.DealReserve, Rejection.ACTION_COUNT_OVERFLOW)
    }
}
