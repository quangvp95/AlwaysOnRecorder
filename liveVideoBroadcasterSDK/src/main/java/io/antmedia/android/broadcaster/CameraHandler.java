package io.antmedia.android.broadcaster;

/*
 * Created by faraklit on 17.02.2016.
 */

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Handles camera operation requests from other threads.  Necessary because the Camera
 * must only be accessed from one thread.
 * <p>
 * The object is created on the UI thread, and all handlers run there.  Messages are
 * sent from other threads, using sendMessage().
 */
public class CameraHandler extends Handler {
    public static final int MSG_SET_SURFACE_TEXTURE = 0;
    private static final String TAG = CameraHandler.class.getSimpleName();
    // Weak reference to the Activity; only access this from the UI thread.
    private final WeakReference<ICameraViewer> cameraViewerWeakReference;


    public CameraHandler(ICameraViewer cameraViewer) {
        cameraViewerWeakReference = new WeakReference<>(cameraViewer);
    }

    /**
     * Drop the reference to the activity.  Useful as a paranoid measure to ensure that
     * attempts to access a stale Activity through a handler are caught.
     */
    public void invalidateHandler() {
        cameraViewerWeakReference.clear();
    }

    @Override  // runs on UI thread
    public void handleMessage(Message inputMessage) {
        int what = inputMessage.what;
        Log.d(TAG, "CameraHandler [" + this + "]: what=" + what);

        ICameraViewer cameraViewer = cameraViewerWeakReference.get();
        if (cameraViewer == null) {
            Log.w(TAG, "CameraHandler.handleMessage: cameraViewer is null");
            return;
        }

        if (what == MSG_SET_SURFACE_TEXTURE) {
            cameraViewer.handleSetSurfaceTexture((SurfaceTexture) inputMessage.obj);
        } else {
            throw new RuntimeException("unknown msg " + what);
        }
    }

    public interface ICameraViewer {

        void handleSetSurfaceTexture(SurfaceTexture st);
    }
}
