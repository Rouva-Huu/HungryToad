package com.example.hungrytoad.utils

object ZodiacUtils {
    fun getZodiacSign(day: Int, month: Int): String {
        return when (month) {
            1 -> if (day > 20) "Козерог" else "Водолей"
            2 -> if (day < 19) "Водолей" else "Рыбы"
            3 -> if (day < 21) "Рыбы" else "Овен"
            4 -> if (day < 20) "Овен" else "Телец"
            5 -> if (day < 21) "Телец" else "Близнецы"
            6 -> if (day < 21) "Близнецы" else "Рак"
            7 -> if (day < 23) "Рак" else "Лев"
            8 -> if (day < 23) "Лев" else "Дева"
            9 -> if (day < 23) "Дева" else "Весы"
            10 -> if (day < 23) "Весы" else "Скорпион"
            11 -> if (day < 22) "Скорпион" else "Стрелец"
            12 -> if (day < 22) "Стрелец" else "Козерог"
            else -> "Не удалось определить знак зодиака"
        }
    }
    fun getZodiacIconResource(zodiacSign: String): Int {
        return when (zodiacSign) {
            "Овен" -> com.example.hungrytoad.R.drawable.ic_aries
            "Телец" -> com.example.hungrytoad.R.drawable.ic_taurus
            "Близнецы" -> com.example.hungrytoad.R.drawable.ic_gemini
            "Рак" -> com.example.hungrytoad.R.drawable.ic_cancer
            "Лев" -> com.example.hungrytoad.R.drawable.ic_leo
            "Дева" -> com.example.hungrytoad.R.drawable.ic_virgo
            "Весы" -> com.example.hungrytoad.R.drawable.ic_libra
            "Скорпион" -> com.example.hungrytoad.R.drawable.ic_scorpio
            "Стрелец" -> com.example.hungrytoad.R.drawable.ic_sagittarius
            "Козерог" -> com.example.hungrytoad.R.drawable.ic_capricorn
            "Водолей" -> com.example.hungrytoad.R.drawable.ic_aquarius
            "Рыбы" -> com.example.hungrytoad.R.drawable.ic_pisces
            else -> com.example.hungrytoad.R.drawable.ic_unknown
        }
    }
}
