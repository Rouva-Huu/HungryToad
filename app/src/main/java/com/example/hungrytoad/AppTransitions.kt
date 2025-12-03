package com.example.hungrytoad

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import com.example.hungrytoad.ui.game.GameScreen
import com.example.hungrytoad.ui.home.HomeScreen
import com.example.hungrytoad.ui.menu.MenuScreen
import com.example.hungrytoad.utils.SettingsManager

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppTransitions(settingsManager: SettingsManager) {
    var currentScreen by remember { mutableStateOf("start") }
    var selectedMenuItem by remember { mutableStateOf("") }
    val appStateManager = remember { AppStateManager() }
    val currentPlayer by appStateManager.currentPlayer.collectAsState()

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) with
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        }
    ) { screen ->
        when (screen) {
            "start" -> HomeScreen(
                onHomeGame = { currentScreen = "game" }
            )
            "game" -> GameScreen(
                onNavigateToMenu = { menuItem ->
                    selectedMenuItem = menuItem
                    currentScreen = "menu"
                },
                onExitGame = { currentScreen = "start" },
                settingsManager = settingsManager,
                currentPlayer = currentPlayer,
                appStateManager = appStateManager
            )
            "menu" -> MenuScreen(
                selectedMenuItem = selectedMenuItem,
                onReturnToGame = { currentScreen = "game" },
                settingsManager = settingsManager,
                appStateManager = appStateManager
            )
        }
    }
}

