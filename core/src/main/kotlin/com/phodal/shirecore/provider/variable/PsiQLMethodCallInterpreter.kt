package com.phodal.shirecore.provider.variable

import com.intellij.lang.Language
import com.intellij.lang.LanguageExtension
import com.intellij.psi.PsiElement

/**
 * For [com.phodal.shirelang.compiler.hobbit.execute.PsiQueryStatementProcessor]
 */
interface PsiQLMethodCallInterpreter {
    /**
     * clazz.getName() or clazz.extensions
     */
    fun resolveCall(element: PsiElement, methodName: String, arguments: List<String>): Any

    companion object {
        private val languageExtension: LanguageExtension<PsiQLMethodCallInterpreter> =
            LanguageExtension("com.phodal.shirePsiQLMethodCallInterpreter")

        fun provide(language: Language): PsiQLMethodCallInterpreter? {
            return languageExtension.forLanguage(language)
        }
    }
}
