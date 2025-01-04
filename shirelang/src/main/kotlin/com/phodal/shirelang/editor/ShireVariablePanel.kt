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
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.datatransfer.StringSelection
import javax.swing.Box
import javax.swing.JPanel
import javax.swing.ScrollPaneConstants

class ShireVariablePanel : JPanel(BorderLayout()) {
    private val contentPanel = JBPanel<JBPanel<*>>(GridBagLayout())

    init {
        val scrollPane = JBScrollPane(
            contentPanel,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        )
        add(scrollPane, BorderLayout.CENTER)
        setupPanel()
    }

    private fun setupPanel() {
        contentPanel.background = JBColor(0xF5F5F5, 0x2B2D30)
        contentPanel.border = JBUI.Borders.empty(4)

        // 添加标题
        val titleLabel = JBLabel("Variables").apply {
            font = JBUI.Fonts.label(14f).asBold()
            border = JBUI.Borders.empty(4, 8)
        }
        
        contentPanel.add(titleLabel, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            anchor = GridBagConstraints.NORTHWEST
            weightx = 1.0
            gridx = 0
            insets = JBUI.insets(2)
        })
    }

    fun updateVariables(variables: Map<String, Any>) {
        contentPanel.removeAll()
        setupPanel()

        var gridy = 1
        variables.forEach { (key, value) ->
            addVariableRow(key, value, gridy++)
        }

        // 填充剩余空间
        contentPanel.add(Box.createVerticalGlue(), GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            anchor = GridBagConstraints.NORTHWEST
            weightx = 1.0
            weighty = 1.0
            gridx = 0
            gridy = gridy
        })

        revalidate()
        repaint()
    }

    private fun addVariableRow(key: String, value: Any, gridy: Int) {
        val varPanel = JBPanel<JBPanel<*>>(FlowLayout(FlowLayout.LEFT, 0, 0))
        varPanel.isOpaque = false
        varPanel.border = JBUI.Borders.compound(
            JBUI.Borders.customLine(JBColor(0xE6E6E6, 0x3C3F41), 0, 0, 1, 0),
            JBUI.Borders.empty(6, 8)
        )

        varPanel.add(createLabel(key, true))
        varPanel.add(JBLabel(": ").apply {
            foreground = JBColor(0x666666, 0x999999)
        })

        val valueStr = value.toString()
        varPanel.add(createLabel(valueStr, false))
        varPanel.add(Box.createHorizontalStrut(4))
        varPanel.add(createCopyButton(valueStr))

        contentPanel.add(varPanel, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            anchor = GridBagConstraints.NORTHWEST
            weightx = 1.0
            gridx = 0
            this.gridy = gridy
            insets = JBUI.insets(2)
        })
    }

    private fun createLabel(text: String, isKey: Boolean) = JBLabel(text).apply {
        font = JBUI.Fonts.label(11f)
        foreground = if (isKey) JBColor(0x666666, 0x999999) else JBColor(0x000000, 0xCCCCCC)
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
