package com.phodal.shirelang.javascript.codeedit

import com.intellij.execution.RunManager
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfile
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.jetbrains.nodejs.run.NodeJsRunConfiguration
import com.jetbrains.nodejs.run.NodeJsRunConfigurationType
import com.phodal.shirecore.provider.shire.FileRunService

class JSFileRunService : FileRunService {
    override fun runConfigurationClass(project: Project): Class<out RunProfile>? {
        return NodeJsRunConfiguration::class.java
    }

    override fun createConfiguration(project: Project, virtualFile: VirtualFile): RunConfiguration? {
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile) as? JSFile ?: return null
        val configurationSetting = RunManager.getInstance(project)
            .createConfiguration(psiFile.name, NodeJsRunConfigurationType.getInstance())

        val configuration = configurationSetting.configuration as NodeJsRunConfiguration
        configuration.mainScriptFilePath = virtualFile.path
        return configurationSetting.configuration
    }
}
