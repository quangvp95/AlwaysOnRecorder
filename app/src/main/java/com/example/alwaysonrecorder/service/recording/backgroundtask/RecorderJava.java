package com.example.alwaysonrecorder.service.recording.backgroundtask;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.alwaysonrecorder.repositories.RecordingRepositoryJava;

import java.io.IOException;

public class RecorderJava extends BackgroundTaskJava {
    public static String currentRecordingPath = null;
    private final MediaRecorder mediaRecorder;
    private final RecordingRepositoryJava recordingRepository;
    boolean isRecording = false;

    public RecorderJava(MediaRecorder mediaRecorder, RecordingRepositoryJava recordingRepository) {
        this.mediaRecorder = mediaRecorder;
        this.recordingRepository = recordingRepository;
        informAboutUpdate();
    }

    public boolean isRecording() {
        return isRecording;
    }

    @Override
    public void start(@NonNull final Context context) {
        clean();

        final long delayMillis = 5 * 60 * 1000;
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isRecording)
                    stop();
                else {
                    record(context);
                    if (handler != null)
                        handler.postDelayed(this, delayMillis);
                }
            }
        };

        handler.postDelayed(runnable, 5 * 1000);
    }

    @Override
    public void stop() {
        Log.d("QuangNHe", "Stop recording " + currentRecordingPath);
        isRecording = false;
        currentRecordingPath = null;

        try {
            mediaRecorder.stop();
        } catch (IllegalStateException e) {
            Log.e("QuangNHe", e.getMessage(), e);
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

            Log.d("QuangNHe", "Stared recording " + currentRecordingPath + " successfully");
//            Toast.makeText(context, "Stared recording \" + currentRecordingPath + \" successfully", Toast.LENGTH_SHORT)
//                    .show();
        } catch (IOException e){
            Log.e("QuangNHe", e.getMessage(), e);
//            Toast.makeText(context, "Unable to start recording", Toast.LENGTH_SHORT).show();
        }
    }

    private void informAboutUpdate() {
//        recordingRepository.recordings() ?.let {
//            EventBus.post(RecordingsUpdatedEvent(it))
//        }
    }
}