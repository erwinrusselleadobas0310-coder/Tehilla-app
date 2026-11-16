package com.example.core.audio

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build

/**
 * Handles real Android MediaPlayer playback when a local MP3/WAV file URI is attached
 * to a song, and releases resources cleanly when switching songs or destroying the ViewModel.
 */
class TehillaAudioController(private val appContext: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var loadedUri: String? = null

    fun loadIfPresent(uriString: String?): Boolean {
        if (uriString == null) {
            release()
            return false
        }
        if (uriString == loadedUri && mediaPlayer != null) return true

        release()
        return try {
            val player = MediaPlayer().apply {
                setDataSource(appContext, Uri.parse(uriString))
                prepare()
            }
            mediaPlayer = player
            loadedUri = uriString
            true
        } catch (_: Exception) {
            release()
            false
        }
    }

    fun play(speed: Float) {
        val player = mediaPlayer ?: return
        applySpeed(speed)
        if (!player.isPlaying) {
            player.start()
        }
    }

    fun pause() {
        val player = mediaPlayer ?: return
        if (player.isPlaying) {
            player.pause()
        }
    }

    fun seekToSeconds(seconds: Int) {
        val player = mediaPlayer ?: return
        player.seekTo((seconds * 1000).coerceIn(0, player.duration))
    }

    fun applySpeed(speed: Float) {
        val player = mediaPlayer ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            runCatching {
                val params = player.playbackParams
                params.speed = speed
                player.playbackParams = params
            }
        }
    }

    fun currentPositionSeconds(): Int? {
        val player = mediaPlayer ?: return null
        return (player.currentPosition / 1000).coerceAtLeast(0)
    }

    fun release() {
        runCatching { mediaPlayer?.stop() }
        runCatching { mediaPlayer?.release() }
        mediaPlayer = null
        loadedUri = null
    }
}
