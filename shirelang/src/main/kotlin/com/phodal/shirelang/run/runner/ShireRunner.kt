package com.phodal.shirelang.run.runner

import com.intellij.execution.console.ConsoleViewWrapperBase
import com.intellij.execution.process.ProcessHandler
import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.PsiContextVariableProvider
import com.phodal.shirelang.compile.VariableTemplateCompiler
import com.phodal.shirelang.compiler.SymbolTable
import com.phodal.shirelang.run.ShireConfiguration

abstract class ShireRunner(
    open val configuration: ShireConfiguration,
    open val processHandler: ProcessHandler,
    open val console: ConsoleViewWrapperBase,
    open val myProject: Project,
    open val symbolTable: SymbolTable,
    open val input: String,
) {
    fun compileShireTemplate(): String {
        val currentEditor = VariableTemplateCompiler.defaultEditor(myProject)
        val currentElement = VariableTemplateCompiler.defaultElement(myProject, currentEditor)

        // preResolverVariables

        if (currentElement != null && currentEditor != null) {
            val language = currentElement.language
//            SymbolResolver(myProject, currentEditor,)

            val file = currentElement.containingFile
            val templateCompiler = VariableTemplateCompiler(file.language, file, currentElement, currentEditor)

            // updateVariables

            return templateCompiler.compile(input)
        }

        return input
    }

    abstract fun execute()
}