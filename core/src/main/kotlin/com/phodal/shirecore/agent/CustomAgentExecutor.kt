package com.phodal.shirecore.agent

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.flow.Flow

@Service(Service.Level.PROJECT)
class CustomAgentExecutor(val project: Project) {
    fun execute(promptText: String, agent: CustomAgent): Flow<String>? {
        return null
    }
}