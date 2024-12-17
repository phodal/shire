package com.phodal.shirelang.run.streaming

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.phodal.shirecore.middleware.post.LifecycleProcessorSignature
import com.phodal.shirecore.provider.streaming.StreamingServiceProvider
import kotlinx.coroutines.flow.Flow

/**
 * Manage all [com.phodal.shirecore.provider.streaming.StreamingServiceProvider]
 */
@Service(Service.Level.APP)
class OnStreamingService {
    val map = mutableMapOf<LifecycleProcessorSignature, StreamingServiceProvider>()

    fun registerStreamingService(sign: LifecycleProcessorSignature) {
        val streamingService = StreamingServiceProvider.getStreamingService(sign.funcName)
        if (streamingService != null) {
            map[sign] = streamingService
        }
    }

    fun unregisterStreamingService(sign: LifecycleProcessorSignature) {
        map.remove(sign)
    }

    fun all(): List<StreamingServiceProvider> {
        return map.values.toList()
    }

    fun notifyAll(project: Project, flow: Flow<String>, args: Map<String, Any>) {
        map.forEach { (sign, service) ->
            service.onStreaming(project, flow, args)
        }
    }

    fun onDone(project: Project) {
        map.forEach { (_, service) ->
            service.onDone(project)
        }
    }
}
