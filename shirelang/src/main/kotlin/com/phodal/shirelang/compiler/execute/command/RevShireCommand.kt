package com.phodal.shirelang.compiler.execute.command

import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.shire.RevisionProvider

/**
 * RevAutoCommand is used to execute a command that retrieves the committed change list for a given revision using Git.
 *
 * @param myProject the Project instance associated with the command
 * @param revision the Git revision for which the committed change list is to be retrieved
 *
 */
class RevShireCommand(private val myProject: Project, private val revision: String) : ShireCommand {
    override suspend fun doExecute(): String {
        return RevisionProvider.provide()?.let {
            val changes = it.fetchChanges(myProject, revision)
            if (changes != null) {
                return changes
            } else {
                return "No changes found for revision $revision"
            }
        } ?: "No revision provider found"
    }
}
