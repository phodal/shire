package com.phodal.shirelang.compiler

import com.intellij.openapi.project.Project
import com.phodal.shirelang.compile.VariableTemplateCompiler
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.variable.CompositeVariableResolver
import com.phodal.shirelang.compiler.variable._base.VariableResolverContext

/**
 * The `ShireTemplateCompiler` class is responsible for compiling templates in a Kotlin project.
 * It takes a `Project`, a `HobbitHole`, a `SymbolTable`, and an `input` string as parameters.
 */
class ShireTemplateCompiler(
    private val myProject: Project,
    private val hole: HobbitHole,
    private val symbolTable: SymbolTable,
    private val input: String,
) {
    fun compile(): String {
        val currentEditor = VariableTemplateCompiler.defaultEditor(myProject)
        val currentElement = VariableTemplateCompiler.defaultElement(myProject, currentEditor)

        if (currentElement != null && currentEditor != null) {
            val context = VariableResolverContext(myProject, currentEditor, hole, symbolTable, null)
            val additionalMap: Map<String, Any> = CompositeVariableResolver(context).resolve()

            val file = currentElement.containingFile
            val templateCompiler = VariableTemplateCompiler(file.language, file)

            templateCompiler.set(additionalMap)
            return templateCompiler.compile(input)
        }

        return input
    }
}
