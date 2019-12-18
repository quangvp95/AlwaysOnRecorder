package com.example.alwaysonrecorder.Service

import android.R
import androidx.core.app.NotificationCompat
import android.app.PendingIntent
import android.app.Service
import com.example.alwaysonrecorder.MainActivity
import android.content.Intent
import android.os.IBinder


class MainService : Service() {

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForeground() {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java), 0
        )

        startForeground(NOTIF_ID, NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
            // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_media_play)
                .setContentTitle("Hello world")
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .build()
        )
    }

    companion object {
        private const val NOTIF_ID = 1
        private const val NOTIF_CHANNEL_ID = "Channel_Id"
    }
}