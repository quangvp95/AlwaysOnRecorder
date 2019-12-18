package com.example.alwaysonrecorder.Events

/**
 * Triggers a request permission dialog that responds with a RequestPermissionsResponseEvent
 * @param permissions - List of permission strings to request for
 * @param callbackEvent - callback event triggered upon request completion
 */
class RequestPermissionsEvent(val permissions: List<String>, val requestCode: Int)

/**
 * Response event triggered by RequestPermissionsEvent
 * @param status - status of the permission request
 * @param requestCode - used to uniquely identify a permission request
 */
class RequestPermissionsResponseEvent(val status: String, val requestCode: Int)