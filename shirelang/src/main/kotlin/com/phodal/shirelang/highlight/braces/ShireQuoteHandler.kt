package com.phodal.shirelang.highlight.braces

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import com.phodal.shirelang.psi.ShireTypes

class ShireQuoteHandler : SimpleTokenSetQuoteHandler(
    ShireTypes.QUOTE_STRING
)
