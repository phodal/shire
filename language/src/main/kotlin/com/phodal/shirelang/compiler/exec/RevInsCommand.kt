package com.phodal.shirelang.compiler.exec

import com.intellij.openapi.project.Project

/**
 * RevAutoCommand is used to execute a command that retrieves the committed change list for a given revision using Git.
 *
 * @param myProject the Project instance associated with the command
 * @param revision the Git revision for which the committed change list is to be retrieved
 *
 */
class RevInsCommand(private val myProject: Project, private val revision: String) : InsCommand {
    override suspend fun execute(): String? {
        throw NotImplementedError()

//        val repository = GitRepositoryManager.getInstance(myProject).repositories.firstOrNull() ?: return null
//        val future = CompletableFuture<List<Change>>()
//
//        val task = object : Task.Backgroundable(myProject, ShireBundle.message("devin.ref.loading"), false) {
//            override fun run(indicator: ProgressIndicator) {
//                val committedChangeList = GitCommittedChangeListProvider.getCommittedChangeList(
//                    myProject!!, repository.root, GitRevisionNumber(revision)
//                )?.changes?.toList()
//
//                future.complete(committedChangeList)
//            }
//        }
//
//        ProgressManager.getInstance()
//            .runProcessWithProgressAsynchronously(task, BackgroundableProcessIndicator(task))
//
//
//        return runBlocking {
//            val changes = future.await()
//            val diffContext = myProject.service<VcsPrompting>().prepareContext(changes)
//            "\n```diff\n${diffContext}\n```\n"
//        }
    }
}
