package com.phodal.shirelang.run

import com.intellij.execution.process.ProcessEvent
import com.intellij.util.messages.Topic
import java.util.*

@FunctionalInterface
interface ShireRunListener : EventListener {
    /**
     * Run finish event
     *
     * @param allOutput all output with Console and debug output, it's design for debug
     * @param llmOutput LLM output
     * @param event ProcessEvent
     * @param scriptPath script path
     * @param consoleView shire consoleView
     */
    fun runFinish(allOutput: String, llmOutput: String, event: ProcessEvent, scriptPath: String, consoleView: ShireConsoleView?)

    companion object {
        @Topic.AppLevel
        val TOPIC: Topic<ShireRunListener> = Topic(
            ShireRunListener::class.java, Topic.BroadcastDirection.TO_DIRECT_CHILDREN
        )
    }
}