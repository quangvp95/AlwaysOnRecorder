package com.example.alwaysonrecorder.service.recording.backgroundtask

import android.content.Context
import android.media.MediaRecorder
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.alwaysonrecorder.events.EventBus
import com.example.alwaysonrecorder.events.RecordingsUpdatedEvent
import com.example.alwaysonrecorder.manager.RecordingRepository
import com.example.alwaysonrecorder.repositories.Settings
import java.io.IOException

class Recorder(
    private val mediaRecorder: MediaRecorder,
    private val recordingRepository: RecordingRepository
) : BackgroundTask() {

    var isRecording: Boolean = false
        private set

    override fun start(context: Context) {
        // Stop and save current recording before
        // cleaning up and starting a new recording
        if (isRecording) {
            stop()
            start(context)
            return
        }

        clean()

        val delayMillis = Settings.recordingTime

        handler = Handler()
        runnable = object : Runnable {
            override fun run() {
                stop()

                if (Settings.recordingEnabled) {
                    record(context)
                    handler?.postDelayed(this, delayMillis)
                }
            }
        }

        runnable?.let { handler?.postDelayed(it, delayMillis) }
    }

    override fun stop() {
        isRecording = false

        clean()

        try {
            mediaRecorder.stop()
        } catch (e: IllegalStateException) {
            Log.e("${this.javaClass}", e.message, e)
        }

        informAboutUpdate()
    }

    private fun record(context: Context) {
        isRecording = true

        val fileName = recordingRepository.fileName()

        try {
            mediaRecorder.reset()
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder.setAudioEncodingBitRate(128000)
            mediaRecorder.setAudioSamplingRate(96000)
            mediaRecorder.setOutputFile(fileName)
            mediaRecorder.prepare()
            mediaRecorder.start()

            Toast.makeText(context, "Stared recording $fileName successfully", Toast.LENGTH_SHORT)
                .show()
        } catch (e: IOException) {
            Toast.makeText(context, "Unable to start recording", Toast.LENGTH_SHORT).show()
        }
    }

    private fun informAboutUpdate() {
        recordingRepository.recordings()?.let {
            EventBus.post(RecordingsUpdatedEvent(it))
        }
    }
}