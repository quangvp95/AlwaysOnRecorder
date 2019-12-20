package com.example.alwaysonrecorder.manager

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.widget.Toast
import com.example.alwaysonrecorder.events.EventBus
import java.io.File
import java.io.FileInputStream


object Player {

    class PlayEvent(val recording: File)
    class PauseEvent(val recording: File)

    private lateinit var mediaPlayer: MediaPlayer
    private var currentlyPlaying: File? = null

    fun play(context: Context, recording: File) {
        currentlyPlaying = recording

        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(FileInputStream(recording).fd)
            mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            mediaPlayer.prepare()
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener { if (currentlyPlaying == recording) pause(recording) }

            EventBus.post(PlayEvent(recording))
        } catch (e: Exception) {
            Toast.makeText(context, "Could not play file", Toast.LENGTH_SHORT).show()
        }
    }

    fun pause(recording: File) {
        currentlyPlaying = null
        mediaPlayer.pause()
        EventBus.post(PauseEvent(recording))
    }

    fun isPlayingFile(file: File) = currentlyPlaying == file
}