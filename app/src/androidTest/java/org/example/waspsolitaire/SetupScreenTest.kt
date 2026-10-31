package org.example.waspsolitaire

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.example.waspsolitaire.game.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetupScreenTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Test fun entryRendersBoardAndRecreationRetainsViewModelAndSession() {
        rule.waitUntil(10_000) { rule.onAllNodesWithTag("board").fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithText("WASP").assertIsDisplayed()
        lateinit var model: GameViewModel
        rule.activityRule.scenario.onActivity { model = ViewModelProvider(it)[GameViewModel::class.java] }
        runBlocking { model.dispatch(GameIntent.DealReserve); model.dispatch(GameIntent.Select(0, 7)) }
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
        rule.onNodeWithTag("reserve-status").performScrollTo().assertTextEquals("Dealt · 0 remaining")
        rule.onAllNodes(hasClickAction()).assertCountEquals(0)
    }
}
