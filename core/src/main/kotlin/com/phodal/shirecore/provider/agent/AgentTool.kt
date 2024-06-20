package com.phodal.shirecore.provider.agent

import com.intellij.openapi.extensions.ExtensionPointName
import com.phodal.shirecore.agenttool.AgentToolContext
import com.phodal.shirecore.agenttool.AgentToolResult

interface AgentTool {
    val name: String
    val description: String
    fun execute(context: AgentToolContext): AgentToolResult

    // extension point
    companion object {
        private val EP_NAME = ExtensionPointName<AgentTool>("com.phodal.shireAgentTool")

        fun allTools(): List<AgentTool> {
            return EP_NAME.extensionList
        }
    }
}
