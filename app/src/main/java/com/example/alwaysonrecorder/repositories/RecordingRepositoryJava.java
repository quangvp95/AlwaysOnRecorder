package com.example.alwaysonrecorder.repositories;


import android.util.Log;

import com.example.alwaysonrecorder.service.recording.backgroundtask.RecorderJava;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RecordingRepositoryJava {
    private final File rootDirectory;

    public RecordingRepositoryJava(File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public String fileName() {
        String dateString = new SimpleDateFormat(
                "dd-M-yyyy hh:mm:ss",
                Locale.getDefault()
        ).format(new Date());

        return rootDirectory.getAbsolutePath() + "/" + dateString + ".mp4";//"${rootDirectory.absolutePath}/$dateString.mp4";
    }

    public void deleteFilesOlderThan(long millis) {
        List<File> list = recordings();
        for (File it : list) {
            if ((it.lastModified() + millis) < System.currentTimeMillis()
//                    && !it.equals(Player.INSTANCE.getMountedFile())
            ) {
                boolean delete = it.delete();
                Log.d("QuangNHe", "Delete old file " + it.getName() + ": " + delete);
            }
        }
    }

    public List<File> recordings() {
        List<File> result = new ArrayList<>();
        if (rootDirectory.listFiles() == null)
            return result;

        for (File i : Objects.requireNonNull(rootDirectory.listFiles())) {
            if (!i.getAbsolutePath().equals(RecorderJava.currentRecordingPath))
                result.add(i);
        }
        return result;
    }
}