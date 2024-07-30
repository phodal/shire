package com.phodal.shirelang.git.provider

import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.shire.ShireQLDataProvider
import com.phodal.shirecore.provider.shire.ShireQLDataType

class GitQLDataProvider : ShireQLDataProvider {
    override fun lookupGitData(myProject: Project, dataTypes: List<ShireQLDataType>): Map<ShireQLDataType, Any?> {
        TODO("Not yet implemented")
    }
}
