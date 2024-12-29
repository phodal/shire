package com.phodal.shirelang.compiler.execute.command

import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.function.ToolchainFunctionProvider
import com.phodal.shirecore.utils.markdown.CodeFence

class DatabaseShireCommand(val myProject: Project, private val prop: String, private val codeContent: String?) :
    ShireCommand {

    override suspend fun doExecute(): String {
        val args = if (codeContent != null) {
            val code = CodeFence.parse(codeContent).text
            listOf(code)
        } else {
            listOf()
        }

        val result = ToolchainFunctionProvider.lookup("DatabaseFunctionProvider")
            ?.execute(myProject, prop, args, emptyMap())

        return result?.toString() ?: "No database provider found"
    }
}
