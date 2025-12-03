package com.example.hungrytoad.ui.data

import android.content.Context
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*
import okhttp3.Request
import kotlin.math.roundToInt

class GoldPriceRepository(private val context: Context) {

    private val _currentGoldPrice = MutableStateFlow(0.0)
    val currentGoldPrice: StateFlow<Double> = _currentGoldPrice

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun fetchGoldPrice(): Double {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val currentDate = Date()
            val today = dateFormat.format(currentDate)

            val url = "https://www.cbr.ru/scripts/xml_metall.asp?date_req1=$today&date_req2=$today"

            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val xmlResponse = response.body?.string() ?: ""
                parseGoldPriceFromXml(xmlResponse)
            } else {
                10000.0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            10000.0
        }
    }

    private fun parseGoldPriceFromXml(xml: String): Double {
        return try {
            val recordStart = xml.indexOf("<Record")
            if (recordStart == -1) return 10000.0

            val recordEnd = xml.indexOf("</Record>", recordStart)
            if (recordEnd == -1) return 10000.0

            val record = xml.substring(recordStart, recordEnd)

            if (!record.contains("Code=\"1\"")) return 10000.0

            val buyStart = record.indexOf("<Buy>")
            if (buyStart == -1) return 10000.0

            val buyEnd = record.indexOf("</Buy>", buyStart)
            if (buyEnd == -1) return 10000.0

            val buyPriceText = record.substring(buyStart + 5, buyEnd)
                .replace(",", ".")
                .trim()

            buyPriceText.toDoubleOrNull() ?: 10000.0
        } catch (e: Exception) {
            e.printStackTrace()
            10000.0
        }
    }

    fun getGoldBeetlePoints(): Int {
        val price = _currentGoldPrice.value
        return ((price / 100).roundToInt()).coerceAtLeast(90)
    }

    suspend fun updateGoldPrice() {
        val newPrice = fetchGoldPrice()
        _currentGoldPrice.value = newPrice
    }
}