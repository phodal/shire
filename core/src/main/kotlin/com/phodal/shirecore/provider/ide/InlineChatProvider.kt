package com.phodal.shirecore.provider.ide

import com.intellij.openapi.extensions.ExtensionPointName

interface InlineChatProvider {
    fun listen()

    companion object {
        private val EP_NAME: ExtensionPointName<InlineChatProvider> =
            ExtensionPointName("com.phodal.shireInlineChatProvider")

        fun provide(): InlineChatProvider? {
            return EP_NAME.extensionList.firstOrNull()
        }
    }
}