package com.phodal.shirecore.sketch.patch

import com.intellij.diff.DiffContentFactoryEx
import com.intellij.diff.chains.SimpleDiffRequestChain
import com.intellij.diff.chains.SimpleDiffRequestProducer
import com.intellij.diff.editor.ChainDiffVirtualFile
import com.intellij.diff.editor.DiffEditorTabFilesManager
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.icons.AllIcons
import com.intellij.lang.Language
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.command.undo.UndoManager
import com.intellij.openapi.diff.impl.patch.TextFilePatch
import com.intellij.openapi.diff.impl.patch.apply.GenericPatchApplier
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.readText
import com.intellij.psi.PsiManager
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.DarculaColors
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.LocalTimeCounter
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.sketch.LangSketch
import com.phodal.shirecore.sketch.lint.SketchCodeInspection
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel


class SingleFileDiffView(
    private val myProject: Project,
    private val currentFile: VirtualFile,
    val patch: TextFilePatch,
) : LangSketch {
    private val mainPanel: JPanel = JPanel(VerticalLayout(5))
    private val myHeaderPanel: JPanel = JPanel(BorderLayout())
    private var filePanel: DialogPanel? = null
    var diffFile: ChainDiffVirtualFile? = null
    private val oldCode = currentFile.readText()
    private val appliedPatch = GenericPatchApplier.apply(oldCode, patch.hunks)
    private val newCode = appliedPatch?.patchedText ?: ""

    init {
        val contentPanel = JPanel(BorderLayout())
        val actions = createActionButtons()
        val filepathLabel = JBLabel(currentFile.name).apply {
            icon = currentFile.fileType.icon
            border = BorderFactory.createEmptyBorder(2, 10, 2, 10)

            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    val isShowDiffSuccess = showDiff()
                    if (isShowDiffSuccess) return

                    FileEditorManager.getInstance(myProject).openFile(currentFile, true)
                }

                override fun mouseEntered(e: MouseEvent) {
                    foreground = JBColor.WHITE
                    filePanel?.background = JBColor(DarculaColors.BLUE, DarculaColors.BLUE)
                }

                override fun mouseExited(e: MouseEvent) {
                    foreground = JBColor.BLACK
                    filePanel?.background = JBColor.PanelBackground
                }
            })
        }

        filePanel = panel {
            row {
                cell(filepathLabel).align(AlignX.FILL).resizableColumn()
                actions.forEachIndexed { index, action ->
                    cell(action).align(AlignX.LEFT)
                    if (index < actions.size - 1) {
                        this@panel.gap(RightGap.SMALL)
                    }
                }
            }
        }.apply {
            background = JBColor.PanelBackground
        }

        val fileContainer = JPanel(BorderLayout(10, 10)).also {
            it.add(filePanel)
        }
        contentPanel.add(fileContainer, BorderLayout.CENTER)

        mainPanel.add(myHeaderPanel)
        mainPanel.add(contentPanel)

        ApplicationManager.getApplication().executeOnPooledThread {
            lintCheckForNewCode(currentFile)
        }
    }

    fun lintCheckForNewCode(currentFile: VirtualFile) {
        if (newCode.isEmpty()) return
        val newFile = LightVirtualFile(currentFile, newCode, LocalTimeCounter.currentTime())
        val psiFile = runReadAction { PsiManager.getInstance(myProject).findFile(newFile) } ?: return
        val errors = SketchCodeInspection.runInspections(myProject, psiFile, currentFile)
        if (errors.isNotEmpty()) {
            SketchCodeInspection.showErrors(errors, this@SingleFileDiffView.mainPanel)
        }
    }

    private fun showDiff(): Boolean {
        if (diffFile != null) {
            showDiffFile(diffFile!!)
            return true
        }

        val document = FileDocumentManager.getInstance().getDocument(currentFile) ?: return false
        val appliedPatch = GenericPatchApplier.apply(document.text, patch.hunks)
            ?: return false

        val newText = appliedPatch.patchedText
        val diffFactory = DiffContentFactoryEx.getInstanceEx()
        val currentDocContent = diffFactory.create(myProject, currentFile)
        val newDocContent = diffFactory.create(newText)

        val diffRequest =
            SimpleDiffRequest(
                "Shire Diff - ${patch.beforeFileName}",
                currentDocContent,
                newDocContent,
                "Original",
                "AI generated"
            )

        val producer = SimpleDiffRequestProducer.create(currentFile.path) {
            diffRequest
        }

        val chain = SimpleDiffRequestChain.fromProducer(producer)
        runInEdt {
            diffFile = ChainDiffVirtualFile(chain, "Diff")
            showDiffFile(diffFile!!)
        }

        return true
    }

    private val diffEditorTabFilesManager = DiffEditorTabFilesManager.getInstance(myProject)

    private fun showDiffFile(diffFile: ChainDiffVirtualFile) {
        diffEditorTabFilesManager.showDiffFile(diffFile, true)
    }

    private fun createActionButtons(): List<JButton> {
        val undoManager = UndoManager.getInstance(myProject)
        val fileEditor = FileEditorManager.getInstance(myProject).getSelectedEditor(currentFile)

        val rollback = JButton(AllIcons.Actions.Rollback).apply {
            toolTipText = ShireCoreBundle.message("sketch.patch.action.rollback.tooltip")
            isEnabled = undoManager.isUndoAvailable(fileEditor)
            border = null
            isFocusPainted = false
            isContentAreaFilled = false

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

    override fun getViewText(): String = currentFile.readText()

    override fun updateViewText(text: String) {}

    override fun getComponent(): JComponent = mainPanel

    override fun updateLanguage(language: Language?, originLanguage: String?) {}

    override fun dispose() {}

    fun openDiffView() {
        showDiff()
    }
}
