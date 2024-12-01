package com.phodal.shirecore.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.EditorMarkupModel
import com.intellij.openapi.editor.ex.FocusChangeListener
import com.intellij.openapi.editor.ex.MarkupModelEx
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.components.JBPanel
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.AlignY
import com.intellij.ui.dsl.builder.Cell
import com.intellij.util.concurrency.annotations.RequiresReadLock
import com.intellij.util.messages.Topic
import com.intellij.util.ui.JBUI
import com.phodal.shirecore.utils.markdown.CodeFence
import java.awt.BorderLayout
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.JComponent

class RightPanelView(
    project: Project,
    text: String,
) : JBPanel<RightPanelView>(BorderLayout()), DataProvider, Disposable {
    private var editor: EditorEx = createCodeViewerEditor(project, text, this)

    init {
        setupActionForEditor(project)

        editor.scrollPane.setBorder(JBUI.Borders.empty(0, 10))
        editor.component.setBorder(JBUI.Borders.empty(0, 10))

        add(editor.component, BorderLayout.CENTER)
    }

    private fun setupActionForEditor(project: Project) {
        val toolbarActionGroup = ActionManager.getInstance().getAction("Shire.ToolWindow.Toolbar") as? DefaultActionGroup
        toolbarActionGroup?.let {
            val toolbar = ActionManager.getInstance().createActionToolbar(
                ActionPlaces.MAIN_TOOLBAR,
                toolbarActionGroup,
                true
            )

            toolbar.component.setBackground(editor.backgroundColor)
            toolbar.component.setOpaque(true)
            toolbar.targetComponent = editor.contentComponent
            editor.headerComponent = toolbar.component

            val connect = project.messageBus.connect(this)
            val topic: Topic<EditorColorsListener> = EditorColorsManager.TOPIC
            connect.subscribe(topic, EditorColorsListener {
                toolbar.component.setBackground(editor.backgroundColor)
            })
        }
    }


    fun appendText(project: Project, char: String) {
        WriteCommandAction.runWriteCommandAction(project) {
            val document = editor.document
            document.insertString(document.textLength, char)

            // scroll to the end
            editor.caretModel.moveToOffset(document.textLength)
            editor.scrollingModel.scrollToCaret(ScrollType.RELATIVE)
        }
    }

    override fun getData(dataId: String): Any? {
        return null
    }

    companion object {
        private fun createCodeViewerEditor(project: Project, text: String, disposable: Disposable): EditorEx {
            val language = CodeFence.findLanguage("markdown")
            val file = LightVirtualFile("", language, text)
            val document: Document =
                file.findDocument() ?: throw IllegalStateException("Document not found")

            return createCodeViewerEditor(project, file, document, disposable)
        }

        private fun createCodeViewerEditor(
            project: Project,
            file: LightVirtualFile,
            document: Document,
            disposable: Disposable,
        ): EditorEx {
            val editor: EditorEx = ReadAction.compute<EditorEx, Throwable> {
                EditorFactory.getInstance()
                    .createViewer(document, project, EditorKind.PREVIEW) as EditorEx
            }

            disposable.whenDisposed(disposable) {
                EditorFactory.getInstance().releaseEditor(editor)
            }

            editor.setFile(file)
            editor.setCaretEnabled(true)

            val highlighter = ApplicationManager.getApplication()
                .getService(EditorHighlighterFactory::class.java)
                .createEditorHighlighter(project, file)

            editor.highlighter = highlighter

            val markupModel: MarkupModelEx = editor.markupModel
            (markupModel as EditorMarkupModel).isErrorStripeVisible = false

            val settings = editor.settings.also {
                it.isDndEnabled = false
                it.isLineNumbersShown = false
                it.additionalLinesCount = 0
                it.isLineMarkerAreaShown = false
                it.isFoldingOutlineShown = false
                it.isRightMarginShown = false
                it.isShowIntentionBulb = false
                it.isUseSoftWraps = true
                it.isRefrainFromScrolling = true
                it.isAdditionalPageAtBottom = false
                it.isCaretRowShown = false
            }

            editor.addFocusListener(object : FocusChangeListener {
                override fun focusGained(focusEditor: Editor) {
                    settings.isCaretRowShown = true
                }

                override fun focusLost(focusEditor: Editor) {
                    settings.isCaretRowShown = false
                    editor.markupModel.removeAllHighlighters()
                }
            })

            return editor
        }
    }

    override fun dispose() {
        // do nothing
    }
}

fun <T : JComponent> Cell<T>.fullWidth(): Cell<T> {
    return this.align(AlignX.FILL)
}

fun <T : JComponent> Cell<T>.fullHeight(): Cell<T> {
    return this.align(AlignY.FILL)
}


@RequiresReadLock
fun VirtualFile.findDocument(): Document? {
    return ReadAction.compute<Document, Throwable> {
        FileDocumentManager.getInstance().getDocument(this)
    }
}

fun Disposable.whenDisposed(listener: () -> Unit) {
    Disposer.register(this) { listener() }
}

fun Disposable.whenDisposed(
    parentDisposable: Disposable,
    listener: () -> Unit,
) {
    val isDisposed = AtomicBoolean(false)

    val disposable = Disposable {
        if (isDisposed.compareAndSet(false, true)) {
            listener()
        }
    }

    Disposer.register(this, disposable)

    Disposer.register(parentDisposable, Disposable {
        if (isDisposed.compareAndSet(false, true)) {
            Disposer.dispose(disposable)
        }
    })
}