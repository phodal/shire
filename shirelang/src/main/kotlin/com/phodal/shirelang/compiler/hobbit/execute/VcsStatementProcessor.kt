package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.openapi.project.Project
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.ast.VariableElement
import com.phodal.shirelang.compiler.patternaction.PatternActionFunc

/**
 * author, authorEmail, committer, committerEmail, hash, date, message, fullMessage
 */
data class ShireVcsCommit(
    val authorName: String,
    val authorEmail: String,
    val committerName: String,
    val committerEmail: String,
    val hash: String,
    val date: String,
    val message: String,
    val fullMessage: String
)

data class ShireFileCommit(
    val filename: String,
    val path: String,
    val status: String,
    val count: Int,
    val commits: List<ShireVcsCommit>
)

data class ShireFileBranch(
    val name: String,
    val count: Int,
    val commits: List<ShireVcsCommit>
)

class VcsStatementProcessor(override val myProject: Project, hole: HobbitHole) :
    FunctionStatementProcessor(myProject, hole) {

    fun variables(fromStmt: PatternActionFunc.From): Map<String, List<ShireVcsCommit>> {
        return fromStmt.variables.associate {
            it.value to lookupElement(it)
        }
    }

    private fun lookupElement(it: VariableElement): List<ShireVcsCommit> {
//        val elements: List<PsiElement> = ShireSymbolProvider.all().flatMap { provider ->
//            provider.lookupElementByName(myProject, it.variableType) ?: emptyList()
//        }
//
        return emptyList()
    }
}