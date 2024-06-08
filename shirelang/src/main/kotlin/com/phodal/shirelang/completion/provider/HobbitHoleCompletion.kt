package com.phodal.shirelang.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext

class HobbitHoleCompletion : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val position = parameters.originalPosition ?: parameters.position
        val psiElement = PsiTreeUtil.prevVisibleLeaf(position)?.let {
            PsiTreeUtil.prevLeaf(it, true)
        } ?: return

        when (psiElement.text) {
            "hobbit" -> {
                result.addElement(LookupElementBuilder.create("Frodo"))
                result.addElement(LookupElementBuilder.create("Sam"))
                result.addElement(LookupElementBuilder.create("Merry"))
                result.addElement(LookupElementBuilder.create("Pippin"))
            }
        }
    }

}