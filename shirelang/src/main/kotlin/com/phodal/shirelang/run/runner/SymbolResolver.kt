package com.phodal.shirelang.run.runner

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.phodal.shirecore.provider.DefaultPsiContextVariableProvider
import com.phodal.shirecore.provider.PsiContextVariableProvider
import com.phodal.shirecore.provider.PsiVariable
import com.phodal.shirelang.compiler.SymbolTable
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.VariablePatternFunc

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

    fun resolve(symbolTable: SymbolTable): Map<String, String> {
        //
        val results = resolveBuiltInVariable(symbolTable)
//
//        hole?.variables?.forEach {
//            results[it.key] = VariableFuncExecutor.execute(it.value)
//        }

        return results
    }

    private fun resolveBuiltInVariable(symbolTable: SymbolTable): MutableMap<String, String> {
        val element: PsiElement? = editor.caretModel.currentCaret.let {
            PsiManager.getInstance(myProject).findFile(editor.virtualFile)?.findElementAt(it.offset)
        }

        val result = mutableMapOf<String, String>()
        symbolTable.getAllVariables().forEach {
            val psiVariable = PsiVariable.fromVariableName(it.key)
            if (psiVariable != null) {
                result[it.key] = variableProvider.resolveVariableValue(element, psiVariable)
            } else {
                result[it.key] = ""
            }
        }

        return result
    }

}

class VariableFuncExecutor {
    companion object {
        fun execute(value: List<VariablePatternFunc>): String {
            TODO("Not yet implemented")
        }
    }

}
