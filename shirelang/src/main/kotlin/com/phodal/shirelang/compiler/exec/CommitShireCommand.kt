package com.phodal.shirelang.compiler.exec

import com.intellij.openapi.project.Project
import com.phodal.shirelang.utils.Code

class CommitShireCommand(val myProject: Project, val code: String) : ShireCommand {
    override suspend fun doExecute(): String {
        val commitMsg = Code.parse(code).text

//        val changeListManager = ChangeListManager.getInstance(myProject)
//        changeListManager.changeLists.forEach {
//            val list: LocalChangeList = changeListManager.getChangeList(it.id) ?: return@forEach
//            GitUtil.doCommit(myProject, list, commitMsg)
        throw NotImplementedError("Not implemented")
//        }

//        return "Committing..."
    }
}
