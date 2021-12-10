package com.example.alwaysonrecorder.activities

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.example.alwaysonrecorder.R
import com.example.alwaysonrecorder.pip.PipManager


class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && !(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).isNotificationPolicyAccessGranted
        ) {
            val intent = Intent(
                Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
            )
            startActivity(intent)
        }

    }

    fun click(view: android.view.View) {
        var pipManager = PipManager(this)
        pipManager.enterPip()
    }
}