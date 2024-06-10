// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.phodal.shirelang.parser

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.ElementManipulators
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.impl.source.tree.injected.InjectionBackgroundSuppressor
import com.intellij.psi.util.elementType
import com.phodal.shirelang.psi.ShireTypes

class PatternElement(node: ASTNode) : ASTWrapperPsiElement(node), PsiLanguageInjectionHost,
    InjectionBackgroundSuppressor {
    override fun isValidHost(): Boolean {
        return this.elementType != ShireTypes.PATTERN
    }

    override fun updateText(text: String): PsiLanguageInjectionHost {
        return ElementManipulators.handleContentChange(this, text)
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> {
        return LiteralTextEscaper.createSimple(this, false)
    }
}

