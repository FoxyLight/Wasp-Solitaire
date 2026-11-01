package org.example.waspsolitaire.board

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import org.example.waspsolitaire.engine.TableauCard
import org.example.waspsolitaire.game.CardSelection
import org.example.waspsolitaire.game.GameIntent
import org.example.waspsolitaire.game.GameUiState

/** Transient screen coordinates and preview only; game legality stays in the controller. */
internal data class DragTail(
    val source: CardSelection, val revision: Long, val cards: List<TableauCard>,
    val geometry: BoardGeometry, val grab: Offset, val pointer: Offset,
)

internal class BoardDrag {
    val cards = mutableMapOf<CardSelection, Rect>()
    val columns = mutableMapOf<Int, Rect>()
    var tableau = Rect.Zero
    var viewport = Rect.Zero
    var origin = Offset.Zero
    var active by mutableStateOf<DragTail?>(null)
    var notice by mutableStateOf<String?>(null)

    fun begin(source: CardSelection, grab: Offset, geometry: BoardGeometry, state: GameUiState, send: (GameIntent) -> Unit) {
        val bounds = cards[source] ?: return
        val tail = state.board.tableau.getOrNull(source.column)?.drop(source.cardIndex).orEmpty()
        if (tail.firstOrNull()?.faceUp != true) return
        notice = null
        active = DragTail(source, state.revision, tail, geometry, grab, bounds.topLeft + grab)
        if (state.selection != source) send(GameIntent.Select(source.column, source.cardIndex))
    }

    fun move(delta: Offset) { active = active?.let { it.copy(pointer = it.pointer + delta) } }

    /** Full tableau height for every column permits horizontal drops beside long/empty tails. */
    fun target(): Int? = active?.pointer?.let { point ->
        columns.entries.firstOrNull { (_, rect) ->
            viewport.contains(point) && point.x >= rect.left && point.x < rect.right && point.y >= tableau.top && point.y < tableau.bottom
        }?.key
    }

    fun cancel() { active = null; notice = "Drag cancelled. Cards have not moved." }

    fun finish(state: GameUiState, send: (GameIntent) -> Unit) {
        val drag = active ?: return
        val target = target()
        active = null
        if (drag.revision != state.revision) notice = "The board changed. Select the cards again."
        else if (target == null) notice = "Drop cancelled. Choose a tableau column."
        else send(GameIntent.Move(drag.source.column, drag.source.cardIndex, target, drag.revision))
    }
}
