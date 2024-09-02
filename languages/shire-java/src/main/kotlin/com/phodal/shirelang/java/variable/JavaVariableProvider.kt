package com.phodal.shirelang.java.variable

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.variable.ToolchainVariableProvider
import com.phodal.shirecore.provider.variable.model.ToolchainVariable
import com.phodal.shirecore.provider.variable.model.toolchain.GradleToolchainVariable
import com.phodal.shirelang.java.toolchain.GradleBuildTool

class JavaVariableProvider : ToolchainVariableProvider {
    override fun isResolvable(variable: ToolchainVariable, psiElement: PsiElement?, project: Project): Boolean {
        return variable is GradleToolchainVariable && GradleBuildTool().prepareLibraryData(project)?.isNotEmpty() == true
    }

    override fun resolve(variable: ToolchainVariable, project: Project, editor: Editor, psiElement: PsiElement?): Any {
        return when (variable) {
            GradleToolchainVariable.GradleDependencies -> GradleBuildTool().prepareLibraryData(project)
            else -> ""
        } ?: ""
    }
}
