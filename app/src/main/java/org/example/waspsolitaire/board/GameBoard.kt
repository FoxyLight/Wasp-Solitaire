package org.example.waspsolitaire.board

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.waspsolitaire.engine.*
import org.example.waspsolitaire.game.GameUiState
import org.example.waspsolitaire.game.GameViewModel

private val Felt = Color(0xFF165443)
private val Ivory = Color(0xFFF7F4EB)
private val Blue = Color(0xFF263B62)
private val Ink = Color(0xFF192327)
private val Red = Color(0xFFB4232C)
private val Gold = Color(0xFFFFD782)

@Composable
fun GameBoardHost(model: GameViewModel) {
    val state by model.uiState.collectAsStateWithLifecycle()
    MaterialTheme { GameBoard(state) }
}

/** Read-only rendering; there are no gameplay callbacks or duplicated move rules. */
@Composable
fun GameBoard(state: GameUiState, modifier: Modifier = Modifier) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(modifier.fillMaxSize().background(Felt).safeDrawingPadding()
            .verticalScroll(rememberScrollState()).padding(horizontal = 8.dp, vertical = 16.dp)
            .testTag("board"), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("WASP", color = Ivory, fontSize = 28.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
            Text("CLASSIC SOLITAIRE", color = Ivory, fontSize = 12.sp, letterSpacing = 2.sp)
            Text(if (state.board.status == GameStatus.WON) "Four suits complete" else "Arrange each suit from King to Ace", color = Ivory, fontSize = 14.sp)
            Text("${state.board.completedRuns.size} of 4 suits complete", color = Gold, modifier = Modifier.testTag("progress"))
            state.selection?.let { Text("Selected tail · Column ${it.column + 1}, card ${it.cardIndex + 1}", color = Gold) }
            if (state.legalDestinations.isNotEmpty()) Text("Legal destinations · ${state.legalDestinations.sorted().joinToString { (it + 1).toString() }}", color = Ivory)
            when (val hint = state.hintedAction) {
                is GameAction.Move -> Text("Hint · Column ${hint.source + 1}, card ${hint.cardIndex + 1} → Column ${hint.destination + 1}", color = Gold)
                GameAction.DealReserve -> Text("Hint · Reserve available", color = Gold)
                else -> Unit
            }
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val geometry = BoardGeometry.fit(maxWidth.value, LocalDensity.current.fontScale)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(BoardGeometry.GUTTER.dp, Alignment.CenterHorizontally)) {
                    state.board.tableau.forEachIndexed { index, cards -> TableauColumn(index, cards, state, geometry) }
                }
            }
            Reserve(state)
            Text("Board preview · Play controls are coming in the next milestone.", color = Ivory, fontSize = 12.sp)
        }
    }
}

@Composable
private fun TableauColumn(index: Int, cards: List<TableauCard>, state: GameUiState, geometry: BoardGeometry) {
    val target = index in state.legalDestinations
    val hint = state.hintedAction as? GameAction.Move
    val hintedTarget = hint?.destination == index
    // Recognition is supplied by the engine. Unique cards identify its column by exposed Ace.
    val complete = cards.lastOrNull()?.let { it.card.rank == Rank.ACE && it.card.suit in state.board.completedRuns } == true
    Column(Modifier.width(geometry.cardWidth.dp).testTag("column-$index"), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("${index + 1}", color = Ivory, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
        Box(Modifier.fillMaxWidth().height(geometry.columnHeight(cards.map { it.faceUp }).dp)
            .border(if (target || hintedTarget) 2.dp else 1.dp, if (target || hintedTarget) Gold else Ivory.copy(alpha = .3f), RoundedCornerShape(5.dp))
            .testTag("column-surface-$index")
            .semantics { stateDescription = if (target) "Legal destination" else if (hintedTarget) "Hint destination" else "Tableau column ${index + 1}" }) {
            if (cards.isEmpty()) Text("—", color = Ivory, modifier = Modifier.align(Alignment.Center))
            var offset = 0f
            cards.forEachIndexed { cardIndex, item ->
                val selected = state.selection?.let { it.column == index && cardIndex >= it.cardIndex } == true
                val hinted = hint?.let { it.source == index && cardIndex >= it.cardIndex } == true
                PlayingCard(item, geometry, selected, hinted, Modifier.offset(y = offset.dp).testTag("card-$index-$cardIndex"), index, cardIndex)
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