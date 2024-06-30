package com.phodal.shirelang.git

import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.variable.ToolchainVariableProvider
import com.phodal.shirecore.provider.variable.ToolchainVariable

class GitToolchainVariableProvider : ToolchainVariableProvider {
    override fun isResolvable(variable: ToolchainVariable, psiElement: PsiElement?): Boolean {
        return when (variable) {
            ToolchainVariable.Diff -> {
                true
            }

            ToolchainVariable.HistoryCommitExample -> {
                true
            }
        }
    }

    override fun resolveAll(variable: ToolchainVariable, psiElement: PsiElement?): List<ToolchainVariable> {
        return when (variable) {
            ToolchainVariable.Diff -> {
                variable.value = ""
                listOf(ToolchainVariable.Diff)
            }

            ToolchainVariable.HistoryCommitExample -> {
                listOf(ToolchainVariable.HistoryCommitExample)
            }
        }
    }
}
