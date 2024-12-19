package com.phodal.shirecore.ui

import com.intellij.icons.AllIcons
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.JProgressBar

class CustomProgressBar(private val view: ShirePanelView) : JPanel(BorderLayout()) {
    private val progressBar: JProgressBar = JProgressBar()

    var isIndeterminate = progressBar.isIndeterminate
        set(value) {
            progressBar.isIndeterminate = value
            field = value
        }

    private val cancelLabel = JBLabel(AllIcons.Actions.CloseHovered)

    init {

        cancelLabel.setBorder(JBUI.Borders.empty(0, 5))
        cancelLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                view.cancel("This progressBar is canceled")
            }
        })

        add(progressBar, BorderLayout.CENTER)
        add(cancelLabel, BorderLayout.EAST)
    }

    override fun setVisible(visible: Boolean) {
        super.setVisible(visible)
        progressBar.isVisible = visible
        cancelLabel.isVisible = visible
    }

}