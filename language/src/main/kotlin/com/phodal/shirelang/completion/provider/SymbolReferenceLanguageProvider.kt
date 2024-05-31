package com.phodal.shirelang.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.util.ProcessingContext
import com.phodal.shirelang.provider.ShireSymbolProvider

class SymbolReferenceLanguageProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        ShireSymbolProvider.all().forEach { completionProvider ->
            val elements = completionProvider.lookupSymbol(parameters.editor.project!!, parameters, result)
            elements.forEach {
                result.addElement(it)
            }
        }
    }

}
