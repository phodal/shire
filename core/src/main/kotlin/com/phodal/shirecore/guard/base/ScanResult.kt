package com.phodal.shirecore.guard.base

/**
 * ScanResult is a data class that encapsulates the result of a scan.
 */
data class ScanResult(
    var isPassed: Boolean = false,
    val modifiedInput: String? = null,
    val message: String? = null
)