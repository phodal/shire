package com.phodal.shirelang.compiler.template

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.phodal.shirelang.compiler.ast.hobbit.HobbitHole
import com.phodal.shirelang.compiler.variable.VariableTable
import com.phodal.shirelang.compiler.variable.resolver.CompositeVariableResolver
import com.phodal.shirelang.compiler.variable.resolver.base.VariableResolverContext

/**
 * The `ShireTemplateCompiler` class is responsible for compiling templates in a Kotlin project.
 * It takes a `Project`, a `HobbitHole`, a `SymbolTable`, and an `input` string as parameters.
 */
class ShireTemplateCompiler(
    private val myProject: Project,
    private val hole: HobbitHole?,
    private val variableTable: VariableTable,
    private val input: String,
) {
    private val customVariables: MutableMap<String, String> = mutableMapOf()

    var compiledVariables: Map<String, Any> = mapOf()

    suspend fun compile(): String {
        val prompt = doExecuteCompile()
        return cleanUp(prompt)
    }

    fun putCustomVariable(varName: String, varValue: String) {
        customVariables[varName] = varValue
    }

    private fun cleanUp(prompt: String) =
        prompt.trim()
            .replace("\n\n\n", "\n\n")

    private suspend fun doExecuteCompile(): String {
        val currentEditor = VariableTemplateCompiler.defaultEditor(myProject)

        if (currentEditor != null) {
            val additionalMap: Map<String, Any> = compileVariable(currentEditor, customVariables)

            compiledVariables = additionalMap.mapValues { it.value.toString() }

            val file = runReadAction {
                PsiManager.getInstance(myProject).findFile(currentEditor.virtualFile)
            } ?: return input

            val templateCompiler = VariableTemplateCompiler(file.language, file)

            templateCompiler.putAll(additionalMap)
            templateCompiler.putAll(customVariables)

            val finalPrompt = templateCompiler.compile(input)
            return finalPrompt
        }

        return input
    }

    suspend fun compileVariable(editor: Editor, customVariables: MutableMap<String, String>): Map<String, Any> {
        val context = VariableResolverContext(myProject, editor, hole, variableTable, null)
        val additionalMap: Map<String, Any> = CompositeVariableResolver(context).resolve(customVariables)
        return additionalMap
    }
}
