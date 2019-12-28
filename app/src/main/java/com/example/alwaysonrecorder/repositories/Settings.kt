package com.example.alwaysonrecorder.repositories

object Settings {

    val ALLOWED_RECORDING_LENGTH_MINUTES = 1..120
    val ALLOWED_DELETION_TIME_HOURS = 1..48

    var recordingTime: Long = 20 * 60 * 1000
    var deletionTime: Long = 48 * 60 * 60 * 1000

    /**
     * @throws OutOfRangeException - if minutes is not within allowed range
     */
    fun setRecordingTimeMinutes(minutes: Int) {
        if (!(minutes within ALLOWED_RECORDING_LENGTH_MINUTES))
            throw OutOfRangeException()

        recordingTime = (minutes * 60 * 1000).toLong()
    }

    /**
     * @throws OutOfRangeException - if hours is not within allowed range
     */
    fun setDeletionTimeHours(hours: Int) {
        if (!(hours within ALLOWED_DELETION_TIME_HOURS))
            throw OutOfRangeException()

        deletionTime = (hours * 60 * 60 * 1000).toLong()
    }

    fun recordingTimeMinutes() = (recordingTime / 1000 / 60).toInt()
    fun deletionTimeHours() = (deletionTime / 1000 / 60 / 60).toInt()

    class OutOfRangeException : RuntimeException()
}

infix fun Int.within(range: IntRange): Boolean = range.contains(this)