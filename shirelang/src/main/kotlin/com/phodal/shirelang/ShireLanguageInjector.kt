// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.phodal.shirelang

import com.intellij.lang.Language
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.InjectedLanguagePlaces
import com.intellij.psi.LanguageInjector
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.util.elementType
import com.phodal.shirecore.utils.markdown.CodeFenceLanguage
import com.phodal.shirelang.parser.CodeBlockElement
import com.phodal.shirelang.parser.PatternElement
import com.phodal.shirelang.psi.ShireTypes
import com.phodal.shirelang.psi.impl.ShireFuncCallImpl

class ShireLanguageInjector : LanguageInjector {
    override fun getLanguagesToInject(host: PsiLanguageInjectionHost, registrar: InjectedLanguagePlaces) {
        injectRegexLanguage(host, registrar)
        injectCodeBlockLanguage(host, registrar)
        injectRegexFunction(host, registrar)
    }

    private fun injectRegexFunction(host: PsiLanguageInjectionHost, registrar: InjectedLanguagePlaces) {
        if (host !is ShireFuncCallImpl || !host.isValidHost()) return

        val args = host.pipelineArgs?.children ?: return

        val language = CodeFenceLanguage.findLanguage("RegExp")
        val funcLength = host.funcName.text.length

        args.firstOrNull()?.let { element ->
            when (element.node.firstChildNode.elementType) {
                ShireTypes.QUOTE_STRING -> {
                    val startOffset = element.startOffsetInParent + funcLength + 2
                    val endOffset = element.startOffsetInParent + funcLength + element.textLength
                    registrar.addPlace(language, TextRange(startOffset, endOffset), null, null)
                }
            }
        }
    }

    private fun injectRegexLanguage(host: PsiLanguageInjectionHost, registrar: InjectedLanguagePlaces) {
        if (host !is PatternElement || !host.isValidHost()) return

        val text = host.text
        val language = CodeFenceLanguage.findLanguage("RegExp")

        val range = TextRange(0, text.length)
        registrar.addPlace(language, range, null, null)
    }

    private fun injectCodeBlockLanguage(
        host: PsiLanguageInjectionHost,
        registrar: InjectedLanguagePlaces,
    ) {
        if (host !is CodeBlockElement || !host.isValidHost()) return

        val hasCodeContents = host.children.any { it.elementType == ShireTypes.CODE_CONTENTS }
        if (!hasCodeContents) return

        val text: String = if (host.isShireTemplateCodeBlock()) {
            ShireLanguage.INSTANCE.id
        } else {
            host.getLanguageId()?.text
        } ?: return

        val contentList = CodeBlockElement.obtainFenceContent(host) ?: return
        if (contentList.isEmpty()) return

        val language = CodeFenceLanguage.findLanguage(text)
        injectAsOnePlace(host, language, registrar)
    }

    private fun injectAsOnePlace(host: CodeBlockElement, language: Language, registrar: InjectedLanguagePlaces) {
        val contentList = CodeBlockElement.obtainFenceContent(host) ?: return

        val first = contentList.first()
        val last = contentList.last()

        try {
            val textRange = TextRange.create(first.startOffsetInParent, last.startOffsetInParent + last.textLength)
            registrar.addPlace(language, textRange, null, null)
        } catch (e: Exception) {
            logger<ShireLanguageInjector>().warn("Failed to inject language", e)
        }
    }
}
