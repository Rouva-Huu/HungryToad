package com.example.hungrytoad.utils

object DifficultySettings {
    const val VERY_EASY = 1f
    const val EASY = 2f
    const val NORMAL = 3f
    const val HARD = 4f
    const val VERY_HARD = 5f

    fun getDifficultyFromSpeed(speed: Float): String {
        return when {
            speed < 1.5f -> "Очень легко"
            speed in 1.5f..2.5f -> "Легко"
            speed in 2.5f..3.5f -> "Нормально"
            speed in 3.5f..4.5f -> "Сложно"
            speed >= 4.5f -> "Очень сложно"
            else -> "Нормально"
        }
    }

    fun getSpeedFromDifficulty(difficulty: String): Float {
        return when (difficulty) {
            "Очень легко" -> VERY_EASY
            "Легко" -> EASY
            "Нормально" -> NORMAL
            "Сложно" -> HARD
            "Очень сложно" -> VERY_HARD
            else -> NORMAL
        }
    }

    fun getSpeedRange(): ClosedFloatingPointRange<Float> = VERY_EASY..VERY_HARD
}