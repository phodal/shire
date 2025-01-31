package com.phodal.shirelang.compiler.execute.command

import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.shire.RevisionProvider
import com.phodal.shirecore.utils.markdown.CodeFence

class CommitShireCommand(val myProject: Project, val commitMsg: String) : ShireCommand {
    override suspend fun doExecute(): String {
        RevisionProvider.provide()?.let {
            return it.commitCode(myProject, commitMsg)
        } ?: return "No revision provider found"
    }
}
