package com.example.alwaysonrecorder.ui

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.alwaysonrecorder.Manager.Player
import com.example.alwaysonrecorder.R
import java.io.File


class RecordingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var textView: TextView? = itemView.findViewById(R.id.textView) as? TextView
    private var imageButton: ImageButton? = itemView.findViewById(R.id.imageButton) as? ImageButton

    companion object {
        fun create(parent: ViewGroup): RecordingViewHolder {
            val recordingViewHolder = LayoutInflater.from(parent.context)
                .inflate(R.layout.recording_view_holder, parent, false)

            // Return a new holder instance
            return RecordingViewHolder(recordingViewHolder!!)
        }
    }

    fun onBind(applicationContext: Context, isPlaying: Boolean, file: File) {
        textView?.text = file.name

        imageButton?.setImageResource(
            if (isPlaying)
                android.R.drawable.ic_media_pause
            else
                android.R.drawable.ic_media_play
        )

        imageButton?.setOnClickListener {
            if (isPlaying)
                Player.pause(file)
            else
                Player.play(applicationContext, file)
        }
    }
}