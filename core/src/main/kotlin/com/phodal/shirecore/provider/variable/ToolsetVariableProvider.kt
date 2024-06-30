package com.phodal.shirecore.provider.variable

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.PsiElement

interface ToolsetVariableProvider {
    fun resolveMethod(variable: ToolsetVariable, psiElement: PsiElement?): Any?

    fun resolveVariable(variable: ToolsetVariable, psiElement: PsiElement?): List<String>

    companion object {
        private val EP_NAME: ExtensionPointName<ToolsetVariableProvider> =
            ExtensionPointName("com.phodal.shireToolsetVariableProvider")

        fun variable(variable: ToolsetVariable, element: PsiElement?): ToolsetVariableProvider? {
            return EP_NAME.extensionList.firstOrNull {
                it.resolveVariable(variable, element).isNotEmpty() || it.resolveMethod(variable, element) != null
            }
        }
    }
}
