package com.phodal.shirelang.saql.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

interface SaqlNameElement : PsiElement {
  val nameAsString: String
  val nameIsQuoted: Boolean
}

abstract class AbstractSaqlNameElement(node: ASTNode) : ASTWrapperPsiElement(node), SaqlNameElement {
  override val nameAsString: String
    get() {
      val leaf = firstChild.node
      return when (leaf.elementType) {
        SaqlPsiTypes.BRACKET_LITERAL -> leaf.text.substring(1, leaf.textLength - 1)
        SaqlPsiTypes.BACKTICK_LITERAL -> leaf.text.substring(1, leaf.textLength - 1).replace("``", "`")
        SaqlPsiTypes.SINGLE_QUOTE_STRING_LITERAL -> leaf.text.substring(1, leaf.textLength - 1).replace("''", "'")
        SaqlPsiTypes.DOUBLE_QUOTE_STRING_LITERAL -> leaf.text.substring(1, leaf.textLength - 1).replace("\"\"", "\"")
        else -> leaf.text
      }
    }

  override val nameIsQuoted get() = firstChild.node.elementType != SaqlPsiTypes.IDENTIFIER
}
