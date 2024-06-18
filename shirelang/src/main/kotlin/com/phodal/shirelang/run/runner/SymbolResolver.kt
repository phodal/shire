package com.phodal.shirelang.run.runner

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiUtilBase
import com.phodal.shirecore.provider.DefaultPsiContextVariableProvider
import com.phodal.shirecore.provider.PsiContextVariableProvider
import com.phodal.shirecore.provider.PsiVariable
import com.phodal.shirelang.compiler.SymbolTable
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.patternaction.VariablePatternActionExecutor
import com.phodal.shirelang.completion.dataprovider.ContextVariable
import com.phodal.shirelang.run.flow.ShireProcessProcessor.Companion.getElementAtOffset

class SymbolResolver(val myProject: Project, val editor: Editor, val hole: HobbitHole?) {
    private val variableProvider: PsiContextVariableProvider

    init {
        val psiFile = PsiManager.getInstance(myProject).findFile(editor.virtualFile)
        variableProvider = if (psiFile?.language != null) {
            PsiContextVariableProvider.provide(psiFile.language)
        } else {
            DefaultPsiContextVariableProvider()
        }
    }

    fun resolve(symbolTable: SymbolTable): Map<String, Any> {
        val element: PsiElement? = try {
            editor.caretModel.currentCaret.offset.let {
                val psiFile = PsiUtilBase.getPsiFileInEditor(editor, myProject) ?: return@let null
                getElementAtOffset(psiFile, it)
            }
        } catch (e: Exception) {
            logger<SymbolResolver>().error("Failed to resolve element", e)
            null
        }

        val results = resolveBuiltInVariable(symbolTable, element)

        results.putAll(ContextVariable.resolve(editor, element))

        results.putAll(SystemInfoVariable.resolve())

        hole?.variables?.forEach {
            results[it.key] = VariablePatternActionExecutor.execute(it.key, it.value)
        }

        return results
    }

    private fun resolveBuiltInVariable(symbolTable: SymbolTable, element: PsiElement?): MutableMap<String, Any> {
        val result = mutableMapOf<String, Any>()
        symbolTable.getAllVariables().forEach {
            val psiVariable = PsiVariable.fromVariableName(it.key)
            if (psiVariable != null) {
                result[it.key] = try {
                    variableProvider.resolveVariableValue(element, psiVariable)
                } catch (e: Exception) {
                    logger<SymbolResolver>().error("Failed to resolve variable: ${it.key}", e)
                    ""
                }
            } else {
                result[it.key] = ""
            }
        }

        return result
    }

}

