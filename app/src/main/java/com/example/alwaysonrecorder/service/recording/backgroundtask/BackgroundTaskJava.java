package com.example.alwaysonrecorder.service.recording.backgroundtask;

import android.content.Context;
import android.os.Handler;

abstract class BackgroundTaskJava {
    protected Handler handler = null;
    protected Runnable runnable = null;

    public abstract void start(Context context);

    public abstract void stop();

    void clean() {
        if (runnable != null && handler != null)
            handler.removeCallbacks(runnable);
        runnable = null;
        handler = null;
    }
}