package org.example.waspsolitaire

import android.graphics.Bitmap
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.example.waspsolitaire.board.GameBoard
import org.example.waspsolitaire.engine.*
import org.example.waspsolitaire.game.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File

@OptIn(ExperimentalTestApi::class)
class BoardRenderingTest {
    @get:Rule val rule = createComposeRule()

    private fun render(state: GameUiState) {
        rule.setContent { MaterialTheme { GameBoard(state) } }
    }
    private fun snapshot(name: String) {
        val output = requireNotNull(InstrumentationRegistry.getArguments().getString("additionalTestOutputDir")) {
            "Run through Gradle connectedDebugAndroidTest to collect screenshot artifacts"
        }
        val directory = File(output, "board-evidence")
        directory.mkdirs()
        File(directory, "$name.png").outputStream().use {
            // Native viewport capture avoids waiting for a PixelCopy draw on an
            // otherwise idle API-35 emulator. Behavior assertions stay independent.
            InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot().compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    @Test fun seededDealRendersSevenOrderedColumnsWithoutRevealingHiddenIdentities() {
        val state = GameUiState(ClassicWasp.newGame(123), 0)
        render(state)
        var previous = Float.NEGATIVE_INFINITY
        state.board.tableau.forEachIndexed { column, cards ->
            val bounds = rule.onNodeWithTag("column-$column").fetchSemanticsNode().boundsInRoot
            assertTrue(bounds.left > previous)
            previous = bounds.left
            cards.forEachIndexed { index, item ->
                val card = rule.onNodeWithTag("card-$column-$index")
                if (item.faceUp) {
                    card.assertContentDescriptionEquals("${item.card.rank.name.lowercase()} of ${item.card.suit.name.lowercase()}, column ${column + 1}, card ${index + 1}")
                } else {
                    card.assertContentDescriptionEquals("Face-down card, column ${column + 1}, card ${index + 1}")
                    rule.onAllNodes(hasAnyAncestor(hasTestTag("card-$column-$index")) and hasText("", substring = true), useUnmergedTree = true).assertCountEquals(0)
                }
            }
        }
        rule.onAllNodes(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Face down")).assertCountEquals(12)
        rule.onAllNodes(SemanticsMatcher("Face up and attached count") { it.config.getOrElse(SemanticsProperties.StateDescription) { "" }.startsWith("Face up,") }).assertCountEquals(37)
        rule.onNodeWithTag("reserve-status").assertTextEquals("3 reserve")
        repeat(3) { rule.onNodeWithTag("reserve-card-$it", useUnmergedTree = true).assertExists() }
        assertTrue(state.session.history.isEmpty())
    }

    @Test fun suppliedControllerSelectionAndLegalTargetsRenderWithoutCommitting() = runBlocking<Unit> {
        val repository = InMemorySessionRepository(123)
        val controller = GameController.open(repository)
        val original = controller.uiState.value.session
        val move = ClassicWasp.legalMoves(original.state).first()
        controller.dispatch(GameIntent.Select(move.source, move.cardIndex))
        val state = controller.uiState.value
        render(state)
        for (index in move.cardIndex..state.board.tableau[move.source].lastIndex) {
            rule.onNodeWithTag("card-${move.source}-$index").assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
        }
        state.legalDestinations.forEach {
            rule.onNodeWithTag("column-surface-$it").assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Legal destination"))
        }
        rule.onNodeWithTag("hint").assertDoesNotExist()
        rule.onAllNodes(hasClickAction()).assertCountEquals(0)
        assertSame(original, repository.load().session)
        assertSame(original, controller.uiState.value.session)
        snapshot("selection-and-targets")
    }

    @Test fun fourCompletedRunsStayOnTableauAndDealtReserveStaysEmpty() {
        val columns = Suit.entries.map { suit -> Rank.entries.reversed().map { TableauCard(Card(suit, it)) } } + List(3) { emptyList() }
        val session = GameSession.restore(GameState.restore(columns, emptyList(), 3))
        render(GameUiState(session, 0))
        repeat(4) { column ->
            repeat(13) { rule.onNodeWithTag("card-$column-$it").assertExists() }
        }
        rule.onNodeWithTag("progress").assertDoesNotExist()
        rule.onNodeWithTag("reserve-status").assertTextEquals("Reserve dealt")
        assertEquals(52, session.state.tableau.flatten().size)
        snapshot("completed-runs")
    }

    @Test(timeout = 120_000) fun viewportAndFontChangesKeepSevenColumnsAndReserveReachableWithoutChangingSession() {
        val session = ClassicWasp.newGame(123)
        var viewport by mutableStateOf(DpSize(320.dp, 640.dp))
        var fontScale by mutableFloatStateOf(1f)
        rule.setContent {
            DeviceConfigurationOverride(DeviceConfigurationOverride.ForcedSize(viewport) then DeviceConfigurationOverride.FontScale(fontScale)) {
                MaterialTheme { GameBoard(GameUiState(session, 0)) }
            }
        }
        val configurations = listOf(
            Triple("narrow-320", DpSize(320.dp,640.dp),1f),
            Triple("portrait-390", DpSize(390.dp,780.dp),1f),
            Triple("landscape-640", DpSize(640.dp,320.dp),1f),
            Triple("tablet-800", DpSize(800.dp,1000.dp),1f),
            Triple("large-text-320", DpSize(320.dp,640.dp),2f),
        )
        configurations.forEach { (name, size, scale) ->
            rule.runOnIdle { viewport = size; fontScale = scale }
            rule.waitForIdle()
            rule.onNodeWithTag("reserve-status", useUnmergedTree = true).assertIsDisplayed()
            snapshot("$name-top")
            android.util.Log.i("WaspLayoutTest", "$name: top viewport captured")
            val root = rule.onNodeWithTag("board").fetchSemanticsNode().boundsInRoot
            var previous = Float.NEGATIVE_INFINITY
            repeat(7) { column ->
                val bounds = rule.onNodeWithTag("column-$column").fetchSemanticsNode().boundsInRoot
                assertTrue("$name column $column stays in width", bounds.left >= root.left && bounds.right <= root.right + 1)
                assertTrue(bounds.left > previous)
                previous = bounds.left
                // Width-driven readable cards may be taller than a heavily inset
                // short window. The exposed rank/suit header is the touch target;
                // asking ScrollTo to fit an oversized full card can never finish.
                android.util.Log.i("WaspLayoutTest", "$name: revealing column $column header")
                rule.onNodeWithTag("card-header-$column-6", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
            }
            rule.onNodeWithTag("reserve-status", useUnmergedTree = true).assertIsDisplayed()
            snapshot("$name-reserve")
            assertEquals(ClassicWasp.newGame(123).state, session.state)
            assertTrue(session.history.isEmpty())
        }
    }
}
