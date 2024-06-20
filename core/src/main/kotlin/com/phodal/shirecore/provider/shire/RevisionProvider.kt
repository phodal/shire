package com.phodal.shirecore.provider.shire

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project

interface RevisionProvider {
    /**
     * Fetches the changes made in the specified revision in the repository of the given project.
     *
     * @param myProject the project in which the Git repository is located
     * @param revision the revision for which changes need to be fetched
     * @return a String containing the changes in the specified revision in a unified diff format,
     * or null if the repository is not found
     */
    fun fetchChanges(myProject: Project, revision: String): String?

    fun fetchCompletions(project: Project, result: CompletionResultSet)

    fun commitCode(project: Project, commitMessage: String): String

    companion object {
        private val EP_NAME: ExtensionPointName<RevisionProvider> =
            ExtensionPointName("com.phodal.shireRevisionProvider")

        fun provide(): RevisionProvider? {
            return EP_NAME.extensionList.firstOrNull()
        }
    }

}
