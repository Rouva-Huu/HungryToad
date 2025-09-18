package com.example.hungrytoad.ui.registration

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hungrytoad.model.Player
import com.example.hungrytoad.ui.theme.LightNude
import com.example.hungrytoad.utils.ZodiacUtils
import java.util.Calendar

@Composable
fun ResultsScreen(navController: NavController) {
    var playerData by remember { mutableStateOf<Player?>(null) }

    LaunchedEffect(Unit) {
        val data = navController.previousBackStackEntry
            ?.savedStateHandle
            ?.get<Player>("playerData")
        playerData = data
    }

//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        if (playerData != null) {
//            PlayerResults(playerData!!, navController)
//        } else {
//            Text("Данные не найдены")
//        }
//    }
}

@Composable
fun PlayerResults(player: Player, navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Регистрация завершена!",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = LightNude,
            ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text("Данные игрока:", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))

                Text("ФИО: ${player.fullName}", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Пол: ${player.gender}", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Курс: ${player.course}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Уровень сложности: ${player.difficultyLevel}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Дата рождения: ${player.birthDate.get(Calendar.DAY_OF_MONTH)}." +
                            "${player.birthDate.get(Calendar.MONTH) + 1}." +
                            "${player.birthDate.get(Calendar.YEAR)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row (
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Знак зодиака: ${player.zodiacSign}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Image(
                        painter = painterResource(ZodiacUtils.getZodiacIconResource(player.zodiacSign)),
                        contentDescription = player.zodiacSign,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(start = 18.dp)
                    )
                }
            }
        }

        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(16.dp)
        ) {
            Text("Вернуться к регистрации", style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                .padding(vertical = 10.dp))
        }
    }
}