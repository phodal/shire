package com.phodal.shirelang.java.impl

import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.module.LanguageLevelUtil
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.JavaSdkType
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.pom.java.LanguageLevel
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtil
import com.phodal.shirecore.provider.ToolchainContextItem
import com.phodal.shirecore.provider.ToolchainPrepareContext
import com.phodal.shirecore.provider.ToolchainProvider

class JavaToolchainProvider : ToolchainProvider {
    override fun isApplicable(project: Project, context: ToolchainPrepareContext): Boolean {
        val sourceFile = context.sourceFile ?: return false
        if (sourceFile.language != JavaLanguage.INSTANCE) return false
        if (ProjectRootManager.getInstance(project).projectSdk !is JavaSdkType) return false

        val module: Module = try {
            ModuleUtilCore.findModuleForFile(sourceFile)
        } catch (e: Exception) {
            return false
        } ?: return false

        return ModuleRootManager.getInstance(module).sdk is JavaSdkType
    }

    override suspend fun collect(
        project: Project,
        context: ToolchainPrepareContext,
    ): List<ToolchainContextItem> {
        val psiFile = context.sourceFile
        val isJavaFile = psiFile?.containingFile?.virtualFile?.extension?.equals("java", true) ?: false

        if (!isJavaFile) return emptyList()
        val languageLevel = detectLanguageLevel(project, psiFile) ?: return emptyList()

        val prompt = "You are working on a project that uses Java SDK version $languageLevel."

        return listOf(
            ToolchainContextItem(JavaToolchainProvider::class, prompt)
        )
    }

    private fun detectLanguageLevel(project: Project, sourceFile: PsiFile?): LanguageLevel? {
        val projectSdk = ProjectRootManager.getInstance(project).projectSdk
        if (projectSdk != null) {
            if (projectSdk.sdkType !is JavaSdkType) return null
            return PsiUtil.getLanguageLevel(project)
        }

        val moduleForFile = ModuleUtilCore.findModuleForFile(sourceFile)
            ?: ModuleManager.getInstance(project).modules.firstOrNull()
            ?: return null

        if (ModuleRootManager.getInstance(moduleForFile).sdk !is JavaSdkType) return null

        return LanguageLevelUtil.getEffectiveLanguageLevel(moduleForFile)
    }

}
