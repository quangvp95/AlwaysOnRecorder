package com.example.alwaysonrecorder.`object`

object Settings {

    val ALLOWED_RECORDING_LENGTH_MINUTES = 1..120
    val ALLOWED_DELETION_TIME_HOURS = 1..48

    // State
    var recordingEnabled: Boolean = true
        get() = field
    var recordingDurationMillis: Long = 1000 * 10 //20 * 60 * 1000
        get() = field
        private set
    var deletionSpanMillis: Long = 1000 * 40 //48 * 60 * 60 * 1000
        get() = field
        private set

    /**
     * @throws OutOfRangeException - if minutes is not within allowed range
     */
    fun setRecordingTimeMinutes(minutes: Int) {
        if (!(minutes within ALLOWED_RECORDING_LENGTH_MINUTES))
            throw OutOfRangeException()

        recordingDurationMillis = (minutes * 60 * 1000).toLong()
    }

    /**
     * @throws OutOfRangeException - if hours is not within allowed range
     */
    fun setDeletionTimeHours(hours: Int) {
        if (!(hours within ALLOWED_DELETION_TIME_HOURS))
            throw OutOfRangeException()

        deletionSpanMillis = (hours * 60 * 60 * 1000).toLong()
    }

    fun recordingTimeMinutes() = (recordingDurationMillis / 1000 / 60).toInt()
    fun deletionTimeHours() = (deletionSpanMillis / 1000 / 60 / 60).toInt()

    class OutOfRangeException : RuntimeException()
}

infix fun Int.within(range: IntRange): Boolean = range.contains(this)