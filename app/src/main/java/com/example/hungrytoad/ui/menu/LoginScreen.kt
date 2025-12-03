package com.example.hungrytoad.ui.menu

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.hungrytoad.model.Player
import com.example.hungrytoad.ui.data.PlayerRepository
import com.example.hungrytoad.ui.theme.*
import com.example.hungrytoad.utils.DifficultySettings
import com.example.hungrytoad.utils.SettingsManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (Player) -> Unit,
    onNavigateToRegistration: () -> Unit,
    settingsManager: SettingsManager
) {
    var fullName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Вход в аккаунт",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(top = 20.dp, start = 10.dp)
            )

            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    errorMessage = null
                },
                label = { Text("ФИО", style = MaterialTheme.typography.labelMedium) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedTextColor = PaleGreen,
                    unfocusedLabelColor = PaleGreen,
                    unfocusedContainerColor = Nude,
                    focusedContainerColor = Nude
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                label = { Text("Пароль", style = MaterialTheme.typography.labelMedium) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    unfocusedTextColor = PaleGreen,
                    unfocusedLabelColor = PaleGreen,
                    unfocusedContainerColor = Nude,
                    focusedContainerColor = Nude
                )
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    if (fullName.isBlank() || password.isBlank()) {
                        errorMessage = "Заполните все поля"
                        return@Button
                    }

                    isLoading = true
                    errorMessage = null

                    scope.launch {
                        try {
                            val repository = PlayerRepository()
                            val player = repository.loginPlayer(fullName, password)

                            if (player != null) {
                                val speed = DifficultySettings.getSpeedFromDifficulty(player.difficultyLevel)
                                settingsManager.updateGameSpeed(speed)
                                onLoginSuccess(player)
                            } else {
                                errorMessage = "Неверное имя пользователя или пароль"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Ошибка входа: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Войти",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    )
                }
            }

            TextButton(
                onClick = onNavigateToRegistration,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Нет аккаунта? Зарегистрируйтесь",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}