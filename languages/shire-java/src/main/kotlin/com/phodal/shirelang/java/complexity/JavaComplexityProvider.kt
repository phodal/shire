package com.phodal.shirelang.java.complexity

import com.intellij.psi.PsiElement
import com.phodal.shirecore.ast.ComplexitySink
import com.phodal.shirecore.ast.ComplexityVisitor
import com.phodal.shirecore.provider.complexity.ComplexityProvider

class JavaComplexityProvider : ComplexityProvider {
    override fun process(element: PsiElement): Int {
        val sink = ComplexitySink()
        val visitor = visitor(sink)
        element.accept(visitor)
        return sink.getComplexity()
    }

    override fun visitor(sink: ComplexitySink): ComplexityVisitor {
        return JavaLanguageVisitor(sink)
    }
}