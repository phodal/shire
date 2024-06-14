package com.phodal.shirelang.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import com.phodal.shirelang.actions.validator.PsiVariables

class WhenConditionCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        PsiVariables.completionProvider().forEach {
            val withTypeText =
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create(it.key).withTypeText(it.value, true),
                    99.0
                )

            result.addElement(withTypeText)
        }
    }
}
