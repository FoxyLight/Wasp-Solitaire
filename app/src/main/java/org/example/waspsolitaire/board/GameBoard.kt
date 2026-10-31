package org.example.waspsolitaire.board

import androidx.compose.foundation.Canvas
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.waspsolitaire.engine.*
import org.example.waspsolitaire.game.GameUiState
import org.example.waspsolitaire.game.GameViewModel
import org.example.waspsolitaire.game.*
import kotlin.math.roundToInt

private val Felt = Color(0xFF165443)
private val Ivory = Color(0xFFF7F4EB)
private val Blue = Color(0xFF263B62)
private val Ink = Color(0xFF192327)
private val Red = Color(0xFFB4232C)
private val Gold = Color(0xFFFFD782)

@Composable
fun GameBoardHost(model: GameViewModel) {
    val state by model.uiState.collectAsStateWithLifecycle()
    val result by model.lastResult.collectAsStateWithLifecycle()
    MaterialTheme { GameBoard(state, onIntent = { model.submit(it) }, feedback = feedbackFor(result)) }
}

internal fun feedbackFor(result: IntentResult?): String? = when (result) {
    is IntentResult.EngineRejected -> when (result.reason) {
        Rejection.INVALID_MOVE -> "That move is not allowed. Choose a highlighted destination."
        Rejection.NOTHING_TO_UNDO -> "No actions to undo."
        Rejection.RESERVE_EMPTY -> "The reserve has already been dealt."
        else -> "This action is not available."
    }
    is IntentResult.InvalidInput -> "Select a face-up card first."
    IntentResult.StaleRevision -> "The board changed. Try again or reopen the game."
    IntentResult.NoAvailableHint -> "No available tableau moves. Undo, Restart and New Game remain available."
    is IntentResult.StorageFailure -> "Could not save this action. Your game is unchanged. ${result.message}"
    is IntentResult.Committed -> "Action completed."
    else -> null
}

/** State projection plus explicit user intents. No move validation is implemented here. */
@Composable
fun GameBoard(state: GameUiState, modifier: Modifier = Modifier,
    onIntent: ((GameIntent) -> Unit)? = null, feedback: String? = null) {
    val currentState by rememberUpdatedState(state)
    val drag = remember { BoardDrag() }
    val dispatch: ((GameIntent) -> Unit)? = if (onIntent == null) null else { intent -> drag.notice = null; onIntent(intent) }
    val send by rememberUpdatedState(dispatch)
    LaunchedEffect(state.revision) { if (drag.active?.revision != state.revision) drag.active = null }
    BackHandler(onIntent != null && (drag.active != null || state.selection != null || state.hintedAction != null)) {
        if (drag.active != null) drag.cancel() else onIntent?.invoke(GameIntent.CancelSelection)
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
      Box(modifier.fillMaxSize().background(Felt).clipToBounds().testTag("game-screen")
          .onGloballyPositioned { drag.origin = it.positionInRoot() }) {
       Column(Modifier.fillMaxSize().safeDrawingPadding()) {
        if (dispatch != null) {
            GameControls(state, drag.active == null, dispatch)
            Text(drag.notice ?: feedback ?: "Tap to select. Hold a card to drag its tail.", color = Ivory, fontSize = 14.sp,
                maxLines = 3, modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).heightIn(min = 40.dp)
                    .testTag("action-feedback").semantics { liveRegion = LiveRegionMode.Polite })
        }
        Column(Modifier.weight(1f).fillMaxWidth()
            .verticalScroll(rememberScrollState(), enabled = drag.active == null).padding(horizontal = 8.dp, vertical = 16.dp)
            .testTag("board"), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("WASP", color = Ivory, fontSize = 28.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
            Text("CLASSIC SOLITAIRE", color = Ivory, fontSize = 12.sp, letterSpacing = 2.sp)
            Text(if (state.board.status == GameStatus.WON) "Four suits complete" else "Arrange each suit from King to Ace", color = Ivory, fontSize = 14.sp)
            Text("${state.board.completedRuns.size} of 4 suits complete", color = Gold, modifier = Modifier.testTag("progress"))
            Column(Modifier.height((120f * LocalDensity.current.fontScale).dp)) {
            Text(state.selection?.let { "Selected tail · Column ${it.column + 1}, card ${it.cardIndex + 1}" } ?: "Tap a face-up card to select its tail.", color = Gold, maxLines = 2)
            Text(if (state.legalDestinations.isNotEmpty()) "Legal destinations · ${state.legalDestinations.sorted().joinToString { (it + 1).toString() }}" else "Choose an explicit destination after selecting.", color = Ivory, maxLines = 2)
            when (val hint = state.hintedAction) {
                is GameAction.Move -> Text("Hint · Column ${hint.source + 1}, card ${hint.cardIndex + 1} → Column ${hint.destination + 1}", color = Gold)
                GameAction.DealReserve -> Text("Hint · Reserve available", color = Gold)
                else -> Text("Touch and hold to drag. Swipe normally to scroll.", color = Ivory, maxLines = 2)
            }
            }
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val geometry = BoardGeometry.fit(maxWidth.value, LocalDensity.current.fontScale)
                Row(Modifier.fillMaxWidth().onGloballyPositioned { drag.tableau = Rect(it.positionInRoot(), it.size.toSize()) }, horizontalArrangement = Arrangement.spacedBy(BoardGeometry.GUTTER.dp, Alignment.CenterHorizontally)) {
                    state.board.tableau.forEachIndexed { index, cards -> TableauColumn(index, cards, state, geometry, dispatch, drag,
                        onDragStart = { chosen, grab -> send?.let { drag.begin(chosen, grab, geometry, currentState, it) } },
                        onDragEnd = { send?.let { drag.finish(currentState, it) } }) }
                }
            }
            Reserve(state)
            Text("Select any face-up card and move its whole tail.", color = Ivory, fontSize = 14.sp)
            if (state.board.status == GameStatus.WON) Text("You won! All four suited runs remain on the table.", color = Gold,
                modifier = Modifier.testTag("victory").semantics { liveRegion = LiveRegionMode.Polite })
        }
       }
       drag.active?.let { lifted ->
           val position = lifted.pointer - lifted.grab - drag.origin
           Box(Modifier.offset { IntOffset(position.x.roundToInt(), position.y.roundToInt()) }
               .size(lifted.geometry.cardWidth.dp, lifted.geometry.columnHeight(lifted.cards.map { it.faceUp }).dp)
               .zIndex(10f).testTag("drag-preview").clearAndSetSemantics { contentDescription = "Dragging ${lifted.cards.size} cards" }) {
               var y = 0f
               lifted.cards.forEachIndexed { index, card ->
                   PlayingCard(card, lifted.geometry, true, false, Modifier.offset(y = y.dp), lifted.source.column, lifted.source.cardIndex + index)
                   y += if (card.faceUp) lifted.geometry.faceStep else lifted.geometry.backStep
               }
           }
           Text("Dragging ${lifted.cards.size} cards · ${drag.target()?.let { "Column ${it + 1}" } ?: "Choose a column"}", color = Gold,
               modifier = Modifier.align(Alignment.BottomCenter).background(Felt).padding(8.dp).zIndex(11f).testTag("drag-status"))
       }
      }
      state.pendingReset?.let { pending ->
          if (onIntent != null) AlertDialog(onDismissRequest = { onIntent(GameIntent.CancelReset) },
              title = { Text(if (pending.kind == ResetKind.RESTART) "Restart this deal?" else "Start a new game?") },
              text = { Text(feedback ?: "This replaces the current board and clears its Undo history.") },
              confirmButton = { TextButton(onClick = { onIntent(GameIntent.ConfirmReset(pending.revision)) }, modifier = Modifier.heightIn(min = 48.dp).testTag("confirm-reset")) { Text("Confirm") } },
              dismissButton = { TextButton(onClick = { onIntent(GameIntent.CancelReset) }, modifier = Modifier.heightIn(min = 48.dp).testTag("cancel-reset")) { Text("Keep playing") } })
      }
    }
}

@Composable
private fun GameControls(state: GameUiState, enabled: Boolean, send: (GameIntent) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Button({ send(GameIntent.Undo) }, Modifier.weight(1f).heightIn(min = 48.dp).testTag("undo"), enabled && state.session.history.isNotEmpty()) { Text("Undo") }
            Button({ send(GameIntent.Hint) }, Modifier.weight(1f).heightIn(min = 48.dp).testTag("hint"), enabled && state.board.status != GameStatus.WON) { Text("Hint") }
            Button({ send(GameIntent.DealReserve) }, Modifier.weight(1f).heightIn(min = 48.dp).testTag("deal"), enabled && state.board.reserve.isNotEmpty() && state.board.status != GameStatus.WON) { Text("Deal 3") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TextButton({ send(GameIntent.RequestRestart) }, Modifier.weight(1f).heightIn(min = 48.dp).testTag("restart"), enabled) { Text("Restart", color = Ivory) }
            TextButton({ send(GameIntent.RequestNewGame) }, Modifier.weight(1f).heightIn(min = 48.dp).testTag("new-game"), enabled) { Text("New Game", color = Ivory) }
            if (state.selection != null || state.hintedAction != null) TextButton({ send(GameIntent.CancelSelection) }, Modifier.weight(1f).heightIn(min = 48.dp).testTag("cancel-selection"), enabled) { Text("Cancel", color = Ivory) }
        }
    }
}

@Composable
private fun TableauColumn(index: Int, cards: List<TableauCard>, state: GameUiState, geometry: BoardGeometry,
    send: ((GameIntent) -> Unit)?, drag: BoardDrag,
    onDragStart: (CardSelection, Offset) -> Unit, onDragEnd: () -> Unit) {
    val start by rememberUpdatedState(onDragStart)
    val end by rememberUpdatedState(onDragEnd)
    val currentState by rememberUpdatedState(state)
    val currentSend by rememberUpdatedState(send)
    val target = index in state.legalDestinations
    val hint = state.hintedAction as? GameAction.Move
    val hintedTarget = hint?.destination == index
    // Recognition is supplied by the engine. Unique cards identify its column by exposed Ace.
    val complete = cards.lastOrNull()?.let { it.card.rank == Rank.ACE && it.card.suit in state.board.completedRuns } == true
    Column(Modifier.width(geometry.cardWidth.dp).testTag("column-$index").onGloballyPositioned { drag.columns[index] = Rect(it.positionInRoot(), it.size.toSize()) }, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("${index + 1}", color = Ivory, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
        Box(Modifier.fillMaxWidth().height(geometry.columnHeight(cards.map { it.faceUp }).dp)
            .border(if (target || hintedTarget) 2.dp else 1.dp, if (target || hintedTarget) Gold else Ivory.copy(alpha = .3f), RoundedCornerShape(5.dp))
            .testTag("column-surface-$index")
            .then(if (send != null && state.selection != null) Modifier.clickable(onClickLabel = "Move selected tail to column ${index + 1}") { send(GameIntent.MoveTo(index)) } else Modifier)
            .semantics { stateDescription = if (target) "Legal destination" else if (hintedTarget) "Hint destination" else "Tableau column ${index + 1}" }) {
            if (cards.isEmpty()) Text("—", color = Ivory, modifier = Modifier.align(Alignment.Center))
            var offset = 0f
            cards.forEachIndexed { cardIndex, item ->
                val selected = state.selection?.let { it.column == index && cardIndex >= it.cardIndex } == true
                val hinted = hint?.let { it.source == index && cardIndex >= it.cardIndex } == true
                val chosen = CardSelection(index, cardIndex)
                val interaction = if (send != null && item.faceUp && state.board.status != GameStatus.WON) Modifier
                    .clickable(onClickLabel = "Select tail or move to column ${index + 1}") {
                        val ui = currentState
                        if (ui.selection != null && index in ui.legalDestinations) currentSend?.invoke(GameIntent.MoveTo(index))
                        else currentSend?.invoke(GameIntent.Select(index, cardIndex))
                    }
                    .pointerInput(index, cardIndex, state.revision) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { start(chosen, it) }, onDragEnd = { end() }, onDragCancel = { drag.cancel() },
                            onDrag = { change, delta -> change.consume(); drag.move(delta) })
                    } else Modifier
                PlayingCard(item, geometry, selected, hinted, Modifier.offset(y = offset.dp).testTag("card-$index-$cardIndex")
                    .onGloballyPositioned { drag.cards[chosen] = Rect(it.positionInRoot(), it.size.toSize()) }
                    .alpha(if (drag.active?.source?.let { it.column == index && cardIndex >= it.cardIndex } == true) .25f else 1f)
                    .then(interaction), index, cardIndex)
                offset += if (item.faceUp) geometry.faceStep else geometry.backStep
            }
        }
        if (complete) Text("✓", color = Gold, modifier = Modifier.testTag("complete-$index").semantics { contentDescription = "Completed suit in column ${index + 1}" })
    }
}

@Composable
private fun PlayingCard(item: TableauCard, geometry: BoardGeometry, selected: Boolean, hinted: Boolean,
    modifier: Modifier, column: Int, index: Int) {
    val shape = RoundedCornerShape(4.dp)
    val label = if (item.faceUp) "${item.card.rank.name.lowercase()} of ${item.card.suit.name.lowercase()}, column ${column + 1}, card ${index + 1}" else "Face-down card, column ${column + 1}, card ${index + 1}"
    Box(modifier.size(geometry.cardWidth.dp, geometry.cardHeight.dp).clip(shape)
        .background(if (item.faceUp) Ivory else Blue)
        .border(if (selected || hinted) 2.dp else 1.dp, if (selected || hinted) Gold else Ink.copy(alpha = .5f), shape)
        .semantics(mergeDescendants = true) { contentDescription = label; stateDescription = if (selected) "Selected tail" else if (hinted) "Hint tail" else if (item.faceUp) "Face up" else "Face down" }) {
        if (item.faceUp) {
            val color = if (item.card.suit == Suit.HEARTS || item.card.suit == Suit.DIAMONDS) Red else Ink
            Column(Modifier.padding(3.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(rankLabel(item.card.rank), color = color, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif,
                    fontSize = (geometry.cardWidth * .27f).coerceAtMost(22f).sp,
                    lineHeight = (geometry.cardWidth * .27f * 1.15f).coerceAtMost(26f).sp, maxLines = 1)
                SuitMark(item.card.suit, color, Modifier.size((geometry.cardWidth * .24f).dp))
            }
            SuitMark(item.card.suit, color.copy(alpha = .85f), Modifier.align(Alignment.BottomEnd).padding(5.dp).size((geometry.cardWidth * .36f).dp))
        } else {
            Canvas(Modifier.fillMaxSize().padding(4.dp)) {
                drawRoundRect(Ivory.copy(alpha = .65f), style = Stroke(1.dp.toPx()))
                var x = -size.height
                while (x < size.width) {
                    drawLine(Ivory.copy(alpha = .18f), Offset(x, 0f), Offset(x + size.height, size.height), 1.dp.toPx())
                    x += 7.dp.toPx()
                }
            }
        }
    }
}

internal fun rankLabel(rank: Rank): String = when (rank) {
    Rank.ACE -> "A"; Rank.JACK -> "J"; Rank.QUEEN -> "Q"; Rank.KING -> "K"
    else -> rank.value.toString()
}

/** Original suit paths, independent of emoji glyphs and third-party artwork. */
@Composable
private fun SuitMark(suit: Suit, color: Color, modifier: Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val path = Path()
        when (suit) {
            Suit.DIAMONDS -> { path.moveTo(w/2, 0f); path.lineTo(w, h/2); path.lineTo(w/2, h); path.lineTo(0f, h/2); path.close(); drawPath(path, color) }
            Suit.HEARTS -> { path.moveTo(w/2, h); path.cubicTo(-w*.3f, h*.4f, 0f, -h*.3f, w/2, h*.2f); path.cubicTo(w, -h*.3f, w*1.3f, h*.4f, w/2, h); drawPath(path, color) }
            Suit.SPADES -> { path.moveTo(w/2, 0f); path.cubicTo(-w*.4f, h*.65f, 0f, h, w/2, h*.65f); path.cubicTo(w, h, w*1.4f, h*.65f, w/2, 0f); drawPath(path, color); drawLine(color, Offset(w/2,h*.55f),Offset(w/2,h),w*.16f) }
            Suit.CLUBS -> { drawCircle(color,w*.26f,Offset(w/2,h*.27f)); drawCircle(color,w*.26f,Offset(w*.27f,h*.60f)); drawCircle(color,w*.26f,Offset(w*.73f,h*.60f)); drawLine(color,Offset(w/2,h*.5f),Offset(w/2,h),w*.16f) }
        }
    }
}

@Composable
private fun Reserve(state: GameUiState) {
    Column(Modifier.fillMaxWidth().testTag("reserve"), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("RESERVE", color = Ivory, fontSize = 12.sp, letterSpacing = 2.sp)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(state.board.reserve.size) { index ->
                    Box(Modifier.size(25.dp, 36.dp).background(Blue, RoundedCornerShape(3.dp)).border(1.dp, Ivory, RoundedCornerShape(3.dp))
                        .testTag("reserve-card-$index").semantics { contentDescription = "Face-down reserve card" })
                }
            }
            Text(if (state.board.reserve.isEmpty()) "Dealt · 0 remaining" else "Ready · 3 cards remaining", color = Ivory,
                modifier = Modifier.weight(1f).testTag("reserve-status"))
        }
    }
}
