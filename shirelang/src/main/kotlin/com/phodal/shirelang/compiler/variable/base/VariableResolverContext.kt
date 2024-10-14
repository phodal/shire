package com.phodal.shirelang.compiler.variable.base

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shirelang.compiler.variable.VariableTable
import com.phodal.shirelang.compiler.ast.hobbit.HobbitHole

data class VariableResolverContext(
    val myProject: Project,
    val editor: Editor,
    val hole: HobbitHole?,
    val variableTable: VariableTable,
    var element: PsiElement?
)