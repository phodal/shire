package com.phodal.shirecore.ui.viewer.patch

import com.intellij.icons.AllIcons
import com.intellij.lang.Language
import com.intellij.openapi.command.undo.UndoManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.readText
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.ui.viewer.LangSketch
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*


class SingleFileDiffView(private val myProject: Project, private val virtualFile: VirtualFile) : LangSketch {
    private val mainPanel: JPanel = JPanel(VerticalLayout(5))
    private val myHeaderPanel: JPanel = JPanel(BorderLayout())

    init {
        val contentPanel = JPanel(BorderLayout())
        val fileIcon = JLabel(virtualFile.fileType.icon)

        val filepathLabel = JBLabel(virtualFile.name).apply {
            foreground = JBColor(0x888888, 0x888888)
            background = JBColor(0xF5F5F5, 0x333333)

            addMouseListener(object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent) {
                    foreground = JBColor(0x0000FF, 0x0000FF)
                    background = JBColor(0xF5F5F5, 0x333333)
                }

                override fun mouseClicked(e: MouseEvent?) {
                    FileEditorManager.getInstance(myProject).openFile(virtualFile, true)
                }

                override fun mouseExited(e: MouseEvent) {
                    foreground = JBColor(0x888888, 0x888888)
                }
            })
        }

        val filepathComponent = JPanel(BorderLayout()).apply {
            add(filepathLabel, BorderLayout.WEST)
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    FileEditorManager.getInstance(myProject).openFile(virtualFile, true)
                }
            })
        }

        val actions = createActions()

        val filePanel = panel {
            row {
                cell(fileIcon).align(AlignX.LEFT)
                cell(filepathComponent).align(AlignX.LEFT)
                actions.forEach {
                    cell(it).align(AlignX.RIGHT)
                }
            }
        }.also {
            it.background = JBColor(0xF5F5F5, 0x333333)
            it.border = JBUI.Borders.empty(10)
        }

        contentPanel.add(filePanel, BorderLayout.CENTER)

        mainPanel.add(myHeaderPanel)
        mainPanel.add(contentPanel)
    }

    private fun createActions(): List<JComponent> {
        val undoManager = UndoManager.getInstance(myProject)
        val fileEditor = FileEditorManager.getInstance(myProject).getSelectedEditor(virtualFile)

        val rollback = JButton(AllIcons.Actions.Rollback).apply {
            toolTipText = ShireCoreBundle.message("sketch.patch.action.rollback.tooltip")
            isOpaque = true
            border = BorderFactory.createEmptyBorder()
            background = JBColor(0xF5F5F5, 0x333333)
            isEnabled = undoManager.isUndoAvailable(fileEditor)

            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    if (undoManager.isUndoAvailable(fileEditor)) {
                        undoManager.undo(fileEditor)
                    }
                }
            })
        }

        return listOf(rollback)
    }

    override fun getViewText(): String = virtualFile.readText()

    override fun updateViewText(text: String) {}

    override fun getComponent(): JComponent = mainPanel

    override fun updateLanguage(language: Language?) {}

    override fun dispose() {}
}
