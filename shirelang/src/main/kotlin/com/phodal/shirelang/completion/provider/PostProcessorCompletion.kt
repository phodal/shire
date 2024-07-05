package com.phodal.shirelang.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import com.phodal.shirecore.middleware.PostProcessor
import com.phodal.shirelang.ShireIcons

class PostProcessorCompletion : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        PostProcessor.allNames().forEach {
            result.addElement(
                LookupElementBuilder
                    .create(it)
                    .withIcon(ShireIcons.DEFAULT)
                    .withInsertHandler { context: InsertionContext, item: LookupElement ->

                    }
            )
        }
    }
}