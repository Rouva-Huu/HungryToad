package com.example.hungrytoad.ui.data

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.hungrytoad.model.Player
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.hungrytoad.model.toCalendar
import com.example.hungrytoad.model.toDateString

class PlayerRepository {
    private val config = RealmConfiguration.Builder(schema = setOf(RealmPlayer::class))
        .schemaVersion(1)
        .build()

    private val realm: Realm = Realm.open(config)

    private object PasswordHasher {
        fun hash(password: String): String {
            return BCrypt.withDefaults().hashToString(12, password.toCharArray())
        }

        fun verify(password: String, hash: String): Boolean {
            return BCrypt.verifyer().verify(password.toCharArray(), hash).verified
        }
    }

    suspend fun registerPlayer(player: Player): Long {
        val existingPlayer = realm.query<RealmPlayer>("fullName == $0", player.fullName)
            .first()
            .find()

        if (existingPlayer != null) {
            throw Exception("Пользователь с таким именем уже существует")
        }

        val newId = (realm.query<RealmPlayer>().count().find() ?: 0) + 1

        realm.write {
            val realmPlayer = RealmPlayer().apply {
                id = newId
                fullName = player.fullName
                gender = player.gender
                course = player.course
                difficultyLevel = player.difficultyLevel
                birthDateString = player.birthDate.toDateString()
                zodiacSign = player.zodiacSign
                passwordHash = PasswordHasher.hash(player.password)
                bestScore = player.bestScore
            }
            copyToRealm(realmPlayer)
        }

        return newId
    }

    suspend fun loginPlayer(fullName: String, password: String): Player? {
        val realmPlayer = realm.query<RealmPlayer>("fullName == $0", fullName)
            .first()
            .find()

        return if (realmPlayer != null && PasswordHasher.verify(password, realmPlayer.passwordHash)) {
            convertToPlayer(realmPlayer)
        } else {
            null
        }
    }

    suspend fun updateBestScore(playerId: Long, newScore: Int) {
        val player = realm.query<RealmPlayer>("id == $0", playerId)
            .first()
            .find()

        player?.let {
            realm.write {
                findLatest(it)?.bestScore = maxOf(it.bestScore, newScore)
            }
        }
    }

    suspend fun getPlayer(playerId: Long): Player? {
        val realmPlayer = realm.query<RealmPlayer>("id == $0", playerId)
            .first()
            .find()

        return realmPlayer?.let { convertToPlayer(it) }
    }

    fun getAllPlayers(): Flow<List<Player>> {
        return realm.query<RealmPlayer>()
            .asFlow()
            .map { result ->
                result.list.map { convertToPlayer(it) }
            }
    }

    private fun convertToPlayer(realmPlayer: RealmPlayer): Player {
        return Player(
            id = realmPlayer.id,
            fullName = realmPlayer.fullName,
            gender = realmPlayer.gender,
            course = realmPlayer.course,
            difficultyLevel = realmPlayer.difficultyLevel,
            birthDate = realmPlayer.birthDateString.toCalendar(),
            zodiacSign = realmPlayer.zodiacSign,
            password = "",
            bestScore = realmPlayer.bestScore
        )
    }

    fun close() {
        realm.close()
    }

    suspend fun updatePlayerDifficulty(playerId: Long, difficulty: String) {
        val player = realm.query<RealmPlayer>("id == $0", playerId)
            .first()
            .find()

        player?.let {
            realm.write {
                findLatest(it)?.difficultyLevel = difficulty
            }
        }
    }

    suspend fun updatePlayer(player: Player) {
        val realmPlayer = realm.query<RealmPlayer>("id == $0", player.id)
            .first()
            .find()

        realmPlayer?.let {
            realm.write {
                findLatest(it)?.apply {
                    fullName = player.fullName
                    gender = player.gender
                    course = player.course
                    difficultyLevel = player.difficultyLevel
                    birthDateString = player.birthDate.toDateString()
                    zodiacSign = player.zodiacSign
                    bestScore = player.bestScore
                }
            }
        }
    }
}