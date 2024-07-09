package com.phodal.shirelang.git.provider

import com.intellij.ide.DataManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.openapi.vcs.ui.CommitMessage
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirecore.provider.context.ActionLocationEditor

class GitActionLocationEditor : ActionLocationEditor {
    private var commitUi: CommitMessage? = null

    override fun isApplicable(hole: ShireActionLocation): Boolean  {
        val commitMessage = getCommitUi(hole)
        if (commitMessage != null) {
            commitUi = commitMessage
        }

        return hole == ShireActionLocation.COMMIT_MENU && commitMessage != null
    }

    override fun resolve(project: Project, hole: ShireActionLocation): Editor? {
        val commitMessageUi = commitUi ?: getCommitUi(hole) ?: return null
        val editorField = commitMessageUi.editorField

        editorField.text = ""
        return editorField.editor
    }

    private fun getCommitUi(hole: ShireActionLocation): CommitMessage? {
        if (hole != ShireActionLocation.COMMIT_MENU) return null

        val dataContext = DataManager.getInstance().dataContextFromFocus.result
        val commitWorkflowUi = dataContext?.getData(VcsDataKeys.COMMIT_WORKFLOW_UI)

        val commitMessageUi = commitWorkflowUi?.commitMessageUi as? CommitMessage

        if (commitMessageUi == null) {
            logger<GitActionLocationEditor>().error("Failed to get commit message UI")
            return null
        }

        return commitWorkflowUi as CommitMessage
    }
}
