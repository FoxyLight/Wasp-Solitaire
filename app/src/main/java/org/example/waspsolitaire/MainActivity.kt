package org.example.waspsolitaire

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.example.waspsolitaire.board.GameBoardHost
import org.example.waspsolitaire.game.GameSessionOwner
import org.example.waspsolitaire.game.GameViewModel
import org.example.waspsolitaire.game.GameViewModelFactory

/** Volatile process-owned repository, without process-death recovery. */
class WaspApplication : Application() {
    val sessions = GameSessionOwner()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            val repository = (application as WaspApplication).sessions.repository
            val factory = GameViewModelFactory.open(repository)
            val model = ViewModelProvider(this@MainActivity, factory)[GameViewModel::class.java]
            setContent { GameBoardHost(model) }
        }
    }
}