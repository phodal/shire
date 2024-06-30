package com.phodal.shirecore.provider.variable

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.PsiElement

interface ToolchainVariableProvider {
    fun isResolvable(variable: ToolchainVariable, psiElement: PsiElement?): Boolean

    fun resolveAll(variable: ToolchainVariable, psiElement: PsiElement?): List<ToolchainVariable>

    companion object {
        private val EP_NAME: ExtensionPointName<ToolchainVariableProvider> =
            ExtensionPointName("com.phodal.shireToolchainVariableProvider")

        fun variable(variable: ToolchainVariable, element: PsiElement?): ToolchainVariableProvider? {
            return EP_NAME.extensionList.firstOrNull {
                it.isResolvable(variable, element)
            }
        }
    }
}
