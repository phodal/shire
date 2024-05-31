package com.phodal.shirelang.completion.provider

import com.phodal.shirelang.completion.dataprovider.ToolHubVariable
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import com.phodal.shirelang.ShireIcons

class AgentToolOverviewCompletion : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        ToolHubVariable.all().forEach { toolHub ->
            val elements = LookupElementBuilder.create(toolHub.hubName)
                .withIcon(ShireIcons.DEFAULT)
                .withTypeText("(${toolHub.description})", true)
                .withPresentableText(toolHub.hubName)
                .withTailText(toolHub.type, true)

            result.addElement(PrioritizedLookupElement.withPriority(elements, 0.0))
        }
    }
}
