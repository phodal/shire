package com.phodal.shirelang.run

import com.intellij.execution.process.ProcessEvent
import com.intellij.util.messages.Topic
import java.util.*

@FunctionalInterface
interface ShireRunListener : EventListener {
    fun runFinish(string: String, event: ProcessEvent, scriptPath: String)

    companion object {
        @Topic.AppLevel
        val TOPIC: Topic<ShireRunListener> = Topic(
            ShireRunListener::class.java, Topic.BroadcastDirection.TO_DIRECT_CHILDREN
        )
    }
}