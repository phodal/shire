package com.phodal.shirelang.compiler.execute.command

import com.intellij.openapi.project.Project
import com.phodal.shirelang.compiler.execute.command.search.RipgrepSearcher
import com.phodal.shirelang.completion.dataprovider.BuiltinCommand

class RipgrepSearchShireCommand(
    val myProject: Project, private val scope: String, val text: String?,
) : ShireCommand {
    override val commandName = BuiltinCommand.RIPGREP_SEARCH

    override fun isApplicable(): Boolean {
        return RipgrepSearcher.findRipgrepBinary() != null
    }

    override suspend fun doExecute(): String? {
        val searchDirectory = myProject.baseDir!!.path
        return RipgrepSearcher.searchFiles(myProject, searchDirectory, text ?: scope, null).get()
    }
}