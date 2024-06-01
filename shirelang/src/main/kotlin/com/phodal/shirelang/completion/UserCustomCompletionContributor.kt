package com.phodal.shirelang.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns
import com.phodal.shirelang.completion.provider.CustomCommandCompletion
import com.phodal.shirelang.psi.ShireTypes

class UserCustomCompletionContributor : CompletionContributor() {
    init {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(ShireTypes.COMMAND_ID), CustomCommandCompletion())
    }
}
