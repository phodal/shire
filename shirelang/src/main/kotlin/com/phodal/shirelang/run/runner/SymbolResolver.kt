package com.phodal.shirelang.run.runner

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.phodal.shirelang.compiler.SymbolTable

class SymbolResolver(val myProject: Project, val editor: Editor) {
    fun resolve(symbolTable: SymbolTable): Map<String, String> {
        return mapOf()
    }

}
