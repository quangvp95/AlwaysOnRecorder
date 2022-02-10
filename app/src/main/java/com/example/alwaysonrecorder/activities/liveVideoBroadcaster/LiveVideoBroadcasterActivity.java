package com.example.alwaysonrecorder.activities.liveVideoBroadcaster;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.ContentLoadingProgressBar;

import com.example.alwaysonrecorder.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import io.antmedia.android.broadcaster.ILiveVideoBroadcaster;
import io.antmedia.android.broadcaster.LiveVideoBroadcaster;
import io.antmedia.android.broadcaster.utils.Resolution;

@SuppressWarnings("deprecation")
public class LiveVideoBroadcasterActivity extends AppCompatActivity {

    public static final String RTMP_BASE_URL = "rtmp://27.72.62.39/LiveApp/";

    private static final String TAG = LiveVideoBroadcasterActivity.class.getSimpleName();
    boolean mIsRecording = false;
    boolean mIsMuted = false;
    private ViewGroup mRootView;
    private EditText mStreamNameEditText;
    private Intent mLiveVideoBroadcasterServiceIntent;
    private GLSurfaceView mGLView;
    private ILiveVideoBroadcaster mLiveVideoBroadcaster;
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LiveVideoBroadcaster.LocalBinder binder = (LiveVideoBroadcaster.LocalBinder) service;
            if (mLiveVideoBroadcaster == null) {
                mLiveVideoBroadcaster = binder.getService();
                mLiveVideoBroadcaster.init(LiveVideoBroadcasterActivity.this, mGLView);
                mLiveVideoBroadcaster.setAdaptiveStreaming(true);
            }
            mLiveVideoBroadcaster.openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mLiveVideoBroadcaster = null;
        }
    };
    private Button mBroadcastControlButton;

    public static String getDurationString(int seconds) {

        if (seconds < 0 || seconds > 2000000)//there is an codec problem and duration is not set correctly,so display meaningfull string
            seconds = 0;
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        if (hours == 0)
            return twoDigitString(minutes) + " : " + twoDigitString(seconds);
        else
            return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    public static String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //binding on resume not to having leaked service connection
        mLiveVideoBroadcasterServiceIntent = new Intent(this, LiveVideoBroadcaster.class);
        //this makes service do its job until done
        startService(mLiveVideoBroadcasterServiceIntent);

        setContentView(R.layout.activity_live_video_broadcaster);

        mStreamNameEditText = findViewById(R.id.stream_name_edit_text);

        mRootView = findViewById(R.id.root_layout);

        mBroadcastControlButton = findViewById(R.id.toggle_broadcasting);

        // Configure the GLSurfaceView.  This will start the Renderer thread, with an
        // appropriate EGL activity.
        mGLView = findViewById(R.id.cameraPreview_surfaceView);
        if (mGLView != null) {
            mGLView.setEGLContextClientVersion(2);     // select GLES 2.0
        }
    }

    public void changeCamera(View v) {
        if (mLiveVideoBroadcaster != null) {
            mLiveVideoBroadcaster.changeCamera();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //this lets activity bind
        bindService(mLiveVideoBroadcasterServiceIntent, mConnection, 0);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LiveVideoBroadcaster.PERMISSIONS_REQUEST) {
            if (mLiveVideoBroadcaster.isPermissionGranted()) {
                mLiveVideoBroadcaster.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.RECORD_AUDIO)) {
                    mLiveVideoBroadcaster.requestPermission();
                } else {
                    new AlertDialog.Builder(LiveVideoBroadcasterActivity.this)
                            .setTitle(R.string.permission)
                            .setMessage(getString(R.string.app_doesnot_work_without_permissions))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    try {
                                        //Open the specific App Info page:
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                        startActivity(intent);

                                    } catch (ActivityNotFoundException e) {
                                        //e.printStackTrace();

                                        //Open the generic Apps page:
                                        Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                        startActivity(intent);

                                    }
                                }
                            })
                            .show();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");

        if (mLiveVideoBroadcaster != null)
            mLiveVideoBroadcaster.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLiveVideoBroadcaster.setDisplayOrientation();
        }

    }

    public void updateResolution() {
        ArrayList<Resolution> sizeList = mLiveVideoBroadcaster.getPreviewSizeList();


        if (sizeList == null || sizeList.size() <= 0) {
            Snackbar.make(mRootView, "No resolution available", Snackbar.LENGTH_LONG).show();
        } else {
            Resolution highestResolution = sizeList.get(0);
            for (Resolution i : sizeList) {
                if (highestResolution.getArea() < i.getArea()) {
                    highestResolution = i;
                }

            }
            setResolution(highestResolution);
//            mCameraResolutionsDialog = new CameraResolutionsFragment();
//
//            mCameraResolutionsDialog.setCameraResolutions(sizeList, mLiveVideoBroadcaster.getPreviewSize());
//            mCameraResolutionsDialog.show(ft, "resolutiton_dialog");
        }

    }

    public void toggleBroadcasting(View v) {
        if (!mIsRecording) {
            if (mLiveVideoBroadcaster != null) {
                if (!mLiveVideoBroadcaster.isConnected()) {
                    String streamName = mStreamNameEditText.getText().toString();
                    updateResolution();

                    new AsyncTask<String, String, Boolean>() {
                        ContentLoadingProgressBar
                                progressBar;

                        @Override
                        protected void onPreExecute() {
                            progressBar = new ContentLoadingProgressBar(LiveVideoBroadcasterActivity.this);
                            progressBar.show();
                        }

                        @Override
                        protected Boolean doInBackground(String... url) {
                            return mLiveVideoBroadcaster.startBroadcasting(url[0]);

                        }

                        @Override
                        protected void onPostExecute(Boolean result) {
                            progressBar.hide();
                            mIsRecording = result;
                            if (result) {
                                mBroadcastControlButton.setText(R.string.stop_broadcasting);
                            } else {
                                Snackbar.make(mRootView, "Failed to start. Please check server url and security credentials.", Snackbar.LENGTH_LONG).show();

                                triggerStopRecording();
                            }
                        }
                    }.execute(RTMP_BASE_URL + streamName);
                } else {
                    Snackbar.make(mRootView, R.string.streaming_not_finished, Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(mRootView, R.string.oopps_shouldnt_happen, Snackbar.LENGTH_LONG).show();
            }
        } else {
            triggerStopRecording();
        }

    }

//    public void toggleMute(View v) {
//        if (v instanceof ImageView) {
//            ImageView iv = (ImageView) v;
//            mIsMuted = !mIsMuted;
//            mLiveVideoBroadcaster.setAudioEnable(!mIsMuted);
//            iv.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
//                    mIsMuted ? R.drawable.animated_play_pause : R.drawable.abc_vector_test, null));
//        }
//    }

    public void triggerStopRecording() {
        if (mIsRecording) {
            mBroadcastControlButton.setText("Start Broadcasting");

            mLiveVideoBroadcaster.stopBroadcasting();
        }

        mIsRecording = false;
    }

    public void setResolution(Resolution size) {
        Snackbar.make(mRootView, "Set Resolution: " + size.width + " - " + size.height, Snackbar.LENGTH_LONG).show();
        mLiveVideoBroadcaster.setResolution(size);
    }
}
