package com.phodal.shirecore.ui.viewer

import com.intellij.icons.AllIcons
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import javax.swing.*


class DiffPatchViewer(filepath: String) : SketchViewer {
    private val mainPanel: JPanel = JPanel(VerticalLayout(5))
    private val myHeaderPanel: JPanel = JPanel(BorderLayout())

    init {
        myHeaderPanel.add(createHeaderAction(), BorderLayout.EAST)

        val contentPanel = JPanel(BorderLayout())
        val langaugeIcon = getFileIcon()

        val filePathLabel = JBLabel(filepath).apply {
            foreground = JBColor(0x888888, 0x888888)
            background = JBColor(0xF5F5F5, 0x333333)

            addMouseListener(object : java.awt.event.MouseAdapter() {
                override fun mouseEntered(e: java.awt.event.MouseEvent) {
                    foreground = JBColor(0x333333, 0x111111)
                }

                override fun mouseExited(e: java.awt.event.MouseEvent) {
                    foreground = JBColor(0x888888, 0x888888)
                }
            })
        }

        val actions = JLabel(
            AllIcons.Actions.Rollback
        ).apply {
            foreground = UIManager.getColor("Label.foreground")
        }

        val filePanel = panel {
            row {
                cell(langaugeIcon).align(AlignX.LEFT)
                cell(filePathLabel).align(AlignX.LEFT)

                cell(actions).align(AlignX.RIGHT)
            }
        }.also {
            it.background = JBColor(0xF5F5F5, 0x333333)
            it.border = JBUI.Borders.empty(10)
        }

        contentPanel.add(filePanel, BorderLayout.CENTER)

        mainPanel.add(myHeaderPanel)
        mainPanel.add(contentPanel)
    }

    /**
     * [com.intellij.util.IconUtil.getIcon]
     */
    private fun getFileIcon(): JLabel {
        val langaugeIcon = JLabel(
            AllIcons.FileTypes.Java
        ).apply {
            foreground = UIManager.getColor("Label.foreground")
        }

        return langaugeIcon
    }

    private fun createHeaderAction(): JComponent {
        val acceptButton = JButton("Accept").apply {
            icon = AllIcons.Actions.SetDefault
            toolTipText = "Accept"
            addActionListener {
                handleAcceptAction()
            }
        }

        val rejectButton = JButton("Reject").apply {
            icon = AllIcons.Actions.Rollback
            toolTipText = "Reject"
            addActionListener {
                handleRejectAction()
            }
        }

        val viewDiffButton = JButton("View Diff").apply {
            toolTipText = "View Diff"
            icon = AllIcons.Actions.ListChanges
            addActionListener {
                handleViewDiffAction()
            }
        }

        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        panel.add(acceptButton)
        panel.add(rejectButton)
        panel.add(viewDiffButton)

        panel.background = JBColor(0xF5F5F5, 0x333333)

        return panel
    }

    private fun handleAcceptAction() {
        println("Accept action triggered")
    }

    private fun handleRejectAction() {
        println("Reject action triggered")
    }

    private fun handleViewDiffAction() {
        println("View Diff action triggered")
    }

    override fun getComponent(): JPanel {
        return mainPanel
    }

    override fun getViewText(): String {
        return ""
    }

    override fun updateViewText(text: String) {
        return
    }

    override fun dispose() {
        // 清理资源
        mainPanel.removeAll()
    }
}
