package com.example.alwaysonrecorder

import android.app.Application
import android.content.Intent
import com.example.alwaysonrecorder.service.recording.RecordingServiceJava

class App : Application() {
    override fun onCreate() {
        super.onCreate()
//        startService(Intent(this, RecordingServiceJava::class.java))
    }
}