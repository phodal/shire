package com.phodal.shirecore.guard.model

import com.charleskorn.kaml.Yaml
import java.net.URL

class SecretPatternsManager {
    private val defaultPiiSecrets = "/secrets/default.shireSecretPattern.yml"
    private var patterns: List<SecretPatternDetail> = initPatterns()

    private fun initPatterns(): List<SecretPatternDetail> {
        val file: URL = javaClass.getResource(defaultPiiSecrets)!!
        val content = file.readText()

        val patterns = Yaml.default.decodeFromString(SecretPatterns.serializer(), content)
        return patterns.patterns.map {
            it.pattern
        }
    }

    fun getPatterns(): List<SecretPatternDetail> {
        return patterns
    }

    fun addPattern(pattern: SecretPatternDetail) {
        patterns = patterns + pattern
    }

    fun removePattern(pattern: SecretPatternDetail) {
        patterns = patterns - pattern
    }

    fun evaluateSecrets(text: String): List<SecretPatternDetail> {
        return patterns.filter {
            text.contains(it.regex)
        }
    }
}
