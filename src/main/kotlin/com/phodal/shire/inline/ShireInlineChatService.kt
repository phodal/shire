package com.phodal.shire.inline

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.IdeFocusManager
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirecore.provider.ide.ShirePromptBuilder
import java.util.concurrent.ConcurrentHashMap

@Service(Service.Level.APP)
class ShireInlineChatService : Disposable {
    private val allChats: ConcurrentHashMap<String, ShireInlineChatPanel> = ConcurrentHashMap()

    fun showInlineChat(editor: Editor) {
        var canShowInlineChat = true
        if (allChats.containsKey(editor.virtualFile.url)) {
            val chatPanel: ShireInlineChatPanel = this.allChats[editor.virtualFile.url]!!
            canShowInlineChat = chatPanel.inlay?.offset != editor.caretModel.primaryCaret.offset
            closeInlineChat(editor)
        }

        if (canShowInlineChat) {
            if (editor.component is ShireInlineChatPanel) return

            val panel = ShireInlineChatPanel(editor)
            editor.contentComponent.add(panel)
            panel.setInlineContainer(editor.contentComponent)

            val offset = if (editor.selectionModel.hasSelection()) {
                editor.selectionModel.selectionStart
            } else {
                editor.caretModel.primaryCaret.offset
            }
            panel.createInlay(offset)

            IdeFocusManager.getInstance(editor.project).requestFocus(panel.inputPanel.getInputComponent(), true)
            allChats[editor.virtualFile.url] = panel
        }
    }

    override fun dispose() {
        allChats.values.forEach {
            closeInlineChat(it.editor)
        }

        allChats.clear()
    }

    fun closeInlineChat(editor: Editor) {
        val chatPanel = this.allChats[editor.virtualFile.url] ?: return

        chatPanel.dispose()

        editor.contentComponent.remove(chatPanel)
        editor.contentComponent.revalidate()
        editor.contentComponent.repaint()
        allChats.remove(editor.virtualFile.url)
    }

    fun prompt(project: Project, prompt: String): String {
        return ShirePromptBuilder.provide()?.build(project, ShireActionLocation.INLINE_CHAT.name, prompt) ?: prompt
    }

    companion object {
        fun getInstance(): ShireInlineChatService {
            return ApplicationManager.getApplication().getService(ShireInlineChatService::class.java)
        }
    }
}

