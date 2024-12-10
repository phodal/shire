package com.phodal.shirecore.ui.viewer

import com.intellij.icons.AllIcons
import com.intellij.lang.Language
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.undo.UndoManager
import com.intellij.openapi.diff.impl.patch.PatchReader
import com.intellij.openapi.diff.impl.patch.TextFilePatch
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vcs.changes.patch.AbstractFilePatchInProgress
import com.intellij.openapi.vcs.changes.patch.ApplyPatchDefaultExecutor
import com.intellij.openapi.vcs.changes.patch.MatchPatchPaths
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.JBColor
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.util.containers.MultiMap
import com.intellij.util.ui.JBUI
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.findFile
import com.phodal.shirecore.provider.sketch.ExtensionLangSketch
import com.phodal.shirecore.ui.viewer.patch.MyApplyPatchFromClipboardDialog
import com.phodal.shirecore.ui.viewer.patch.SingleFileDiffView
import java.awt.BorderLayout
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class DiffLangSketch(private val myProject: Project, private var patchContent: String) : ExtensionLangSketch {
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
        val header = createHeaderAction()
        header.border = JBUI.Borders.emptyTop(10)

        myHeaderPanel.add(header, BorderLayout.EAST)
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
                    val diffPanel = SingleFileDiffView(myProject, originFile).getComponent()
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
        val beforeFileNames = filePatches.mapNotNull { it.beforeFileName }
        if (beforeFileNames.size > 1) {
            return defaultView()
        } else {
            val firstOrNull = FileEditorProvider.EP_FILE_EDITOR_PROVIDER.extensionList.firstOrNull {
                it.javaClass.simpleName == "DiffPatchFileEditorProvider"
            }

            if (firstOrNull != null) {
                val virtualFile = LightVirtualFile("diff.diff", patchContent)
                val editor = firstOrNull.createEditor(myProject, virtualFile)
                object: DialogWrapper(myProject) {
                    init {
                        title = "Diff Preview"
                        setOKButtonText("Accept")
                        init()
                    }

                    override fun doOKAction() {
                        handleAcceptAction()
                        super.doOKAction()
                    }

                    override fun createCenterPanel(): JComponent {
                        return editor.component
                    }
                }.show()
            } else {
                return defaultView()
            }
        }
    }

    private fun defaultView() {
        MyApplyPatchFromClipboardDialog(myProject, patchContent).show()
    }

    override fun getExtensionName(): String = "patch"
    override fun getViewText(): String = patchContent
    override fun updateViewText(text: String) {
        this.patchContent = text
    }

    override fun getComponent(): JComponent = mainPanel
    override fun updateLanguage(language: Language?, originLanguage: String?) {}
    override fun dispose() {}
}