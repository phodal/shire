package com.phodal.shirelang.compiler

import com.phodal.shirelang.agenttool.browse.BrowseTool
import com.phodal.shirelang.compiler.exec.InsCommand
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.project.Project

class BrowseInsCommand(val myProject: Project, private val prop: String) : InsCommand {
    override suspend fun execute(): String? {
        var body: String? = null
        runInEdt {
            val parse = BrowseTool.parse(prop)
            body = parse.body
        }

        return body
    }
}

