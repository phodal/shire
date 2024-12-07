package com.phodal.shirecore.ui

import com.intellij.ide.scratch.ScratchRootType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.vfs.readText
import com.intellij.psi.PsiManager
import com.phodal.shirecore.provider.shire.FileRunService

class ShireRunCodeAction : DumbAwareAction() {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT

    override fun update(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(com.intellij.openapi.actionSystem.PlatformDataKeys.EDITOR) ?: return
        val document = editor.document
        val file = FileDocumentManager.getInstance().getFile(document) ?: return

        val language = PsiManager.getInstance(project).findFile(file) ?: return

        e.presentation.isEnabled = FileRunService.provider(project, file) != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(com.intellij.openapi.actionSystem.PlatformDataKeys.EDITOR) ?: return
        val project = e.project ?: return

        val document = editor.document
        val file = FileDocumentManager.getInstance().getFile(document) ?: return
        val psiFile = PsiManager.getInstance(project).findFile(file)
            ?: return

        val scratchFile = ScratchRootType.getInstance()
            .createScratchFile(project, file.name, psiFile.language, file.readText())
            ?: return

        FileRunService.provider(project, file)?.runFile(
            project,
            scratchFile,
            psiFile,
        )
    }
}
