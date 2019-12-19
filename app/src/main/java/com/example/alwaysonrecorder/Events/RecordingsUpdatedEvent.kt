package com.example.alwaysonrecorder.events

import java.io.File

/**
 * Triggered when more recordings have been added
 * @param recordings - all of the currently active recordings
 */
class RecordingsUpdatedEvent(val recordings: List<File>)