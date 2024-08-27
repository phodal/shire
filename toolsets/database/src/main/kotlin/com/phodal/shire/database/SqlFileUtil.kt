package com.phodal.shire.database

import com.intellij.openapi.application.runReadAction
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.sql.psi.SqlFile


fun SqlFile.verifySqlElement(): MutableList<String> {
    val errors = mutableListOf<String>()
    val visitor = object : SqlSyntaxCheckingVisitor() {
        override fun visitElement(element: PsiElement) {
            if (element is PsiErrorElement) {
                errors.add("Syntax error at position ${element.textRange.startOffset}: ${element.errorDescription}")
            }
            super.visitElement(element)
        }
    }

    this.accept(visitor)
    return errors
}

abstract class SqlSyntaxCheckingVisitor : com.intellij.psi.PsiElementVisitor() {
    override fun visitElement(element: PsiElement) {
        runReadAction {
            element.children.forEach { it.accept(this) }
        }
    }
}