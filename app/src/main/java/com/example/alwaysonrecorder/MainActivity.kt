package com.example.alwaysonrecorder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.alwaysonrecorder.Events.RequestPermissionsEvent
import com.example.alwaysonrecorder.Events.RequestPermissionsResponseEvent
import com.squareup.otto.Subscribe

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        EventBus.register(this)
        super.onResume()
    }

    override fun onPause() {
        EventBus.unregister(this)
        super.onPause()
    }

    @Subscribe
    fun onRequestPermissionsEvent(event: RequestPermissionsEvent) {
        ActivityCompat.requestPermissions(this, event.permissions.toTypedArray(), event.requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EventBus.post(RequestPermissionsResponseEvent("perfect!", requestCode))
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}

