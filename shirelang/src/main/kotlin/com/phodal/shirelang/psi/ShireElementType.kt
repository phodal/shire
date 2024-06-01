package com.phodal.shirelang.psi

import com.intellij.psi.tree.IElementType
import com.phodal.shirelang.ShireLanguage

class ShireElementType(debugName: String): IElementType(debugName, ShireLanguage.INSTANCE)