package com.phodal.shirelang.saql.psi

import com.intellij.psi.PsiElement

interface SaqlNameElement : PsiElement {
  val nameAsString: String
  val nameIsQuoted: Boolean
}
