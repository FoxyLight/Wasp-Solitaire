package org.example.waspsolitaire.engine

import java.util.Collections

internal fun <T> frozen(items: Collection<T>): List<T> =
    Collections.unmodifiableList(ArrayList(items))

enum class Suit { CLUBS, DIAMONDS, HEARTS, SPADES }

enum class Rank(val value: Int) {
    ACE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7),
    EIGHT(8), NINE(9), TEN(10), JACK(11), QUEEN(12), KING(13)
}

data class Card(val suit: Suit, val rank: Rank)
data class TableauCard(val card: Card, val faceUp: Boolean = true)
enum class GameStatus { IN_PROGRESS, WON }

/** Zero-based columns; cards run from deepest to exposed. No cards leave the tableau. */
class GameState private constructor(
    tableau: List<List<TableauCard>>,
    reserve: List<Card>,
    val seed: Long,
    val actionCount: Long,
) {
    val tableau: List<List<TableauCard>> = frozen(tableau.map { frozen(it) })
    val reserve: List<Card> = frozen(reserve)

    /** A complete run is an exposed K-to-A suffix, even if other cards precede it. */
    val completedRuns: List<Suit> = frozen(this.tableau.mapNotNull { column ->
        val run = column.takeLast(13)
        if (run.size == 13 && run.withIndex().all { (index, item) ->
                item.faceUp && item.card.suit == run.first().card.suit &&
                    item.card.rank.value == 13 - index
            }) run.first().card.suit else null
    })
    val status: GameStatus = if (completedRuns.size == 4) GameStatus.WON else GameStatus.IN_PROGRESS

    override fun equals(other: Any?): Boolean = other is GameState &&
        tableau == other.tableau && reserve == other.reserve &&
        seed == other.seed && actionCount == other.actionCount

    override fun hashCode(): Int = listOf(tableau, reserve, seed, actionCount).hashCode()

    companion object {
        /** Validates restored snapshots and defensively copies all collection inputs. */
        fun restore(
            tableau: List<List<TableauCard>>,
            reserve: List<Card>,
            seed: Long,
            actionCount: Long = 0,
        ): GameState {
            require(tableau.size == 7) { "Exactly seven tableau columns required" }
            require(reserve.size == 0 || reserve.size == 3) { "Reserve must contain zero or three cards" }
            require(actionCount >= 0) { "Action count cannot be negative" }
            val cards = tableau.flatten().map { it.card } + reserve
            require(cards.size == 52 && cards.toSet().size == 52) { "All 52 unique cards required" }
            tableau.forEach { column ->
                require(column.isEmpty() || column.last().faceUp) { "Exposed cards must be face up" }
                require(column.dropWhile { !it.faceUp }.all { it.faceUp }) { "Hidden cards must form a prefix" }
            }
            return GameState(tableau, reserve, seed, actionCount)
        }
    }
}

/** Immutable prior-state snapshots, without a fixed history cap. Persist together with state. */
class GameSession private constructor(val state: GameState, history: List<GameState>) {
    val history: List<GameState> = frozen(history)

    companion object {
        fun restore(state: GameState, history: List<GameState> = emptyList()): GameSession {
            val snapshots = history + state
            require(snapshots.all { it.seed == state.seed }) { "Undo must stay within the same deal" }
            require(snapshots.zipWithNext().all { (before, after) ->
                before.actionCount < Long.MAX_VALUE && after.actionCount == before.actionCount + 1
            }) { "Undo snapshots must have consecutive action counts" }
            return GameSession(state, history)
        }
    }
}
