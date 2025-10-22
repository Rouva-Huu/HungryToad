package com.example.hungrytoad.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.hungrytoad.model.GameSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {
    companion object {
        private val GAME_SPEED = floatPreferencesKey("game_speed")
        private val BONUS_INTERVAL = floatPreferencesKey("bonus_interval")
        private val ROUND_DURATION = floatPreferencesKey("round_duration")
        private val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        private val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
    }

    suspend fun saveSettings(settings: GameSettings) {
        context.dataStore.edit { preferences ->
            preferences[GAME_SPEED] = settings.gameSpeed
            preferences[BONUS_INTERVAL] = settings.bonusInterval
            preferences[ROUND_DURATION] = settings.roundDuration
            preferences[SOUND_ENABLED] = settings.soundEnabled
            preferences[VIBRATION_ENABLED] = settings.vibrationEnabled
        }
    }

    val settingsFlow: Flow<GameSettings> = context.dataStore.data.map { preferences ->
        GameSettings(
            gameSpeed = preferences[GAME_SPEED] ?: 5f,
            bonusInterval = preferences[BONUS_INTERVAL] ?: 3f,
            roundDuration = preferences[ROUND_DURATION] ?: 60f,
            soundEnabled = preferences[SOUND_ENABLED] ?: true,
            vibrationEnabled = preferences[VIBRATION_ENABLED] ?: true
        )
    }
}