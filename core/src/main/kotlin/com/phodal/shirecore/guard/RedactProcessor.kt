package com.phodal.shirecore.guard

import com.phodal.shirecore.guard.model.SecretPatternsManager

object RedactProcessor {
    fun redact(lastResult: Any): Any {
        if (lastResult is String) {
            return SecretPatternsManager().mask(lastResult)
        }

        return lastResult
    }
}
