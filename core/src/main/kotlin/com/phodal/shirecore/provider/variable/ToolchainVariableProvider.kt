package com.phodal.shirecore.provider.variable

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

interface ToolchainVariableProvider {
    fun isResolvable(variable: ToolchainVariable, psiElement: PsiElement?): Boolean

    fun resolve(project: Project, element: PsiElement?, variable: ToolchainVariable): ToolchainVariable

    companion object {
        private val EP_NAME: ExtensionPointName<ToolchainVariableProvider> =
            ExtensionPointName("com.phodal.shireToolchainVariableProvider")

        fun provide(variable: ToolchainVariable, element: PsiElement?): ToolchainVariableProvider? {
            return EP_NAME.extensionList.firstOrNull {
                it.isResolvable(variable, element)
            }
        }
    }
}
