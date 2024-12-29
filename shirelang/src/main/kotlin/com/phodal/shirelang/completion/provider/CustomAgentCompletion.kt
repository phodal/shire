package com.phodal.shirelang.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import com.phodal.shirecore.agent.CustomAgent

class CustomAgentCompletion : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val configs: List<CustomAgent> = CustomAgent.loadFromProject(parameters.originalFile.project)
        configs.forEach { config ->
            result.addElement(
                LookupElementBuilder.create(config.name)
                    .withInsertHandler { context, _ ->
                        context.document.insertString(context.tailOffset, " ")
                        context.editor.caretModel.moveCaretRelatively(1, 0, false, true, false)
                    }
                    .withTypeText(config.description, true))
        }
    }
}
