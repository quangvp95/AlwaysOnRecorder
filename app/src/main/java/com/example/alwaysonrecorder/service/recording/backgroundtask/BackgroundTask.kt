package com.example.alwaysonrecorder.service.recording.backgroundtask

import android.content.Context
import android.os.Handler

abstract class BackgroundTask {
    protected var handler: Handler? = null
    protected var runnable: Runnable? = null

    open fun start(context: Context) {

    }

    open fun stop() {

    }

    fun clean() {
        runnable?.let { handler?.removeCallbacks(it) }
        runnable = null
        handler = null
    }
}