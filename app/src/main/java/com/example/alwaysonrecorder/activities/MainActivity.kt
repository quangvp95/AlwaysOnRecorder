package com.example.alwaysonrecorder.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alwaysonrecorder.R
import com.example.alwaysonrecorder.`object`.EventBus
import com.example.alwaysonrecorder.events.RecordingsUpdatedEvent
import com.example.alwaysonrecorder.events.RequestPermissionsEvent
import com.example.alwaysonrecorder.events.RequestPermissionsResponseEvent
import com.example.alwaysonrecorder.`object`.Player
import com.example.alwaysonrecorder.repositories.RecordingRepository
import com.example.alwaysonrecorder.ui.RecordingViewHolder
import com.squareup.otto.Subscribe
import java.io.File


class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    // State
    private var recordings = mutableListOf<File>()

    private lateinit var recordingRepository: RecordingRepository
    private lateinit var emptyTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        recordingRepository =
            RecordingRepository(
                application.filesDir
            )

        finish()
//        viewManager = LinearLayoutManager(this)
//        viewAdapter = Adapter(recordings, this)
//
//        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
//            layoutManager = viewManager
//            adapter = viewAdapter
//        }
//
//        emptyTextView = findViewById(R.id.emptyTextView)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Todo only trigger action when item with correct Id is selected.
        startActivity(Intent(this, SettingsActivity::class.java))
        return true
    }

    class Adapter(private val dataset: List<File>, private val context: Context) :
        RecyclerView.Adapter<RecordingViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RecordingViewHolder.create(parent)

        override fun getItemCount(): Int {
            return dataset.count()
        }

        override fun onBindViewHolder(holder: RecordingViewHolder, position: Int) {
            dataset.getOrNull(position)?.let { file ->
                val onClickHandler = { _: View ->
                    if (Player.mount(file)) {
                        val intent = Intent(context, PlayerActivity::class.java)
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Could not play file", Toast.LENGTH_SHORT).show()
                    }
                }

                holder.onBind(file.name, onClickHandler)
            }
        }
    }
}

