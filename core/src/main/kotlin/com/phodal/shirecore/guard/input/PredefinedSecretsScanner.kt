package com.phodal.shirecore.guard.input

import com.intellij.openapi.components.Service
import com.phodal.shirecore.guard.LocalScanner
import com.phodal.shirecore.guard.ScanResult
import com.phodal.shirecore.guard.secret.GitHubTokenCustomDetector
import com.phodal.shirecore.guard.secret.JWTBase64Detector
import com.phodal.shirecore.guard.secret.OpenAIApiKeyDetector
import com.phodal.shirecore.guard.secret.RegexBasedDetector

@Service(Service.Level.PROJECT)
class PredefinedSecretsScanner : LocalScanner {
    /**
     * Load from resources
     */
    private val detectors: List<RegexBasedDetector> = listOf(
        GitHubTokenCustomDetector(),
        JWTBase64Detector(),
        OpenAIApiKeyDetector()
    )

    override fun scan(prompt: String): ScanResult {
        for (detector in detectors) {
            for (pattern in detector.denylist) {
                val matcher = pattern.matcher(prompt)
                if (matcher.find()) {
                    return ScanResult(
                        isPassed = false,
                        message = "Detected ${detector.description} in the input."
                    )
                }
            }
        }

        return ScanResult(isPassed = true)
    }
}