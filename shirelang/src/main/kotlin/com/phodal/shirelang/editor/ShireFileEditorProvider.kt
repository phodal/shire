package com.phodal.shirelang.editor

import com.intellij.codeHighlighting.BackgroundEditorHighlighter
import com.intellij.icons.AllIcons
import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.fileEditor.impl.text.TextEditorImpl
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.pom.Navigatable
import com.intellij.testFramework.LightVirtualFile
import com.phodal.shirelang.ShireFileType
import org.jetbrains.annotations.NonNls
import java.awt.BorderLayout
import java.beans.PropertyChangeListener
import javax.swing.JPanel

class ShireFileEditorProvider : FileEditorProvider, DumbAware {
    override fun getEditorTypeId() = "r-editor"

    override fun accept(project: Project, file: VirtualFile) =
        FileTypeRegistry.getInstance().isFileOfType(file, ShireFileType.INSTANCE)

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        val editor = TextEditorProvider.getInstance().createEditor(project, file) as TextEditorImpl
        if (editor.file is LightVirtualFile) {
            return editor
        }

        return ShireFileEditor(project, editor, file)
    }

    override fun getPolicy() = FileEditorPolicy.HIDE_DEFAULT_EDITOR
}

open class ShireFileEditor(
    val project: Project,
    val textEditor: TextEditor,
    val virtualFile: VirtualFile,
) : UserDataHolderBase(), TextEditor {
    protected val mainComponent = JPanel(BorderLayout())

    init {
        val toolbarComponent = createToolbar().component
        mainComponent.add(textEditor.component, BorderLayout.CENTER)
        mainComponent.add(toolbarComponent, BorderLayout.NORTH)
    }

    override fun dispose() {
        TextEditorProvider.getInstance().disposeEditor(textEditor)
    }

    private fun createToolbar(): ActionToolbar {
        return ActionManager.getInstance()
            .createActionToolbar(ActionPlaces.EDITOR_TOOLBAR, createActionGroup(project, virtualFile, editor), true)
            .also {
                it.targetComponent = editor.contentComponent
            }
    }

    private fun createActionGroup(project: Project, report: VirtualFile, editor: Editor): ActionGroup {
        return DefaultActionGroup(
            createRunShireAction(project),
        )
    }

    private fun createRunShireAction(project: Project): AnAction {
        val idleIcon = AllIcons.Actions.Execute
        return object : AnAction("Run Shire", "Run Shire", idleIcon) {
            override fun actionPerformed(e: AnActionEvent) {
                /// run current File
            }
        }
    }

    override fun getComponent() = mainComponent
    override fun getState(level: FileEditorStateLevel): FileEditorState = textEditor.getState(level)
    override fun setState(state: FileEditorState) = textEditor.setState(state)
    override fun isModified(): Boolean = textEditor.isModified
    override fun isValid(): Boolean = textEditor.isValid
    override fun getBackgroundHighlighter(): BackgroundEditorHighlighter? = textEditor.backgroundHighlighter
    override fun getCurrentLocation(): FileEditorLocation? = textEditor.currentLocation
    override fun getPreferredFocusedComponent() = textEditor.preferredFocusedComponent
    override fun getName(): String = "Shire Editor"

    override fun getStructureViewBuilder(): StructureViewBuilder? = textEditor.structureViewBuilder
    override fun getEditor(): Editor = textEditor.editor
    override fun navigateTo(navigatable: Navigatable) = textEditor.navigateTo(navigatable)
    override fun canNavigateTo(navigatable: Navigatable): Boolean = textEditor.canNavigateTo(navigatable)
    override fun getFile(): VirtualFile = virtualFile

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {
        textEditor.addPropertyChangeListener(listener)
    }

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {
        textEditor.removePropertyChangeListener(listener)
    }
}
