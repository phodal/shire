package com.phodal.shirecore.ui

import com.intellij.openapi.application.runInEdt
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
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JProgressBar
import javax.swing.ScrollPaneConstants
import javax.swing.SwingUtilities

class ShirePanelView(val project: Project) : SimpleToolWindowPanel(true, true), NullableComponent {
    private var progressBar: JProgressBar = JProgressBar()
    private var myList = JPanel(VerticalLayout(JBUI.scale(10))).apply {
        this.isOpaque = true
        this.background = UIUtil.getListBackground()
    }
    private val myScrollPane: JBScrollPane = JBScrollPane(
        myList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
    ).apply {
        this.verticalScrollBar.autoscrolls = true
        this.background = UIUtil.getListBackground()
    }

    private val lastCodeBlockView: CodeBlockView = CodeBlockView(project, "")

    private var panelContent: DialogPanel = panel {
        row { cell(myScrollPane).fullWidth().fullHeight() }.resizableRow()
        row { cell(lastCodeBlockView).fullWidth() }.resizableRow()
        row { cell(progressBar).fullWidth() }
    }

    init {
        setContent(panelContent)
    }

    fun updateText(text: String) {
        lastCodeBlockView.updateText(text)
    }

    fun showFinalText(text: String) {
        runInEdt {
            panelContent.remove(lastCodeBlockView)

            val codeFence = CodeFence.parseAll(text)
            if (codeFence.isNotEmpty()) {
                codeFence.forEach {
                    val codeBlockView = CodeBlockView(project, it.text)
                    myList.add(codeBlockView)
                }
            }

            scrollToBottom()
        }
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