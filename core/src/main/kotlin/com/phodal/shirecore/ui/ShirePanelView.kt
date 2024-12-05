package com.phodal.shirecore.ui

import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.NullableComponent
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.phodal.shirecore.utils.markdown.CodeFence
import javax.swing.JPanel
import javax.swing.JProgressBar
import javax.swing.ScrollPaneConstants
import javax.swing.SwingUtilities

class ShirePanelView(val project: Project) : SimpleToolWindowPanel(true, true), NullableComponent {
    private var progressBar: JProgressBar = JProgressBar()

    private var myList = JPanel(VerticalLayout(JBUI.scale(10))).apply {
        this.isOpaque = true
        this.background = UIUtil.getLabelBackground()
    }

    private val myScrollPane: JBScrollPane = JBScrollPane(
        myList,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
    ).apply {
        this.verticalScrollBar.autoscrolls = true
    }

    private var panelContent: DialogPanel = panel {
        row { cell(progressBar).fullWidth() }
        row { cell(myScrollPane).fullWidth().fullHeight() }.resizableRow()
    }

    init {
        setContent(panelContent)
    }

    fun onStart() {
        initializePreAllocatedBlocks(project)
        progressBar.isIndeterminate = true
    }

    private val blockViews: MutableList<CodeBlockView> = mutableListOf()
    private fun initializePreAllocatedBlocks(project: Project) {
        repeat(16) {
            runInEdt {
                val codeBlockView = CodeBlockView(project, "", PlainTextLanguage.INSTANCE)
                blockViews.add(codeBlockView)
                myList.add(codeBlockView)
            }
        }
    }

    fun onUpdate(text: String) {
        val codeFenceList = CodeFence.parseAll(text)
        codeFenceList.forEachIndexed { index, codeFence ->
            if (index < blockViews.size) {
                val codeBlockView = blockViews[index]

                codeBlockView.updateLanguage(codeFence.ideaLanguage)
                codeBlockView.updateText(codeFence.text)
            } else {
                runInEdt {
                    val codeBlockView = CodeBlockView(project, codeFence.text, PlainTextLanguage.INSTANCE)
                    blockViews.add(codeBlockView)
                    myList.add(codeBlockView)
                }
            }
        }

        myList.revalidate()
        myList.repaint()
    }

    fun onFinish(text: String) {
        runInEdt {
            blockViews.filter { it.getEditorText().isEmpty() }.forEach {
                myList.remove(it)
            }
        }

        progressBar.isIndeterminate = false
        progressBar.isVisible = false
        scrollToBottom()
    }

    private fun scrollToBottom() {
        SwingUtilities.invokeLater {
            val verticalScrollBar = myScrollPane.verticalScrollBar
            verticalScrollBar.value = verticalScrollBar.maximum
        }
    }

    override fun isNull(): Boolean {
        return !isVisible
    }
}