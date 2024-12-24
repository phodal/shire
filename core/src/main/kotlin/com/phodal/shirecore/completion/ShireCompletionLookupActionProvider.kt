package com.phodal.shirecore.completion

import com.intellij.codeInsight.lookup.Lookup
import com.intellij.codeInsight.lookup.LookupActionProvider
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementAction
import com.intellij.util.Consumer

class ShireCompletionLookupActionProvider: LookupActionProvider {
    override fun fillActions(element: LookupElement, lookup: Lookup, consumer: Consumer<in LookupElementAction>) {
        if (element is ShireLookupElement<*>) {
            println("ShireLookupElement: $element")
        }
    }
}