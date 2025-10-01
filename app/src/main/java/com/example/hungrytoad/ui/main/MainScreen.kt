package com.example.hungrytoad.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.hungrytoad.model.Player
import com.example.hungrytoad.ui.rules.RulesScreen
import com.example.hungrytoad.ui.authors.AuthorsScreen
import com.example.hungrytoad.ui.player.ResultsScreen
import com.example.hungrytoad.ui.registration.RegistrationScreen
import com.example.hungrytoad.ui.settings.SettingsScreen
import com.example.hungrytoad.ui.theme.*

@Composable
fun MainScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var playerData by remember { mutableStateOf<Player?>(null) }
    val tabs = listOf("Аккаунт", "Настройки", "Правила", "Авторы")

    Scaffold(
        topBar = {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = PaleGreen,
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(top = 58.dp, bottom = 10.dp)
                            )
                        },
                        selectedContentColor = Color.White,
                        unselectedContentColor = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTabIndex) {
                0 -> {
                    if (playerData != null) {
                        ResultsScreen(
                            playerData = playerData!!,
                            onReturnToRegistration = { playerData = null }
                        )
                    } else {
                        RegistrationScreen(
                            onRegistrationComplete = { player -> playerData = player }
                        )
                    }
                }
                1 -> SettingsScreen()
                2 -> RulesScreen()
                3 -> AuthorsScreen()
            }
        }
    }
}