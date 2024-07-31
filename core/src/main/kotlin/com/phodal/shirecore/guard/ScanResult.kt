package com.phodal.shirecore.guard

/**
 * ScanResult is a data class that encapsulates the result of a scan.
 */
data class ScanResult(
    val isPassed: Boolean,
    val modifiedInput: String? = null,
    val message: String? = null
)