package com.phodal.shirecore.provider.streaming

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.middleware.post.LifecycleProcessorSignature

/**
 * Manage all [com.phodal.shirecore.provider.streaming.StreamingServiceProvider]
 */
@Service(Service.Level.PROJECT)
class OnStreamingService {
    val map = mutableMapOf<LifecycleProcessorSignature, StreamingServiceProvider>()

    fun registerStreamingService(sign: LifecycleProcessorSignature) {
        val streamingService = StreamingServiceProvider.getStreamingService(sign.funcName)
        if (streamingService != null) {
            map[sign] = streamingService
        }
    }

    fun clearStreamingService() {
        map.clear()
    }

    fun all(): List<StreamingServiceProvider> {
        return StreamingServiceProvider.all()
    }

    fun onStart(project: Project, userPrompt: String) {
        map.forEach { (_, service) ->
            try {
                service.onStart(project, userPrompt)
            } catch (e: Exception) {
                ShirelangNotifications.error(project, "Error on start streaming service: ${e.message}")
            }
        }
    }

    fun onStreaming(project: Project, chunk: String) {
        map.forEach { (sign, service) ->
            try {
                service.onStreaming(project, chunk, sign.args)
            } catch (e: Exception) {
                ShirelangNotifications.error(project, "Error on streaming service: ${e.message}")
            }
        }
    }

    fun onDone(project: Project) {
        map.forEach { (_, service) ->
            try {
                service.onDone(project)
            } catch (e: Exception) {
                ShirelangNotifications.error(project, "Error on done streaming service: ${e.message}")
            }
        }
    }

    fun onError() {
        // todo
    }
}
