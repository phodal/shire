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

    override fun buildVariables(fromStmt: PatternActionFunc.From): Map<String, List<Any>> {
        return fromStmt.variables.associate {
            when {
                it.variableType.startsWith("Psi") -> {
                    it.value to lookupElement(it)
                }
                it.variableType.startsWith("Git") -> {
                    it.value to lookupVcsCommit(it)
                }
                else -> {
                    it.value to lookupElement(it)
                }
            }
        }
    }

    override fun processSelect(selectStmt: PatternActionFunc.Select, handledElements: List<Any>): List<String> {
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


    private fun lookupVcsCommit(it: VariableElement): List<ShireVcsCommit> {
        val elements: List<ShireVcsCommit> = ShireQLDataProvider.all().flatMap { provider ->
            provider.lookup(myProject, it.variableType) ?: emptyList()
        }

        return elements
    }

    private fun processSelectStatement(statement: Statement, handledElements: List<Any>): List<String> {
        val result = mutableListOf<String>()
        handledElements.forEach { element ->
            when (element) {
                is PsiElement -> {
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

                is ShireVcsCommit -> {
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
            }
        }

        return result
    }
}
