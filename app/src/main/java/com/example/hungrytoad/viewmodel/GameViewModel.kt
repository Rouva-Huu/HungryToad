package com.example.hungrytoad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hungrytoad.model.GameSettings
import com.example.hungrytoad.utils.SettingsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameViewModel(private val settingsManager: SettingsManager) : ViewModel() {
    private val _timeLeft = MutableStateFlow(60)
    private val _score = MutableStateFlow(0)
    private val _isPaused = MutableStateFlow(false)
    private val _gameSettings = MutableStateFlow(GameSettings())
    private val _gameOver = MutableStateFlow(false)

    val timeLeft: StateFlow<Int> = _timeLeft.asStateFlow()
    val score: StateFlow<Int> = _score.asStateFlow()
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()
    val gameSettings: StateFlow<GameSettings> = _gameSettings.asStateFlow()
    val gameOver: StateFlow<Boolean> = _gameOver.asStateFlow()

    init {
        startTimer()
        loadSettings()
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                if (!_isPaused.value && !_gameOver.value) {
                    delay(1000L)
                    if (_timeLeft.value > 0) {
                        _timeLeft.value = _timeLeft.value - 1
                    } else {
                        _gameOver.value = true
                        _isPaused.value = true
                    }
                } else {
                    delay(100L)
                }
            }
        }
    }
    private fun loadSettings() {
        viewModelScope.launch {
            settingsManager.settingsFlow.collect { settings ->
                _gameSettings.value = settings
                _timeLeft.value = settings.roundDuration.toInt()
            }
        }
    }

    fun togglePause() {
        if (!_gameOver.value) {
            _isPaused.value = !_isPaused.value
        }
    }
    fun setPaused(paused: Boolean) {
        if (!_gameOver.value) {
            _isPaused.value = paused
        }
    }

    fun updateScore(points: Int) {
        if (!_gameOver.value) {
            _score.value = _score.value + points
        }
    }

    fun resetGame() {
        viewModelScope.launch {
            settingsManager.settingsFlow.collect { settings ->
                _timeLeft.value = settings.roundDuration.toInt()
                _score.value = 0
                _isPaused.value = false
                _gameOver.value = false
                return@collect
            }
        }
    }
}

class GameViewModelFactory(private val settingsManager: SettingsManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(settingsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}