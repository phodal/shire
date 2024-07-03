package com.phodal.shirecore.provider.variable

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.variable.model.VcsToolchainVariable

interface ToolchainVariableProvider : VariableProvider<VcsToolchainVariable> {
    fun isResolvable(variable: VcsToolchainVariable, psiElement: PsiElement?): Boolean

    companion object {
        private val EP_NAME: ExtensionPointName<ToolchainVariableProvider> =
            ExtensionPointName("com.phodal.shireToolchainVariableProvider")

        fun provide(variable: VcsToolchainVariable, element: PsiElement?): ToolchainVariableProvider? {
            return EP_NAME.extensionList.firstOrNull {
                it.isResolvable(variable, element)
            }
        }
    }
}
