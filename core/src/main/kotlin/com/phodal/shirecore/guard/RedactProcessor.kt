package com.phodal.shirecore.guard

import com.phodal.shirecore.guard.scanner.SecretPatternsScanner

object RedactProcessor {
    fun redact(lastResult: Any): Any {
        if (lastResult is String) {
            return SecretPatternsScanner().mask(lastResult)
        }

        return lastResult
    }
}
