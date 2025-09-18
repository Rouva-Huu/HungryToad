package com.example.hungrytoad.model

import java.util.Calendar
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Player(
    val fullName: String = "",
    val gender: String = "",
    val course: String = "",
    val difficultyLevel: String = "Нормально",
    val birthDate: Calendar = Calendar.getInstance(),
    val zodiacSign: String = ""
) : Parcelable