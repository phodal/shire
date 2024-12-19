package com.phodal.shirecore.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.openapi.editor.actions.EnterAction
import com.intellij.openapi.editor.actions.IncrementalFindAction
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.EditorTextField
import com.intellij.util.EventDispatcher
import com.intellij.util.messages.MessageBusConnection
import com.intellij.util.ui.JBUI
import com.phodal.shirecore.sketch.highlight.findDocument
import com.phodal.shirecore.utils.markdown.CodeFenceLanguage
import java.awt.Color
import java.awt.event.KeyEvent
import java.util.*
import javax.swing.KeyStroke

class ShireInputTextField(
    project: Project,
    private val listeners: List<DocumentListener>,
    val disposable: Disposable?,
    val inputSection: ShireInputSection,
) : EditorTextField(project, FileTypes.PLAIN_TEXT), Disposable {
    private var editorListeners: EventDispatcher<ShireInputListener> = inputSection.editorListeners

    init {
        isOneLineMode = false
        setFontInheritedFromLAF(true)
        addSettingsProvider {
            it.putUserData(IncrementalFindAction.SEARCH_DISABLED, true)
            it.colorsScheme.lineSpacing = 1.2f
            it.settings.isUseSoftWraps = true
            it.isEmbeddedIntoDialogWrapper = true
            it.contentComponent.setOpaque(false)
        }

        DumbAwareAction.create {
            object : AnAction() {
                override fun actionPerformed(actionEvent: AnActionEvent) {
                    val editor = editor ?: return
                    CommandProcessor.getInstance().executeCommand(project, {
                        val eol = "\n"
                        val caretOffset = editor.caretModel.offset
                        editor.document.insertString(caretOffset, eol)
                        editor.caretModel.moveToOffset(caretOffset + eol.length)
                        EditorModificationUtil.scrollToCaret(editor)
                    }, null, null)
                }
            }
        }.registerCustomShortcutSet(
            CustomShortcutSet(
                KeyboardShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK), null),
                KeyboardShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.META_DOWN_MASK), null)
            ), this
        )

        val connect: MessageBusConnection = project.messageBus.connect(disposable ?: this)
        val topic = AnActionListener.TOPIC
        connect.subscribe(topic, object : AnActionListener {
            override fun afterActionPerformed(action: AnAction, event: AnActionEvent, result: AnActionResult) {
                if (event.dataContext.getData(CommonDataKeys.EDITOR) === editor && action is EnterAction) {
                    editorListeners.multicaster.onSubmit(inputSection, ShireInputTrigger.Key)
                }
            }
        })

        listeners.forEach { listener ->
            document.addDocumentListener(listener)
        }
    }

    override fun onEditorAdded(editor: Editor) {
        editorListeners.multicaster.editorAdded((editor as EditorEx))
    }

    public override fun createEditor(): EditorEx {
        val editor = super.createEditor()
        editor.setVerticalScrollbarVisible(true)
        setBorder(JBUI.Borders.empty())
        editor.setShowPlaceholderWhenFocused(true)
        editor.caretModel.moveToOffset(0)
        editor.scrollPane.setBorder(border)
        editor.contentComponent.setOpaque(false)
        return editor
    }

    override fun getBackground(): Color {
        val editor = editor ?: return super.getBackground()
        return editor.colorsScheme.defaultBackground
    }

    override fun getData(dataId: String): Any? {
        if (!PlatformCoreDataKeys.FILE_EDITOR.`is`(dataId)) {
            return super.getData(dataId)
        }

        val currentEditor = editor ?: return super.getData(dataId)
        return TextEditorProvider.getInstance().getTextEditor(currentEditor)
    }

    override fun dispose() {
        listeners.forEach {
            editor?.document?.removeDocumentListener(it)
        }
    }

    fun recreateDocument() {
        val id = UUID.randomUUID()
        val language = CodeFenceLanguage.findLanguage("Shire")
        val file = LightVirtualFile("ShireInput-$id", language, "")

        val document = file.findDocument() ?: throw IllegalStateException("Can't create in-memory document")

        initializeDocumentListeners(document)
        setDocument(document)
        inputSection.initEditor()
    }

    private fun initializeDocumentListeners(inputDocument: Document) {
        listeners.forEach { listener ->
            inputDocument.addDocumentListener(listener)
        }
    }
}