package com.phodal.shirelang.editor

import com.intellij.openapi.fileEditor.AsyncFileEditorProvider
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.WeighedFileEditorProvider
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.phodal.shirelang.ShireFileType

class ShirePreviewEditorProvider : WeighedFileEditorProvider(), AsyncFileEditorProvider {
    override fun accept(project: Project, file: VirtualFile): Boolean {
        return FileTypeRegistry.getInstance().isFileOfType(file, ShireFileType.INSTANCE)
    }

    override fun createEditor(project: Project, virtualFile: VirtualFile): FileEditor {
        return ShirePreviewEditor(project, virtualFile)
    }

    override fun createEditorAsync(project: Project, file: VirtualFile): AsyncFileEditorProvider.Builder {
        return object : AsyncFileEditorProvider.Builder() {
            override fun build(): FileEditor {
                return ShirePreviewEditor(project, file)
            }
        }
    }

    override fun getEditorTypeId(): String = "shire-preview-editor"

    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR
}
