package com.phodal.shirelang.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import com.phodal.shirelang.psi.ShireTypes

class HobbitHoleCompletion : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val text = parameters.position.text
        // get keyword from hobbit hole
        val psiElement = PsiTreeUtil.findSiblingBackward(parameters.position, ShireTypes.FRONTMATTER_KEY, null)

        if (psiElement != null) {
            val keyword = psiElement.text
            if (keyword == "hobbit") {
                result.addElement(LookupElementBuilder.create("Frodo"))
                result.addElement(LookupElementBuilder.create("Sam"))
                result.addElement(LookupElementBuilder.create("Merry"))
                result.addElement(LookupElementBuilder.create("Pippin"))
            }
        }
    }

}