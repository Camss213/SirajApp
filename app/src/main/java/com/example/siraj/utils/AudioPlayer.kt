package com.example.siraj.utils

import android.content.Context
import android.media.MediaPlayer

object AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun play(context: Context, fileName: String) {
        stop()
        val resId = context.resources.getIdentifier(fileName, "raw", context.packageName)
        if (resId != 0) {
            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer?.start()
        }
    }

    fun togglePause() {
        mediaPlayer?.let {
            if (it.isPlaying) it.pause()
            else it.start()
        }
    }

    fun seekForward(ms: Int = 5000) {
        mediaPlayer?.let {
            val newPos = it.currentPosition + ms
            if (newPos < it.duration) it.seekTo(newPos)
        }
    }

    fun seekBackward(ms: Int = 5000) {
        mediaPlayer?.let {
            val newPos = it.currentPosition - ms
            it.seekTo(if (newPos > 0) newPos else 0)
        }
    }

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
}
