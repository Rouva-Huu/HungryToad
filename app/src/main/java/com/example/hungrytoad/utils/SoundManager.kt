package com.example.hungrytoad.utils

import android.content.Context
import android.media.MediaPlayer
import com.example.hungrytoad.R

class SoundManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun playBugScream() {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, R.raw.bug_scream)
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}