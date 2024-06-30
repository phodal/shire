package com.phodal.shirelang.git

import com.intellij.ide.DataManager
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.openapi.vcs.changes.Change
import com.intellij.openapi.vcs.changes.CurrentContentRevision
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.vcs.commit.CommitWorkflowUi
import com.intellij.vcs.log.VcsLogFilterCollection
import com.intellij.vcs.log.VcsLogProvider
import com.intellij.vcs.log.impl.VcsProjectLog
import com.intellij.vcs.log.visible.filters.VcsLogFilterObject
import com.phodal.shirecore.provider.variable.ToolchainVariable
import com.phodal.shirecore.provider.variable.ToolchainVariableProvider


class GitToolchainVariableProvider : ToolchainVariableProvider {
    private val logger = logger<GitToolchainVariableProvider>()

    override fun isResolvable(variable: ToolchainVariable, psiElement: PsiElement?): Boolean {
        return when (variable) {
            ToolchainVariable.Diff -> {
                true
            }

            ToolchainVariable.HistoryCommitMessages -> {
                true
            }
        }
    }

    override fun resolve(project: Project, element: PsiElement?, variable: ToolchainVariable, ): ToolchainVariable {
        when (variable) {
            ToolchainVariable.Diff -> {
                val dataContext = DataManager.getInstance().dataContextFromFocus.result
                val commitWorkflowUi = dataContext.getData(VcsDataKeys.COMMIT_WORKFLOW_UI)
                    ?: return variable
                val changes = getDiff(commitWorkflowUi) ?: return variable
                val diffContext = project.service<VcsPrompting>().prepareContext(changes)

                if (diffContext.isEmpty() || diffContext == "\n") {
                    logger.warn("Diff context is empty or cannot get enough useful context.")
                    return variable
                }
//
//                val commitMessageUi = dataContext.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL) as CommitMessage
//                commitMessageUi.editorField.text = "hello, text"

                variable.value = diffContext
                return variable
            }

            ToolchainVariable.HistoryCommitMessages -> {
                val exampleCommitMessages = getHistoryCommitMessages(project)
                if (exampleCommitMessages != null) {
                    variable.value = exampleCommitMessages
                }
            }
        }

        return variable
    }

    /**
     * Finds example commit messages based on the project's VCS log, takes the first three commits.
     * If the no user or user has committed anything yet, the current branch name is used instead.
     *
     * @param project The project for which to find example commit messages.
     * @return A string containing example commit messages, or null if no example messages are found.
     */
    private fun getHistoryCommitMessages(project: Project): String? {
        val logProviders = VcsProjectLog.getLogProviders(project)
        val entry = logProviders.entries.firstOrNull() ?: return null

        val logProvider = entry.value
        val branch = logProvider.getCurrentBranch(entry.key) ?: return null
        val user = logProvider.getCurrentUser(entry.key)

        val logFilter = if (user != null) {
            VcsLogFilterObject.collection(VcsLogFilterObject.fromUser(user, setOf()))
        } else {
            VcsLogFilterObject.collection(VcsLogFilterObject.fromBranch(branch))
        }

        return collectExamples(logProvider, entry.key, logFilter)
    }

    /**
     * Collects examples from the VcsLogProvider based on the provided filter.
     *
     * @param logProvider The VcsLogProvider used to retrieve commit information.
     * @param root The root VirtualFile of the project.
     * @param filter The VcsLogFilterCollection used to filter the commits.
     * @return A string containing the collected examples, or null if no examples are found.
     */
    private fun collectExamples(
        logProvider: VcsLogProvider,
        root: VirtualFile,
        filter: VcsLogFilterCollection,
    ): String? {
        val commits = logProvider.getCommitsMatchingFilter(root, filter, 3)

        if (commits.isEmpty()) return null

        val builder = StringBuilder("")
        val commitIds = commits.map { it.id.asString() }

        logProvider.readMetadata(root, commitIds) {
            val shortMsg = it.fullMessage.split("\n").firstOrNull() ?: it.fullMessage
            builder.append(shortMsg).append("\n")
        }

        return builder.toString()
    }

    private fun getDiff(commitWorkflowUi: CommitWorkflowUi): List<Change>? {
        val changes = commitWorkflowUi.getIncludedChanges()
        val unversionedFiles = commitWorkflowUi.getIncludedUnversionedFiles()

        val unversionedFileChanges = unversionedFiles.map {
            Change(null, CurrentContentRevision(it))
        }

        if (changes.isNotEmpty() || unversionedFileChanges.isNotEmpty()) {
            return changes + unversionedFileChanges
        }

        return null
    }

}
