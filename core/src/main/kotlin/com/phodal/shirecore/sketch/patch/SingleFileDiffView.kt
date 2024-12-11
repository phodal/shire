package com.phodal.shirecore.sketch.patch

import com.intellij.icons.AllIcons
import com.intellij.lang.Language
import com.intellij.openapi.command.undo.UndoManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.readText
import com.intellij.ui.DarculaColors
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.sketch.LangSketch
import java.awt.BorderLayout
import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*


class SingleFileDiffView(private val myProject: Project, private val virtualFile: VirtualFile) : LangSketch {
    private val mainPanel: JPanel = JPanel(VerticalLayout(5))
    private val myHeaderPanel: JPanel = JPanel(BorderLayout())

    init {
        val contentPanel = JPanel(BorderLayout())

        val filepathLabel = JBLabel(virtualFile.name).apply {
            icon = virtualFile.fileType.icon
            background = JBColor(Color(200, 216, 230), Color(135, 206, 235))
            isOpaque = true
            border = BorderFactory.createEmptyBorder(5, 10, 5, 10)

            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    FileEditorManager.getInstance(myProject).openFile(virtualFile, true)
                }

                override fun mouseEntered(e: MouseEvent) {
                    foreground = JBColor(Color.WHITE, Color.WHITE)
                    background = JBColor(DarculaColors.BLUE, DarculaColors.BLUE)
                }

                override fun mouseExited(e: MouseEvent) {
                    background = JBColor(Color(200, 216, 230), Color(135, 206, 235))
                }
            })
        }

        val actions = createActions()
        val filePanel: DialogPanel = panel {
            row {
                cell(filepathLabel).align(AlignX.FILL).resizableColumn()
                actions.forEach {
                    cell(it).align(AlignX.LEFT)
                }
            }
        }

        val fileContainer = JPanel(BorderLayout(10, 10)).also {
            it.add(filePanel)
        }
        contentPanel.add(fileContainer, BorderLayout.CENTER)

        mainPanel.add(myHeaderPanel)
        mainPanel.add(contentPanel)
    }

    private fun createActions(): List<JComponent> {
        val undoManager = UndoManager.getInstance(myProject)
        val fileEditor = FileEditorManager.getInstance(myProject).getSelectedEditor(virtualFile)

        val rollback = JButton(AllIcons.Actions.Rollback).apply {
            toolTipText = ShireCoreBundle.message("sketch.patch.action.rollback.tooltip")
            isOpaque = true
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

    override fun updateLanguage(language: Language?, originLanguage: String?) {}

    override fun dispose() {}
}
