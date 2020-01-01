package com.example.alwaysonrecorder.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alwaysonrecorder.R
import com.example.alwaysonrecorder.events.EventBus
import com.example.alwaysonrecorder.helper.TimestampHelper.padWithZero
import com.example.alwaysonrecorder.helper.TimestampHelper.toMinutesAndSeconds
import com.example.alwaysonrecorder.manager.Player
import com.squareup.otto.Subscribe


class PlayerActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener,
    View.OnClickListener {

    enum class PlayState {
        Playing,
        Paused;

        companion object {
            fun tag(isPlaying: Boolean): PlayState {
                return if (isPlaying) Playing else Paused
            }
        }
    }

    private lateinit var seekBar: SeekBar
    private lateinit var timestampTextView: TextView
    private lateinit var playPauseButton: ImageButton
    private lateinit var rewindButton: ImageButton
    private lateinit var forwardButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        seekBar = findViewById<SeekBar>(R.id.seek_bar_playback).apply {
            this.setOnSeekBarChangeListener(this@PlayerActivity)
        }

        playPauseButton = findViewById<ImageButton>(R.id.play_button).apply {
            this.setOnClickListener(this@PlayerActivity)
            this.setTag(R.id.play_button, PlayState.tag(false))
        }

        rewindButton = findViewById<ImageButton>(R.id.rewind_15_button).apply {
            this.setOnClickListener(this@PlayerActivity)
        }

        forwardButton = findViewById<ImageButton>(R.id.forward_15_button).apply {
            this.setOnClickListener(this@PlayerActivity)
        }

        timestampTextView = findViewById(R.id.time_text_view)
    }

    override fun onResume() {
        super.onResume()
        EventBus.register(this)
        Player.onResume()
    }

    override fun onPause() {
        super.onPause()
        EventBus.unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Player.onDestroy()
    }

    @Subscribe
    fun onPlayerUpdate(event: Player.UpdateEvent) {
        // Update play pause button
        if (event.isPlaying && playPauseButton.getTag(R.id.play_button) != PlayState.Playing) {
            playPauseButton.setImageResource(R.drawable.ic_icon_pause)
        } else if (!event.isPlaying && playPauseButton.getTag(R.id.play_button) != PlayState.Paused) {
            playPauseButton.setImageResource(R.drawable.ic_icon_play)
        }

        playPauseButton.setTag(R.id.play_button, PlayState.tag(event.isPlaying))

        // Update seekbar
        seekBar.progress =
            ((event.timestamp.toDouble() / event.fileLength.toDouble()) * 100).toInt()

        // Update timestamp text
        val timestampMinutes = padWithZero(toMinutesAndSeconds(event.timestamp).minutes)
        val timestampSeconds = padWithZero(toMinutesAndSeconds(event.timestamp).seconds)

        val fileLengthMinutes = padWithZero(toMinutesAndSeconds(event.fileLength).minutes)
        val fileLengthSeconds = padWithZero(toMinutesAndSeconds(event.fileLength).seconds)

        timestampTextView.text =
            "$timestampMinutes:$timestampSeconds / $fileLengthMinutes:$fileLengthSeconds"
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        Player.onReleaseSeekBar(seekBar.progress)
    }

    override fun onClick(view: View) {
        when (view) {
            forwardButton -> Player.forwardSeconds(15)
            rewindButton -> Player.rewindSeconds(15)
            playPauseButton -> Player.togglePlay(this)
            else -> Toast.makeText(this, "Could not perform action", Toast.LENGTH_SHORT).show()
        }
    }
}