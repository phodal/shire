package com.phodal.shirecore.guard

import com.intellij.openapi.project.Project
import com.phodal.shirecore.guard.scanner.SecretPatternsScanner

object RedactProcessor {
    fun execute(project: Project, lastResult: Any): Any {
        if (lastResult is String) {
            return project.getService(SecretPatternsScanner::class.java).mask(lastResult)
        }

        return lastResult
    }
}
