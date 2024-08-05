package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.shire.ShireQLDataProvider
import com.phodal.shirecore.provider.shire.ShireSymbolProvider
import com.phodal.shirecore.vcs.ShireVcsCommit
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.ast.MethodCall
import com.phodal.shirelang.compiler.hobbit.ast.Statement
import com.phodal.shirelang.compiler.hobbit.ast.Value
import com.phodal.shirelang.compiler.hobbit.ast.VariableElement
import com.phodal.shirelang.compiler.patternaction.PatternActionFunc

open class BaseQueryStatementProcessor(override val myProject: Project, hole: HobbitHole) :
    FunctionStatementProcessor(myProject, hole) {

    override fun buildVariables(fromStmt: PatternActionFunc.From): Map<String, List<PsiElement>> {
        return fromStmt.variables.associate {
            when {
                it.value.startsWith("Psi") -> {
                    it.value to lookupElement(it)
                }
                it.value.startsWith("Git") -> {
                    it.value to lookupVcsCommit(it)
                }
                else -> {
                    it.value to emptyList()
                }
            }
        }
    }

    override fun processSelect(selectStmt: PatternActionFunc.Select, handledElements: List<PsiElement>): List<String> {
        return selectStmt.statements.flatMap {
            processSelectStatement(it, handledElements)
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


    private fun lookupVcsCommit(it: VariableElement): List<PsiElement> {
        val elements: List<ShireVcsCommit> = ShireQLDataProvider.all().flatMap { provider ->
            provider.lookup(myProject, it.variableType) ?: emptyList()
        }

//        return elements
        return emptyList()
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
