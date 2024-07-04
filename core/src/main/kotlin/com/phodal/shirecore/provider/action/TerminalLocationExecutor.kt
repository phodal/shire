package com.phodal.shirecore.provider.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.action.terminal.TerminalHandler
import java.awt.Component

interface TerminalLocationExecutor {
    fun getComponent(e: AnActionEvent): Component?
    fun bundler(project: Project, userInput: String): TerminalHandler?

    companion object {
        private val EP_NAME: ExtensionPointName<TerminalLocationExecutor> =
            ExtensionPointName.create("com.phodal.shireTerminalExecutor")

        fun provide(project: Project): TerminalLocationExecutor? {
            return EP_NAME.extensionList.firstOrNull()
        }
    }
}
