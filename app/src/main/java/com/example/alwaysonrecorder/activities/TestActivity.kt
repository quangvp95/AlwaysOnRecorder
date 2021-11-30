package com.example.alwaysonrecorder.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.alwaysonrecorder.R
import com.example.alwaysonrecorder.pip.PipManager

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    fun click(view: android.view.View) {
        var pipManager = PipManager(this)
        pipManager.enterPip()
    }
}