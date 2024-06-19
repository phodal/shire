package com.phodal.shirelang.compiler.variable

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.phodal.shirecore.provider.DefaultPsiContextVariableProvider
import com.phodal.shirecore.provider.PsiContextVariableProvider
import com.phodal.shirecore.provider.PsiVariable
import com.phodal.shirelang.compiler.SymbolTable

class BuiltinVariableResolver(
    myProject: Project,
    editor: Editor,
    private val symbolTable: SymbolTable,
    val element: PsiElement?
) : VariableResolver {
    private val variableProvider: PsiContextVariableProvider

    init {
        val psiFile = PsiManager.getInstance(myProject).findFile(editor.virtualFile)
        variableProvider = if (psiFile?.language != null) {
            PsiContextVariableProvider.provide(psiFile.language)
        } else {
            DefaultPsiContextVariableProvider()
        }
    }

    override fun resolve(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        symbolTable.getAllVariables().forEach {
            val psiVariable = PsiVariable.fromVariableName(it.key)
            if (psiVariable != null) {
                result[it.key] = try {
                    variableProvider.resolveVariableValue(element, psiVariable)
                } catch (e: Exception) {
                    logger<CompositeVariableSolver>().error("Failed to resolve variable: ${it.key}", e)
                    ""
                }
            } else {
                result[it.key] = ""
            }
        }

        return result
    }
}