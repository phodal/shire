package com.phodal.shirelang.saql.psi

import com.intellij.psi.tree.IElementType
import com.phodal.shirelang.saql.SaqlLanguage

class SaqlAstNodeType(debugName: String) : IElementType(debugName, SaqlLanguage.INSTANCE)