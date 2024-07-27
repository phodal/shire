package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.shire.ShireSymbolProvider
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.ast.MethodCall
import com.phodal.shirelang.compiler.hobbit.ast.Statement
import com.phodal.shirelang.compiler.hobbit.ast.Value
import com.phodal.shirelang.compiler.hobbit.ast.VariableElement
import com.phodal.shirelang.compiler.patternaction.PatternActionFunc
import com.phodal.shirelang.compiler.patternaction.PatternActionTransform

open class QueryStatementProcessor(override val myProject: Project, hole: HobbitHole) :
    FunctionStatementProcessor(myProject, hole) {
    fun execute(expression: Statement, variables: Map<String, List<PsiElement>>): List<PsiElement> {
        return processStatement(expression, variables)
    }

    fun execute(transform: PatternActionTransform): String {
        val fromStmt = transform.patternActionFuncs.find { it is PatternActionFunc.From } as PatternActionFunc.From
        val selectStmt =
            transform.patternActionFuncs.find { it is PatternActionFunc.Select } as PatternActionFunc.Select
        val whereStmt = transform.patternActionFuncs.find { it is PatternActionFunc.Where } as PatternActionFunc.Where

        val variableElementsMap: Map<String, List<PsiElement>> = runReadAction { buildVariables(fromStmt) }
        val handledElements = processStatement(whereStmt.statement, variableElementsMap)
        val selectElements = processSelect(selectStmt, handledElements)

        return selectElements.joinToString("\n")
    }

    private fun buildVariables(fromStmt: PatternActionFunc.From): Map<String, List<PsiElement>> {
        return fromStmt.variables.associate {
            it.value to lookupElement(it)
        }
    }

    // cache
    private val cache = mutableMapOf<String, List<PsiElement>>()

    private fun lookupElement(it: VariableElement): List<PsiElement> {
        if (cache.containsKey(it.variableType)) {
            return cache[it.variableType] ?: emptyList()
        }

        val elements: List<PsiElement> = ShireSymbolProvider.all().flatMap { provider ->
            provider.lookupElementByName(myProject, it.variableType) ?: emptyList()
        }

        cache[it.variableType] = elements
        return elements
    }

    private fun processSelect(selectStmt: PatternActionFunc.Select, handledElements: List<PsiElement>): List<String> {
        return selectStmt.statements.flatMap {
            processSelectStatement(it, handledElements)
        }
    }

    private fun processSelectStatement(statement: Statement, handledElements: List<PsiElement>): List<String> {
        val result = mutableListOf<String>()
        handledElements.forEach { element ->
            when (statement) {
                is Value -> {
                    result.add(statement.display())
                }

                is MethodCall -> {
                    invokeMethodOrField(statement, element)?.let {
                        result.add(it.toString())
                    }
                }
            }
        }

        return result
    }
}
