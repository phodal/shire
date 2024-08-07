package com.phodal.shirelang.compiler.hobbit.execute.variable

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.shire.ShireQLDataProvider
import com.phodal.shirecore.provider.shire.ShireSymbolProvider
import com.phodal.shirecore.vcs.ShireGitCommit
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.ast.VariableElement
import com.phodal.shirelang.compiler.hobbit.execute.schema.ShireDate
import com.phodal.shirelang.compiler.patternaction.PatternActionFunc


class ShireQLVariableBuilder(val myProject: Project, hole: HobbitHole) {
    fun buildVariables(fromStmt: PatternActionFunc.From): Map<String, List<Any>> {
        return fromStmt.variables.associate {
            when {
                it.variableType.startsWith("Psi") -> {
                    it.value to lookupElement(it)
                }
                it.variableType.startsWith("Git") -> {
                    it.value to lookupVcsCommit(it)
                }
                it.variableType == ShireQLFromType.Date.typeName -> {
                    it.value to createDateFunc(it)
                }
                else -> {
                    it.value to lookupElement(it)
                }
            }
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


    private fun lookupVcsCommit(it: VariableElement): List<ShireGitCommit> {
        val elements: List<ShireGitCommit> = ShireQLDataProvider.all().flatMap { provider ->
            provider.lookup(myProject, it.variableType) ?: emptyList()
        }

        return elements
    }

    private fun createDateFunc(it: VariableElement): List<ShireDate> {
        return listOf(ShireDate())
    }
}
