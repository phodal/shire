package com.phodal.shirelang.editor

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.event.VisibleAreaEvent
import com.intellij.openapi.editor.event.VisibleAreaListener
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.UserDataHolder
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.readText
import com.intellij.psi.PsiManager
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.phodal.shirecore.sketch.highlight.CodeHighlightSketch
import com.phodal.shirelang.ShireFileType
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.run.runner.ShireRunner
import com.phodal.shirelang.run.runner.ShireRunnerContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.intellij.plugins.markdown.lang.MarkdownLanguage
import java.awt.BorderLayout
import java.beans.PropertyChangeListener
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.ScrollPaneConstants

class ShirePreviewEditorProvider : WeighedFileEditorProvider(), AsyncFileEditorProvider {
    override fun accept(project: Project, file: VirtualFile): Boolean {
        return FileTypeRegistry.getInstance().isFileOfType(file, ShireFileType.INSTANCE)
    }

    override fun createEditor(project: Project, virtualFile: VirtualFile): FileEditor {
        return ShirePreviewEditor(project, virtualFile)
    }

    override fun getEditorTypeId(): String = "shire-preview-editor"

    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR
}

/**
 * Display shire file render prompt and have a sample file as view
 */
open class ShirePreviewEditor(
    val project: Project,
    val virtualFile: VirtualFile,
) : UserDataHolder by UserDataHolderBase(), FileEditor {
    val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
    private var mainEditor = MutableStateFlow<Editor?>(null)
    private val mainPanel = JPanel(BorderLayout())
    private val visualPanel: JBScrollPane = JBScrollPane(
        mainPanel,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
    )

    private var shireRunnerContext: ShireRunnerContext? = null
    private val variablePanel = ShireVariablePanel()

    private var highlightSketch: CodeHighlightSketch? = null

    init {
        val corePanel = panel {
            row {
                val label = JBLabel("Shire Preview (Experimental)").apply {
                    fontColor = UIUtil.FontColor.BRIGHTER
                    font = JBUI.Fonts.label(14.0f)
                    foreground = JBColor(0x4A4A4A, 0xBBBBBB)
                    background = JBColor(0xF5F7FA, 0x2B2D30)
                    border = JBUI.Borders.compound(
                        JBUI.Borders.empty(12, 16),
                        JBUI.Borders.customLine(JBColor(0xE0E0E0, 0x3C3F41), 0, 0, 1, 0)
                    )
                    isOpaque = true
                }

                cell(label).align(Align.FILL).resizableColumn()
            }
            row {
                cell(variablePanel).align(Align.FILL)
            }
            row {
                highlightSketch = CodeHighlightSketch(project, "", MarkdownLanguage.INSTANCE).apply {
                    initEditor(virtualFile.readText())
                }

                cell(highlightSketch!!).align(Align.FILL)
            }
        }

        this.mainPanel.add(corePanel, BorderLayout.CENTER)
        mainEditor.value?.document?.addDocumentListener(ReparseContentDocumentListener())
        updateOutput()
    }

    private inner class ReparseContentDocumentListener : DocumentListener {
        override fun documentChanged(event: DocumentEvent) {
            updateOutput()
        }
    }

    fun updateOutput() {
        ApplicationManager.getApplication().invokeLater {
            try {
                val psiFile = PsiManager.getInstance(project).findFile(virtualFile) as? ShireFile ?: return@invokeLater
                shireRunnerContext = runBlocking {
                    ShireRunner.compileFileContext(project, psiFile, mapOf())
                }

                val variables = shireRunnerContext?.compiledVariables
                if (variables != null) {
                    variablePanel.updateVariables(variables)
                }

                highlightSketch?.updateViewText(shireRunnerContext!!.finalPrompt)
                highlightSketch?.repaint()

                mainPanel.revalidate()
                mainPanel.repaint()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun rerenderShire() {
        updateOutput()
    }

    fun setMainEditor(editor: Editor) {
        check(mainEditor.value == null)
        mainEditor.value = editor
    }

    fun scrollToSrcOffset(offset: Int) {
        val highlightEditor = highlightSketch?.editorFragment?.editor
        if (highlightEditor == null) {
            visualPanel.verticalScrollBar.value = offset
            return
        }

        val position = highlightEditor.offsetToLogicalPosition(offset)
        highlightEditor.scrollingModel.scrollTo(position, ScrollType.MAKE_VISIBLE)
    }

    override fun getComponent(): JComponent = visualPanel
    override fun getName(): String = "Shire Prompt Preview"
    override fun setState(state: FileEditorState) {}
    override fun isModified(): Boolean = false
    override fun isValid(): Boolean = true
    override fun getFile(): VirtualFile = virtualFile
    override fun getPreferredFocusedComponent(): JComponent? = null
    override fun addPropertyChangeListener(listener: PropertyChangeListener) {}
    override fun removePropertyChangeListener(listener: PropertyChangeListener) {}
    override fun dispose() {}
}

class ShireFileEditorWithPreview(
    private val ourEditor: TextEditor,
    @JvmField var preview: ShirePreviewEditor,
    private val project: Project,
) : TextEditorWithPreview(
    ourEditor, preview,
    "Shire Split Editor",
    Layout.SHOW_EDITOR_AND_PREVIEW,
) {
    val virtualFile: VirtualFile = ourEditor.file

    init {
        // allow launching actions while in preview mode;
        // FIXME: better solution IDEA-354102
        ourEditor.editor.contentComponent.putClientProperty(ActionUtil.ALLOW_ACTION_PERFORM_WHEN_HIDDEN, true)
        preview.setMainEditor(ourEditor.editor)
        ourEditor.editor.scrollingModel.addVisibleAreaListener(MyVisibleAreaListener(), this)
    }

    override fun dispose() {
        TextEditorProvider.getInstance().disposeEditor(ourEditor)
    }

    inner class MyVisibleAreaListener() : VisibleAreaListener {
        private var previousLine = 0

        override fun visibleAreaChanged(event: VisibleAreaEvent) {
            val editor = event.editor
            val y = editor.scrollingModel.verticalScrollOffset
            val currentLine = if (editor is EditorImpl) editor.yToVisualLine(y) else y / editor.lineHeight
            if (currentLine == previousLine) {
                return
            }

            previousLine = currentLine
            preview.scrollToSrcOffset(EditorUtil.getVisualLineEndOffset(editor, currentLine))
        }
    }

    override fun createToolbar(): ActionToolbar {
        return ActionManager.getInstance()
            .createActionToolbar(ActionPlaces.EDITOR_TOOLBAR, createActionGroup(project, virtualFile, editor), true)
            .also {
                it.targetComponent = editor.contentComponent
            }
    }

    private fun createActionGroup(project: Project, virtualFile: VirtualFile, editor: Editor): ActionGroup {
        return DefaultActionGroup(
            showPreviewAction(project, virtualFile, editor),
            Separator(),
            createHelpAction(project)
        )
    }

    private fun createHelpAction(project: Project): AnAction {
        val idleIcon = AllIcons.Actions.Help
        return object : AnAction("Help", "Help", idleIcon) {
            override fun actionPerformed(e: AnActionEvent) {
                val url = "https://shire.phodal.com/"
                com.intellij.ide.BrowserUtil.browse(url)
            }
        }
    }

    private fun showPreviewAction(project: Project, virtualFile: VirtualFile, editor: Editor): AnAction {
        val idleIcon = AllIcons.Actions.Preview
        return object : AnAction("Show Preview", "Show Preview", idleIcon) {
            override fun actionPerformed(e: AnActionEvent) {
                preview.component.isVisible = true
                (preview as ShirePreviewEditor).rerenderShire()
            }
        }
    }
}