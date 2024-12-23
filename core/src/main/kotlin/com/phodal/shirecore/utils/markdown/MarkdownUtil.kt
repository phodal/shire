// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.phodal.shirecore.utils.markdown

import org.intellij.markdown.IElementType
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

// https://github.com/JetBrains/markdown/issues/72
private val embeddedHtmlType = IElementType("ROOT")

object MarkdownUtil {
  fun toHtml(markdownText: String): String {
    val flavour = GFMFlavourDescriptor()
    val parsedTree = MarkdownParser(flavour).parse(embeddedHtmlType, markdownText)
    return HtmlGenerator(markdownText, parsedTree, flavour).generateHtml()
  }
}