package com.phodal.shirelang.compiler.variable._base

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shirelang.compiler.SymbolTable
import com.phodal.shirelang.compiler.hobbit.HobbitHole

data class VariableResolverContext(
    val myProject: Project,
    val editor: Editor,
    val hole: HobbitHole?,
    val symbolTable: SymbolTable,
    var element: PsiElement?
)