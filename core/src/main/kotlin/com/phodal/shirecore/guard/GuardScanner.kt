package com.phodal.shirecore.guard

// Define a common interface for all scanners
interface GuardScanner {
    fun scan(input: String): ScanResult
}

// Define a ScanResult class to encapsulate the result of a scan
data class ScanResult(
    val isPassed: Boolean,
    val modifiedInput: String? = null,
    val message: String? = null
)
