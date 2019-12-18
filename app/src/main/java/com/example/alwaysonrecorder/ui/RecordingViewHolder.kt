package com.example.alwaysonrecorder.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.alwaysonrecorder.R


class RecordingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var textView: TextView? = itemView.findViewById(R.id.textView) as? TextView

    companion object {
        fun create(parent: ViewGroup): RecordingViewHolder {
            val recordingViewHolder = LayoutInflater.from(parent.context)
                .inflate(R.layout.recording_view_holder, parent, false)

            // Return a new holder instance
            return RecordingViewHolder(recordingViewHolder!!)
        }
    }

    fun onBind(name: String) {
        textView?.text = name

        // @TODO make it play when pressing play button
    }
}