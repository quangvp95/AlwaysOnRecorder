package com.example.alwaysonrecorder.Service

import android.Manifest.permission.*
import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.*
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.NotificationCompat
import com.example.alwaysonrecorder.Events.EventBus
import com.example.alwaysonrecorder.Events.RecordingsUpdatedEvent
import com.example.alwaysonrecorder.Events.RequestPermissionsEvent
import com.example.alwaysonrecorder.Events.RequestPermissionsResponseEvent
import com.example.alwaysonrecorder.MainActivity
import com.squareup.otto.Subscribe
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainService : Service() {

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground()
        checkPermissionsAndInitialize()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun recordings() = directory().listFiles()?.toList()?.sortedDescending()

    private fun deleteFilesRecursively() {
        recordings()
            ?.filter<File> { (it.lastModified() + REAPER_SPAN_MILLIS) < System.currentTimeMillis() }
            ?.map { it.delete() }

        Handler().postDelayed({
            deleteFilesRecursively()
        }, REAPER_INTERVAL_MILLIS)
    }

    private fun checkPermissionsAndInitialize() {
        when {
            checkSelfPermission(this, RECORD_AUDIO) != PERMISSION_GRANTED -> {
                val event = RequestPermissionsEvent(listOf(RECORD_AUDIO), PERMISSIONS_RESPONSE_CODE)
                EventBus.post(event)
            }
            checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED -> {
                val event = RequestPermissionsEvent(listOf(WRITE_EXTERNAL_STORAGE), PERMISSIONS_RESPONSE_CODE)
                EventBus.post(event)
            }
            checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED -> {
                val event = RequestPermissionsEvent(listOf(READ_EXTERNAL_STORAGE), PERMISSIONS_RESPONSE_CODE)
                EventBus.post(event)
            }
            else -> startRecursiveTasks()
        }
    }

    private fun startRecursiveTasks() {
        recordRecursively(MediaRecorder())
        deleteFilesRecursively()
    }


    @Subscribe
    fun onRequestPermissionsEvent(event: RequestPermissionsResponseEvent) {
        if (PERMISSIONS_RESPONSE_CODE == event.requestCode) {
            //@TODO check response
            checkPermissionsAndInitialize()
        }
    }

    private fun startForeground() {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java), 0
        )

        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("recorder", "Background recorder")
        } else {
            // If earlier version channel ID is not used
            // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
            ""
        }

        startForeground(
            NOTIF_ID, NotificationCompat.Builder(this, channelId)
                // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_media_play)
                .setContentTitle("Hello world")
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .build()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)


        return channelId
    }

    private fun recordRecursively(recorder: MediaRecorder) {
        record(recorder)

        Handler().postDelayed({
            stop(recorder)
            recordRecursively(recorder)
        }, 5000)
    }

    private fun stop(recorder: MediaRecorder) {
        try {
            recorder.stop()
        } catch (e: IllegalStateException) {
            println("Error!")
        }

        recordings()?.let {
            EventBus.post(RecordingsUpdatedEvent(it))
        }
    }

    private fun record(recorder: MediaRecorder) {
        val fileName = fileName()
        recorder.reset()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        recorder.setAudioEncodingBitRate(128000)
        recorder.setAudioSamplingRate(96000)
        recorder.setOutputFile(fileName)

        try {
            recorder.prepare()
            recorder.start()
            Toast.makeText(this, "Stared recording $fileName successfully", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "Unable to start recording", Toast.LENGTH_SHORT).show()
        }
    }

    private fun directory(): File {
        return application.filesDir
    }

    private fun fileName(): String {
        val dateString = SimpleDateFormat(
            "dd-M-yyyy hh:mm:ss",
            Locale.getDefault()
        ).format(
            Date()
        )

        return "${directory().absolutePath}/$dateString.mp4"
    }

    companion object {
        private const val NOTIF_ID = 1
        private const val PERMISSIONS_RESPONSE_CODE = 1337

        private const val REAPER_INTERVAL_MILLIS: Long = 5_000//60 * 60 * 1000 // 1 hr
        private const val REAPER_SPAN_MILLIS: Long = 30_000//60 * 60 * 1000 * 48 // 48 hrs
    }
}