package com.phodal.shirecore.guard

/**
 * GuardScanner is an interface for scanning user input for security vulnerabilities.
 */
interface GuardScanner {
    fun scan(prompt: String): ScanResult
}

interface Replacer {
    fun replace(prompt: String): String
}

interface LocalScanner : GuardScanner

abstract class EntityRecognizer {
    abstract fun load()

    abstract fun analyze(text: String, entities: List<String>): List<ScanResult>
}
