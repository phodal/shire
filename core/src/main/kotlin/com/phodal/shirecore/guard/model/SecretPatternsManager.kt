package com.phodal.shirecore.guard.model

import com.charleskorn.kaml.Yaml
import com.intellij.openapi.components.Service
import com.phodal.shirecore.guard.LocalScanner
import com.phodal.shirecore.guard.ScanResult
import java.net.URL

@Service(Service.Level.APP)
class SecretPatternsManager: LocalScanner {
    private val defaultPiiSecrets = "/secrets/default.shireSecretPattern.yml"
    private var patterns: List<SecretPattern>

    init {
        patterns = initPatterns()
    }

    fun getPatterns(): List<SecretPattern> {
        return patterns
    }

    private fun initPatterns(): List<SecretPattern> {
        val file: URL = javaClass.getResource(defaultPiiSecrets)!!
        val content = file.readText()

        val patterns = Yaml.default.decodeFromString(SecretPatterns.serializer(), content)
        return patterns.patterns.map {
            it.pattern
        }
    }

    fun addPattern(pattern: SecretPattern) {
        patterns = patterns + pattern
    }

    fun removePattern(pattern: SecretPattern) {
        patterns = patterns - pattern
    }

    fun mask(text: String): String {
        var newText = text
        patterns.forEach {
            newText = it.mask(newText)
        }

        return newText
    }

    override fun scan(prompt: String): ScanResult {
        val result = ScanResult()
        patterns.forEach {
            if (it.matches(prompt)) {
                result.isPassed = false
            }
        }

        return result
    }
}
