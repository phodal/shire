package com.phodal.shirecore.middleware.select

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.PsiElement

sealed class SelectElementStrategy {
    /**
     * Selection element
     */
    abstract fun select()

    /**
     * Auto select parent block element, like function, class, etc.
     */
    object Blocked : SelectElementStrategy() {
        override fun select() {
            val project = ProjectManager.getInstance().openProjects.firstOrNull() ?: return
            val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
            val elementToAction = DefaultPsiElementStrategy().getElementToAction(project, editor) ?: return
            selectElement(elementToAction, editor)
        }

        /**
         * This function selects the specified PsiElement in the editor by setting the selection range from the start offset to the end offset of the element.
         *
         * @param elementToExplain the PsiElement to be selected in the editor
         * @param editor the Editor in which the selection is to be made
         */
        private fun selectElement(elementToExplain: PsiElement, editor: Editor) {
            val startOffset = elementToExplain.textRange.startOffset
            val endOffset = elementToExplain.textRange.endOffset

            editor.selectionModel.setSelection(startOffset, endOffset)
        }
    }

    object Selected: SelectElementStrategy() {
        override fun select() {
            // do nothing
        }
    }

    object Default: SelectElementStrategy() {
        override fun select() {
            val project = ProjectManager.getInstance().openProjects.firstOrNull() ?: return
            val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return

            val selectionModel = editor.selectionModel
            if (!selectionModel.hasSelection()) {
                Blocked.select()
            }
        }
    }

    object All: SelectElementStrategy() {
        override fun select() {
            val project = ProjectManager.getInstance().openProjects.firstOrNull() ?: return
            val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return

            val selectionModel = editor.selectionModel
            selectionModel.setSelection(0, editor.document.textLength)
        }
    }

    companion object {
        fun fromString(strategy: String): SelectElementStrategy {
            return when (strategy.lowercase()) {
                "block" -> Blocked
                "select" -> Selected
                "all" -> All
                else -> Default
            }
        }
    }
}
