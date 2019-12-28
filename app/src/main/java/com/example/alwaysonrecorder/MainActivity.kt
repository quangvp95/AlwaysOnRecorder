package com.example.alwaysonrecorder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alwaysonrecorder.events.EventBus
import com.example.alwaysonrecorder.events.RecordingsUpdatedEvent
import com.example.alwaysonrecorder.events.RequestPermissionsEvent
import com.example.alwaysonrecorder.events.RequestPermissionsResponseEvent
import com.example.alwaysonrecorder.manager.Player
import com.example.alwaysonrecorder.manager.RecordingRepository
import com.example.alwaysonrecorder.ui.RecordingViewHolder
import com.squareup.otto.Subscribe
import java.io.File


class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    data class Recording(var isPlaying: Boolean, val file: File)

    // State
    private var recordings = mutableListOf<Recording>()

    private lateinit var recordingRepository: RecordingRepository
    private lateinit var emptyTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recordingRepository = RecordingRepository(application.filesDir)

        viewManager = LinearLayoutManager(this)
        viewAdapter = Adapter(recordings, applicationContext)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        emptyTextView = findViewById(R.id.emptyTextView)
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
    fun onRecordingFinishedEvent(event: RecordingsUpdatedEvent) {
        reload(event.recordings)
    }

    @Subscribe
    fun onPlay(event: Player.PlayEvent) {
        val recording = recordings.find { it.file == event.recording } ?: return
        recording.isPlaying = true
        viewAdapter.notifyItemChanged(recordings.indexOf(recording))
    }

    @Subscribe
    fun onPause(event: Player.PauseEvent) {
        val recording = recordings.find { it.file == event.recording } ?: return
        recording.isPlaying = false
        viewAdapter.notifyItemChanged(recordings.indexOf(recording))
    }

    private fun reload(files: List<File>) {
        if (files.isNotEmpty())
            emptyTextView.visibility = View.GONE

        recordings.clear()
        recordings.addAll(files.map { Recording(Player.isPlayingFile(it), it) })
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Todo only trigger action when item with correct Id is selected.
        startActivity(Intent(this, SettingsActivity::class.java))
        return true
    }

    class Adapter(private val dataset: List<Recording>, private val applicationContext: Context) : RecyclerView.Adapter<RecordingViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RecordingViewHolder.create(parent)

        override fun getItemCount(): Int {
            return dataset.count()
        }

        override fun onBindViewHolder(holder: RecordingViewHolder, position: Int) {
            dataset.getOrNull(position)?.let { holder.onBind(applicationContext, it.isPlaying, it.file) }
        }
    }
}

