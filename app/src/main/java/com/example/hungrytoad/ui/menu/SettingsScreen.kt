package com.example.hungrytoad.ui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hungrytoad.model.GameSettings
import com.example.hungrytoad.ui.theme.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.example.hungrytoad.utils.SettingsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsManager: SettingsManager,
    onSettingsChanged: (GameSettings) -> Unit = {}
) {
    var gameSettings by remember { mutableStateOf(GameSettings()) }

    // Загрузка настроек при открытии экрана
    LaunchedEffect(Unit) {
        settingsManager.settingsFlow.collect { settings ->
            gameSettings = settings
        }
    }

    // Сохранение настроек при изменении
    LaunchedEffect(gameSettings) {
        if (gameSettings != GameSettings()) {
            settingsManager.saveSettings(gameSettings)
            onSettingsChanged(gameSettings)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Настройки игры",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp)
            )

            SettingItem(
                title = "Скорость игры",
                value = gameSettings.gameSpeed,
                valueRange = 1f..10f,
                onValueChange = {
                    gameSettings = gameSettings.copy(gameSpeed = it)
                },
                valueFormatter = { "${it.toInt()}/10" }
            )

            SettingItem(
                title = "Интервал бонусов",
                value = gameSettings.bonusInterval,
                valueRange = 1f..10f,
                onValueChange = {
                    gameSettings = gameSettings.copy(bonusInterval = it)
                },
                valueFormatter = { "${it.toInt()} сек" }
            )

            SettingItem(
                title = "Длительность раунда",
                value = gameSettings.roundDuration,
                valueRange = 30f..180f,
                onValueChange = {
                    gameSettings = gameSettings.copy(roundDuration = it)
                },
                valueFormatter = { "${it.toInt()} сек" }
            )

            Text(
                "Дополнительные настройки",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp)
            )

            SwitchSettingItem(
                title = "Звуковые эффекты",
                checked = gameSettings.soundEnabled,
                onCheckedChange = {
                    gameSettings = gameSettings.copy(soundEnabled = it)
                }
            )

            SwitchSettingItem(
                title = "Вибрация",
                checked = gameSettings.vibrationEnabled,
                onCheckedChange = {
                    gameSettings = gameSettings.copy(vibrationEnabled = it)
                }
            )

            // Кнопка сброса настроек
            Button(
                onClick = {
                    gameSettings = GameSettings()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Marsh
                )
            ) {
                Text(
                    "Сбросить настройки",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    valueFormatter: (Float) -> String = { it.toString() }
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = LightNude)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = valueFormatter(value),
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGreen
                )
            }

            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = PaleGreen,
                    activeTrackColor = PaleGreen,
                    inactiveTrackColor = Marsh
                )
            )
        }
    }
}

@Composable
fun SwitchSettingItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = LightNude)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = DarkGreen,
                    checkedTrackColor = Marsh,
                    uncheckedThumbColor = Marsh,
                    uncheckedTrackColor = LightNude
                )
            )
        }
    }
}