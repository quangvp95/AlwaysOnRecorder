package com.example.alwaysonrecorder

import android.os.Build
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.alwaysonrecorder.repositories.Settings


class SettingsActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {
    private lateinit var recordingLengthSeekBar: SeekBar
    private lateinit var deletionIntervalSeekBar: SeekBar
    private lateinit var recordingLengthValueTextView: TextView
    private lateinit var deletionIntervalValueTextView: TextView

    private var recordingLengthMinValuePolyfill: Int? = null
    private var deletionIntervalMinvaluePolyfill: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        recordingLengthSeekBar = findViewById<SeekBar>(R.id.seek_bar_recording_length).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.min = Settings.ALLOWED_RECORDING_LENGTH_MINUTES.first
                this.max = Settings.ALLOWED_RECORDING_LENGTH_MINUTES.last
                this.progress = Settings.recordingTimeMinutes()
            } else {
                recordingLengthMinValuePolyfill = Settings.ALLOWED_RECORDING_LENGTH_MINUTES.first
                this.max =
                    Settings.ALLOWED_RECORDING_LENGTH_MINUTES.last - Settings.ALLOWED_RECORDING_LENGTH_MINUTES.first
                this.progress =
                    Settings.recordingTimeMinutes() - Settings.ALLOWED_RECORDING_LENGTH_MINUTES.first
            }
        }

        deletionIntervalSeekBar = findViewById<SeekBar>(R.id.seek_bar_deletion_interval).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.min = Settings.ALLOWED_DELETION_TIME_HOURS.first
                this.max = Settings.ALLOWED_DELETION_TIME_HOURS.last
                this.progress = Settings.deletionTimeHours()
            } else {
                deletionIntervalMinvaluePolyfill = Settings.ALLOWED_DELETION_TIME_HOURS.first
                this.max =
                    Settings.ALLOWED_DELETION_TIME_HOURS.last - Settings.ALLOWED_DELETION_TIME_HOURS.first
                this.progress =
                    Settings.deletionTimeHours() - Settings.ALLOWED_DELETION_TIME_HOURS.first
            }
        }

        findViewById<Switch>(R.id.enabled_switch).apply {
            this.setOnCheckedChangeListener { buttonView, isChecked ->
                Settings.recordingEnabled = isChecked
            }
        }

        recordingLengthSeekBar.setOnSeekBarChangeListener(this)
        deletionIntervalSeekBar.setOnSeekBarChangeListener(this)

        recordingLengthValueTextView = findViewById(R.id.value_recording_length)
        deletionIntervalValueTextView = findViewById(R.id.value_deletion_interval)

        updateRecordingTimeTextView()
        updateDeletionIntervalTextView()
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        when (seekBar.id) {
            recordingLengthSeekBar.id -> {
                Settings.setRecordingTimeMinutes(progress + (recordingLengthMinValuePolyfill ?: 0))
                updateRecordingTimeTextView()
            }
            deletionIntervalSeekBar.id -> {
                Settings.setDeletionTimeHours(progress + (deletionIntervalMinvaluePolyfill ?: 0))
                updateDeletionIntervalTextView()
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {

    }

    private fun updateRecordingTimeTextView() {
        val minutes = Settings.recordingTimeMinutes()
        var prefix = if (minutes == 1) "minute" else "minutes"
        recordingLengthValueTextView.text = "$minutes $prefix"
    }

    private fun updateDeletionIntervalTextView() {
        val hours = Settings.deletionTimeHours()
        var prefix = if (hours == 1) "hour" else "hours"
        deletionIntervalValueTextView.text = "$hours $prefix"
    }
}