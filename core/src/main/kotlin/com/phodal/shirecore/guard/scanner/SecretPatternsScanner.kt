package com.phodal.shirecore.guard.scanner

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.psi.search.FilenameIndex
import com.phodal.shirecore.agent.schema.SECRET_PATTERN_EXTENSION
import com.phodal.shirecore.guard.base.LocalScanner
import com.phodal.shirecore.guard.base.ScanResult
import com.phodal.shirecore.guard.model.SecretPattern
import com.phodal.shirecore.guard.model.SecretPatterns
import org.yaml.snakeyaml.Yaml
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

        val patterns = Yaml().loadAs(content, SecretPatterns::class.java)
        return patterns.patterns.map {
            it.pattern
        }
    }

    private fun loadFromProject(project: Project): List<SecretPattern> {
        return FilenameIndex.getAllFilesByExt(project, SECRET_PATTERN_EXTENSION)
            .mapNotNull {
                val content = it.inputStream.reader().readText()
                try {
                    val loadAs: SecretPatterns = Yaml().loadAs(content, SecretPatterns::class.java)
                    loadAs
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
