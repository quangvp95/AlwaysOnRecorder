package com.example.alwaysonrecorder.activities

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.alwaysonrecorder.R
import com.example.alwaysonrecorder.`object`.EventBus
import com.example.alwaysonrecorder.events.RecordingsUpdatedEvent
import com.example.alwaysonrecorder.events.RequestPermissionsEvent
import com.example.alwaysonrecorder.events.RequestPermissionsResponseEvent
import com.example.alwaysonrecorder.repositories.RecordingRepository
import com.example.alwaysonrecorder.repositories.RecordingRepositoryJava
import com.squareup.otto.Subscribe
import java.io.File


class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    // State
    private var recordings = mutableListOf<File>()

    private lateinit var recordingRepository: RecordingRepository
    private lateinit var recordingRepositoryJava: RecordingRepositoryJava
    private lateinit var emptyTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recordingRepository =
            RecordingRepository(
                application.filesDir
            )
        recordingRepositoryJava =
            RecordingRepositoryJava(
                application.filesDir
            )

//        finish()
    }

    override fun onResume() {
        EventBus.register(this)
        recordingRepository.recordings()?.let { reload(it) }
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

    @Subscribe
    fun onRecordingsUpdatedEvent(event: RecordingsUpdatedEvent) {
        reload(event.recordings)
    }

    private fun reload(files: List<File>) {
        if (files.isNotEmpty())
            emptyTextView.visibility = View.GONE

        recordings.clear()
        recordings.addAll(files)
        viewAdapter.notifyDataSetChanged()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EventBus.post(RequestPermissionsResponseEvent("perfect!", requestCode))
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}

