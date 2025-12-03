package com.example.hungrytoad.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.Context
import com.example.hungrytoad.ui.data.GoldPriceRepository

class GoldManager(private val context: Context) {
    private val repository = GoldPriceRepository(context)
    private var updateJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    val currentGoldPrice = repository.currentGoldPrice

    fun startAutoUpdates() {
        stopAutoUpdates()

        updateJob = scope.launch {
            while (true) {
                repository.updateGoldPrice()
                delay(30 * 60 * 1000)
            }
        }
    }

    fun stopAutoUpdates() {
        updateJob?.cancel()
        updateJob = null
    }

    suspend fun getGoldBeetlePoints(): Int {
        return repository.getGoldBeetlePoints()
    }

    suspend fun forceUpdate() {
        repository.updateGoldPrice()
    }
}