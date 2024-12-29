package com.phodal.shirelang.compiler.execute.command

import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.function.ToolchainFunctionProvider

class DatabaseShireCommand(val myProject: Project, private val prop: String, private val codeContent: String) :
    ShireCommand {

    override suspend fun doExecute(): String {
        val result = ToolchainFunctionProvider.lookup(myProject, "database")
            ?.execute(myProject, prop, listOf(codeContent), emptyMap())

        return result.toString()
    }
}
