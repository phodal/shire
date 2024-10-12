package com.phodal.shire.openrewrite

import com.intellij.execution.RunManager
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.configurations.RunProfile
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.shire.FileRunService

class OpenRewriteFileRunService: FileRunService {
    override fun isApplicable(project: Project, file: VirtualFile): Boolean = true

    override fun runConfigurationClass(project: Project): Class<out RunProfile>? = null

    override fun runFile(project: Project, virtualFile: VirtualFile, psiElement: PsiElement?): String? {
        val psiFile = psiElement?.containingFile ?: return null
        val configurationSettings = ConfigurationContext(psiFile)
            .configurationsFromContext
            ?.firstOrNull()
            ?.configurationSettings

        val runManager: RunManager = RunManager.getInstance(project)
        runManager.selectedConfiguration = configurationSettings

        return configurationSettings?.name
    }
}