package org.example.waspsolitaire.engine

import java.util.Random

sealed interface GameAction {
    data class Move(val source: Int, val cardIndex: Int, val destination: Int) : GameAction
    data object DealReserve : GameAction
    data object Undo : GameAction
}

enum class Rejection { GAME_WON, INVALID_MOVE, RESERVE_EMPTY, NOTHING_TO_UNDO, ACTION_COUNT_OVERFLOW }

sealed interface Transition {
    val session: GameSession
    data class Accepted(override val session: GameSession) : Transition
    data class Rejected(override val session: GameSession, val reason: Rejection) : Transition
}

/** Pure rules API. Callers publish/persist only Accepted transitions. No UI or clock effects. */
object ClassicWasp {
    /** Stable suit/rank order, Java's specified seeded Random, and explicit Fisher-Yates. */
    fun deck(seed: Long): List<Card> {
        val deck = Suit.entries.flatMap { suit -> Rank.entries.map { Card(suit, it) } }.toMutableList()
        val random = Random(seed)
        for (index in deck.lastIndex downTo 1) {
            val other = random.nextInt(index + 1)
            val card = deck[index]
            deck[index] = deck[other]
            deck[other] = card
        }
        return frozen(deck)
    }

    /** Column-major deal; the last three shuffled cards retain their order in reserve. */
    fun newGame(seed: Long): GameSession {
        val deck = deck(seed)
        val columns = (0..6).map { column ->
            (0..6).map { row -> TableauCard(deck[column * 7 + row], column >= 4 || row >= 3) }
        }
        return GameSession.restore(GameState.restore(columns, deck.takeLast(3), seed))
    }

    fun isLegalMove(state: GameState, move: GameAction.Move): Boolean {
        if (state.status == GameStatus.WON || move.source !in 0..6 ||
            move.destination !in 0..6 || move.source == move.destination) return false
        val source = state.tableau[move.source]
        val selected = source.getOrNull(move.cardIndex) ?: return false
        if (!selected.faceUp) return false
        val target = state.tableau[move.destination].lastOrNull() ?: return true
        return target.faceUp && selected.card.suit == target.card.suit &&
            selected.card.rank.value + 1 == target.card.rank.value
    }

    fun legalMoves(state: GameState): List<GameAction.Move> = frozen(buildList {
        state.tableau.forEachIndexed { source, column ->
            column.indices.forEach { cardIndex ->
                (0..6).forEach { destination ->
                    val move = GameAction.Move(source, cardIndex, destination)
                    if (isLegalMove(state, move)) add(move)
                }
            }
        }
    })

    /** Deterministic legal suggestion only; does not rank strategies or mutate state/history. */
    fun hint(state: GameState): GameAction? = legalMoves(state).firstOrNull()
        ?: GameAction.DealReserve.takeIf { state.status == GameStatus.IN_PROGRESS && state.reserve.isNotEmpty() }

    fun apply(session: GameSession, action: GameAction): Transition {
        fun reject(reason: Rejection) = Transition.Rejected(session, reason)
        val state = session.state
        if (action == GameAction.Undo) {
            val previous = session.history.lastOrNull() ?: return reject(Rejection.NOTHING_TO_UNDO)
            return Transition.Accepted(GameSession.restore(previous, session.history.dropLast(1)))
        }
        if (state.status == GameStatus.WON) return reject(Rejection.GAME_WON)
        if (state.actionCount == Long.MAX_VALUE) return reject(Rejection.ACTION_COUNT_OVERFLOW)
        val columns = state.tableau.map { it.toMutableList() }
        var reserve = state.reserve
        when (action) {
            is GameAction.Move -> {
                if (!isLegalMove(state, action)) return reject(Rejection.INVALID_MOVE)
                val source = columns[action.source]
                val tail = source.subList(action.cardIndex, source.size).toList()
                source.subList(action.cardIndex, source.size).clear()
                columns[action.destination].addAll(tail)
                if (source.isNotEmpty() && !source.last().faceUp) {
                    source[source.lastIndex] = source.last().copy(faceUp = true)
                }
            }
            GameAction.DealReserve -> {
                if (reserve.isEmpty()) return reject(Rejection.RESERVE_EMPTY)
                reserve.forEachIndexed { column, card -> columns[column].add(TableauCard(card)) }
                reserve = emptyList()
            }
            GameAction.Undo -> error("Undo handled above")
        }
        val next = GameState.restore(columns, reserve, state.seed, state.actionCount + 1)
        return Transition.Accepted(GameSession.restore(next, session.history + state))
    }
}
