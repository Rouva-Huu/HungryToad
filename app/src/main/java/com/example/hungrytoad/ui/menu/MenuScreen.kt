package com.example.hungrytoad.ui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.hungrytoad.model.Player
import com.example.hungrytoad.ui.theme.*
import com.example.hungrytoad.utils.SettingsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    selectedMenuItem: String,
    onReturnToGame: () -> Unit,
    settingsManager: SettingsManager
) {
    var playerData by remember { mutableStateOf<Player?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Меню", style = MaterialTheme.typography.headlineLarge) },
                navigationIcon = {
                    IconButton(onClick = onReturnToGame) {
                        Icon(painter = painterResource(com.example.hungrytoad.R.drawable.ic_arrow_back),
                            contentDescription = "Назад к игре",
                            Modifier.size(24.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PaleGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedMenuItem) {
                "account" -> {
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
                "settings" -> SettingsScreen(settingsManager = settingsManager)
                "rules" -> RulesScreen()
                "authors" -> AuthorsScreen()
            }
        }
    }
}