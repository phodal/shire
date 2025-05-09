package com.phodal.shire.sonarqube

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.variable.ToolchainVariableProvider
import com.phodal.shirecore.provider.variable.model.toolchain.SonarqubeVariable
import com.phodal.shirecore.provider.variable.model.ToolchainVariable

class SonarLintVariableProvider : ToolchainVariableProvider {
    override fun isResolvable(variable: ToolchainVariable, psiElement: PsiElement?, project: Project): Boolean {
        return variable is SonarqubeVariable
    }

    override fun resolve(variable: ToolchainVariable, project: Project, editor: Editor, psiElement: PsiElement?): Any {
        return when (variable) {
            SonarqubeVariable.Issue -> {
                val file: VirtualFile = FileDocumentManager.getInstance().getFile(editor.document)
                    ?: throw IllegalStateException("No file found for editor")

                SonarLintProvider.analysisFile(project, file) ?: ""
            }
            SonarqubeVariable.Results -> {
                val file: VirtualFile = FileDocumentManager.getInstance().getFile(editor.document)
                    ?: throw IllegalStateException("No file found for editor")

                SonarLintProvider.analysisResults(project, file) ?: ""
            }
            else -> ""
        }
    }
}
