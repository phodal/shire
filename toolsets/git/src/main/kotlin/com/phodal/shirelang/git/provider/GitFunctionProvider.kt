package com.phodal.shirelang.git.provider

import com.intellij.dvcs.DvcsUtil
import com.intellij.dvcs.push.PushSpec
import com.intellij.dvcs.ui.DvcsBundle
import com.intellij.openapi.progress.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.CommitContext
import com.intellij.util.ObjectUtils
import com.phodal.shirecore.provider.function.ToolchainFunctionProvider
import git4idea.GitRemoteBranch
import git4idea.GitVcs
import git4idea.branch.GitBranchesCollection
import git4idea.commands.Git
import git4idea.commands.GitStandardProgressAnalyzer
import git4idea.i18n.GitBundle
import git4idea.push.GitPushOperation
import git4idea.push.GitPushSource
import git4idea.push.GitPushSupport
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager
import java.util.concurrent.CompletableFuture


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

        val repositoryManager = GitRepositoryManager.getInstance(project)
        val repository = repositoryManager.repositories.stream().findFirst().orElse(null)
            ?: return throw IllegalArgumentException("Shire[GitTool]: No git repository found")

        return when (gitFunc) {
            GitFunction.Commit -> {
                commitChanges(repository, args.first() as String)
            }

            GitFunction.Push -> {
                pushChanges(project, repository)
            }
        }
    }

    /**
     *
     * How to find code in IDEA: [GitCommand.Commit]
     */
    fun commitChanges(repository: GitRepository, commitMessage: String) {
        var commitContext: CommitContext = CommitContext()
        val option: GitCommitOptions = GitCommitOptions(commitContext)

//        try {
        GitRepositoryCommitter(repository, option).commitStaged(commitMessage)
//        } catch (e: Exception) {
//            throw RuntimeException("Shire[GitTool]: Failed to commit changes")
//        }
    }

    fun pushChanges(project: Project, repository: GitRepository): CompletableFuture<GitRemoteBranch> {
        val progressIndicator =
            ObjectUtils.notNull(ProgressManager.getInstance().progressIndicator, EmptyProgressIndicator())

        val branchesCollection: GitBranchesCollection = repository.branches
        val future = CompletableFuture<GitRemoteBranch>()

        for (branch in branchesCollection.localBranches) {
            val pushTarget = GitPushSupport.getPushTargetIfExist(repository, branch!!)
                ?: continue

            val gitPushSupport = DvcsUtil.getPushSupport(GitVcs.getInstance(project)) as? GitPushSupport
                ?: return CompletableFuture.failedFuture(ProcessCanceledException())

            ProgressManager.getInstance().runProcessWithProgressAsynchronously(
                object : Task.Backgroundable(repository.project, DvcsBundle.message("push.process.pushing"), true) {

                    override fun run(indicator: ProgressIndicator) {
                        indicator.text = DvcsBundle.message("push.process.pushing")
                        val pushSpec = PushSpec(GitPushSource.create(branch), pushTarget)
                        val pushResult = GitPushOperation(
                            repository.project,
                            gitPushSupport,
                            mapOf(repository to pushSpec),
                            null,
                            false,
                            false
                        )
                            .execute().results[repository] ?: error("Missing push result")
                        check(pushResult.error == null) {
                            GitBundle.message("push.failed.error.message", pushResult.error.orEmpty())
                        }
                    }

                    override fun onSuccess() {
                        future.complete(pushTarget.branch)
                    }

                    override fun onThrowable(error: Throwable) {
                        future.completeExceptionally(error)
                    }

                    override fun onCancel() {
                        future.completeExceptionally(ProcessCanceledException())
                    }
                }, progressIndicator
            )

            return future
        }

        return future
    }
}