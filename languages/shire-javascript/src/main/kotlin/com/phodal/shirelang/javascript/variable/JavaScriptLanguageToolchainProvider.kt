package com.phodal.shirelang.javascript.variable

import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.context.LanguageToolchainProvider
import com.phodal.shirecore.provider.context.ToolchainContextItem
import com.phodal.shirecore.provider.context.ToolchainPrepareContext
import com.phodal.shirelang.javascript.util.LanguageApplicableUtil

class JavaScriptLanguageToolchainProvider : LanguageToolchainProvider {
    override fun isApplicable(project: Project, creationContext: ToolchainPrepareContext): Boolean {
        return LanguageApplicableUtil.isWebChatCreationContextSupported(creationContext.sourceFile)
    }

    override suspend fun collect(project: Project, creationContext: ToolchainPrepareContext): List<ToolchainContextItem> {
        val preferType = if (LanguageApplicableUtil.isPreferTypeScript(creationContext)) {
            "TypeScript"
        } else {
            "JavaScript"
        }

        return ToolchainContextItem(
            JavaScriptLanguageToolchainProvider::class,
            "Prefer $preferType language if the used language and toolset are not defined below or in the user messages"
        ).let { listOf(it) }
    }
}
