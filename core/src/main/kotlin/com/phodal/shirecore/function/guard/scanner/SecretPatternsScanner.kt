package com.phodal.shirecore.function.guard.scanner

import com.charleskorn.kaml.Yaml
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.psi.search.FilenameIndex
import com.phodal.shirecore.schema.SECRET_PATTERN_EXTENSION
import com.phodal.shirecore.function.guard.base.LocalScanner
import com.phodal.shirecore.function.guard.base.ScanResult
import com.phodal.shirecore.function.guard.model.SecretPattern
import com.phodal.shirecore.function.guard.model.SecretPatterns
import java.net.URL

@Service(Service.Level.PROJECT)
class SecretPatternsScanner(val project: Project) : LocalScanner {
    private val defaultPiiSecrets = "/secrets/default.shireSecretPattern.yml"
    private var patterns: List<SecretPattern>

    init {
        patterns = initPatterns() + loadFromProject(project)
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

    private fun loadFromProject(project: Project): List<SecretPattern> {
        return FilenameIndex.getAllFilesByExt(project, SECRET_PATTERN_EXTENSION)
            .mapNotNull {
                val content = it.inputStream.reader().readText()
                try {
                    Yaml.default.decodeFromString(SecretPatterns.serializer(), content)
                } catch (e: Exception) {
                    logger<SecretPatternsScanner>().error("Failed to load custom agent configuration", e)
                    null
                }
            }.flatMap { secretPatterns ->
                secretPatterns.patterns.map {
                    it.pattern
                }
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
