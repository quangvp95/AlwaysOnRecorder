package com.example.alwaysonrecorder.events

import java.io.File

/**
 * Triggered when recordings have been added or removed
 * @param recordings - all of the currently active recordings
 */
class RecordingsUpdatedEvent(val recordings: List<File>)