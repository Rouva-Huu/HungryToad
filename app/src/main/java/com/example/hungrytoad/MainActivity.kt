package com.example.hungrytoad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.hungrytoad.ui.theme.HungryToadTheme
import com.example.hungrytoad.utils.SettingsManager

class MainActivity : ComponentActivity() {
    private lateinit var settingsManager: SettingsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager(this)
        setContent {
            HungryToadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppTransitions(settingsManager = settingsManager)
                }
            }
        }
    }
}