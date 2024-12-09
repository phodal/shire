package com.phodal.shirecore.ui.viewer

import com.intellij.icons.AllIcons
import com.intellij.lang.Language
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.command.undo.UndoManager
import com.intellij.openapi.diff.impl.patch.PatchReader
import com.intellij.openapi.diff.impl.patch.TextFilePatch
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.VcsException
import com.intellij.openapi.vcs.changes.patch.*
import com.intellij.openapi.vcs.changes.ui.RollbackProgressModifier
import com.intellij.openapi.vcs.impl.projectlevelman.AllVcses
import com.intellij.openapi.vcs.rollback.RollbackEnvironment
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.sql.change
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.containers.MultiMap
import com.intellij.util.ui.JBUI
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.findFile
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class DiffLangSketch(private val myProject: Project, private var patchContent: String) : LangSketch {
    private val mainPanel: JPanel = JPanel(VerticalLayout(5))
    private val myHeaderPanel: JPanel = JPanel(BorderLayout())
    private val shelfExecutor = ApplyPatchDefaultExecutor(myProject)
    private val myReader = PatchReader(patchContent).also {
        try {
            it.parseAllPatches()
        } catch (e: Exception) {
            ShirelangNotifications.error(myProject, "Failed to parse patch: ${e.message}")
        }
    }
    private val filePatches: MutableList<TextFilePatch> = myReader.textPatches

    init {
        val createHeaderAction = createHeaderAction()
        myHeaderPanel.add(createHeaderAction, BorderLayout.EAST)

        mainPanel.add(myHeaderPanel)

        ApplicationManager.getApplication().invokeAndWait {
            if (filePatches.isEmpty()) {
                ShirelangNotifications.error(myProject, "PatchProcessor: no patches found")
                return@invokeAndWait
            }

            filePatches
                .filter { it.beforeFileName != null }
                .forEach { patch ->
                    val originFile = myProject.findFile(patch.beforeFileName!!) ?: return@forEach
                    val diffPanel = SingleFileDiffLangSketch(myProject, originFile).getComponent()
                    mainPanel.add(diffPanel)
                }
        }
    }

    private fun createHeaderAction(): JComponent {
        val acceptButton = JButton(ShireCoreBundle.message("sketch.patch.action.accept")).apply {
            icon = AllIcons.Actions.SetDefault
            toolTipText = ShireCoreBundle.message("sketch.patch.action.accept.tooltip")
            addActionListener {
                handleAcceptAction()
            }
        }

        val rejectButton = JButton(ShireCoreBundle.message("sketch.patch.action.reject")).apply {
            this.icon = AllIcons.Actions.Rollback
            this.toolTipText = ShireCoreBundle.message("sketch.patch.action.reject.tooltip")
            addActionListener {
                handleRejectAction()
            }
        }

        val viewDiffButton = JButton(ShireCoreBundle.message("sketch.patch.action.viewDiff")).apply {
            this.toolTipText = ShireCoreBundle.message("sketch.patch.action.viewDiff.tooltip")
            this.icon = AllIcons.Actions.ListChanges
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
        ApplicationManager.getApplication().invokeAndWait {
            val matchedPatches =
                MatchPatchPaths(myProject).execute(filePatches, true)

            val patchGroups = MultiMap<VirtualFile, AbstractFilePatchInProgress<*>>()
            for (patchInProgress in matchedPatches) {
                patchGroups.putValue(patchInProgress.base, patchInProgress)
            }

            if (filePatches.isEmpty()) {
                ShirelangNotifications.error(myProject, "PatchProcessor: no patches found")
                return@invokeAndWait
            }

            val pathsFromGroups = ApplyPatchDefaultExecutor.pathsFromGroups(patchGroups)
            val additionalInfo = myReader.getAdditionalInfo(pathsFromGroups)
            shelfExecutor.apply(filePatches, patchGroups, null, "LlmGen.diff", additionalInfo)
        }
    }

    private fun handleRejectAction() {
        val undoManager = UndoManager.getInstance(myProject)
        val fileEditor = FileEditorManager.getInstance(myProject).selectedEditor ?: return
        if (undoManager.isUndoAvailable(fileEditor)) {
            undoManager.undo(fileEditor)
        }
    }

    private fun handleViewDiffAction() {
        MyApplyPatchFromClipboardDialog(myProject, patchContent).show()
    }


    override fun getViewText(): String = patchContent
    override fun updateViewText(text: String) {
        this.patchContent = text
    }

    override fun getComponent(): JComponent = mainPanel
    override fun updateLanguage(language: Language?) {}
    override fun dispose() {}
}


class SingleFileDiffLangSketch(private val myProject: Project, private val filepath: VirtualFile) {
    private val mainPanel: JPanel = JPanel(VerticalLayout(5))
    private val myHeaderPanel: JPanel = JPanel(BorderLayout())

    private val originContent = String(filepath.contentsToByteArray())

    init {
        val contentPanel = JPanel(BorderLayout())
        val fileIcon = JLabel(filepath.fileType.icon)

        val filepathLabel = JBLabel(filepath.name).apply {
            foreground = JBColor(0x888888, 0x888888)
            background = JBColor(0xF5F5F5, 0x333333)

            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    FileEditorManager.getInstance(myProject).openFile(filepath, true)
                }

                override fun mouseExited(e: MouseEvent) {
                    foreground = JBColor(0x888888, 0x888888)
                }
            })
        }

        val filepathComponent = JPanel(BorderLayout()).apply {
            add(filepathLabel, BorderLayout.WEST)
            addMouseListener(object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent) {
                    foreground = JBColor(0x0000FF, 0x0000FF)
                    background = JBColor(0x0000FF, 0x0000FF)
                }

                override fun mouseClicked(e: MouseEvent?) {
                    FileEditorManager.getInstance(myProject).openFile(filepath, true)
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
        val fileEditor = FileEditorManager.getInstance(myProject).getSelectedEditor(filepath)

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

    fun getComponent(): JComponent {
        return mainPanel
    }
}
