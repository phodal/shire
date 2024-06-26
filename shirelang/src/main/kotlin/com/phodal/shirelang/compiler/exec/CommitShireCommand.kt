package com.phodal.shirelang.compiler.exec

import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.shire.RevisionProvider
import com.phodal.shirecore.markdown.Code

class CommitShireCommand(val myProject: Project, val code: String) : ShireCommand {
    override suspend fun doExecute(): String {
        val commitMsg = Code.parse(code).text
        RevisionProvider.provide()?.let {
            return it.commitCode(myProject, commitMsg)
        } ?: return "No revision provider found"
    }
}
