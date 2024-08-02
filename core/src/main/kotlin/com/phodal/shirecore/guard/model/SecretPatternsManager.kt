package com.phodal.shirecore.guard.model

import com.charleskorn.kaml.Yaml
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile

class SecretPatternsManager {
    private val defaultPiiSecrets = "/secrets/pii-stable.yml"
    private var patterns: List<SecretPatternDetail> = initPatterns()

    private fun initPatterns(): List<SecretPatternDetail> {
        val file: VirtualFile = VfsUtil.findFileByURL(javaClass.getResource(defaultPiiSecrets)!!)!!
        val content = file.inputStream.reader().readText()

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
            text.contains(it.regex.toRegex())
        }
    }
}
