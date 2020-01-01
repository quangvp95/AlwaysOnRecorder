package com.example.alwaysonrecorder.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alwaysonrecorder.R

class RecordingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var textView: TextView? = itemView.findViewById(R.id.text_view) as? TextView
    private var imageButton: ImageButton? = itemView.findViewById(R.id.imageButton) as? ImageButton

    companion object {
        fun create(parent: ViewGroup): RecordingViewHolder {
            val recordingViewHolder = LayoutInflater.from(parent.context)
                .inflate(R.layout.recording_view_holder, parent, false)

            return RecordingViewHolder(recordingViewHolder!!)
        }
    }

    fun onBind(name: String, onClickHandler: (View) -> Unit) {
        textView?.text = name
        imageButton?.setOnClickListener(onClickHandler)
    }
}