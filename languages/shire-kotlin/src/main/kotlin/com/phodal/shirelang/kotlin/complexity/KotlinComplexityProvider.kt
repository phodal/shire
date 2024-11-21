package com.phodal.shirelang.kotlin.complexity

import com.intellij.psi.PsiElement
import com.phodal.shirecore.ast.ComplexitySink
import com.phodal.shirecore.ast.ComplexityVisitor
import com.phodal.shirecore.provider.complexity.ComplexityProvider

class KotlinComplexityProvider : ComplexityProvider {
    override fun process(element: PsiElement): Int {
        val sink = ComplexitySink()
        val visitor = visitor(sink)
        element.accept(visitor)
        return sink.getComplexity()
    }

    override fun visitor(sink: ComplexitySink): ComplexityVisitor {
        return KotlinLanguageVisitor(sink)
    }
}
