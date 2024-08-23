package com.phodal.shirelang.kotlin.variable

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.context.LanguageToolchainProvider
import com.phodal.shirecore.provider.context.ToolchainContextItem
import com.phodal.shirecore.provider.context.ToolchainPrepareContext
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.base.projectStructure.languageVersionSettings

class KotlinLanguageToolchainProvider : LanguageToolchainProvider {
    override fun isApplicable(project: Project, context: ToolchainPrepareContext): Boolean {
        return context.sourceFile?.language is KotlinLanguage
    }

    override suspend fun collect(project: Project, context: ToolchainPrepareContext): List<ToolchainContextItem> {
        val languageVersionSettings = runReadAction {
            project.languageVersionSettings
        }

        val languageVersion = languageVersionSettings.languageVersion.versionString
        return listOf(
            ToolchainContextItem(
                KotlinLanguageToolchainProvider::class,
                "- Kotlin API version: $languageVersion"
            )
        )
    }
}
