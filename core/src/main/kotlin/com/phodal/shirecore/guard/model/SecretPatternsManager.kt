package com.phodal.shirecore.guard.model

import com.charleskorn.kaml.Yaml
import com.intellij.openapi.components.Service
import java.net.URL

@Service(Service.Level.APP)
class SecretPatternsManager {
    private val defaultPiiSecrets = "/secrets/default.shireSecretPattern.yml"
    private var patterns: List<SecretPatternDetail>

    init {
        patterns = initPatterns()
    }

    fun getPatterns(): List<SecretPatternDetail> {
        return patterns
    }

    private fun initPatterns(): List<SecretPatternDetail> {
        val file: URL = javaClass.getResource(defaultPiiSecrets)!!
        val content = file.readText()

        val patterns = Yaml.default.decodeFromString(SecretPatterns.serializer(), content)
        return patterns.patterns.map {
            it.pattern
        }
    }

    fun addPattern(pattern: SecretPatternDetail) {
        patterns = patterns + pattern
    }

    fun removePattern(pattern: SecretPatternDetail) {
        patterns = patterns - pattern
    }

    fun mask(text: String): String {
        var newText = text
        patterns.forEach {
            newText = it.mask(newText)
        }

        return newText
    }
}
