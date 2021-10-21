package com.example.alwaysonrecorder.service.recording;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.alwaysonrecorder.activities.MainActivity;
import com.example.alwaysonrecorder.repositories.RecordingRepositoryJava;
import com.example.alwaysonrecorder.service.recording.backgroundtask.ReaperJava;
import com.example.alwaysonrecorder.service.recording.backgroundtask.RecorderJava;

public class RecordingServiceJava extends Service {

    // State
    private RecordingRepositoryJava recordingRepository;
    private RecorderJava recorder;
    private ReaperJava reaper;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("${this.javaClass}", "onCreate called");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("${this.javaClass}", "onDestroy called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopForeground(false);

        if (intent.hasExtra("recordingEnabled")) {
            if (!intent.getBooleanExtra("recordingEnabled", true)
                    && recorder.isRecording()
            ) {
                recorder.stop();
                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
            }
        } else {
            // Setup dependencies
            recordingRepository =
                    new RecordingRepositoryJava(getApplication().getExternalCacheDir());

            recorder = new RecorderJava(new MediaRecorder(), recordingRepository);
            reaper = new ReaperJava(recordingRepository);

            showNotification();
            startBackgroundTasksIfPossible();
        }

        return super.onStartCommand(intent, flags, startId);
    }


    private void startBackgroundTasksIfPossible() {
//        RequestPermissionsEvent event;
//        if (checkSelfPermission(this, RECORD_AUDIO) != PERMISSION_GRANTED) {
//            event = RequestPermissionsEvent(
//                    listOf(RECORD_AUDIO),
//                    RecordingService.PERMISSIONS_RESPONSE_CODE
//            );
//            EventBus.post(event)
//        } else if (checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
//            event = new RequestPermissionsEvent(
//                    listOf(WRITE_EXTERNAL_STORAGE),
//                    PERMISSIONS_RESPONSE_CODE
//            );
//            new Bus().post(event);
//        } else if (checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
//            val event = RequestPermissionsEvent(
//                    listOf(READ_EXTERNAL_STORAGE),
//                    PERMISSIONS_RESPONSE_CODE
//            )
//            EventBus.post(event)
//        } else ->{
        startBackgroundTasks();
//        }
    }

    private void startBackgroundTasks() {
        recorder.start(this);
        reaper.start(this);
    }

//    @Subscribe
//    fun onRequestPermissionsEvent(event:RequestPermissionsResponseEvent) {
//        if (PERMISSIONS_RESPONSE_CODE == event.requestCode) {
//            //@TODO check response
//            startBackgroundTasksIfPossible()
//        }
//    }

    private void showNotification() {
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0,
                new Intent(this, MainActivity.class), 0
        );

        // If earlier version channel ID is not used
        // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel();
        }

        startForeground(
                1, new NotificationCompat.Builder(this, channelId)
                        .setOngoing(true)
                        .setSmallIcon(android.R.drawable.ic_media_play)
                        .setContentTitle("Say something smart!")
                        .setContentText("Always on recorder is running in the background")
                        .setContentIntent(pendingIntent)
                        .build()
        );
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel() {
        String channelId = "recorder";
        String channelName = "Background recorder";


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancelAll();
        notificationManager.createNotificationChannel(
                new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        );

        return channelId;
    }

}