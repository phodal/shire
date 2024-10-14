package com.phodal.shirelang.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.compiler.ast.hobbit.HobbitHole

class HobbitHoleKeyCompletion : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        HobbitHole.keys().forEach {
            val element = LookupElementBuilder.create(it.key)
                .withIcon(ShireIcons.Idea)
                .withTypeText(it.value, true)

            result.addElement(PrioritizedLookupElement.withPriority(element, 0.0))
        }
    }

}
