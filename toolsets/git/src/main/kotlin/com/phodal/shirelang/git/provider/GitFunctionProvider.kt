package com.phodal.shirelang.git.provider

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.CommitContext
import com.phodal.shirecore.provider.function.ToolchainFunctionProvider
import git4idea.repo.GitRepositoryManager


/**
 * Use example:
 *
 * ```
 * commit("commit message") | push
 * ```
 *
 */
enum class GitFunction(val funName: String) {
    Commit("commit"),
    Push("push");

    companion object {
        fun fromString(value: String): GitFunction? {
            return values().firstOrNull { it.funName == value }
        }
    }
}

class GitFunctionProvider : ToolchainFunctionProvider {
    override fun isApplicable(project: Project, funcName: String): Boolean {
        return GitFunction.values().any { it.funName == funcName }
    }

    override fun execute(project: Project, funcName: String, args: List<Any>, allVariables: Map<String, Any?>): Any {
        val gitFunc = GitFunction.fromString(funcName)
            ?: throw IllegalArgumentException("Shire[GitTool]: Invalid Git function name")

        return when (gitFunc) {
            GitFunction.Commit -> {
                commitChanges(project, args.first() as String)
            }

            GitFunction.Push -> {

            }
        }
    }

    fun commitChanges(project: Project, commitMessage: String) {
        val repositoryManager = GitRepositoryManager.getInstance(project)
        val repository = repositoryManager.repositories.stream().findFirst().orElse(null)
            ?: return throw IllegalArgumentException("Shire[GitTool]: No git repository found")

        var commitContext: CommitContext = CommitContext()
        val option: GitCommitOptions = GitCommitOptions(commitContext)

        try {
            GitRepositoryCommitter(repository, option).commitStaged(commitMessage)
        } catch (e: Exception) {
            throw RuntimeException("Shire[GitTool]: Failed to commit changes")
        }
    }

    fun pushChanges(project: Project) {

    }
}