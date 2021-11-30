package com.example.alwaysonrecorder.service.recording

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.alwaysonrecorder.pip.PipManager

class CameraService : Service() {

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        var pipManager = PipManager(this)
        pipManager.enterPip()

        return super.onStartCommand(intent, flags, startId)
    }
}