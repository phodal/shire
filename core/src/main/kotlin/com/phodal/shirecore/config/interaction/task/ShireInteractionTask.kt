package com.phodal.shirecore.config.interaction.task

import com.intellij.openapi.progress.Task.Backgroundable
import com.intellij.openapi.project.Project
import com.phodal.shirecore.config.interaction.PostFunction
import java.util.concurrent.CompletableFuture

/**
 * @author lk
 */
abstract class ShireInteractionTask(project: Project, taskName: String, val postExecute: PostFunction?): Backgroundable(project, taskName) {

    /**
     * An unexpected exception occurred, causing the shire process cannot be canceled,
     * postExecute was not executed,it may have used the [CompletableFuture].
     */
    override fun onThrowable(error: Throwable) {
        super.onThrowable(error)
        postExecute?.invoke(null, null)
    }

}