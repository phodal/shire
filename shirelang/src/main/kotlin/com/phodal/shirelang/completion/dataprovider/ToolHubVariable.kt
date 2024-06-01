package com.phodal.shirelang.completion.dataprovider

import com.intellij.openapi.project.Project
import com.phodal.shirecore.agent.CustomAgent

/**
 * The tool hub provides a list of tools - agents and commands for the AI Agent to decide which one to call
 * For example, you prompt could be:
 * ```devin
 * Here is the tools you can use:
 * $agents
 * ```
 *
 * Or
 *
 * ```devin
 * Here is the tools you can use:
 * $commands
 * ```
 */
enum class ToolHubVariable(val hubName: String, val type: String, val description: String) {
    AGENTS("agents", CustomAgent::class.simpleName.toString(), "Shire all agent for AI Agent to call"),
    COMMANDS("commands", BuiltinCommand::class.simpleName.toString(), "Shire all commands for AI Agent to call"),
    ;

    companion object {
        fun all(): List<ToolHubVariable> {
            return entries
        }

        /**
         * @param variableId should be one of the [ToolHubVariable] name
         */
        fun lookup(myProject: Project, variableId: String?): List<String> {
            return when (variableId) {
                COMMANDS.hubName -> BuiltinCommand.all().map {
                    "- " + it.commandName + ". " + it.description
                }
                else -> emptyList()
            }
        }
    }
}
