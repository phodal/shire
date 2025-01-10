package com.phodal.shirelang.java.variable

import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.JavaSdkType
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiJavaFile
import com.phodal.shirecore.provider.context.LanguageToolchainProvider
import com.phodal.shirecore.provider.context.ToolchainContextItem
import com.phodal.shirecore.provider.context.ToolchainPrepareContext
import com.phodal.shirelang.java.toolchain.JvmLanguageDetector

class JavaLanguageToolchainProvider : LanguageToolchainProvider {
    override fun isApplicable(project: Project, context: ToolchainPrepareContext): Boolean {
        val sourceFile = context.sourceFile ?: return false
        if (sourceFile.language != JavaLanguage.INSTANCE) return false
        val projectSdk = ProjectRootManager.getInstance(project).projectSdk
        if (projectSdk?.sdkType !is JavaSdkType) return false

        val module: Module = try {
            ModuleUtilCore.findModuleForFile(sourceFile)
        } catch (e: Exception) {
            return false
        } ?: return false

        return ModuleRootManager.getInstance(module).sdk?.sdkType is JavaSdkType
    }

    override suspend fun collect(
        project: Project,
        context: ToolchainPrepareContext,
    ): List<ToolchainContextItem> {
        return collectJavaVersion(context, project)?.let { listOf(it) } ?: emptyList()
    }

    private fun collectJavaVersion(
        context: ToolchainPrepareContext,
        project: Project,
    ): ToolchainContextItem? {
        val psiFile = context.sourceFile as? PsiJavaFile ?: return null

        val languageLevel = JvmLanguageDetector.detectLanguageLevel(project, psiFile) ?: return null

        val prompt = "You are working on a project that uses Java SDK version $languageLevel."

        return ToolchainContextItem(JavaLanguageToolchainProvider::class, prompt)
    }
}
