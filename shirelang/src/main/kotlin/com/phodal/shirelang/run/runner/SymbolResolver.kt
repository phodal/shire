package com.phodal.shirelang.run.runner

import com.intellij.lang.Language
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.phodal.shirecore.provider.DefaultPsiContextVariableProvider
import com.phodal.shirecore.provider.PsiContextVariableProvider
import com.phodal.shirelang.compiler.SymbolTable

class SymbolResolver(val myProject: Project, val editor: Editor) {
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
        // HobbitHole
//        symbolTable.getAllVariables().map {
//            variableProvider.resolveVariableValue(editor.document, it)
//        }
        return mapOf()
    }

}
