package com.example.alwaysonrecorder.service.recording.backgroundtask;


import android.content.Context;
import android.os.Handler;

import com.example.alwaysonrecorder.repositories.RecordingRepositoryJava;

public class ReaperJava extends BackgroundTaskJava {
    private final RecordingRepositoryJava recordingRepository;

    public ReaperJava(RecordingRepositoryJava recordingRepository) {
        this.recordingRepository = recordingRepository;
    }

    @Override
    public void start(Context context) {
        if (runnable != null && handler != null)
            handler.removeCallbacks(runnable);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                recordingRepository.deleteFilesOlderThan(1 * 3600 * 1000);
            }
        };

        handler.postDelayed(runnable, 5 * 1000);
    }

    @Override
    public void stop() {
        clean();
    }
}