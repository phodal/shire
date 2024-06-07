package com.phodal.shirelang.compiler.hobbit

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.DefaultPsiElementStrategy
import com.phodal.shirecore.provider.PsiElementStrategy

sealed class SelectElementStrategy {
    /**
     * Selection element
     */
    abstract fun select()

    /**
     * Auto select parent block element, like function, class, etc.
     */
    object DEFAULT : SelectElementStrategy() {
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
}
