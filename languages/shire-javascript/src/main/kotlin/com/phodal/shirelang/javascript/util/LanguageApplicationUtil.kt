package com.phodal.shirelang.javascript.util

import com.intellij.lang.Language
import com.intellij.lang.html.HTMLLanguage
import com.intellij.lang.javascript.DialectDetector
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.lang.javascript.buildTools.npm.PackageJsonUtil
import com.intellij.psi.PsiFile
import com.phodal.shirecore.provider.context.ToolchainPrepareContext

object LanguageApplicableUtil {
    fun isJavaScriptApplicable(language: Language) =
        language.isKindOf(JavascriptLanguage.INSTANCE) || language.isKindOf(HTMLLanguage.INSTANCE)

    fun isPreferTypeScript(context: ToolchainPrepareContext): Boolean {
        val sourceFile = context.sourceFile ?: return false
        return DialectDetector.isTypeScript(sourceFile)
    }

    fun isWebChatCreationContextSupported(psiFile: PsiFile?): Boolean {
        return isWebLLMContext(psiFile)
    }

    fun isWebLLMContext(psiFile: PsiFile?): Boolean {
        if (psiFile == null) return false
        if (PackageJsonUtil.isPackageJsonFile(psiFile)) return true

        return isJavaScriptApplicable(psiFile.language)
    }
}