package com.phodal.shire.git.provider

import com.intellij.dvcs.DvcsUtil
import com.intellij.dvcs.push.PushSpec
import com.intellij.dvcs.ui.DvcsBundle
import com.intellij.openapi.progress.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.AbstractVcs
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.openapi.vcs.changes.CommitContext
import com.intellij.openapi.vcs.changes.actions.ScheduleForAdditionActionExtension
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.ObjectUtils
import com.intellij.vcsUtil.VcsUtil
import com.phodal.shirecore.provider.function.ToolchainFunctionProvider
import git4idea.GitRemoteBranch
import git4idea.GitVcs
import git4idea.i18n.GitBundle
import git4idea.push.GitPushOperation
import git4idea.push.GitPushSource
import git4idea.push.GitPushSupport
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager
import git4idea.util.GitFileUtils
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
                commitChanges(project, repository, args.first() as String)
            }

            GitFunction.Push -> {
                pushChanges(project, repository).get().toString()
            }
        }
    }

    /**
     *
     * How to find code in IDEA: [GitCommand.Commit]
     */
    fun commitChanges(project: Project, repository: GitRepository, commitMessage: String) {
        val root = repository.root
        val changeListManager = ChangeListManager.getInstance(project)
        val virtualFiles = changeListManager
            .allChanges
            .mapNotNull { it.virtualFile }
            .asSequence()

        val files = collectPathsFromFiles(
            project, virtualFiles
        ).toList()

        GitFileUtils.addPaths(project, root, files, false, false)
        var commitContext: CommitContext = CommitContext()
        val option: GitCommitOptions = GitCommitOptions(commitContext)

//        try {
        GitRepositoryCommitter(repository, option).commitStaged(commitMessage)
//        } catch (e: Exception) {
//            throw RuntimeException("Shire[GitTool]: Failed to commit changes")
//        }
    }

    private fun collectPathsFromFiles(project: Project, allFiles: Sequence<VirtualFile>): Sequence<FilePath> {
        val vcsManager = ProjectLevelVcsManager.getInstance(project)
        val changeListManager = ChangeListManager.getInstance(project)

        return allFiles
            .filter { file ->
                val actionExtension = getExtensionFor(project, vcsManager.getVcsFor(file))
                actionExtension != null &&
                        changeListManager.getStatus(file).let { status ->
                            if (file.isDirectory) actionExtension.isStatusForDirectoryAddition(status) else actionExtension.isStatusForAddition(
                                status
                            )
                        }
            }
            .map(VcsUtil::getFilePath)
    }

    private fun getExtensionFor(project: Project, vcs: AbstractVcs?) =
        if (vcs == null) null
        else ScheduleForAdditionActionExtension.EP_NAME.findFirstSafe { it.getSupportedVcs(project) == vcs }

    fun pushChanges(project: Project, repository: GitRepository): CompletableFuture<GitRemoteBranch> {
        val progressIndicator =
            ObjectUtils.notNull(ProgressManager.getInstance().progressIndicator, EmptyProgressIndicator())

        val future = CompletableFuture<GitRemoteBranch>()

        val branch = repository.currentBranch ?: return CompletableFuture.failedFuture(ProcessCanceledException())
        val pushTarget =
            GitPushSupport.getPushTargetIfExist(repository, branch) ?: return CompletableFuture.failedFuture(
                ProcessCanceledException()
            )

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
}