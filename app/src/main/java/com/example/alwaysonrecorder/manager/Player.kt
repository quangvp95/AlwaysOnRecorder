package com.example.alwaysonrecorder.manager

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import com.example.alwaysonrecorder.events.EventBus
import java.io.File
import java.io.FileInputStream
import kotlin.math.max
import kotlin.math.min


object Player {

    data class UpdateEvent(
        val isPlaying: Boolean,
        val timestamp: Int,
        val fileLength: Int
    )

    // State
    var mountedFile: File? = null
        private set
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying: Boolean = false
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    fun mount(file: File): Boolean {
        mountedFile = file

        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setDataSource(FileInputStream(file).fd)
            mediaPlayer?.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )

            mediaPlayer?.prepare()

            mediaPlayer?.setOnCompletionListener {
                if (isPlaying) {
                    isPlaying = false
                    mediaPlayer?.seekTo(0)
                    sendUpdate()
                }
            }
        } catch (e: Exception) {
            Log.e("$this.javaClass", e.message, e)
            return false
        }

        return true
    }

    fun onResume() {
        sendUpdate()
    }

    fun onDestroy() {
        pause()
        mountedFile = null
        mediaPlayer = null
    }

    fun forwardSeconds(seconds: Int) {
        val player = mediaPlayer ?: return

        val target = min(player.duration, player.currentPosition + seconds * 1000)

        mediaPlayer?.seekTo(target)
        sendUpdate()
    }

    fun rewindSeconds(seconds: Int) {
        val player = mediaPlayer ?: return

        val target = max(0, player.currentPosition - seconds * 1000)

        mediaPlayer?.seekTo(target)
        sendUpdate()
    }

    fun togglePlay(context: Context) {
        if (isPlaying) pause() else play(context)
        sendUpdate()
    }

    fun onReleaseSeekBar(progress: Int) {
        val player = mediaPlayer ?: return
        player.seekTo(((progress / 100F) * player.duration).toInt())
        sendUpdate()
    }

    private fun scheduleUpdateLooper(context: Context) {
        val delayMillis = 1000L

        handler = Handler(context.mainLooper)
        runnable = object : Runnable {
            override fun run() {
                if (isPlaying) {
                    sendUpdate()
                    Handler(context.mainLooper).postDelayed(this, delayMillis)
                }
            }
        }

        runnable?.let { handler?.postDelayed(it, delayMillis) }
    }

    private fun clearUpdateLooper() {
        val handler = handler ?: return
        val runnable = runnable ?: return

        handler.removeCallbacks(runnable)
    }

    private fun play(context: Context) {
        mediaPlayer?.start()
        isPlaying = true
        clearUpdateLooper()
        scheduleUpdateLooper(context)
    }

    private fun pause() {
        mediaPlayer?.pause()
        isPlaying = false
        clearUpdateLooper()
    }

    private fun sendUpdate() {
        val player = mediaPlayer ?: return
        EventBus.post(UpdateEvent(isPlaying, player.currentPosition, player.duration))
    }
}