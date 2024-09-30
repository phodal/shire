package com.phodal.shirelang.provider

import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.context.LanguageToolchainProvider
import com.phodal.shirecore.provider.context.ToolchainContextItem
import com.phodal.shirecore.provider.context.ToolchainPrepareContext
import com.phodal.shirelang.ShireLanguage

class ShireLanguageToolchainProvider : LanguageToolchainProvider {
    override fun isApplicable(project: Project, context: ToolchainPrepareContext): Boolean {
        return context.element?.language is ShireLanguage
    }

    override suspend fun collect(project: Project, context: ToolchainPrepareContext): List<ToolchainContextItem> {
        val text = "Shire is a DSL for building AI Agent for IDEs."
        return listOf(ToolchainContextItem(ShireLanguageToolchainProvider::class, text))
    }
}
