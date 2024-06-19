package com.phodal.shirelang.compiler.variable

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shirecore.middleware.select.SelectElementStrategy
import com.phodal.shirelang.compiler.SymbolTable
import com.phodal.shirelang.compiler.hobbit.HobbitHole

data class VariableResolverContext(
    val myProject: Project,
    val editor: Editor,
    val hole: HobbitHole?,
    val symbolTable: SymbolTable,
    var element: PsiElement?
)

class CompositeVariableResolver(
   private val context: VariableResolverContext
) : VariableResolver {
    override fun resolve(): Map<String, Any> {
        val element: PsiElement? = SelectElementStrategy.resolvePsiElement(context.myProject, context.editor)
        context.element = element

        val results = mutableMapOf<String, Any>()
        results.putAll(BuiltinVariableResolver(context).resolve())
        results.putAll(ContextVariableResolver(context).resolve())
        results.putAll(SystemInfoVariableResolver(context).resolve())
        results.putAll(UserCustomVariableResolver(context).resolve())
        return results
    }
}

