package com.example.hungrytoad

import com.example.hungrytoad.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppStateManager {
    private val _currentPlayer = MutableStateFlow<Player?>(null)
    val currentPlayer: StateFlow<Player?> = _currentPlayer.asStateFlow()

    fun setCurrentPlayer(player: Player?) {
        _currentPlayer.value = player
    }

    fun updateBestScore(newScore: Int) {
        _currentPlayer.value?.let { currentPlayer ->
            if (newScore > currentPlayer.bestScore) {
                _currentPlayer.value = currentPlayer.copy(bestScore = newScore)
            }
        }
    }
}