package com.phodal.shirelang.editor

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorState
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