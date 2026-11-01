package org.example.waspsolitaire

import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.espresso.Espresso.pressBack
import kotlinx.coroutines.runBlocking
import org.example.waspsolitaire.board.GameBoardHost
import org.example.waspsolitaire.engine.*
import org.example.waspsolitaire.game.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File

@OptIn(ExperimentalTestApi::class)
class GameplayTest {
    @get:Rule val rule = createComposeRule()
    private lateinit var model: GameViewModel
    private lateinit var repository: SessionRepository
    private fun card(rank: Rank, suit: Suit = Suit.HEARTS, up: Boolean = true) = TableauCard(Card(suit, rank), up)
    private fun fixture(): GameSession {
        val reserve = listOf(Card(Suit.CLUBS, Rank.ACE), Card(Suit.DIAMONDS, Rank.TWO), Card(Suit.SPADES, Rank.THREE))
        val source = listOf(card(Rank.NINE, Suit.DIAMONDS, false), card(Rank.FOUR), card(Rank.TWO, Suit.CLUBS), card(Rank.KING, Suit.SPADES))
        val target = listOf(card(Rank.FIVE))
        val used = (source + target).map { it.card } + reserve
        val rest = Suit.entries.flatMap { suit -> Rank.entries.map { Card(suit, it) } }.filterNot { it in used }.map { TableauCard(it) }
        return GameSession.restore(GameState.restore(listOf(source, target, emptyList(), emptyList(), emptyList(), emptyList(), rest), reserve, 41))
    }
    private fun prepare(session: GameSession = fixture(), failing: Boolean = false) {
        val base = InMemorySessionRepository(session)
        repository = if (!failing) base else object : SessionRepository {
            override suspend fun load() = base.load()
            override suspend fun commit(expectedRevision: Long, next: SessionRecord) = CommitResult.Failure("Injected UI test failure")
        }
        model = GameViewModel(runBlocking { GameController.open(repository) { 777L } })
    }
    private fun render() { rule.setContent { GameBoardHost(model) } }
    private fun click(tag: String) {
        if (rule.onAllNodesWithTag(tag).fetchSemanticsNodes().isEmpty()) rule.onNodeWithTag("more").performClick()
        rule.onNodeWithTag(tag).performClick(); rule.waitForIdle()
    }
    private fun tapCard(column: Int, index: Int) {
        rule.onNodeWithTag("card-$column-$index").performScrollTo().performTouchInput { click(Offset(width / 2f, 4f)) }
        rule.waitForIdle()
    }
    private fun destination(column: Int) {
        rule.onNodeWithTag("column-surface-$column").performTouchInput { click(Offset(width / 2f, 4f)) }
        rule.waitForIdle()
    }
    private fun revision(value: Long) { rule.waitUntil(10_000) { model.uiState.value.revision == value } }
    private fun dragTo(column: Int, index: Int, target: Int?, capture: Boolean = false) {
        val node = rule.onNodeWithTag("card-$column-$index")
        node.performScrollTo()
        val bounds = node.fetchSemanticsNode().boundsInRoot
        val targetBounds = if (target != null) rule.onNodeWithTag("column-$target").fetchSemanticsNode().boundsInRoot else null
        val delta = if (targetBounds != null) Offset(targetBounds.center.x - bounds.center.x, 0f)
                    else Offset(0f, 20f - bounds.top)
        node.performTouchInput {
            down(Offset(width / 2f, 4f))
            advanceEventTime(650)
            moveTo(Offset(width / 2f, 4f) + delta, delayMillis = 400)
        }
        rule.waitForIdle()
        rule.onNodeWithTag("drag-preview").assertExists()
        if (capture) snapshot("drag-whole-tail")
        node.performTouchInput { up() }
        rule.waitForIdle()
    }
    private fun snapshot(name: String) {
        // Native popup/ripple animations are not all driven by Compose's test clock.
        android.os.SystemClock.sleep(500)
        val directory = File(requireNotNull(InstrumentationRegistry.getArguments().getString("additionalTestOutputDir")), "phase4d-evidence")
        directory.mkdirs()
        File(directory, "$name.png").outputStream().use {
            val bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    @Test fun tapMovesUnorderedTailRevealsCardAndUndoRestoresExactState() {
        prepare(); render()
        val before = model.uiState.value.session
        tapCard(0, 1)
        assertEquals(CardSelection(0, 1), model.uiState.value.selection)
        assertTrue(1 in model.uiState.value.legalDestinations)
        assertTrue(2 in model.uiState.value.legalDestinations)
        destination(1); revision(1)
        assertEquals(listOf(card(Rank.FIVE)) + before.state.tableau[0].drop(1), model.uiState.value.board.tableau[1])
        assertTrue(model.uiState.value.board.tableau[0].single().faceUp)
        snapshot("tap-move-and-reveal")
        click("undo"); revision(2)
        assertEquals(before.state, model.uiState.value.board)
        assertTrue(model.uiState.value.session.history.isEmpty())
        rule.onNodeWithTag("undo").assertIsNotEnabled()
    }

    @Test fun dragMovesEntireUnorderedTailAndMatchesTapEngineResult() {
        prepare(); render()
        val before = model.uiState.value.session
        dragTo(0, 1, 1, capture = true); revision(1)
        val expected = (ClassicWasp.apply(before, GameAction.Move(0, 1, 1)) as Transition.Accepted).session
        assertEquals(expected.state, model.uiState.value.board)
        assertEquals(expected.history, model.uiState.value.session.history)
        rule.onNodeWithTag("drag-preview").assertDoesNotExist()
        click("undo"); revision(2)
        assertEquals(before.state, model.uiState.value.board)
    }

    @Test fun illegalAndIncompleteDropsLeaveBoardAndHistoryUnchanged() {
        prepare(); render()
        val before = model.uiState.value.session
        dragTo(0, 1, 0)
        assertSame(before, model.uiState.value.session)
        rule.onNodeWithTag("action-feedback").assertTextContains("Selection cleared", substring = true)
        dragTo(0, 1, null)
        assertSame(before, model.uiState.value.session)
        assertEquals(0L, model.uiState.value.revision)
        rule.onNodeWithTag("action-feedback").assertTextContains("Drop cancelled", substring = true)
    }

    @Test fun illegalTapClearsSelectionWithoutSelectingTheTappedCard() {
        prepare(); render()
        val before = model.uiState.value.session
        tapCard(0, 1)
        tapCard(0, 2)
        assertNull(model.uiState.value.selection)
        tapCard(0, 2)
        assertEquals(CardSelection(0, 2), model.uiState.value.selection)
        pressBack(); rule.waitForIdle()
        assertNull(model.uiState.value.selection)
        tapCard(0, 1); tapCard(6, 0)
        assertNull(model.uiState.value.selection)
        assertSame(before, model.uiState.value.session)
        assertEquals(0L, model.uiState.value.revision)
    }

    @Test fun emptyColumnAcceptsTailByTapAndDragWithRepeatedUndo() {
        prepare(); render()
        val before = model.uiState.value.session
        tapCard(0, 1); destination(2); revision(1)
        assertEquals(before.state.tableau[0].drop(1), model.uiState.value.board.tableau[2])
        click("undo"); revision(2)
        dragTo(0, 1, 2); revision(3)
        assertEquals(before.state.tableau[0].drop(1), model.uiState.value.board.tableau[2])
        click("undo"); revision(4)
        assertEquals(before.state, model.uiState.value.board)
    }

    @Test fun reserveWorksWhileTableauMovesExistAndUndoRestoresReserve() {
        prepare(); render()
        val before = model.uiState.value.session
        assertTrue(ClassicWasp.legalMoves(before.state).isNotEmpty())
        rule.onNodeWithTag("hint").assertDoesNotExist()
        assertSame(before, model.uiState.value.session)
        click("deal"); revision(1)
        assertTrue(model.uiState.value.board.reserve.isEmpty())
        rule.onNodeWithTag("deal").assertIsNotEnabled()
        click("undo"); revision(2)
        assertEquals(before.state, model.uiState.value.board)
        rule.onNodeWithTag("deal").assertIsEnabled()
    }

    @Test fun restartAndNewGameRequireExplicitConfirmationAndCancellationPreservesPlay() {
        prepare(); render()
        val initial = model.uiState.value.session
        click("deal"); revision(1)
        val played = model.uiState.value.session
        click("restart")
        rule.onNodeWithTag("cancel-reset").performClick()
        assertSame(played, model.uiState.value.session)
        click("restart")
        snapshot("restart-confirmation")
        click("confirm-reset"); revision(2)
        assertEquals(initial.state, model.uiState.value.board)
        assertTrue(model.uiState.value.session.history.isEmpty())
        click("new-game"); click("cancel-reset")
        assertEquals(initial.state, model.uiState.value.board)
        click("new-game"); click("confirm-reset"); revision(3)
        assertEquals(ClassicWasp.newGame(777).state, model.uiState.value.board)
        assertTrue(model.uiState.value.session.history.isEmpty())
        assertNull(model.uiState.value.selection)
    }

    @Test fun victoryKeepsAllRunsAndUndoRemainsAvailable() {
        val columns = Suit.entries.map { suit -> Rank.entries.reversed().map { TableauCard(Card(suit, it)) }.toMutableList() }.toMutableList()
        val ace = columns[3].removeAt(12)
        columns.add(mutableListOf(ace)); columns.add(mutableListOf()); columns.add(mutableListOf())
        prepare(GameSession.restore(GameState.restore(columns, emptyList(), 91))); render()
        tapCard(4, 0); destination(3); revision(1)
        assertEquals(GameStatus.WON, model.uiState.value.board.status)
        assertEquals(52, model.uiState.value.board.tableau.flatten().size)
        assertEquals(4, model.uiState.value.board.completedRuns.size)
        rule.onNodeWithTag("victory").assertIsDisplayed()
        snapshot("victory-runs-retained")
        click("undo"); revision(2)
        assertEquals(GameStatus.IN_PROGRESS, model.uiState.value.board.status)
    }

    @Test fun failedWriteDisplaysErrorWithoutMovingCardsOrAddingHistory() {
        prepare(failing = true); render()
        val before = model.uiState.value.session
        tapCard(0, 1); destination(1)
        rule.onNodeWithTag("action-feedback").assertTextContains("unchanged", substring = true)
        assertSame(before, model.uiState.value.session)
        assertTrue(model.uiState.value.session.history.isEmpty())
    }

    private fun noMoves(withReserve: Boolean): GameSession {
        val reserve = if (withReserve) Suit.entries.take(3).map { Card(it, Rank.KING) } else emptyList()
        val tips = Suit.entries.map { Card(it, Rank.ACE) } + Suit.entries.take(3).map { Card(it, if (withReserve) Rank.QUEEN else Rank.KING) }
        val buried = Suit.entries.flatMap { suit -> Rank.entries.map { Card(suit, it) } }
            .filterNot { it in reserve || it in tips }.map { TableauCard(it, false) }
        val columns = tips.mapIndexed { index, tip -> (if (index == 0) buried else emptyList()) + TableauCard(tip) }
        return GameSession.restore(GameState.restore(columns, reserve, 51))
    }

    @Test fun reserveRemainsExplicitWithNoTableauMovesAndNoHintFeature() {
        prepare(noMoves(true)); render()
        val before = model.uiState.value.session
        rule.onNodeWithTag("hint").assertDoesNotExist()
        assertTrue(ClassicWasp.legalMoves(before.state).isEmpty())
        click("deal"); revision(1)
        click("undo"); revision(2)
        assertEquals(before.state, model.uiState.value.board)
    }

    @Test fun noMovesDoesNotDeclareLossOrDisableSecondaryActions() {
        prepare(noMoves(false)); render()
        val before = model.uiState.value.session
        assertEquals(GameStatus.IN_PROGRESS, model.uiState.value.board.status)
        assertSame(before, model.uiState.value.session)
        rule.onNodeWithTag("hint").assertDoesNotExist()
        click("more")
        rule.onNodeWithTag("restart").assertIsEnabled()
        rule.onNodeWithTag("new-game").assertIsEnabled()
    }

    @Test fun interactiveControlsAndMovesWorkAcrossSizesAndLargeText() {
        prepare(ClassicWasp.newGame(123))
        var size by mutableStateOf(DpSize(320.dp, 640.dp))
        var fontScale by mutableFloatStateOf(1f)
        rule.setContent {
            DeviceConfigurationOverride(DeviceConfigurationOverride.ForcedSize(size) then DeviceConfigurationOverride.FontScale(fontScale)) {
                GameBoardHost(model)
            }
        }
        val cases = listOf(Triple("narrow", DpSize(320.dp,640.dp),1f), Triple("portrait",DpSize(390.dp,780.dp),1f),
            Triple("landscape",DpSize(640.dp,320.dp),1f), Triple("tablet",DpSize(800.dp,1000.dp),1f), Triple("large-text",DpSize(320.dp,640.dp),2f))
        cases.forEach { (name, viewport, scale) ->
            rule.runOnIdle { size = viewport; fontScale = scale }; rule.waitForIdle()
            val before = model.uiState.value.session
            snapshot("controls-$name")
            for (tag in listOf("undo","deal")) {
                try { rule.onNodeWithTag(tag).assertIsDisplayed().assertHeightIsAtLeast(48.dp) }
                catch (error: AssertionError) { throw AssertionError("$name / $tag: ${error.message}", error) }
            }
            val compact = rule.onAllNodesWithTag("more").fetchSemanticsNodes().isNotEmpty()
            if (compact) click("more")
            for (tag in listOf("restart","new-game")) rule.onNodeWithTag(tag).assertIsDisplayed().assertHeightIsAtLeast(48.dp)
            if (compact) { pressBack(); rule.waitForIdle() }
            click("deal"); revision(model.uiState.value.revision)
            rule.waitUntil(10_000) { model.uiState.value.board.reserve.isEmpty() }
            click("undo"); rule.waitUntil(10_000) { model.uiState.value.board.reserve.size == 3 }
            val move = ClassicWasp.legalMoves(model.uiState.value.board).first()
            tapCard(move.source, move.cardIndex); destination(move.destination)
            rule.waitUntil(10_000) { model.uiState.value.board.actionCount == 1L }
            click("undo"); rule.waitUntil(10_000) { model.uiState.value.board.actionCount == 0L }
            assertEquals(before.state, model.uiState.value.board)
            snapshot("interactive-$name")
        }
    }
}
