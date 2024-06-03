package com.phodal.shire.llm

import com.intellij.openapi.project.Project
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockProvider : LlmProvider {
    override fun isApplicable(project: Project): Boolean {
        return false
    }

    override fun text(promptText: String): String {
        return "mock"
    }

    override fun stream(promptText: String, systemPrompt: String, keepHistory: Boolean): Flow<String> {
        return flowOf("mock")
    }

    override fun clearMessage() {

    }
}
