package com.phodal.shirelang.completion.dataprovider

import com.phodal.shirecore.agent.CustomAgent

/**
 * The tool hub provides a list of tools - agents and commands for the AI Agents to decide which one to call
 * For example, you prompt could be:
 * ```shire
 * Here is the tools you can use:
 * $agents
 * ```
 *
 * Or
 *
 * ```shire
 * Here is the tools you can use:
 * $commands
 * ```
 */
enum class ToolHubVariable(val hubName: String, val type: String, val description: String) {
    AGENTS("agents", CustomAgent::class.simpleName.toString(), "Shire all agent for AI Agents to call"),
    COMMANDS("commands", BuiltinCommand::class.simpleName.toString(), "Shire all commands for AI Agents to call"),
    ;

    companion object {
        fun all(): List<ToolHubVariable> {
            return values().toList()
        }

        /**
         * @param variableId should be one of the [ToolHubVariable] name
         */
        fun lookup(variableId: String?): List<String> {
            return when (variableId) {
                COMMANDS.hubName -> ToolHubVariable.all().map {
                    "- " + it.hubName + ". " + it.description
                }
                else -> emptyList()
            }
        }
    }
}
