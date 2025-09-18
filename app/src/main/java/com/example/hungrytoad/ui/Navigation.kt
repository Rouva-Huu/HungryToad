package com.example.hungrytoad.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hungrytoad.ui.registration.RegistrationScreen
import com.example.hungrytoad.ui.registration.ResultsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "registration"
    ) {
        composable("registration") {
            RegistrationScreen(navController = navController)
        }
        composable("results") {
            ResultsScreen(navController = navController)
        }
    }
}