package com.phodal.shirelang.editor

import com.intellij.icons.AllIcons
import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.smartReadAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.UserDataHolder
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.ui.JBColor
import com.intellij.ui.RoundedLineBorder
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.phodal.shirecore.ShireCoroutineScope
import com.phodal.shirecore.sketch.highlight.CodeHighlightSketch
import com.phodal.shirecore.sketch.highlight.EditorFragment
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.run.runner.ShireRunner
import com.phodal.shirelang.run.runner.ShireRunnerContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.intellij.plugins.markdown.lang.MarkdownLanguage
import java.awt.BorderLayout
import java.beans.PropertyChangeListener
import javax.swing.BorderFactory
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
    private val variablePanel = ShireVariableViewPanel()

    private var highlightSketch: CodeHighlightSketch? = null
    private var sampleEditor: Editor? = null
    private var language: Language? = Language.findLanguageByID("JAVA")
    private val javaHelloWorld = """
        package com.phodal.shirelang;
        
        class HelloWorld {
            public static void main(String[] args) {
                System.out.println("Hello, World");
            }
        }
    """.trimIndent()

    init {
        val corePanel = panel {
            row {
                val label = JBLabel("Shire Preview (Experimental)").apply {
                    fontColor = UIUtil.FontColor.BRIGHTER
                    background = JBColor(0xF5F5F5, 0x2B2D30)
                    font = JBUI.Fonts.label(16.0f).asBold()
                    border = JBUI.Borders.empty(0, 16)
                    isOpaque = true
                }

                cell(label).align(Align.FILL).resizableColumn()
            }
            if (language != null) {
                row {
                    cell(JBLabel("Sample file for variable").apply {
                        fontColor = UIUtil.FontColor.BRIGHTER
                        background = JBColor(0xF5F5F5, 0x2B2D30)
                        font = JBUI.Fonts.label(14.0f).asBold()
                        border = JBUI.Borders.empty(0, 16)
                        isOpaque = true
                    }).align(Align.FILL).resizableColumn()

                    cell(JBLabel("(/shire.java)")).align(Align.FILL).resizableColumn()
                    button("", object : AnAction() {
                        override fun actionPerformed(p0: AnActionEvent) {
                            rerenderShire()
                        }
                    }).also {
                        it.component.icon = AllIcons.Actions.Refresh
                        it.component.preferredSize = JBUI.size(24, 24)
                    }
                }
                row {
                    val editor = CodeHighlightSketch.createCodeViewerEditor(
                        project,
                        javaHelloWorld,
                        language,
                        this@ShirePreviewEditor
                    )

                    editor.isViewer = false
                    editor.settings.isLineNumbersShown = true

                    val editorFragment = EditorFragment(editor)
                    editorFragment.setCollapsed(true)
                    editorFragment.updateExpandCollapseLabel()

                    this@ShirePreviewEditor.sampleEditor = editor
                    cell(editorFragment.getContent()).align(Align.FILL).resizableColumn()
                }
            }
            row {
                cell(JBLabel("Variables").apply {
                    fontColor = UIUtil.FontColor.BRIGHTER
                    background = JBColor(0xF5F5F5, 0x2B2D30)
                    font = JBUI.Fonts.label(14.0f).asBold()
                    border = JBUI.Borders.empty(0, 16)
                    isOpaque = true
                }).align(Align.FILL).resizableColumn()
            }
            row {
                cell(variablePanel).align(Align.FILL)
            }
            row {
                cell(JBLabel("Prompt (some variable may be error)").apply {
                    fontColor = UIUtil.FontColor.BRIGHTER
                    background = JBColor(0xF5F5F5, 0x2B2D30)
                    font = JBUI.Fonts.label(14.0f).asBold()
                    border = JBUI.Borders.empty(0, 16)
                    isOpaque = true
                }).align(Align.FILL).resizableColumn()
            }
            row {
                highlightSketch = CodeHighlightSketch(project, "", MarkdownLanguage.INSTANCE, 18).apply {
                    initEditor("Please refresh to see the result")
                }
                highlightSketch?.editorFragment?.setCollapsed(true)
                highlightSketch?.editorFragment?.updateExpandCollapseLabel()

                val panel = JPanel(BorderLayout())
                panel.border = BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(12, 12, 12, 12),
                    RoundedLineBorder(JBColor.border(), 8, 1)
                )
                panel.add(highlightSketch, BorderLayout.CENTER)

                cell(panel).align(Align.FILL)
            }
        }

        this.mainPanel.add(corePanel, BorderLayout.CENTER)
//        updateDisplayedContent()
    }

    fun updateDisplayedContent() {
        ApplicationManager.getApplication().invokeLater {
            ShireCoroutineScope.scope(project).launch {
                try {
                    val psiFile = smartReadAction(project) {
                        PsiManager.getInstance(project).findFile(virtualFile) as? ShireFile
                    } ?: return@launch

                    shireRunnerContext = ShireRunner.compileOnly(project, psiFile, mapOf(), sampleEditor)

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
    }

    fun rerenderShire() {
        updateDisplayedContent()
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