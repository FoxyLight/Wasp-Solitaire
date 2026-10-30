package org.example.waspsolitaire

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Requires an Android emulator/device; CI runs JVM tests and assembles the app. */
@RunWith(AndroidJUnit4::class)
class SetupScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun displaysSetupOnlyLabel() {
        composeRule.onNodeWithText("Project setup only", substring = true).assertIsDisplayed()
    }
}
