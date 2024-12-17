package com.phodal.shirelang.run.streaming

import com.intellij.openapi.components.Service
import com.phodal.shirecore.middleware.post.LifecycleProcessorSignature

@Service(Service.Level.APP)
class OnStreamingService {
    val map = mutableMapOf<String, LifecycleProcessorSignature>()

    fun registerStreamingService(sign: LifecycleProcessorSignature) {

    }
}
