package com.phodal.shirecore.provider.streaming

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import kotlinx.coroutines.flow.Flow

interface StreamingServiceProvider : Disposable {
    var name: String

    /**
     * Receive streaming data
     */
    fun onStreaming(project: Project, flow: Flow<String>, args: Map<String, Any>)

    fun onDone(project: Project)

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