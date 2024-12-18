package com.phodal.shirecore.provider.streaming

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.project.Project
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.runner.console.ShireConsoleViewBase

/**
 * Logging start time and end time for each lifecycle
 */
class TimingStreamingService : StreamingServiceProvider {
    override var name: String = "timing"

    private var time: Long = 0
    private var console: ShireConsoleViewBase? = null

    override fun onStart(project: Project, userPrompt: String, console: ShireConsoleViewBase?) {
        this.console = console
        val currentTime = System.currentTimeMillis()
        time = currentTime
        console?.print("Start timing: $currentTime", ConsoleViewContentType.SYSTEM_OUTPUT)
    }

    override fun onDone(project: Project) {
        val currentTime = System.currentTimeMillis()
        console?.print("End timing: $currentTime", ConsoleViewContentType.SYSTEM_OUTPUT)
        ShirelangNotifications.info(project, "Timing: ${currentTime - time}ms")
    }

    override fun dispose() {
        // do nothing
    }
}