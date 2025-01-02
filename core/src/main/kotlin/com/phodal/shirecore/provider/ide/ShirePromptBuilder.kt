package com.phodal.shirecore.provider.ide

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project

interface ShirePromptBuilder {
    fun build(project: Project, actionLocation: String, originPrompt: String) : String

    companion object {
        private val EP_NAME: ExtensionPointName<ShirePromptBuilder> =
            ExtensionPointName("com.phodal.shirePromptBuilder")

        fun provide(): ShirePromptBuilder? {
            return EP_NAME.extensionList.firstOrNull()
        }
    }
}