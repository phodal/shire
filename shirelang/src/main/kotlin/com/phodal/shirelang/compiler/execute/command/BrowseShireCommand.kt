package com.phodal.shirelang.compiler.execute.command

import com.phodal.shirecore.agent.agenttool.browse.BrowseTool
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.project.Project
import com.phodal.shirelang.completion.dataprovider.BuiltinCommand

class BrowseShireCommand(val myProject: Project, private val prop: String,
) : ShireCommand {
    override val commandName = BuiltinCommand.BROWSE

    override suspend fun doExecute(): String? {
        var body: String? = null
        runInEdt {
            val parse = BrowseTool.parse(prop)
            body = parse.body
        }

        return body
    }
}

