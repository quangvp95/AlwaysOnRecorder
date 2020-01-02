package com.example.alwaysonrecorder.helper

import kotlin.math.roundToInt

object TimestampHelper {
    data class MinutesAndSeconds(val minutes: Int, val seconds: Int) {
        init {
            assert(seconds < 60)
        }
    }

    fun toMinutesAndSeconds(millis: Int): MinutesAndSeconds {
        val minutes = millis / 60 / 1000
        val seconds = ((millis - minutes * 60 * 1000) / 1000F).roundToInt()

        return MinutesAndSeconds(minutes, seconds)
    }

    fun padWithZero(value: Int): String {
        return when (value) {
            in 0..9 -> "0$value"
            else -> "$value"
        }
    }
}