package com.phodal.shirecore.ui

import com.intellij.lang.Language
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
import javax.swing.JPanel
import javax.swing.JProgressBar
import javax.swing.ScrollPaneConstants
import javax.swing.SwingUtilities

class ShirePanelView(val project: Project) : SimpleToolWindowPanel(true, true), NullableComponent {
    private var progressBar: JProgressBar = JProgressBar()
    private val lastCodeBlockView: CodeBlockView = CodeBlockView(project, "", Language.findLanguageByID("Markdown")!!)

    private var myList = JPanel(VerticalLayout(JBUI.scale(10))).apply {
        add(lastCodeBlockView)
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
        progressBar.isIndeterminate = true
    }

    fun onUpdate(text: String) {
        lastCodeBlockView.updateText(text)
    }

    fun onFinish(text: String) {
        progressBar.isIndeterminate = false
        progressBar.isVisible = false

        runInEdt {
            myList.remove(lastCodeBlockView)

            val codeFence = CodeFence.parseAll(text)
            if (codeFence.isNotEmpty()) {
                codeFence.forEach {
                    val codeBlockView = CodeBlockView(project, it.text, it.ideaLanguage)
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