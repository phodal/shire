package com.phodal.shirecore.provider.streaming

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.project.Project
import com.intellij.util.io.IOUtil
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.runner.console.ShireConsoleViewBase


/**
 * The ProfilingStreamingService class is a concrete implementation of the StreamingServiceProvider interface.
 * It provides profiling capabilities during the streaming process, outputting memory usage information to the console.
 */
class ProfilingStreamingService : StreamingServiceProvider {
    override var name: String = "profiling"
    private var console: ShireConsoleViewBase? = null

    override fun onStart(project: Project, userPrompt: String, console: ShireConsoleViewBase?) {
        this.console = console
        console?.print("Start profiling: ${getMemory()}", ConsoleViewContentType.SYSTEM_OUTPUT)

    }

    override fun onDone(project: Project) {
        console?.print("End profiling: ${getMemory()}", ConsoleViewContentType.SYSTEM_OUTPUT)
        ShirelangNotifications.info(project, "Memory: ${getMemory()}MB")
    }

    private fun getMemory(): Long {
        val runtime = Runtime.getRuntime()
        val allocatedMem = runtime.totalMemory()
        val usedMem = allocatedMem - runtime.freeMemory()
        return toMb(usedMem)
    }

    private fun toMb(value: Long): Long {
        return value / IOUtil.MiB
    }

    override fun dispose() {
        //
    }
}
