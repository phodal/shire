package com.phodal.shirecore.guard.input

import com.phodal.shirecore.guard.GuardScanner
import com.phodal.shirecore.guard.ScanResult
import com.phodal.shirecore.guard.secret.GitHubTokenCustomDetector
import com.phodal.shirecore.guard.secret.JWTBase64Detector
import com.phodal.shirecore.guard.secret.OpenAIApiKeyDetector
import com.phodal.shirecore.guard.secret.RegexBasedDetector

class SecretsScanner : GuardScanner {
    private val detectors: List<RegexBasedDetector> = listOf(
        GitHubTokenCustomDetector(),
        JWTBase64Detector(),
        OpenAIApiKeyDetector()
    )

    override fun scan(prompt: String): ScanResult {
        // 遍历所有的检测器
        for (detector in detectors) {
            for (pattern in detector.denylist) {
                // 检查输入是否匹配检测器中的任何一个正则表达式模式
                val matcher = pattern.matcher(prompt)
                if (matcher.find()) {
                    return ScanResult(
                        isPassed = false,
                        message = "Detected ${detector.secretType} in the input."
                    )
                }
            }
        }
        // 如果没有检测到任何匹配，返回通过结果
        return ScanResult(isPassed = true)
    }
}