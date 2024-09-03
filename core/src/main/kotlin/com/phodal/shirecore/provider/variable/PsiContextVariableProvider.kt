package com.phodal.shirecore.provider.variable

import com.intellij.lang.Language
import com.intellij.lang.LanguageExtension
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.context.LanguageToolchainProvider
import com.phodal.shirecore.provider.context.ToolchainPrepareContext
import com.phodal.shirecore.provider.variable.impl.DefaultPsiContextVariableProvider
import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture

/**
 * Resolve variables for code struct generation.
 * This is used to provide the variables that are used in the code struct generation.
 */
interface PsiContextVariableProvider : VariableProvider<PsiContextVariable> {
    /**
     * Calculate the value for the given variable based on the provided PsiElement.
     *
     * @param psiElement the PsiElement to use for resolving the variable value
     * @param variable the PsiVariable for which to calculate the value
     * @return the calculated value for the variable as a String
     */
    override fun resolve(variable: PsiContextVariable, project: Project, editor: Editor, psiElement: PsiElement?): Any


    fun collectFrameworkContext(psiElement: PsiElement?, project: Project): String {
        val future = CompletableFuture<String>()
        runBlocking {
            val prepareContext = ToolchainPrepareContext(psiElement?.containingFile, psiElement)
            val contextItems =
                LanguageToolchainProvider.collectToolchainContext(project, prepareContext)

            future.complete(contextItems.joinToString("\n") { it.text })
        }

        return future.get()
    }

    companion object {
        private val languageExtension: LanguageExtension<PsiContextVariableProvider> =
            LanguageExtension("com.phodal.shirePsiVariableProvider")

        fun provide(language: Language): PsiContextVariableProvider {
            return languageExtension.forLanguage(language)  ?: DefaultPsiContextVariableProvider()
        }
    }
}
