package com.example.alwaysonrecorder.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.alwaysonrecorder.pip.PipManager
import com.example.alwaysonrecorder.pip.PipView
import com.example.alwaysonrecorder.pip.PipView.TAG

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(PipView.TAG, "MainActivity2 onCreate $savedInstanceState")
        var pipManager = PipManager(this)
        pipManager.enterPip()
    }

    override fun onStart() {
        super.onStart()
        finish()
    }
}