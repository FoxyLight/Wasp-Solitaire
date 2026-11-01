package org.example.waspsolitaire

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.example.waspsolitaire.game.*
import org.example.waspsolitaire.engine.ClassicWasp
import androidx.compose.ui.geometry.Offset
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetupScreenTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Test fun entryRendersBoardAndRecreationRetainsViewModelAndSession() {
        rule.waitUntil(10_000) { rule.onAllNodesWithTag("board").fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithTag("more").assertIsDisplayed()
        lateinit var model: GameViewModel
        rule.activityRule.scenario.onActivity { model = ViewModelProvider(it)[GameViewModel::class.java] }
        // Install a deterministic fixture through the real repository contract, then use UI controls.
        val repository = (rule.activity.application as WaspApplication).sessions.repository
        runBlocking {
            val current = repository.load()
            repository.commit(current.revision, SessionRecord(current.revision + 1, ClassicWasp.newGame(123)))
            model.dispatch(GameIntent.Refresh)
        }
        val move = ClassicWasp.legalMoves(model.uiState.value.board).first()
        val startingRevision = model.uiState.value.revision
        rule.onNodeWithTag("card-${move.source}-${move.cardIndex}").performScrollTo().performTouchInput { click(Offset(width / 2f, 4f)) }
        rule.waitUntil(10_000) { model.uiState.value.selection != null }
        rule.onNodeWithTag("column-surface-${move.destination}").performTouchInput { click(Offset(width / 2f, 4f)) }
        rule.waitUntil(10_000) { model.uiState.value.revision == startingRevision + 1 }
        rule.onNodeWithTag("deal").performClick()
        rule.waitUntil(10_000) { model.uiState.value.board.reserve.isEmpty() }
        runBlocking { model.dispatch(GameIntent.Select(0, model.uiState.value.board.tableau[0].lastIndex)) }
        val before = model.uiState.value
        rule.activityRule.scenario.recreate()
        rule.waitUntil(10_000) { rule.onAllNodesWithTag("board").fetchSemanticsNodes().isNotEmpty() }
        rule.activityRule.scenario.onActivity {
            assertSame(model, ViewModelProvider(it)[GameViewModel::class.java])
        }
        assertSame(before.session, model.uiState.value.session)
        assertEquals(before.selection, model.uiState.value.selection)
        rule.activityRule.scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE }
        rule.waitUntil(10_000) {
            var landscape = false
            rule.activityRule.scenario.onActivity { landscape = it.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE }
            landscape
        }
        rule.waitUntil(10_000) { rule.onAllNodesWithTag("board").fetchSemanticsNodes().isNotEmpty() }
        rule.activityRule.scenario.onActivity { assertSame(model, ViewModelProvider(it)[GameViewModel::class.java]) }
        assertSame(before.session, model.uiState.value.session)
        assertEquals(before.selection, model.uiState.value.selection)
        rule.onNodeWithTag("reserve-status").assertTextEquals("Reserve dealt")
        rule.onNodeWithTag("undo").assertIsDisplayed().assertIsEnabled()
        rule.onNodeWithTag("board").performTouchInput { swipeDown() }
        rule.waitForIdle()
        val output = java.io.File(requireNotNull(androidx.test.platform.app.InstrumentationRegistry.getArguments().getString("additionalTestOutputDir")), "phase4d-evidence")
        output.mkdirs()
        java.io.File(output, "physical-landscape.png").outputStream().use {
            androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
                .compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}
