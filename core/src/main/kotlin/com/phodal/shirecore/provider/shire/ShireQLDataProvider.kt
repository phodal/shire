package com.phodal.shirecore.provider.shire

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.phodal.shirecore.vcs.ShireVcsCommit

enum class ShireQLDataType {
    VCS_COMMIT,
    VCS_BRANCH,
    VCS_FILE_COMMIT,
    VCS_FILE_BRANCH
}

interface ShireQLDataProvider {
    fun lookupElementByName(myProject: Project, variableType: String): List<ShireVcsCommit>?

    companion object {
        private val EP_NAME: ExtensionPointName<ShireQLDataProvider> =
            ExtensionPointName("com.phodal.shireQLDataProvider")

        fun all(): List<ShireQLDataProvider> {
            return EP_NAME.extensionList
        }
    }
}
