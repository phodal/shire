package com.phodal.shirelang.compiler.execute.command

import com.intellij.openapi.project.Project
import com.phodal.shirelang.compiler.execute.command.search.RipgrepSearcher

class RipgrepSearchShireCommand(
    val myProject: Project, private val scope: String, val text: String?,
) : ShireCommand {
    override fun isApplicable(): Boolean {
        return RipgrepSearcher.findRipgrepBinary() != null
    }

    override suspend fun doExecute(): String? {
        val searchDirectory = myProject.baseDir!!.path
        return RipgrepSearcher.searchFiles(myProject, searchDirectory, text ?: scope, null).get()
    }
}