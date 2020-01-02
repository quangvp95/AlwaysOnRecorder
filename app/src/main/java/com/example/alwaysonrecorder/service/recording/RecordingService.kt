package com.example.alwaysonrecorder.service.recording

import android.Manifest.permission.*
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.NotificationCompat
import com.example.alwaysonrecorder.activities.MainActivity
import com.example.alwaysonrecorder.`object`.EventBus
import com.example.alwaysonrecorder.events.RequestPermissionsEvent
import com.example.alwaysonrecorder.events.RequestPermissionsResponseEvent
import com.example.alwaysonrecorder.repositories.RecordingRepository
import com.example.alwaysonrecorder.service.recording.backgroundtask.Reaper
import com.example.alwaysonrecorder.service.recording.backgroundtask.Recorder
import com.squareup.otto.Subscribe


class RecordingService : Service() {

    // State
    private lateinit var recordingRepository: RecordingRepository
    private lateinit var recorder: Recorder
    private lateinit var reaper: Reaper

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("${this.javaClass}", "onCreate called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("${this.javaClass}", "onDestroy called")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        stopForeground(false)

        if (intent.hasExtra("recordingEnabled")) {
            if (!intent.getBooleanExtra("recordingEnabled", true)
                && ::recorder.isInitialized
                && recorder.isRecording
            ) {
                recorder.stop()
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancelAll()
            }
        } else {
            // Setup dependencies
            recordingRepository =
                RecordingRepository(
                    application.filesDir
                )

            recorder = Recorder(MediaRecorder(), recordingRepository)
            reaper = Reaper(recordingRepository)

            showNotification()
            startBackgroundTasksIfPossible()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startBackgroundTasksIfPossible() {
        when {
            checkSelfPermission(this, RECORD_AUDIO) != PERMISSION_GRANTED -> {
                val event = RequestPermissionsEvent(
                    listOf(RECORD_AUDIO),
                    PERMISSIONS_RESPONSE_CODE
                )
                EventBus.post(event)
            }
            checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED -> {
                val event = RequestPermissionsEvent(
                    listOf(WRITE_EXTERNAL_STORAGE),
                    PERMISSIONS_RESPONSE_CODE
                )
                EventBus.post(event)
            }
            checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED -> {
                val event = RequestPermissionsEvent(
                    listOf(READ_EXTERNAL_STORAGE),
                    PERMISSIONS_RESPONSE_CODE
                )
                EventBus.post(event)
            }
            else -> {
                startBackgroundTasks()
            }
        }
    }

    private fun startBackgroundTasks() {
        recorder.start(this)
        reaper.start(this)
    }

    @Subscribe
    fun onRequestPermissionsEvent(event: RequestPermissionsResponseEvent) {
        if (PERMISSIONS_RESPONSE_CODE == event.requestCode) {
            //@TODO check response
            startBackgroundTasksIfPossible()
        }
    }

    private fun showNotification() {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java), 0
        )

        // If earlier version channel ID is not used
        // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannel()
        else ""

        startForeground(
            NOTIF_ID, NotificationCompat.Builder(this, channelId)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_media_play)
                .setContentTitle("Say something smart!")
                .setContentText("Always on recorder is running in the background")
                .setContentIntent(pendingIntent)
                .build()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelId = "recorder"
        val channelName = "Background recorder"


        val notificationManager =
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)

        notificationManager.cancelAll()
        notificationManager.createNotificationChannel(
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        )

        return channelId
    }

    companion object {
        private const val NOTIF_ID = 1
        private const val PERMISSIONS_RESPONSE_CODE = 1337
    }
}