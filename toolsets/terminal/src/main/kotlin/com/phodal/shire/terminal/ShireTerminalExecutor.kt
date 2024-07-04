package com.phodal.shire.terminal

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.terminal.ui.TerminalWidget
import com.intellij.ui.components.panels.Wrapper
import com.intellij.ui.content.Content
import com.phodal.shirecore.provider.action.TerminalLocationExecutor
import com.phodal.shirecore.provider.action.terminal.TerminalHandler
import org.jetbrains.plugins.terminal.TerminalToolWindowFactory
import org.jetbrains.plugins.terminal.TerminalToolWindowManager
import org.jetbrains.plugins.terminal.exp.TerminalPromptController
import java.awt.Component

class ShireTerminalExecutor : TerminalLocationExecutor {
    override fun getComponent(e: AnActionEvent): Component? {
        return e.getData(PlatformCoreDataKeys.CONTEXT_COMPONENT)
    }

    override fun bundler(project: Project, userInput: String): TerminalHandler? {
        val content = getContent(project) ?: return null
        val findWidgetByContent = TerminalToolWindowManager.findWidgetByContent(content) ?: return null
        val controller: TerminalPromptController? = lookupTerminalPromptControllerByView(findWidgetByContent)
        if (controller == null) {
            trySendMsgInOld(project, userInput, content)
            return null
        }

        val sb = StringBuilder()
        return TerminalHandler(
            userInput,
            project,
            onChunk = { string ->
                sb.append(string)
            },
            onFinish = {
                runInEdt {
                    CopyPasteManager.copyTextToClipboard(sb.toString())
                    controller.performPaste()
                }
            })
    }

    private fun lookupTerminalPromptControllerByView(findWidgetByContent: TerminalWidget): TerminalPromptController? {
        val terminalView = (findWidgetByContent.component as? Wrapper)?.targetComponent ?: return null
        if (terminalView is DataProvider) {
            val controller = terminalView.getData(TerminalPromptController.KEY.name)
            return (controller as? TerminalPromptController)
        }

        return null
    }

    private fun trySendMsgInOld(project: Project, userInput: String, content: Content): TerminalHandler? {
        val widget = TerminalToolWindowManager.getWidgetByContent(content) ?: return null
        return TerminalHandler(
            userInput,
            project,
            onChunk = { string ->
                widget.terminalStarter?.sendString(string, true)
            },
            onFinish = {})
    }

    private fun getContent(project: Project): Content? {
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(TerminalToolWindowFactory.TOOL_WINDOW_ID)
        return toolWindow?.contentManager?.selectedContent
    }
}
