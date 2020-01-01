package com.example.alwaysonrecorder.helper

import com.example.alwaysonrecorder.helper.TimestampHelper.padWithZero
import com.example.alwaysonrecorder.helper.TimestampHelper.toMinutesAndSeconds
import org.junit.Test

import org.junit.Assert.*

class TimestampHelperTest {

    @Test
    fun `test thirty minutes three seconds`() {
        val minutesAndSeconds = toMinutesAndSeconds(1_803_000)

        assertEquals(30, minutesAndSeconds.minutes)
        assertEquals(3, minutesAndSeconds.seconds)
    }

    @Test
    fun `test two seconds and then some`() {
        val minutesAndSeconds = toMinutesAndSeconds(2_025)

        assertEquals(0, minutesAndSeconds.minutes)
        assertEquals(2, minutesAndSeconds.seconds)
    }

    @Test
    fun `test one minute fiftythree seconds`() {
        val minutesAndSeconds = toMinutesAndSeconds(60_000 + 53_999)

        assertEquals(1, minutesAndSeconds.minutes)
        assertEquals(53, minutesAndSeconds.seconds)
    }

    @Test
    fun `test pad with zero small value`() {
        assertEquals("02", padWithZero(2))
        assertEquals("00", padWithZero(0))
        assertEquals("09", padWithZero(9))
    }

    @Test
    fun `test pad with zero big value`() {
        assertEquals("22", padWithZero(22))
        assertEquals("19", padWithZero(19))
        assertEquals("123", padWithZero(123))
    }
}