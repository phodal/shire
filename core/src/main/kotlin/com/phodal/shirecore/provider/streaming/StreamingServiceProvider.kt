package com.phodal.shirecore.provider.streaming

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.phodal.shirecore.runner.console.ShireConsoleViewBase

/**
 * all - Returns a list of all registered StreamingServiceProvider implementations.
 *
 * @return A list of StreamingServiceProvider instances.
 */
interface StreamingServiceProvider : Disposable {
    var name: String

    /**
     * When create the service, you can do some initialization here
     */
    fun onCreated(console: ShireConsoleViewBase?) {
        /// do nothing
    }

    /**
     * For the start of the LLM streaming, you can do some initialization here, for example, you can create a file to log the data
     */
    fun onStart(project: Project, userPrompt: String, console: ShireConsoleViewBase?) {
        /// do nothing
    }

    /**
     * For the streaming data, you can do some processing here, for example, you can log the data to a file
     */
    fun onStreaming(project: Project, flow: String, args: List<Any>) {
        /// do nothing
    }

    /**
     * For the end of the streaming, for example, you can do some cleanup here, or show some notification
     */
    fun onDone(project: Project) {
        /// do nothing
    }

    companion object {
        val EP_NAME =
            com.intellij.openapi.extensions.ExtensionPointName.create<StreamingServiceProvider>("com.phodal.shireStreamingService")

        fun getStreamingService(name: String): StreamingServiceProvider? {
            return EP_NAME.extensions.firstOrNull { it.name == name }
        }

        fun all(): List<StreamingServiceProvider> {
            return EP_NAME.extensions.toList()
        }
    }
}