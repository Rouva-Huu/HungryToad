package com.example.hungrytoad.widget

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import com.example.hungrytoad.R
import com.example.hungrytoad.ui.data.GoldPriceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoldPriceWidgetWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val repository = GoldPriceRepository(applicationContext)
                val goldPrice = repository.fetchGoldPrice()

                updateAllWidgets(goldPrice)

                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }
    }

    private fun updateAllWidgets(goldPrice: Double) {
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val thisWidget = ComponentName(applicationContext, GoldPriceWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

        for (appWidgetId in appWidgetIds) {
            updateSingleWidget(appWidgetManager, appWidgetId, goldPrice)
        }
    }

    private fun updateSingleWidget(
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        goldPrice: Double
    ) {
        val views = android.widget.RemoteViews(
            applicationContext.packageName,
            R.layout.widget_gold_price
        )

        val priceText = "${goldPrice.toInt()}₽"
        views.setTextViewText(R.id.tvGoldPrice, priceText)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}