package com.phodal.shire.uitest.provider

import com.intellij.aqua.runners.playwright.js.PlaywrightRunConfigurationProducer
import com.intellij.execution.RunManager
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfile
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.phodal.shirecore.provider.shire.FileRunService

class PlaywrightFileRunService : FileRunService {
    override fun isApplicable(project: Project, file: VirtualFile): Boolean {
        val isSpecFile = file.name.endsWith(".spec.ts") || file.name.endsWith(".spec.js")
        if (isSpecFile) {
            return true
        }

        val content = file.inputStream.bufferedReader().use { it.readText() }
        val hasPlaywright = content.contains("playwright")
        return hasPlaywright
    }

    override fun runConfigurationClass(project: Project): Class<out RunProfile>? {
//        return PlaywrightRunConfiguration::class.java
        return null
    }

    override fun createConfiguration(project: Project, virtualFile: VirtualFile): RunConfiguration? {
        val configurationSetting = runReadAction {
            val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return@runReadAction null
            val runManager = RunManager.getInstance(project)

            val configProducer = RunConfigurationProducer.getInstance(
                PlaywrightRunConfigurationProducer::class.java
            )

            val configurationType = configProducer.findOrCreateConfigurationFromContext(
                ConfigurationContext(psiFile)
            )?.configurationType?.javaClass ?: return@runReadAction null

            val configuration = runManager.createConfiguration("Playwright", configurationType)

            runManager.addConfiguration(configuration)
            configuration
        }

        return configurationSetting?.configuration
    }
}