package com.example.hungrytoad.model

import android.os.Parcelable
import java.util.Calendar
import kotlinx.parcelize.Parcelize

@Parcelize
data class Player(
    val id: Long = 0,
    val fullName: String = "",
    val gender: String = "",
    val course: String = "",
    val difficultyLevel: String = "Нормально",
    val birthDate: Calendar = Calendar.getInstance(),
    val zodiacSign: String = "",
    val password: String = "",
    val bestScore: Int = 0
) : Parcelable

fun Calendar.toDateString(): String {
    val day = this.get(Calendar.DAY_OF_MONTH)
    val month = this.get(Calendar.MONTH) + 1
    val year = this.get(Calendar.YEAR)
    return "${day}.${month}.${year}"
}

fun String.toCalendar(): Calendar {
    val parts = this.split(".")
    if (parts.size != 3) return Calendar.getInstance()

    val day = parts[0].toIntOrNull() ?: 1
    val month = parts[1].toIntOrNull()?.minus(1) ?: 0
    val year = parts[2].toIntOrNull() ?: 2000

    val calendar = Calendar.getInstance()
    calendar.set(year, month, day)
    return calendar
}