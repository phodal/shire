package com.phodal.shirelang.compiler.variable

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shirecore.middleware.select.SelectElementStrategy
import com.phodal.shirelang.compiler.SymbolTable
import com.phodal.shirelang.compiler.hobbit.HobbitHole

class CompositeVariableResolver(
    private val myProject: Project,
    private val editor: Editor,
    private val hole: HobbitHole?,
    private val symbolTable: SymbolTable,
) : VariableResolver {
    override fun resolve(): Map<String, Any> {
        val element: PsiElement? = SelectElementStrategy.resolvePsiElement(myProject, editor)

        // TODO: refactor code
        val results = mutableMapOf<String, Any>()
        results.putAll(BuiltinVariableResolver(myProject, editor, symbolTable, element).resolve())
        results.putAll(ContextVariableResolver(editor, element).resolve())
        results.putAll(SystemInfoVariableResolver().resolve())
        results.putAll(UserCustomVariableResolver(myProject, editor, hole).resolve())
        return results
    }
}

