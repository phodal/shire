package com.phodal.shirelang.java.complexity

import com.intellij.psi.PsiElement
import com.phodal.shirecore.ast.ComplexitySink
import com.phodal.shirecore.ast.ComplexityVisitor
import com.phodal.shirecore.provider.complexity.ComplexityProvider

class JavaComplexityProvider : ComplexityProvider {
    override fun process(element: PsiElement): Int {
        return 0
    }

    override fun visitor(sink: ComplexitySink): ComplexityVisitor {
        return JavaLanguageVisitor(sink)
    }
}