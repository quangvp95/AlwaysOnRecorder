package com.example.alwaysonrecorder.service

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
import android.os.Handler
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.NotificationCompat
import com.example.alwaysonrecorder.MainActivity
import com.example.alwaysonrecorder.events.EventBus
import com.example.alwaysonrecorder.events.RequestPermissionsEvent
import com.example.alwaysonrecorder.events.RequestPermissionsResponseEvent
import com.example.alwaysonrecorder.manager.Recorder
import com.example.alwaysonrecorder.manager.RecordingRepository
import com.squareup.otto.Subscribe


class MainService : Service() {

    private lateinit var recordingRepository: RecordingRepository
    private lateinit var recorder: Recorder

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Setup dependencies
        recordingRepository = RecordingRepository(application.filesDir)
        recorder = Recorder(MediaRecorder(), recordingRepository)

        startForeground()
        startRecursiveTasksIfPossible()
        
        return super.onStartCommand(intent, flags, startId)
    }

    private fun recordRecursively() {
        recorder.record(this)

        Handler().postDelayed({
            recorder.stop()
            recordRecursively()
        }, RECORDING_INTERVAL)
    }

    private fun deleteFilesRecursively() {
        recordingRepository.deleteFilesOlderThan(REAPER_SPAN_MILLIS)

        Handler().postDelayed({
            deleteFilesRecursively()
        }, REAPER_INTERVAL_MILLIS)
    }

    private fun startRecursiveTasksIfPossible() {
        when {
            checkSelfPermission(this, RECORD_AUDIO) != PERMISSION_GRANTED -> {
                val event = RequestPermissionsEvent(listOf(RECORD_AUDIO), PERMISSIONS_RESPONSE_CODE)
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
            else -> startRecursiveTasks()
        }
    }

    private fun startRecursiveTasks() {
        recordRecursively()
        deleteFilesRecursively()
    }

    @Subscribe
    fun onRequestPermissionsEvent(event: RequestPermissionsResponseEvent) {
        if (PERMISSIONS_RESPONSE_CODE == event.requestCode) {
            //@TODO check response
            startRecursiveTasksIfPossible()
        }
    }

    private fun startForeground() {
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

        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)

        return channelId
    }

    companion object {
        private const val NOTIF_ID = 1
        private const val PERMISSIONS_RESPONSE_CODE = 1337

        private const val RECORDING_INTERVAL: Long = 1000 * 60 * 60 // 1 hrs
        private const val REAPER_INTERVAL_MILLIS: Long = 1000 * 60 * 60 * 48 // 48 hrs
        private const val REAPER_SPAN_MILLIS: Long = 1000 * 60 * 60 // 1 hrs
    }
}