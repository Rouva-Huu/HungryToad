package com.example.hungrytoad.ui.data

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class RealmPlayer : RealmObject {
    @PrimaryKey
    var id: Long = 0
    var fullName: String = ""
    var gender: String = ""
    var course: String = ""
    var difficultyLevel: String = "Нормально"
    var birthDateString: String = ""
    var zodiacSign: String = ""
    var passwordHash: String = ""
    var bestScore: Int = 0
}