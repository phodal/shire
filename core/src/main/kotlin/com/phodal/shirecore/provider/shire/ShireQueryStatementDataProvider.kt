package com.phodal.shirecore.provider.shire

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.phodal.shirecore.vcs.ShireVcsCommit

enum class QueryStatementDataType {
    VCS_COMMIT,
    VCS_BRANCH,
    VCS_FILE_COMMIT,
    VCS_FILE_BRANCH
}

interface ShireQueryStatementDataProvider {
    fun lookupElementByName(myProject: Project, variableType: String): List<ShireVcsCommit>?

    companion object {
        private val EP_NAME: ExtensionPointName<ShireQueryStatementDataProvider> =
            ExtensionPointName("com.phodal.shireStatementDataProvider")

        fun all(): List<ShireQueryStatementDataProvider> {
            return EP_NAME.extensionList
        }
    }
}
