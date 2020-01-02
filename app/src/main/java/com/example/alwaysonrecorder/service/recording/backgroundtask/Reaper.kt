package com.example.alwaysonrecorder.service.recording.backgroundtask

import android.content.Context
import android.os.Handler
import com.example.alwaysonrecorder.events.EventBus
import com.example.alwaysonrecorder.events.RecordingsUpdatedEvent
import com.example.alwaysonrecorder.manager.RecordingRepository
import com.example.alwaysonrecorder.repositories.Settings

class Reaper(private val recordingRepository: RecordingRepository) : BackgroundTask() {
    override fun start(context: Context) {
        runnable?.let { handler?.removeCallbacks(it) }

        val delayMillis = Settings.recordingDurationMillis

        handler = Handler()
        runnable = object : Runnable {
            override fun run() {
                recordingRepository.deleteFilesOlderThan(Settings.deletionSpanMillis)
                recordingRepository.recordings()?.let { EventBus.post(RecordingsUpdatedEvent(it)) }

                if (Settings.recordingEnabled) {
                    handler?.postDelayed(this, delayMillis)
                }
            }
        }

        runnable?.let { handler?.postDelayed(it, delayMillis) }
    }

    override fun stop() {
        clean()
    }
}