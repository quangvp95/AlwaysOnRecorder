package com.example.alwaysonrecorder.activities

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.example.alwaysonrecorder.pip.PipManager
import com.example.alwaysonrecorder.pip.PipView.TAG

class MainActivity2 : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity2 onCreate $savedInstanceState")
        val pipManager = PipManager(this)
        pipManager.enterPip()
    }

    override fun onStart() {
        super.onStart()
        finish()
    }
}