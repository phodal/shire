package com.phodal.shirelang.provider

import com.intellij.openapi.extensions.ExtensionPointName
import com.phodal.shirelang.agenttool.AgentToolContext
import com.phodal.shirelang.agenttool.AgentToolResult

interface AgentTool {
    val name: String
    val description: String
    fun execute(context: AgentToolContext): AgentToolResult

    // extension point
    companion object {
        val EP_NAME = ExtensionPointName<AgentTool>("com.phodal.shireAgentTool")

        fun allTools(): List<AgentTool> {
            return EP_NAME.extensionList
        }
    }
}
