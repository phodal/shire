package com.phodal.shirecore

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.AppExecutorUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher


public val workerThread = AppExecutorUtil.getAppExecutorService().asCoroutineDispatcher()


@Service(Service.Level.PROJECT)
class ShireCoroutineScope {
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Logger.getInstance(ShireCoroutineScope::class.java).error(throwable)
    }

    val coroutineScope: CoroutineScope = CoroutineScope(
        SupervisorJob() + workerThread + coroutineExceptionHandler
    )

    companion object {
        fun scope(project: Project): CoroutineScope =
            project.getService(ShireCoroutineScope::class.java).coroutineScope
    }
}
