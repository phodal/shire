package com.phodal.shirelang.parser

import com.intellij.lang.ASTNode
import com.intellij.psi.ElementManipulators
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.impl.source.tree.injected.InjectionBackgroundSuppressor
import com.phodal.shirelang.psi.impl.ShireFuncCallImpl

class ShireGrepFuncCall(node: ASTNode) : ShireFuncCallImpl(node), PsiLanguageInjectionHost,
    InjectionBackgroundSuppressor {
    override fun isValidHost(): Boolean = true

    override fun updateText(text: String): PsiLanguageInjectionHost {
        return ElementManipulators.handleContentChange(this, text)
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> {
        return LiteralTextEscaper.createSimple(this, false)
    }
}