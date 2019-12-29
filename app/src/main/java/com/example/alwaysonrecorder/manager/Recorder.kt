package com.example.alwaysonrecorder.manager

import android.content.Context
import android.media.MediaRecorder
import android.widget.Toast
import com.example.alwaysonrecorder.events.EventBus
import com.example.alwaysonrecorder.events.RecordingsUpdatedEvent
import java.io.IOException

class Recorder(
    private val mediaRecorder: MediaRecorder,
    private val recordingRepository: RecordingRepository
) {

    init {
        informAboutUpdate()
    }

    var isRecording: Boolean = false
        private set

    fun stop() {
        try {
            mediaRecorder.stop()
            isRecording = false
        } catch (e: IllegalStateException) {
            println("Error!")
        }

        informAboutUpdate()
    }

    fun record(context: Context) {
        val fileName = recordingRepository.fileName()
        mediaRecorder.reset()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder.setAudioEncodingBitRate(128000)
        mediaRecorder.setAudioSamplingRate(96000)
        mediaRecorder.setOutputFile(fileName)

        try {
            mediaRecorder.prepare()
            mediaRecorder.start()
            isRecording = true
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