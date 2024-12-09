package com.phodal.shirecore.ui

import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.NullableComponent
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.phodal.shirecore.ui.viewer.*
import com.phodal.shirecore.utils.markdown.CodeFence
import com.phodal.shirecore.utils.markdown.CodeFenceLanguage
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JProgressBar
import javax.swing.ScrollPaneConstants
import javax.swing.SwingUtilities

class ShirePanelView(val project: Project) : SimpleToolWindowPanel(true, true), NullableComponent {
    private var progressBar: JProgressBar = JProgressBar()

    private var myList = JPanel(VerticalLayout(JBUI.scale(0))).apply {
        this.isOpaque = true
        this.background = UIUtil.getLabelBackground()
    }

    private var userPrompt: JPanel = JPanel(BorderLayout()).apply {
        this.isOpaque = true
        this.background = JBUI.CurrentTheme.CustomFrameDecorations.titlePaneInactiveBackground()
        this.border = JBUI.Borders.empty(10, 0)
    }

    private var panelContent: DialogPanel = panel {
        row { cell(progressBar).fullWidth() }
        row { cell(userPrompt).fullWidth().fullHeight() }
        row { cell(myList).fullWidth().fullHeight() }
    }

    private val scrollPanel: JBScrollPane = JBScrollPane(
        panelContent,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
    ).apply {
        this.verticalScrollBar.autoscrolls = true
    }

    init {
        setContent(scrollPanel)
    }

    fun onStart() {
        initializePreAllocatedBlocks(project)
        progressBar.isIndeterminate = true
    }

    private val blockViews: MutableList<LangSketch> = mutableListOf()
    private fun initializePreAllocatedBlocks(project: Project) {
        repeat(16) {
            runInEdt {
                val codeBlockViewer = CodeHighlightSketch(project, "", PlainTextLanguage.INSTANCE)
                blockViews.add(codeBlockViewer)
                myList.add(codeBlockViewer)
            }
        }
    }

    fun addRequestPrompt(text: String) {
        runInEdt {
            val codeBlockViewer = CodeHighlightSketch(project, text, CodeFenceLanguage.findLanguage("Markdown")).apply {
                initEditor(text)
            }

            codeBlockViewer.editorFragment?.setCollapsed(true)
            codeBlockViewer.editorFragment!!.updateExpandCollapseLabel()

            codeBlockViewer.editorFragment!!.editor.backgroundColor = JBColor(0xF7FAFDF, 0x2d2f30)

            userPrompt.add(codeBlockViewer, BorderLayout.CENTER)

            this.revalidate()
            this.repaint()
        }
    }

    fun onUpdate(text: String) {
        val codeFenceList = CodeFence.parseAll(text)
        codeFenceList.forEachIndexed { index, codeFence ->
            if (index < blockViews.size) {
                when (codeFence.originLanguage) {
                    "diff" -> {
                        if(codeFence.isComplete && blockViews[index] !is DiffLangSketch) {
                            blockViews[index] = DiffLangSketch(project, codeFence.text)
                            myList.remove(index)
                            myList.add(blockViews[index].getComponent(), index)
                            myList.revalidate() // 刷新布局
                            myList.repaint() // 重绘界面
                        } else {
                            blockViews[index].updateLanguage(codeFence.ideaLanguage)
                            blockViews[index].updateViewText(codeFence.text)
                        }
                    }
                    else -> {
                        blockViews[index].updateLanguage(codeFence.ideaLanguage)
                        blockViews[index].updateViewText(codeFence.text)
                    }
                }
            } else {
                runInEdt {
                    val codeBlockViewer = CodeHighlightSketch(project, codeFence.text, PlainTextLanguage.INSTANCE)
                    blockViews.add(codeBlockViewer)
                    myList.add(codeBlockViewer)
                }
            }
        }

        scrollToBottom()
    }

    fun onFinish(text: String) {
        runInEdt {
            blockViews.filter { it.getViewText().isEmpty() }.forEach {
                myList.remove(it.getComponent())
            }
        }

        progressBar.isIndeterminate = false
        progressBar.isVisible = false
        scrollToBottom()
    }

    private fun scrollToBottom() {
        SwingUtilities.invokeLater {
            val verticalScrollBar = scrollPanel.verticalScrollBar
            verticalScrollBar.value = verticalScrollBar.maximum
        }
    }

    override fun isNull(): Boolean {
        return !isVisible
    }
}