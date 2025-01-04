package com.phodal.shirelang.editor

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import com.intellij.ui.table.JBTable
import java.awt.BorderLayout
import java.awt.datatransfer.StringSelection
import javax.swing.JPanel
import javax.swing.ScrollPaneConstants
import javax.swing.table.DefaultTableModel

class ShireVariablePanel : JPanel(BorderLayout()) {
    private val contentPanel = JBPanel<JBPanel<*>>(BorderLayout())
    private val tableModel = DefaultTableModel(arrayOf("Key", "Value", ""), 0)

    init {
        val table = JBTable(tableModel).apply {
            tableHeader.reorderingAllowed = true
            tableHeader.resizingAllowed = true
            setShowGrid(true)
            gridColor = JBColor.PanelBackground
            intercellSpacing = JBUI.size(0, 0)
        }

        val scrollPane = JBScrollPane(
            table,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        ).apply {
            minimumSize = JBUI.size(0, 200)
            preferredSize = JBUI.size(0, 200)
        }
        add(scrollPane, BorderLayout.CENTER)
        setupPanel()
    }

    private fun setupPanel() {
        contentPanel.background = JBColor(0xF5F5F5, 0x2B2D30)

        val titleLabel = JBLabel("Variables").apply {
            font = JBUI.Fonts.label(14f).asBold()
            border = JBUI.Borders.empty(4, 8)
        }

        contentPanel.add(titleLabel, BorderLayout.NORTH)
    }

    fun updateVariables(variables: Map<String, Any>) {
        tableModel.rowCount = 0 // 清空表格内容

        variables.forEach { (key, value) ->
            val valueStr = value.toString()
            tableModel.addRow(arrayOf(key, valueStr, createCopyButton(valueStr)))
        }

        revalidate()
        repaint()
    }

    private fun createCopyButton(text: String) = ActionButton(
        object : AnAction(AllIcons.Actions.Copy) {
            override fun actionPerformed(e: AnActionEvent) {
                CopyPasteManager.getInstance().setContents(StringSelection(text))
            }
        },
        Presentation().apply { icon = AllIcons.Actions.Copy },
        "Variables",
        JBUI.size(16)
    ).apply {
        toolTipText = "Copy value"
        border = JBUI.Borders.empty(2)
    }
}
