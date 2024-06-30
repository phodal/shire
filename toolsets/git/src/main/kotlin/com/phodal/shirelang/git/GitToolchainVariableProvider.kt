package com.phodal.shirelang.git

import com.intellij.ide.DataManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.openapi.vcs.ui.CommitMessage
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.variable.ToolchainVariable
import com.phodal.shirecore.provider.variable.ToolchainVariableProvider


class GitToolchainVariableProvider : ToolchainVariableProvider {
    private val logger = logger<GitToolchainVariableProvider>()

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
                val dataContext = DataManager.getInstance().dataContextFromFocus.result
//                val commitWorkflowUi = VcsUtil.getCommitWorkFlowUi(event) ?: return
//                val changes = getChanges(commitWorkflowUi) ?: return
//                val diffContext = project.service<VcsPrompting>().prepareContext(changes)
//
//                if (diffContext.isEmpty() || diffContext == "\n") {
//                    logger.warn("Diff context is empty or cannot get enough useful context.")
//                    return
//                }

                val commitMessageUi = dataContext.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL) as CommitMessage
                commitMessageUi.editorField.text = "hello, text"

                variable.value = ""
                listOf(ToolchainVariable.Diff)
            }

            ToolchainVariable.HistoryCommitExample -> {
                listOf(ToolchainVariable.HistoryCommitExample)
            }
        }
    }
}
