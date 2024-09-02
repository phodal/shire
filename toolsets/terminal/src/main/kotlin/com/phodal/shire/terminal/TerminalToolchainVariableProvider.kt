package com.phodal.shire.terminal

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.variable.ToolchainVariableProvider
import com.phodal.shirecore.provider.variable.model.toolchain.TerminalToolchainVariable
import com.phodal.shirecore.provider.variable.model.ToolchainVariable
import org.jetbrains.plugins.terminal.TerminalProjectOptionsProvider

class TerminalToolchainVariableProvider : ToolchainVariableProvider {
    override fun isResolvable(variable: ToolchainVariable, psiElement: PsiElement?, project: Project): Boolean {
        return variable is TerminalToolchainVariable
    }

    override fun resolve(variable: ToolchainVariable, project: Project, editor: Editor, psiElement  : PsiElement?): Any {
        val options = TerminalProjectOptionsProvider.getInstance(project)

        return when (variable) {
            TerminalToolchainVariable.SHELL_PATH -> options.shellPath
            TerminalToolchainVariable.PWD -> {
                options.startingDirectory ?: project.guessProjectDir()?.path ?: System.getProperty("user.home")
            }

            else -> ""
        } ?: ""
    }
}
