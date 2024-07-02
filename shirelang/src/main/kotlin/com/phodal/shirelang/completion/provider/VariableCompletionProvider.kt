package com.phodal.shirelang.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import com.phodal.shirecore.provider.variable.PsiContextVariable
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.completion.dataprovider.ContextVariable

class VariableCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        ContextVariable.values().forEach {
            val withTypeText =
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create(it.variable)
                        .withIcon(ShireIcons.Variable)
                        .withTypeText(it.description, true),
                    99.0
                )
            result.addElement(withTypeText)
        }

        PsiContextVariable.values().forEach {
            val withTypeText =
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create(it.variableName)
                        .withIcon(ShireIcons.Variable)
                        .withTypeText(it.description, true)
                        ,
                    90.0
                )
            result.addElement(withTypeText)
        }
    }
}
