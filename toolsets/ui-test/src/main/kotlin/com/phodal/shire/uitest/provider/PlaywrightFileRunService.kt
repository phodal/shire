package com.phodal.shire.uitest.provider

import com.intellij.aqua.runners.playwright.js.PlaywrightRunConfiguration
import com.intellij.execution.configurations.RunProfile
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
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
        return PlaywrightRunConfiguration::class.java
    }
}