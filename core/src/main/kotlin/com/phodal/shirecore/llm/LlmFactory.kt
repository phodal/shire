package com.phodal.shirecore.llm

import com.intellij.openapi.project.Project
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.provider.LlmProvider

class LlmFactory {
    companion object {
        fun obtain(project: Project): LlmProvider? {
            val providers = LlmProvider.EP_NAME.extensions.filter { it.isApplicable(project) }
            if (providers.isEmpty()) {
                ShirelangNotifications.notify(project, "No LLM provider found")
                return null
            }

            return providers.first()
        }
    }
}