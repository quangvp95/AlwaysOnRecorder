package com.example.alwaysonrecorder.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alwaysonrecorder.pip.PipManager

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var pipManager = PipManager(this)
        pipManager.enterPip()
    }

    override fun onStart() {
        super.onStart()
        finish()
    }
}