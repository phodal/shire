package com.phodal.shirelang.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.compiler.hobbit.HobbitHole

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
            HobbitHole.ACTION_LOCATION -> {
                ShireActionLocation.all().forEach {
                    result.addElement(LookupElementBuilder
                        .create(it)
                        .withIcon(ShireIcons.DEFAULT)
                        .withTypeText("Action Location", true)
                    )
                }
            }
        }
    }

}