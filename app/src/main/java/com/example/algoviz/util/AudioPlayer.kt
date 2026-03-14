package com.example.algoviz.util

import android.content.Context
import android.media.MediaPlayer
import android.util.Base64
import java.io.File
import java.io.FileOutputStream

class AudioPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun playBase64Audio(base64Audio: String, onCompletion: () -> Unit = {}) {
        stop() // Stop any previous playback

        try {
            val audioBytes = Base64.decode(base64Audio, Base64.DEFAULT)
            val tempFile = File(context.cacheDir, "tts_playback_${System.currentTimeMillis()}.wav")
            FileOutputStream(tempFile).use { it.write(audioBytes) }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(tempFile.absolutePath)
                setOnCompletionListener {
                    it.release()
                    tempFile.delete()
                    onCompletion()
                }
                setOnErrorListener { mp, _, _ ->
                    mp.release()
                    tempFile.delete()
                    onCompletion()
                    true
                }
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onCompletion()
        }
    }

    fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        } catch (_: Exception) {}
        mediaPlayer = null
    }

    fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying == true
        } catch (_: Exception) {
            false
        }
    }
}
