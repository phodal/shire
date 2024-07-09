package com.phodal.shirelang.git.provider

import com.intellij.ide.DataManager
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.vcs.commit.CommitWorkflowUi
import com.phodal.shirecore.template.VcsVariableActionDataContext

fun getCommitWorkflowUi(): CommitWorkflowUi? {
    VcsVariableActionDataContext.getData()?.dataContext?.let {
        val commitWorkflowUi = it.getData(VcsDataKeys.COMMIT_WORKFLOW_UI)
        return commitWorkflowUi as CommitWorkflowUi?
    }

    val dataContext = DataManager.getInstance().dataContextFromFocus.result
    val commitWorkflowUi = dataContext?.getData(VcsDataKeys.COMMIT_WORKFLOW_UI)
    return commitWorkflowUi
}