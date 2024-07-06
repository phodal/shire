package com.phodal.shirelang.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import com.phodal.shirelang.ShireIcons
import com.phodal.shirecore.provider.variable.model.ConditionPsiVariable
import com.phodal.shirelang.compiler.hobbit.ast.ExpressionBuiltInMethod

class WhenConditionCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        ConditionPsiVariable.values().forEach {
            val withTypeText =
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder
                        .create(it.name)
                        .withIcon(ShireIcons.DEFAULT)
                        .withTypeText(it.description, true),
                    199.0
                )

            result.addElement(withTypeText)
        }
    }
}

class WhenConditionFunctionCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        ExpressionBuiltInMethod.completionProvider().forEach {
            val elementBuilder = LookupElementBuilder.create(it.methodName)
                .withTypeText(it.description, true)
                .withInsertHandler { context, _ ->
                    context.document.insertString(context.tailOffset, it.postInsertString)
                    context.editor.caretModel.moveCaretRelatively(it.moveCaret, 0, false, false, false)
                }

            val withTypeText =
                PrioritizedLookupElement.withPriority(
                    elementBuilder, 99.0
                )

            result.addElement(withTypeText)
        }
    }
}