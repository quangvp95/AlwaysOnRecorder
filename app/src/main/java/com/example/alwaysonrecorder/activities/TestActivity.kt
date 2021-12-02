package com.example.alwaysonrecorder.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.alwaysonrecorder.R
import com.example.alwaysonrecorder.pip.PipManager

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pipManager = PipManager(this)
        pipManager.enterPip()
        finish()
    }

    fun click(view: android.view.View) {
        val pipManager = PipManager(this)
        pipManager.enterPip()
        finish()
    }
}