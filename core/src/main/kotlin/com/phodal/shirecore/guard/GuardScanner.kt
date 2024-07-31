package com.phodal.shirecore.guard

/**
 * GuardScanner is an interface for scanning user input for security vulnerabilities.
 */
interface GuardScanner {
    fun scan(prompt: String): ScanResult
}

