package org.example.waspsolitaire.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Android lifecycle owner for the Phase 4A controller. Later Compose screens
 * will obtain this through a ViewModelProvider factory after opening the repository.
 * Retaining the ViewModel across rotation retains this in-memory controller,
 * but killing the process still loses all state until Phase 5A Room persistence.
 */
class GameViewModel(private val controller: GameController) : ViewModel() {
    val uiState: StateFlow<GameUiState> = controller.uiState

    private val mutableLastResult = MutableStateFlow<IntentResult?>(null)
    val lastResult: StateFlow<IntentResult?> = mutableLastResult.asStateFlow()

    /** Deterministic entry point for callers that already have a coroutine scope. */
    suspend fun dispatch(intent: GameIntent): IntentResult {
        val result = controller.dispatch(intent)
        mutableLastResult.value = result
        return result
    }

    /** Nonblocking entry point for future Compose UI event handlers. */
    fun submit(intent: GameIntent): Job = viewModelScope.launch {
        dispatch(intent)
    }
}
