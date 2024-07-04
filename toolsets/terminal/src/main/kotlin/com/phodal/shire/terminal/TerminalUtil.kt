package com.phodal.shire.terminal

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.terminal.ui.TerminalWidget
import com.intellij.ui.components.panels.Wrapper
import com.intellij.ui.content.Content
import com.phodal.shire.terminal.ShireTerminalAction.executeAction
import com.phodal.shirecore.provider.action.terminal.TerminalHandler
import org.jetbrains.plugins.terminal.TerminalToolWindowFactory
import org.jetbrains.plugins.terminal.TerminalToolWindowManager
import org.jetbrains.plugins.terminal.exp.TerminalPromptController

object TerminalUtil {
    fun sendMsg(project: Project, userInput: String, e: AnActionEvent) {
        val content = getContent(project) ?: return
        val findWidgetByContent = TerminalToolWindowManager.findWidgetByContent(content) ?: return
        val controller: TerminalPromptController? = lookupTerminalPromptControllerByView(findWidgetByContent)
        if (controller == null) {
            trySendMsgInOld(project, userInput, content)
            return
        }

        val sb = StringBuilder()
        executeAction(
            TerminalHandler(
                userInput,
                project,
                onChunk = { string ->
                    sb.append(string)
                },
                onFinish = {
                    runInEdt {
                        CopyPasteManager.copyTextToClipboard(sb.toString())
                        controller.performPaste(e.dataContext)
                    }
                })
        )
    }

    private fun lookupTerminalPromptControllerByView(findWidgetByContent: TerminalWidget): TerminalPromptController? {
        val terminalView = (findWidgetByContent.component as? Wrapper)?.targetComponent ?: return null
        if (terminalView is DataProvider) {
            val controller = terminalView.getData(TerminalPromptController.KEY.name)
            return (controller as? TerminalPromptController)
        }

        return null
    }

    private fun trySendMsgInOld(project: Project, userInput: String, content: Content): Boolean {
        val widget = TerminalToolWindowManager.getWidgetByContent(content) ?: return true
        executeAction(
            TerminalHandler(
                userInput,
                project,
                onChunk = { string ->
                    widget.terminalStarter?.sendString(string, true)
                },
                onFinish = {})
        )

        return false
    }

    private fun getContent(project: Project): Content? {
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(TerminalToolWindowFactory.TOOL_WINDOW_ID)
        return toolWindow?.contentManager?.selectedContent
    }
}
