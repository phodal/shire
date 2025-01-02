package com.phodal.shirecore.provider.ide

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project

interface InlineChatProvider {

    fun addListener(project: Project)

    fun removeListener(project: Project)

    companion object {
        private val EP_NAME: ExtensionPointName<InlineChatProvider> =
            ExtensionPointName("com.phodal.shireInlineChatProvider")

        fun provide(): InlineChatProvider? {
            return EP_NAME.extensionList.firstOrNull()
        }
    }
}