package org.example.waspsolitaire.board

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.example.waspsolitaire.engine.*
import org.example.waspsolitaire.game.*
import kotlin.math.roundToInt

private val Felt = Color(0xFF165443)
private val Ivory = Color(0xFFF7F4EB)
private val Blue = Color(0xFF263B62)
private val Ink = Color(0xFF192327)
private val Red = Color(0xFFB4232C)
private val Gold = Color(0xFFFFD782)
private val DeepGreen = Color(0xFF103C31)

@Composable
fun GameBoardHost(model: GameViewModel) {
    val state by model.uiState.collectAsStateWithLifecycle()
    val result by model.lastResult.collectAsStateWithLifecycle()
    MaterialTheme { GameBoard(state, onIntent = { model.submit(it) }, feedback = feedbackFor(result)) }
}

internal fun feedbackFor(result: IntentResult?): String? = when (result) {
    is IntentResult.EngineRejected -> when (result.reason) {
        Rejection.INVALID_MOVE -> "Selection cleared; no move made."
        Rejection.NOTHING_TO_UNDO -> "No actions to undo."
        Rejection.RESERVE_EMPTY -> "The reserve has already been dealt."
        else -> "This action is not available."
    }
    is IntentResult.InvalidInput -> "Select a face-up card first."
    IntentResult.StaleRevision -> "The board changed. Select the cards again."
    is IntentResult.StorageFailure -> "Could not apply this action. Your game is unchanged. ${result.message}"
    is IntentResult.Committed -> "Action completed."
    else -> null
}

/** The board and all accessible actions observe the same authoritative state. */
@Composable
fun GameBoard(state: GameUiState, modifier: Modifier = Modifier,
    onIntent: ((GameIntent) -> Unit)? = null, feedback: String? = null) {
    val currentState by rememberUpdatedState(state)
    val drag = remember { BoardDrag() }
    val scroll = rememberScrollState()
    var help by remember { mutableStateOf(false) }
    val dispatch: ((GameIntent) -> Unit)? = onIntent?.let { handler -> { intent -> drag.notice = null; handler(intent) } }
    val send by rememberUpdatedState(dispatch)
    LaunchedEffect(state.revision) { if (drag.active?.revision != state.revision) drag.active = null }
    BackHandler(onIntent != null && !help && (drag.active != null || state.selection != null)) {
        if (drag.active != null) drag.cancel() else onIntent?.invoke(GameIntent.CancelSelection)
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        BoxWithConstraints(modifier.fillMaxSize().background(Felt).clipToBounds().testTag("game-screen")
            .onGloballyPositioned { drag.origin = it.positionInRoot() }) {
            val shortWindow = maxHeight < 480.dp
            val fontScale = LocalDensity.current.fontScale
            val insets = WindowInsets.safeDrawing.asPaddingValues()
            val safeHeight = maxHeight - insets.calculateTopPadding() - insets.calculateBottomPadding()
            val inlineControls = shortWindow && maxWidth >= 480.dp && (safeHeight < 224.dp || fontScale >= 1.75f)
            val rail = shortWindow && maxWidth >= 600.dp && !inlineControls
            LaunchedEffect(maxWidth, maxHeight) { drag.active = null }
            val selectionMessage = state.selection?.let { "${state.selectedTail.first().card.spokenName()}, ${state.selectedTail.size}-card tail selected. Choose a legal column." }
            val message = when {
                state.board.status == GameStatus.WON -> "You won! All four suited runs remain on the table. Undo or start a New Game."
                drag.notice != null -> drag.notice
                feedback?.startsWith("Could not") == true || feedback?.startsWith("The board changed") == true -> feedback
                state.selection != null -> selectionMessage
                else -> feedback
            }
            Column(Modifier.fillMaxSize().safeDrawingPadding()) {
                UtilityRow(state, dispatch, drag.active == null, inlineControls) { help = true }
                val quiet = message == selectionMessage || message == "Selection cleared; no move made." || message == "Action completed."
                if (message != null) Text(message, color = if (quiet) Felt else Ivory, fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth().then(if (quiet) Modifier.height(1.dp) else Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                        .testTag(if (state.board.status == GameStatus.WON) "victory" else "action-feedback")
                        .semantics { liveRegion = LiveRegionMode.Polite })
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    BoxWithConstraints(Modifier.weight(1f).fillMaxHeight()
                        .onGloballyPositioned { drag.viewport = Rect(it.positionInRoot(), it.size.toSize()) }) {
                        val geometry = BoardGeometry.fit(maxWidth.value - 16f, fontScale,
                            maxRankSize = if (shortWindow && fontScale >= 1.75f) 16.8f else if (shortWindow) 20.4f else 26.4f)
                        val height = state.board.tableau.maxOf { geometry.columnHeight(it.map { card -> card.faceUp }) }
                        Column(Modifier.fillMaxSize().verticalScroll(scroll, enabled = drag.active == null)
                            .padding(horizontal = 8.dp, vertical = if (inlineControls) 4.dp else 8.dp).testTag("board")) {
                            Row(Modifier.fillMaxWidth().onGloballyPositioned { drag.tableau = Rect(it.positionInRoot(), it.size.toSize()) },
                                horizontalArrangement = Arrangement.spacedBy(BoardGeometry.GUTTER.dp, Alignment.CenterHorizontally)) {
                                state.board.tableau.forEachIndexed { index, cards ->
                                    TableauColumn(index, cards, state, geometry, height, dispatch, drag,
                                        onDragStart = { chosen, grab -> send?.let { drag.begin(chosen, grab, geometry, currentState, it) } },
                                        onDragEnd = { send?.let { drag.finish(currentState, it) } })
                                }
                            }
                        }
                        if (scroll.maxValue > 0) Canvas(Modifier.align(Alignment.CenterEnd).width(3.dp).fillMaxHeight()
                            .padding(vertical = 12.dp).clearAndSetSemantics { }) {
                            val total = scroll.viewportSize.toFloat() + scroll.maxValue
                            val thumb = (size.height * scroll.viewportSize / total).coerceIn(24.dp.toPx().coerceAtMost(size.height), size.height)
                            drawRoundRect(Ivory.copy(alpha = .25f), cornerRadius = CornerRadius(2.dp.toPx()))
                            drawRoundRect(Ivory, topLeft = Offset(0f, (size.height - thumb) * scroll.value / scroll.maxValue),
                                size = androidx.compose.ui.geometry.Size(size.width, thumb), cornerRadius = CornerRadius(2.dp.toPx()))
                        }
                        drag.active?.let { lifted ->
                            Text("Dragging ${lifted.cards.size} cards · ${drag.target()?.let { "Column ${it + 1}" } ?: "Choose a column"}",
                                color = Ivory, modifier = Modifier.align(Alignment.BottomCenter).background(DeepGreen)
                                    .padding(8.dp).zIndex(11f).testTag("drag-status"))
                        }
                    }
                    if (rail && dispatch != null) PlayControls(state, drag.active == null, true, dispatch)
                }
                if (!rail && !inlineControls && dispatch != null) PlayControls(state, drag.active == null, false, dispatch)
            }
            drag.active?.let { lifted ->
                val position = lifted.pointer - lifted.grab - drag.origin
                Box(Modifier.offset { IntOffset(position.x.roundToInt(), position.y.roundToInt()) }
                    .size(lifted.geometry.cardWidth.dp, lifted.geometry.columnHeight(lifted.cards.map { it.faceUp }).dp)
                    .zIndex(10f).testTag("drag-preview").clearAndSetSemantics { contentDescription = "Dragging ${lifted.cards.size} cards" }) {
                    var y = 0f
                    lifted.cards.forEachIndexed { index, card ->
                        PlayingCard(card, lifted.geometry, false, "", Modifier.offset(y = y.dp), lifted.source.column, lifted.source.cardIndex + index)
                        y += if (card.faceUp) lifted.geometry.faceStep else lifted.geometry.backStep
                    }
                }
            }
        }
        state.pendingReset?.let { pending ->
            if (onIntent != null) AlertDialog(onDismissRequest = { onIntent(GameIntent.CancelReset) }, containerColor = Ivory,
                titleContentColor = Ink, textContentColor = Ink,
                title = { Text(if (pending.kind == ResetKind.RESTART) "Restart this deal?" else "Start a new game?") },
                text = { Text(if (feedback?.contains("unchanged") == true) feedback else "This replaces the current board and clears its Undo history.") },
                confirmButton = { TextButton({ onIntent(GameIntent.ConfirmReset(pending.revision)) }, Modifier.heightIn(min = 48.dp).testTag("confirm-reset")) {
                    Text(if (pending.kind == ResetKind.RESTART) "Restart" else "New Game", color = DeepGreen)
                } },
                dismissButton = { TextButton({ onIntent(GameIntent.CancelReset) }, Modifier.heightIn(min = 48.dp).testTag("cancel-reset")) { Text("Keep playing", color = DeepGreen) } })
        }
        if (help) AlertDialog(onDismissRequest = { help = false }, containerColor = Ivory, titleContentColor = Ink, textContentColor = Ink,
            title = { Text("How to play") },
            text = { Column(Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState())) {
                Text("Tap any face-up card to select it and every card below it. Tap a legal column to move the whole group. Tap an illegal column to clear selection without moving. Back also clears selection.\n\nMove onto the next higher rank of the same suit, or onto an empty column. Attached cards may be unordered. Deal the three reserve cards once, even when other moves are available. Completed King-to-Ace suits stay on the board. Undo reverses committed actions.\n\nTalkBack: use a column's actions to select any exposed card, then move to a legal column. Keyboard: focus a column, use Up/Down to choose a card, Enter to select or move, Escape to cancel.\n\nThis session is held in memory. Closing the process loses the game until durable saves are implemented.")
            } }, confirmButton = { TextButton({ help = false }, Modifier.heightIn(min = 48.dp)) { Text("Back to game", color = DeepGreen) } })
    }
}

@Composable
private fun UtilityRow(state: GameUiState, send: ((GameIntent) -> Unit)?, enabled: Boolean, inlineControls: Boolean, help: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth().padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Row(Modifier.weight(1f).testTag("reserve"), verticalAlignment = Alignment.CenterVertically) {
            if (state.board.reserve.isNotEmpty()) Box(Modifier.width(46.dp).height(40.dp).clearAndSetSemantics { }) {
                repeat(state.board.reserve.size) { index ->
                    Box(Modifier.offset(x = (index * 8).dp).size(25.dp, 36.dp).background(Blue, RoundedCornerShape(3.dp))
                        .border(1.dp, Ivory, RoundedCornerShape(3.dp)).testTag("reserve-card-$index"))
                }
            }
            Text(if (state.board.reserve.isEmpty()) "Reserve dealt" else "3 reserve", color = Ivory, fontSize = 16.sp,
                modifier = Modifier.testTag("reserve-status"))
        }
        if (send != null && inlineControls) PlayControls(state, enabled, false, send, inline = true)
        if (send != null) Box {
            TextButton({ expanded = true }, Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp).testTag("more"), enabled = enabled,
                contentPadding = PaddingValues(8.dp)) {
                    Canvas(Modifier.size(24.dp).clearAndSetSemantics { contentDescription = "Menu" }) {
                        for (y in listOf(4f, 12f, 20f)) drawCircle(Ivory, 2.dp.toPx(), Offset(size.width / 2, y.dp.toPx()))
                    }
                }
            DropdownMenu(expanded, { expanded = false }, containerColor = Ivory) {
                DropdownMenuItem(text = { Text("Restart", color = Ink) }, onClick = { expanded = false; send(GameIntent.RequestRestart) }, modifier = Modifier.heightIn(min = 48.dp).testTag("restart"))
                DropdownMenuItem(text = { Text("New Game", color = Ink) }, onClick = { expanded = false; send(GameIntent.RequestNewGame) }, modifier = Modifier.heightIn(min = 48.dp).testTag("new-game"))
                DropdownMenuItem(text = { Text("Help", color = Ink) }, onClick = { expanded = false; help() }, modifier = Modifier.heightIn(min = 48.dp).testTag("help"))
            }
        }
    }
}

@Composable
private fun PlayControls(state: GameUiState, enabled: Boolean, rail: Boolean, send: (GameIntent) -> Unit, inline: Boolean = false) {
    @Composable fun Action(deal: Boolean, modifier: Modifier) {
        Button(onClick = { send(if (deal) GameIntent.DealReserve else GameIntent.Undo) },
            modifier = modifier.heightIn(min = 56.dp).testTag(if (deal) "deal" else "undo"),
            enabled = enabled && (if (deal) state.board.reserve.isNotEmpty() && state.board.status != GameStatus.WON else state.session.history.isNotEmpty()),
            shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (deal) Ivory else Blue, contentColor = if (deal) Ink else Ivory,
                disabledContainerColor = if (deal) Ivory.copy(alpha = .6f) else Blue.copy(alpha = .6f), disabledContentColor = if (deal) Ink else Ivory)) {
            Text(if (deal) "Deal 3" else "Undo", fontSize = 18.sp)
        }
    }
    if (rail) Column(Modifier.widthIn(min = 96.dp, max = 128.dp).fillMaxHeight().padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)) {
        Action(false, Modifier.fillMaxWidth()); Action(true, Modifier.fillMaxWidth())
    } else Row((if (inline) Modifier.width(280.dp) else Modifier.fillMaxWidth())
        .padding(horizontal = if (inline) 4.dp else 12.dp, vertical = if (inline) 4.dp else 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Action(false, Modifier.weight(1f)); Action(true, Modifier.weight(1f))
    }
}

private fun Card.spokenName() = "${rank.name.lowercase()} of ${suit.name.lowercase()}"

@Composable
private fun TableauColumn(index: Int, cards: List<TableauCard>, state: GameUiState, geometry: BoardGeometry, surfaceHeight: Float,
    send: ((GameIntent) -> Unit)?, drag: BoardDrag, onDragStart: (CardSelection, Offset) -> Unit, onDragEnd: () -> Unit) {
    val currentState by rememberUpdatedState(state)
    val currentSend by rememberUpdatedState(send)
    val start by rememberUpdatedState(onDragStart)
    val end by rememberUpdatedState(onDragEnd)
    val target = index in state.legalDestinations
    val source = state.selection?.takeIf { it.column == index }
    val playable = send != null && state.board.status != GameStatus.WON
    val exposed = cards.indices.filter { cards[it].faceUp }
    var cursor by remember(index, cards) { mutableIntStateOf(exposed.firstOrNull() ?: 0) }
    var focused by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bring = remember { BringIntoViewRequester() }
    val offsets = cards.indices.map { n -> cards.take(n).sumOf { (if (it.faceUp) geometry.faceStep else geometry.backStep).toDouble() }.toFloat() }
    val density = LocalDensity.current
    fun revealCursor() { scope.launch {
        val y = with(density) { (offsets.getOrNull(cursor) ?: 0f).dp.toPx() }
        val h = with(density) { geometry.cardHeight.dp.toPx() }
        bring.bringIntoView(Rect(0f, y, with(density) { geometry.cardWidth.dp.toPx() }, y + h))
    } }
    LaunchedEffect(source) {
        if (source != null) { cursor = source.cardIndex; revealCursor() }
    }
    val summary = "Column ${index + 1}, ${cards.size} cards, ${cards.count { !it.faceUp }} face down, " +
        (cards.lastOrNull()?.takeIf { it.faceUp }?.let { "exposed ${it.card.spokenName()}" } ?: "empty column")
    val actions = if (!playable) emptyList() else if (state.selection != null) buildList {
        if (target) add(CustomAccessibilityAction("Move ${state.selectedTail.size}-card tail to column ${index + 1}") { currentSend?.invoke(GameIntent.MoveTo(index)); true })
        add(CustomAccessibilityAction("Cancel selection") { currentSend?.invoke(GameIntent.CancelSelection); true })
    } else exposed.map { n -> CustomAccessibilityAction("Select ${cards[n].card.spokenName()} and ${cards.size - n - 1} attached cards") {
        currentSend?.invoke(GameIntent.Select(index, n)); true
    } }
    Column(Modifier.width(geometry.cardWidth.dp).testTag("column-$index")
        .onGloballyPositioned { drag.columns[index] = Rect(it.positionInRoot(), it.size.toSize()) }
        .semantics { contentDescription = summary; customActions = actions; isTraversalGroup = true }
        .onFocusChanged { focused = it.isFocused }
        .onKeyEvent { event ->
            if (!playable || event.type != KeyEventType.KeyDown) false else when (event.key) {
                Key.DirectionDown, Key.DirectionUp -> {
                    val position = exposed.indexOf(cursor)
                    cursor = exposed.getOrNull((position + if (event.key == Key.DirectionDown) 1 else -1).coerceIn(0, (exposed.size - 1).coerceAtLeast(0))) ?: 0
                    revealCursor(); true
                }
                Key.Enter, Key.NumPadEnter, Key.Spacebar -> {
                    if (currentState.selection != null) currentSend?.invoke(GameIntent.MoveTo(index))
                    else if (cursor in exposed) currentSend?.invoke(GameIntent.Select(index, cursor))
                    true
                }
                Key.Escape -> { currentSend?.invoke(GameIntent.CancelSelection); true }
                else -> false
            }
        }.focusable(playable), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.fillMaxWidth().height(surfaceHeight.dp).bringIntoViewRequester(bring)
            .testTag("column-surface-$index")
            .then(if (send != null && state.selection != null) Modifier.clickable(onClickLabel = if (target) "Move selected tail to column ${index + 1}" else "Clear selection; illegal destination") { send(GameIntent.MoveTo(index)) } else Modifier)
            .semantics { stateDescription = if (target) "Legal destination" else if (state.selection != null) "Illegal destination" else if (cards.isEmpty()) "Empty column" else "Tableau column ${index + 1}" }
            .drawWithContent {
                drawContent()
                if (target && cards.isNotEmpty()) drawRoundRect(Ivory,
                    topLeft = Offset(0f, offsets.last().dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(size.width, geometry.cardHeight.dp.toPx()),
                    cornerRadius = CornerRadius(5.dp.toPx()),
                    style = Stroke(2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 4.dp.toPx()))))
                source?.let {
                    val top = offsets[it.cardIndex].dp.toPx()
                    val bottom = geometry.columnHeight(cards.map { item -> item.faceUp }).dp.toPx()
                    drawRoundRect(Gold, Offset(0f, top), androidx.compose.ui.geometry.Size(size.width, bottom - top), CornerRadius(5.dp.toPx()), style = Stroke(3.dp.toPx()))
                    drawRoundRect(DeepGreen, Offset(1.dp.toPx(), top + 1.dp.toPx()), androidx.compose.ui.geometry.Size(size.width - 2.dp.toPx(), bottom - top - 2.dp.toPx()), CornerRadius(4.dp.toPx()), style = Stroke(1.dp.toPx()))
                }
            }) {
            if (cards.isEmpty()) Text("—", color = Ivory, modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp).clearAndSetSemantics { })
            cards.forEachIndexed { cardIndex, item ->
                val selected = source != null && cardIndex >= source.cardIndex
                val chosen = CardSelection(index, cardIndex)
                val stateLabel = when {
                    source?.cardIndex == cardIndex -> "Selected start, ${cards.size - cardIndex}-card tail, ${cards.size - cardIndex - 1} attached cards"
                    selected -> "Attached to selected tail"
                    item.faceUp -> "Face up, ${cards.size - cardIndex}-card tail, ${cards.size - cardIndex - 1} attached cards"
                    else -> "Face down"
                }
                val interaction = if (playable && item.faceUp) Modifier.clickable(onClickLabel = "Select tail or choose destination column ${index + 1}") {
                    if (currentState.selection != null) currentSend?.invoke(GameIntent.MoveTo(index))
                    else currentSend?.invoke(GameIntent.Select(index, cardIndex))
                }.pointerInput(index, cardIndex, state.revision, geometry) {
                    detectDragGesturesAfterLongPress(onDragStart = { start(chosen, it) }, onDragEnd = { end() }, onDragCancel = { drag.cancel() },
                        onDrag = { change, delta -> change.consume(); drag.move(delta) })
                } else Modifier
                PlayingCard(item, geometry, selected, stateLabel, Modifier.offset(y = offsets[cardIndex].dp).testTag("card-$index-$cardIndex")
                    .onGloballyPositioned { drag.cards[chosen] = Rect(it.positionInRoot(), it.size.toSize()) }
                    .alpha(if (drag.active?.source?.let { it.column == index && cardIndex >= it.cardIndex } == true) .25f else 1f)
                    .then(if (focused && cursor == cardIndex) Modifier.border(2.dp, DeepGreen) else Modifier).then(interaction), index, cardIndex)
            }
        }
    }
}

@Composable
private fun PlayingCard(item: TableauCard, geometry: BoardGeometry, isSelected: Boolean, stateLabel: String,
    modifier: Modifier, column: Int, index: Int) {
    val scale = LocalDensity.current.fontScale
    val stacked = geometry.stackedIndices(scale)
    val shape = RoundedCornerShape(4.dp)
    val label = if (item.faceUp) "${item.card.spokenName()}, column ${column + 1}, card ${index + 1}" else "Face-down card, column ${column + 1}, card ${index + 1}"
    Box(modifier.size(geometry.cardWidth.dp, geometry.cardHeight.dp).clip(shape).background(if (item.faceUp) Ivory else Blue)
        .border(1.dp, Ink.copy(alpha = .5f), shape).semantics(mergeDescendants = true) {
            contentDescription = label; stateDescription = stateLabel; selected = isSelected
        }) {
        if (item.faceUp) {
            val color = if (item.card.suit == Suit.HEARTS || item.card.suit == Suit.DIAMONDS) Red else Ink
            val textMeasurer = rememberTextMeasurer()
            val rank = textMeasurer.measure(rankLabel(item.card.rank), style = TextStyle(
                color = color, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif,
                fontSize = geometry.rankSize.sp, lineHeight = (geometry.rankSize * 1.2f).sp), maxLines = 1)
            @Composable fun RankText(modifier: Modifier) {
                Canvas(modifier.height((geometry.rankSize * scale * 1.2f).dp)) {
                    // Only condense horizontally when a two-digit rank needs room;
                    // preserve the enlarged height and full rank instead of clipping.
                    scale(scaleX = minOf(1f, size.width / rank.size.width), scaleY = 1f, pivot = Offset.Zero) {
                        drawText(rank)
                    }
                }
            }
            val markSize = (geometry.rankSize * scale * .9f).dp
            Box(Modifier.fillMaxWidth().height(geometry.faceStep.dp).testTag("card-header-$column-$index")) {
                if (stacked) Column(Modifier.padding(start = 2.dp, top = 4.dp), horizontalAlignment = Alignment.Start) {
                    RankText(Modifier.width((geometry.cardWidth - 4f).dp))
                    SuitMark(item.card.suit, color, Modifier.size(markSize))
                } else Row(Modifier.fillMaxWidth().padding(start = 2.dp, end = 2.dp, top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.Top) {
                    RankText(Modifier.weight(1f))
                    SuitMark(item.card.suit, color, Modifier.size(markSize))
                }
            }
        } else Canvas(Modifier.fillMaxSize().padding(4.dp)) {
            drawRoundRect(Ivory.copy(alpha = .65f), style = Stroke(.7.dp.toPx()))
            var x = -size.height
            while (x < size.width) {
                drawLine(Ivory.copy(alpha = .12f), Offset(x, 0f), Offset(x + size.height, size.height), .6.dp.toPx()); x += 7.dp.toPx()
            }
        }
    }
}

internal fun rankLabel(rank: Rank): String = when (rank) {
    Rank.ACE -> "A"; Rank.JACK -> "J"; Rank.QUEEN -> "Q"; Rank.KING -> "K"; else -> rank.value.toString()
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
