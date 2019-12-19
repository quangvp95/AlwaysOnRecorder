package com.example.alwaysonrecorder.manager

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RecordingRepository(private val rootDirectory: File) {
    fun fileName(): String {
        val dateString = SimpleDateFormat(
            "dd-M-yyyy hh:mm:ss",
            Locale.getDefault()
        ).format(Date())

        return "${rootDirectory.absolutePath}/$dateString.mp4"
    }

    fun deleteFilesOlderThan(millis: Long) {
        recordings()
            ?.filter<File> { (it.lastModified() + millis) < System.currentTimeMillis() }
            ?.map { it.delete() }
    }

    fun recordings() = rootDirectory.listFiles()?.toList()?.sortedDescending()
}