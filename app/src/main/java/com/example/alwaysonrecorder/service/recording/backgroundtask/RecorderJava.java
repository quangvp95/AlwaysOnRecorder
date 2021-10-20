package com.example.alwaysonrecorder.service.recording.backgroundtask;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.alwaysonrecorder.object.Settings;
import com.example.alwaysonrecorder.repositories.RecordingRepository;

import java.io.IOException;

class RecorderJava extends BackgroundTaskJava {
    private MediaRecorder mediaRecorder;
    private RecordingRepository recordingRepository;

    String currentRecordingPath = null;

    public RecorderJava(MediaRecorder mediaRecorder, RecordingRepository recordingRepository) {
        this.mediaRecorder = mediaRecorder;
        this.recordingRepository = recordingRepository;
        informAboutUpdate();
    }

    Boolean isRecording = false;

    @Override
    public void start(@NonNull final Context context) {
        clean();

        final long delayMillis = Settings.INSTANCE.getRecordingDurationMillis();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isRecording)
                    stop();

                if (Settings.INSTANCE.getRecordingEnabled()) {
                    record(context);
                    if (handler != null)
                        handler.postDelayed(this, delayMillis);
                }
            }
        };

        handler.postDelayed(runnable, delayMillis);
    }

    @Override
    public void stop() {
        isRecording = false;
        currentRecordingPath = null;

        try {
            mediaRecorder.stop();
        } catch (IllegalStateException e) {
            Log.e("${this.javaClass}", e.getMessage(), e);
        }

        informAboutUpdate();
    }

    private void record(Context context) {
        isRecording = true;
        currentRecordingPath = recordingRepository.fileName();

        try {
            mediaRecorder.reset();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setAudioEncodingBitRate(128000);
            mediaRecorder.setAudioSamplingRate(96000);
            mediaRecorder.setOutputFile(currentRecordingPath);
            mediaRecorder.prepare();
            mediaRecorder.start();

            Log.d("${this.javaClass}", "Stared recording $currentRecordingPath successfully");
            Toast.makeText(context, "Stared recording $currentRecordingPath successfully", Toast.LENGTH_SHORT)
                    .show();
        } catch (IOException e){
            Log.e("${this.javaClass}", e.getMessage(), e);
            Toast.makeText(context, "Unable to start recording", Toast.LENGTH_SHORT).show();
        }
    }

    private void informAboutUpdate() {
//        recordingRepository.recordings() ?.let {
//            EventBus.post(RecordingsUpdatedEvent(it))
//        }
    }
}