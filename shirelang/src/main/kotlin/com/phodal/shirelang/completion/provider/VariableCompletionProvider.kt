package com.phodal.shirelang.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import com.phodal.shirecore.provider.variable.model.VcsToolchainVariable
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.completion.dataprovider.CompositeVariableProvider
import com.phodal.shirelang.completion.dataprovider.ContextVariable

class VariableCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        CompositeVariableProvider.all().forEach {
            val withTypeText =
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create(it.name)
                        .withIcon(ShireIcons.Variable)
                        .withTypeText(it.description, true),
                    it.priority
                )
            result.addElement(withTypeText)
        }
    }
}
