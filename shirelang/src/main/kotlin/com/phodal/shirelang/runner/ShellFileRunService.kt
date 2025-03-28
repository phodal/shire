package com.phodal.shirelang.runner

import com.intellij.execution.RunManager
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfile
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.sh.psi.ShFile
import com.intellij.sh.run.ShConfigurationType
import com.intellij.sh.run.ShRunConfiguration
import com.phodal.shirecore.provider.shire.FileRunService

class ShellFileRunService : FileRunService {
    override fun isApplicable(project: Project, file: VirtualFile): Boolean {
        return file.extension == "sh" || file.extension == "bash"
    }

    override fun runConfigurationClass(project: Project): Class<out RunProfile> = ShRunConfiguration::class.java

    override fun createConfiguration(project: Project, virtualFile: VirtualFile): RunConfiguration? {
        val configurationSetting = runReadAction {
            val psiFile = PsiManager.getInstance(project).findFile(virtualFile) as? ShFile ?: return@runReadAction null
            RunManager.getInstance(project)
                .createConfiguration(psiFile.name, ShConfigurationType.getInstance())
        } ?: return null

        val configuration = configurationSetting.configuration as ShRunConfiguration
        configuration.scriptPath = virtualFile.path
        return configurationSetting.configuration
    }
}