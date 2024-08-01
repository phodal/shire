package com.phodal.shirecore.guard

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

/**
 * GuardScanner is an interface for scanning user input for security vulnerabilities.
 */
interface GuardScanner {
    fun scan(prompt: String): ScanResult
}

interface LocalScanner : GuardScanner {

}

/**
 * A class representing an abstract PII entity recognizer.
 */
abstract class EntityRecognizer {
    abstract fun load()

    abstract fun analyze(text: String, entities: List<String>): List<ScanResult>
}
