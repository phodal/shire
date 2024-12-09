package com.phodal.shirecore.ui.viewer

import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffManager
import com.intellij.diff.contents.DocumentContent
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.diff.util.DiffUserDataKeys
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.icons.AllIcons
import com.intellij.lang.Language
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.diff.impl.patch.PatchReader
import com.intellij.openapi.diff.impl.patch.TextFilePatch
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vcs.VcsApplicationSettings
import com.intellij.openapi.vcs.VcsBundle
import com.intellij.openapi.vcs.changes.patch.*
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.IdeBorderFactory
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
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
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
            icon = AllIcons.Actions.Rollback
            toolTipText = ShireCoreBundle.message("sketch.patch.action.reject.tooltip")
            addActionListener {
                handleRejectAction()
            }
        }

        val viewDiffButton = JButton(ShireCoreBundle.message("sketch.patch.action.viewDiff")).apply {
            toolTipText = ShireCoreBundle.message("sketch.patch.action.viewDiff.tooltip")
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
        ApplicationManager.getApplication().invokeAndWait {
            val matchedPatches =
                MatchPatchPaths(myProject).execute(filePatches, true)

            val patchGroups = MultiMap<VirtualFile, AbstractFilePatchInProgress<*>>()
            for (patchInProgress in matchedPatches) {
                patchGroups.putValue(patchInProgress.base, patchInProgress)
            }

            if(filePatches.isEmpty() ) {
                ShirelangNotifications.error(myProject, "PatchProcessor: no patches found")
                return@invokeAndWait
            }

            val additionalInfo = myReader.getAdditionalInfo(ApplyPatchDefaultExecutor.pathsFromGroups(patchGroups))
            shelfExecutor.apply(filePatches, patchGroups, null, "LlmGen.diff", additionalInfo)
        }
    }

    private fun handleRejectAction() {
        //
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

        val filepathComponent = JBLabel(filepath.name).apply {
            foreground = JBColor(0x888888, 0x888888)
            background = JBColor(0xF5F5F5, 0x333333)

            addMouseListener(object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent) {
                    foreground = JBColor(0x0000FF, 0x0000FF)
                }

                override fun mouseClicked(e: MouseEvent?) {
                    FileEditorManager.getInstance(myProject).openFile(filepath, true)
                }

                override fun mouseExited(e: MouseEvent) {
                    foreground = JBColor(0x888888, 0x888888)
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
        val rollback = JLabel(AllIcons.Actions.Rollback).apply {
            toolTipText = ShireCoreBundle.message("sketch.patch.action.rollback.tooltip")
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    runWriteAction {
                        filepath.setBinaryContent(originContent.toByteArray())
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

class MyApplyPatchFromClipboardDialog(project: Project, clipboardText: String) :
    ApplyPatchDifferentiatedDialog(
        project, ApplyPatchDefaultExecutor(project), emptyList(), ApplyPatchMode.APPLY_PATCH_IN_MEMORY,
        LightVirtualFile("clipboardPatchFile", clipboardText), null, null,  //NON-NLS
        null, null, null, false
    ) {
    override fun createDoNotAskCheckbox(): JComponent? {
        return createAnalyzeOnTheFlyOptionPanel()
    }

    companion object {
        private fun createAnalyzeOnTheFlyOptionPanel(): JCheckBox {
            val removeOptionCheckBox =
                JCheckBox(VcsBundle.message("patch.apply.analyze.from.clipboard.on.the.fly.checkbox"))
            removeOptionCheckBox.mnemonic = KeyEvent.VK_L
            removeOptionCheckBox.isSelected = VcsApplicationSettings.getInstance().DETECT_PATCH_ON_THE_FLY
            removeOptionCheckBox.addActionListener { e: ActionEvent? ->
                VcsApplicationSettings.getInstance().DETECT_PATCH_ON_THE_FLY = removeOptionCheckBox.isSelected
            }
            return removeOptionCheckBox
        }
    }
}
