package com.phodal.shirelang.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.util.ProcessingContext
import com.phodal.shirecore.provider.ProjectRunService

class ProjectRunProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        ProjectRunService.all().forEach { provider ->
            provider
                .lookupAvailableTask(parameters.editor.project!!, parameters, result)
                .map { PrioritizedLookupElement.withPriority(it, 99.0) }
                .forEach {
                    result.addElement(it)
                }
        }
    }
}