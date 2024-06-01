package com.phodal.shirelang.compiler

import com.phodal.shirecore.agenttool.browse.BrowseTool
import com.phodal.shirelang.compiler.exec.ShireCommand
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.project.Project

class BrowseShireCommand(val myProject: Project, private val prop: String) : ShireCommand {
    override suspend fun doExecute(): String? {
        var body: String? = null
        runInEdt {
            val parse = BrowseTool.parse(prop)
            body = parse.body
        }

        return body
    }
}

