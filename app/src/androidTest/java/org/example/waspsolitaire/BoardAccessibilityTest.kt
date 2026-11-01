package org.example.waspsolitaire

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import kotlinx.coroutines.runBlocking
import org.example.waspsolitaire.board.GameBoardHost
import org.example.waspsolitaire.engine.*
import org.example.waspsolitaire.game.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class BoardAccessibilityTest {
    @get:Rule val rule = createComposeRule()
    private lateinit var model: GameViewModel
    private fun action(column: Int, label: String) {
        val actions = rule.onNodeWithTag("column-$column").fetchSemanticsNode().config[SemanticsActions.CustomActions]
        val selected = actions.single { it.label.startsWith(label) }
        rule.runOnIdle { assertTrue(selected.action()) }
        rule.waitForIdle()
    }
    private fun open(seed: Long = 123) {
        model = GameViewModel(runBlocking { GameController.open(InMemorySessionRepository(seed)) })
        rule.setContent { GameBoardHost(model) }
    }

    @Test fun everyLegalInitialMoveHasAnExplicitNonDragBoardAction() {
        open()
        val before = model.uiState.value.session
        for (move in ClassicWasp.legalMoves(before.state)) {
            val card = before.state.tableau[move.source][move.cardIndex].card
            action(move.source, "Select ${card.rank.name.lowercase()} of ${card.suit.name.lowercase()} and")
            assertEquals(CardSelection(move.source, move.cardIndex), model.uiState.value.selection)
            action(move.destination, "Move ")
            val expected = (ClassicWasp.apply(before, move) as Transition.Accepted).session
            assertEquals(expected.state, model.uiState.value.board)
            assertEquals(expected.history, model.uiState.value.session.history)
            rule.onNodeWithTag("undo").performClick(); rule.waitForIdle()
            assertEquals(before.state, model.uiState.value.board)
            assertTrue(model.uiState.value.session.history.isEmpty())
        }
    }

    @Test fun selectedStartAttachedCountHiddenCardsAndCancellationHaveDistinctSemantics() {
        open()
        val before = model.uiState.value.session
        action(3, "Select eight of diamonds and 2 attached")
        rule.onNodeWithTag("card-3-4").assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Selected start, 3-card tail, 2 attached cards"))
        rule.onNodeWithTag("card-3-5").assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Attached to selected tail"))
        rule.onNodeWithTag("card-3-0").assertContentDescriptionEquals("Face-down card, column 4, card 1")
        action(3, "Cancel selection")
        assertNull(model.uiState.value.selection)
        assertSame(before, model.uiState.value.session)
        rule.onNodeWithTag("hint").assertDoesNotExist()
        rule.onNodeWithText("Inspect", substring = true).assertDoesNotExist()
        rule.onNodeWithText("WASP").assertDoesNotExist()
        rule.onNodeWithTag("progress").assertDoesNotExist()
    }

    @Test fun keyboardCanChooseAnAttachedTailAndExplicitDestinationWithoutDragging() {
        open()
        val before = model.uiState.value.session
        rule.onNodeWithTag("column-3").performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        rule.onNodeWithTag("column-3").performKeyInput { pressKey(Key.DirectionDown); pressKey(Key.Enter) }
        rule.waitForIdle()
        assertEquals(CardSelection(3, 4), model.uiState.value.selection)
        rule.onNodeWithTag("column-6").performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        rule.onNodeWithTag("column-6").performKeyInput { pressKey(Key.Enter) }
        rule.waitForIdle()
        val expected = (ClassicWasp.apply(before, GameAction.Move(3, 4, 6)) as Transition.Accepted).session
        assertEquals(expected.state, model.uiState.value.board)
        rule.onNodeWithTag("undo").performClick(); rule.waitForIdle()
        assertEquals(before.state, model.uiState.value.board)
    }
}
