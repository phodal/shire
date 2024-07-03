package com.phodal.shirecore.provider.context

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.phodal.shirecore.action.ShireActionLocation

interface ActionLocationEditor {
    fun isApplicable(hole: ShireActionLocation): Boolean

    fun resolve(project: Project, hole: ShireActionLocation): Editor?

    companion object {
        private val EP_NAME: ExtensionPointName<ActionLocationEditor> =
            ExtensionPointName.create("com.phodal.shireActionLocationEditor")

        fun provide(project: Project, location: ShireActionLocation? = null): Editor? {
            if (location == null) {
                return defaultEditor(project)
            }

            return EP_NAME.extensionList.filter {
                it.isApplicable(location)
            }.map {
                it.resolve(project, location)
            }.first() ?: defaultEditor(project)
        }

        fun defaultEditor(project: Project) =
            FileEditorManager.getInstance(project).selectedTextEditor
    }
}
