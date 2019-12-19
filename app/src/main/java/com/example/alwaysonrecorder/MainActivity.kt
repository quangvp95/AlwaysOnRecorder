package com.example.alwaysonrecorder

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alwaysonrecorder.Events.EventBus
import com.example.alwaysonrecorder.Events.RecordingsUpdatedEvent
import com.example.alwaysonrecorder.Events.RequestPermissionsEvent
import com.example.alwaysonrecorder.Events.RequestPermissionsResponseEvent
import com.example.alwaysonrecorder.Manager.Player
import com.example.alwaysonrecorder.ui.RecordingViewHolder
import com.squareup.otto.Subscribe
import java.io.File


class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    data class Recording(var isPlaying: Boolean, val file: File)

    private var recordings = mutableListOf<Recording>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewManager = LinearLayoutManager(this)
        viewAdapter = Adapter(recordings, applicationContext)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
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

    @Subscribe
    fun onRecordingFinishedEvent(event: RecordingsUpdatedEvent) {
        recordings.clear()
        recordings.addAll(event.recordings.map { Recording(Player.isPlayingFile(it), it) })
        viewAdapter.notifyDataSetChanged()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //@TODO implement status
        EventBus.post(RequestPermissionsResponseEvent("perfect!", requestCode))
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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

