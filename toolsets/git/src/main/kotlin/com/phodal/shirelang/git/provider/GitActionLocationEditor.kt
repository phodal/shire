package com.phodal.shirelang.git.provider

import com.intellij.ide.DataManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.openapi.vcs.ui.CommitMessage
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirecore.provider.context.ActionLocationEditor

class GitActionLocationEditor : ActionLocationEditor {
    override fun isApplicable(hole: ShireActionLocation): Boolean = hole == ShireActionLocation.COMMIT_MENU

    override fun resolve(project: Project, hole: ShireActionLocation): Editor? {
        val dataContext = DataManager.getInstance().dataContextFromFocus.result
        val commitMessageUi = dataContext.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL) as? CommitMessage ?: return null

        val editorField = commitMessageUi.editorField

        editorField.text = ""
        return editorField.editor
    }
}
