package com.phodal.shirecore.guard.model

import com.charleskorn.kaml.Yaml

class SecretPatternsManager {
    private val defaultPiiSecrets = "/secrets/pii-stable.yml"
    private var patterns: List<SecretPatternDetail> = load()

    fun load(): List<SecretPatternDetail> {
        val url = javaClass.getResource(defaultPiiSecrets)!!
        val content = url.readText()

        val patterns = Yaml.default.decodeFromString(SecretPatterns.serializer(), content)
        return patterns.patterns.map {
            it.pattern
        }
    }

}