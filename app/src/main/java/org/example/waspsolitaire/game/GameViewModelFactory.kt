package org.example.waspsolitaire.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.security.SecureRandom

/** One seeded initial deal per process owner. Tests inject a fixed seed supplier. */
class GameSessionOwner(private val seed: () -> Long = { SecureRandom().nextLong() }) {
    val repository: SessionRepository by lazy { InMemorySessionRepository(seed()) }
}

class GameViewModelFactory private constructor(private val controller: GameController) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == GameViewModel::class.java)
        @Suppress("UNCHECKED_CAST")
        return GameViewModel(controller) as T
    }

    companion object {
        suspend fun open(repository: SessionRepository): GameViewModelFactory =
            GameViewModelFactory(GameController.open(repository))
    }
}
