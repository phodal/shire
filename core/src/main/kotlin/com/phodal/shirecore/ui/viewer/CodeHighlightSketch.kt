package com.phodal.shirecore.ui.viewer

import com.intellij.lang.Language
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.EditorKind
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.EditorMarkupModel
import com.intellij.openapi.editor.ex.FocusChangeListener
import com.intellij.openapi.editor.ex.MarkupModelEx
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.components.JBPanel
import com.intellij.util.concurrency.annotations.RequiresReadLock
import com.intellij.util.messages.Topic
import com.phodal.shirecore.ui.EditorFragment
import com.phodal.shirecore.utils.markdown.CodeFenceLanguage
import java.awt.BorderLayout
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.JComponent

class CodeHighlightSketch(val project: Project, val text: String, private var ideaLanguage: Language?) :
    JBPanel<CodeHighlightSketch>(BorderLayout()), DataProvider, LangSketch {

    var editorFragment: EditorFragment? = null
    private var hasSetupAction = false

    init {
        if (text.isEmpty() && (ideaLanguage?.displayName != "Markdown" && ideaLanguage != PlainTextLanguage.INSTANCE)) {
            initEditor(text)
        }
    }

    fun initEditor(text: String) {
        if (hasSetupAction) return
        hasSetupAction = true

        val editor = createCodeViewerEditor(project, text, ideaLanguage, this)

        editorFragment = EditorFragment(editor)
        add(editorFragment!!.getContent(), BorderLayout.CENTER)

        if (ideaLanguage?.displayName != "Markdown" && ideaLanguage != PlainTextLanguage.INSTANCE) {
            setupActionBar()
        }
    }

    private fun setupActionBar() {
        val toolbarActionGroup = ActionManager.getInstance().getAction("Shire.ToolWindow.Toolbar") as? ActionGroup
            ?: return

        val toolbar = ActionManager.getInstance()
            .createActionToolbar(ActionPlaces.MAIN_TOOLBAR, toolbarActionGroup, true)

        val editor = editorFragment?.editor
        toolbar.component.setBackground(editor!!.backgroundColor)
        toolbar.component.setOpaque(true)
        toolbar.targetComponent = editor.contentComponent
        editor.headerComponent = toolbar.component

        val connect = project.messageBus.connect(this)
        val topic: Topic<EditorColorsListener> = EditorColorsManager.TOPIC
        connect.subscribe(topic, EditorColorsListener {
            toolbar.component.setBackground(editor.backgroundColor)
        })
    }

    override fun getViewText(): String {
        return editorFragment?.editor?.document?.text ?: ""
    }

    override fun updateLanguage(language: Language?) {
        if (ideaLanguage == null || ideaLanguage == PlainTextLanguage.INSTANCE) {
            ideaLanguage = language
        }
    }

    override fun updateViewText(text: String) {
        if (!hasSetupAction && text.isNotEmpty()) {
            initEditor(text)
        }

        WriteCommandAction.runWriteCommandAction(project) {
            val document = editorFragment?.editor?.document
            document?.replaceString(0, document.textLength, text)
        }
    }

    override fun getComponent(): JComponent = this

    override fun getData(dataId: String): Any? = null

    companion object {
        private fun createCodeViewerEditor(
            project: Project,
            text: String,
            ideaLanguage: Language?,
            disposable: Disposable,
        ): EditorEx {
            val language = ideaLanguage ?: CodeFenceLanguage.findLanguage("Plain text")
            val ext = CodeFenceLanguage.lookupFileExt(language.displayName)
            val file = LightVirtualFile("shire.${ext}", language, text)
            val document: Document = file.findDocument() ?: throw IllegalStateException("Document not found")

            return createCodeViewerEditor(project, file, document, disposable)
        }

        private fun createCodeViewerEditor(
            project: Project,
            file: LightVirtualFile,
            document: Document,
            disposable: Disposable,
        ): EditorEx {
            val editor: EditorEx = ReadAction.compute<EditorEx, Throwable> {
                EditorFactory.getInstance().createViewer(document, project, EditorKind.PREVIEW) as EditorEx
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