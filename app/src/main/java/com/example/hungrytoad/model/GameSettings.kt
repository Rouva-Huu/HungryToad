package com.example.hungrytoad.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameSettings(
    val gameSpeed: Float = 5f,
    val bonusInterval: Float = 3f,
    val roundDuration: Float = 60f,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
) : java.io.Serializable, Parcelable