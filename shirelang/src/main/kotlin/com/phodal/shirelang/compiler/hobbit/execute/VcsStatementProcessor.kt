package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.shire.ShireQueryStatementDataProvider
import com.phodal.shirecore.vcs.ShireVcsCommit
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.ast.VariableElement
import com.phodal.shirelang.compiler.patternaction.PatternActionFunc

class VcsStatementProcessor(override val myProject: Project, hole: HobbitHole) :
    FunctionStatementProcessor(myProject, hole) {

    fun variables(fromStmt: PatternActionFunc.From): Map<String, List<ShireVcsCommit>> {
        return fromStmt.variables.associate {
            it.value to lookupVcsCommit(it)
        }
    }

    private fun lookupVcsCommit(it: VariableElement): List<ShireVcsCommit> {
        val elements: List<ShireVcsCommit> = ShireQueryStatementDataProvider.all().flatMap { provider ->
            provider.lookupElementByName(myProject, it.variableType) ?: emptyList()
        }

        return elements
    }
}