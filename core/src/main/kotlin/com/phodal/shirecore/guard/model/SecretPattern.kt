package com.phodal.shirecore.guard.model

import com.intellij.openapi.diagnostic.logger
import java.util.regex.Matcher

data class SecretPattern(
    var name: String = "",
    var regex: String = "",
    var confidence: String = "",
) {
    private val regexPattern: Regex?
        get() = try {
            Regex(regex)
        } catch (e: Exception) {
            logger<SecretPattern>().error("Invalid regex pattern: $regex, name: $name")
            null
        }

    fun matches(text: String): Boolean {
        return regexPattern?.containsMatchIn(text) ?: false
    }

    fun mask(text: String): String {
        if (regexPattern?.pattern.isNullOrBlank()) {
            return text
        }

        return regexPattern?.replace(text, "****") ?: text
    }
}