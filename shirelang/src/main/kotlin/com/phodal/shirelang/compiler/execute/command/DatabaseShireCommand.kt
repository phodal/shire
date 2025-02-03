package com.phodal.shirelang.compiler.execute.command

import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.function.ToolchainFunctionProvider
import com.phodal.shirecore.utils.markdown.CodeFence
import com.phodal.shirelang.completion.dataprovider.BuiltinCommand

class DatabaseShireCommand(val myProject: Project, private val prop: String, private val codeContent: String?) :
    ShireCommand {
    override val commandName = BuiltinCommand.DATABASE

    override suspend fun doExecute(): String {
        val args = if (codeContent != null) {
            listOf(codeContent)
        } else {
            listOf()
        }

        val result = ToolchainFunctionProvider.lookup("DatabaseFunctionProvider")
            ?.execute(myProject, prop, args, emptyMap())

        return result?.toString() ?: "No database provider found"
    }
}
