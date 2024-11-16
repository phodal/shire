package com.phodal.shirecore.provider.variable.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor

class ComplexityVisitor : PsiRecursiveElementVisitor(true) {
    var complexity = 0

    override fun visitElement(element: PsiElement) {
        processElement(element)
        if (shouldVisitElement(element)) {
            super.visitElement(element)
        }
        postProcess(element)
    }

    protected fun processElement(element: PsiElement) {
        // Implement logic to increase complexity and nesting
        complexity++
    }

    protected fun postProcess(element: PsiElement) {
        // Implement logic to decrease nesting
    }

    protected fun shouldVisitElement(element: PsiElement): Boolean {
        // Implement logic to determine if the element should be visited
        return true
    }
}
