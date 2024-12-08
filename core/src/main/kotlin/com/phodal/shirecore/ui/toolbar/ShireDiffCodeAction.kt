package com.phodal.shirecore.ui.toolbar

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.testFramework.LightVirtualFile

class ShireDiffCodeAction : DumbAwareAction() {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT

    override fun update(e: AnActionEvent) {
        val project = e.project
        if (project == null) {
            e.presentation.isEnabled = false
            return
        }

        val editor = e.dataContext.getData(CommonDataKeys.EDITOR) ?: return
        val lightVirtualFile = FileDocumentManager.getInstance().getFile(editor.document) as? LightVirtualFile ?: return

        println(lightVirtualFile)
    }

    override fun actionPerformed(e: AnActionEvent) {
        TODO("Not yet implemented")
    }
}
