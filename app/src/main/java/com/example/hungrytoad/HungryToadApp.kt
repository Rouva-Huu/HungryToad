package com.example.hungrytoad

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class HungryToadApp : Application() {

    override fun onCreate() {
        super.onCreate()
        setupGoldPriceUpdates()
    }

    private fun setupGoldPriceUpdates() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val widgetUpdateRequest = PeriodicWorkRequestBuilder<com.example.hungrytoad.widget.GoldPriceWidgetWorker>(
            1, TimeUnit.HOURS
        ).setConstraints(constraints).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "widget_gold_price_update",
            ExistingPeriodicWorkPolicy.KEEP,
            widgetUpdateRequest
        )
    }
}