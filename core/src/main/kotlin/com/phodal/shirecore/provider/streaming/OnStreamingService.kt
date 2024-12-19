package com.phodal.shirecore.provider.streaming

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.middleware.post.LifecycleProcessorSignature
import com.phodal.shirecore.runner.console.ShireConsoleViewBase

/**
 * The OnStreamingService class is responsible for managing all the [StreamingServiceProvider] instances related to streaming services.
 * It offers methods for registering, clearing, and initiating streaming services within the application.
 *
 * This class is annotated with the @Service annotation at the project level, indicating its role in the service management infrastructure.
 *
 * The class maintains a mutable map to associate [LifecycleProcessorSignature] objects with corresponding [StreamingServiceProvider] instances.
 * It also holds an optional reference to a console view object that can be used for outputting information to the user.
 */
@Service(Service.Level.PROJECT)
class OnStreamingService {
    val map = mutableMapOf<LifecycleProcessorSignature, StreamingServiceProvider>()
    var console: ShireConsoleViewBase? = null

    fun registerStreamingService(sign: LifecycleProcessorSignature, console: ShireConsoleViewBase?) {
        this.console = console
        val streamingService = StreamingServiceProvider.getStreamingService(sign.funcName)
        if (streamingService != null) {
            map[sign] = streamingService
            streamingService.onCreated(console)
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
                service.onBeforeStreaming(project, userPrompt, console)
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
                service.afterStreamingDone(project)
            } catch (e: Exception) {
                ShirelangNotifications.error(project, "Error on done streaming service: ${e.message}")
            }
        }
    }

    fun onStreamingError() {
        // todo
    }
}
