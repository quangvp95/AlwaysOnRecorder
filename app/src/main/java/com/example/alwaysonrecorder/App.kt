package com.example.alwaysonrecorder

import android.app.Application
import android.content.Intent
import com.example.alwaysonrecorder.Service.MainService

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startService(Intent(this, MainService::class.java))
    }
}