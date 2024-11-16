package com.phodal.shirecore.provider.variable.impl

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.phodal.shirecore.provider.variable.PsiContextVariableProvider
import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import git4idea.repo.GitRepositoryManager
import git4idea.history.GitFileHistory
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.PsiElementVisitor

class PsiContextVariableProviderImpl : PsiContextVariableProvider {
    override fun resolve(variable: PsiContextVariable, project: Project, editor: Editor, psiElement: PsiElement?): Any {
        val psiFile = PsiManager.getInstance(project).findFile(editor.virtualFile) ?: return ""

        return when (variable) {
            PsiContextVariable.CHANGE_COUNT -> changeCount(psiFile, project)
            PsiContextVariable.LINE_COUNT -> lineCount(psiFile)
            PsiContextVariable.COMPLEXITY_COUNT -> complexityCount(psiFile)
            else -> ""
        }
    }

    private fun changeCount(psiFile: PsiFile, project: Project): Int {
        val repository = GitRepositoryManager.getInstance(project).getRepositoryForFileQuick(psiFile.virtualFile)
        val history = GitFileHistory(project, repository!!.root, psiFile.virtualFile)
        return history.commits.size
    }

    private fun lineCount(psiFile: PsiFile): Int {
        return psiFile.text.lines().size
    }

    private fun complexityCount(psiFile: PsiFile): Int {
        val visitor = ComplexityVisitor()
        psiFile.accept(visitor)
        return visitor.complexity
    }

    private class ComplexityVisitor : PsiRecursiveElementVisitor() {
        var complexity = 0

        override fun visitElement(element: PsiElement) {
            super.visitElement(element)
            if (shouldVisitElement(element)) {
                complexity++
            }
        }

        private fun shouldVisitElement(element: PsiElement): Boolean {
            // Implement logic to determine if the element should be visited
            return true
        }
    }
}
