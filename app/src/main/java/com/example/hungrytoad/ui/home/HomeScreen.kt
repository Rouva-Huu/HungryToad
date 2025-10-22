package com.example.hungrytoad.ui.home

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hungrytoad.ui.theme.*

@Composable
fun HomeScreen(onHomeGame: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Marsh),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Голодная жаба",
                style = MaterialTheme.typography.labelLarge,
                color = DarkGreen,
                modifier = Modifier.padding(bottom = 60.dp)
            )

            Button(
                onClick = onHomeGame,
                colors = ButtonDefaults.buttonColors(containerColor = PaleGreen),
                modifier = Modifier
                    .width(200.dp)
                    .height(60.dp)
            ) {
                Text(
                    "Начать игру",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}