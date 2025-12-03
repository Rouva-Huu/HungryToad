package com.example.hungrytoad.widget

import com.example.hungrytoad.ui.data.GoldPriceRepository
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.hungrytoad.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class GoldPriceWidget : AppWidgetProvider() {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        setupWidgetUpdates(context)
    }

    override fun onDisabled(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("widget_gold_price_update")
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_gold_price)

        scope.launch {
            try {
                val repository = GoldPriceRepository(context)
                val goldPrice = repository.fetchGoldPrice()
                val priceText = "${goldPrice.toInt()}₽"

                views.setTextViewText(R.id.tvGoldPrice, priceText)
                appWidgetManager.updateAppWidget(appWidgetId, views)

            } catch (e: Exception) {
                e.printStackTrace()
                views.setTextViewText(R.id.tvGoldPrice, "10000₽")
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    private fun setupWidgetUpdates(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val updateRequest = PeriodicWorkRequestBuilder<GoldPriceWidgetWorker>(
            1, TimeUnit.HOURS
        ).setConstraints(constraints).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "widget_gold_price_update",
            ExistingPeriodicWorkPolicy.KEEP,
            updateRequest
        )
    }
}