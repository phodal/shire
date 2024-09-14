package com.phodal.shirelang.go.variable

import com.goide.GoLanguage
import com.goide.sdk.GoSdkService
import com.goide.sdk.GoTargetSdkVersionProvider
import com.goide.util.GoUtil
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.phodal.shirecore.provider.context.LanguageToolchainProvider
import com.phodal.shirecore.provider.context.ToolchainContextItem
import com.phodal.shirecore.provider.context.ToolchainPrepareContext
import java.lang.reflect.Method

class GoLanguageProvider : LanguageToolchainProvider {
    override fun isApplicable(project: Project, context: ToolchainPrepareContext): Boolean {
        return context.sourceFile?.language is GoLanguage
    }

    override suspend fun collect(project: Project, context: ToolchainPrepareContext): List<ToolchainContextItem> {
        val psiElement = context.element ?: return emptyList()
        val sourceFile = context.sourceFile ?: return emptyList()

        return ReadAction.compute<List<ToolchainContextItem>, Throwable> {
            val goVersion = GoSdkService.getInstance(project).getSdk(GoUtil.module(sourceFile)).version
            val targetVersion = getGoVersion(sourceFile)

            val prompt = "Go Version: $goVersion, Target Version: $targetVersion"
            val element = ToolchainContextItem(GoLanguageProvider::class, prompt)
            listOf(element)
        }
    }

    private fun getGoVersion(sourceFile: PsiFile): String {
        return try {
            val clazz = Class.forName("com.goide.sdk.GoTargetSdkVersionProvider")
            val method: Method = clazz.getMethod("getTargetGoSdkVersion", PsiElement::class.java)
            val result = method.invoke(null, sourceFile)
            result?.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } ?: ""
    }
}
