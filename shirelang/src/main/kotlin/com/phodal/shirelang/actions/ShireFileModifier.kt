package com.phodal.shirelang.actions

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.util.messages.MessageBusConnection
import com.intellij.util.messages.Topic
import com.phodal.shirecore.ShireCoroutineScope
import com.phodal.shirelang.actions.base.DynamicShireActionConfig
import com.phodal.shirelang.actions.base.DynamicShireActionService
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.parser.HobbitHoleParser
import com.phodal.shirelang.psi.ShireFile
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

/**
 * This class is provided to ShireFileChangesProvider to dynamically adjust
 * the shire action config when the content of the shire file changes.
 *
 * It supports delayed processing([delayTime]) to avoid duplicate updates as much as possible.
 *
 * @author lk
 */
class ShireFileModifier(val project: Project, val afterUpdater: ((HobbitHole, ShireFile) -> Unit)?) {

    private val dynamicShireActionService: DynamicShireActionService = DynamicShireActionService.getInstance()

    private val queue: MutableSet<ShireFile> = mutableSetOf()

    private val waitingUpdateQueue: ArrayDeque<ShireFile> = ArrayDeque()

    private val delayTime: Long = TimeUnit.SECONDS.toMillis(3)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = Dispatchers.IO.limitedParallelism(1)

    @Volatile
    var connect: MessageBusConnection? = null

    fun modify(afterUpdater: ((HobbitHole, ShireFile) -> Unit)?) {
        ShireCoroutineScope.scope(project).launch(dispatcher) {
            delay(delayTime)
            synchronized(queue) {
                waitingUpdateQueue.addAll(queue)
                queue.clear()
            }
            runBlocking {
                waitingUpdateQueue.forEach { file ->
                    if (!file.isValid) {
                        dynamicShireActionService.removeAction(file)
                        logger.debug("Shire file[${file.name}] is deleted")
                        return@forEach
                    }
                    if (!file.isPhysical) return@forEach
                    try {
                        HobbitHoleParser.parse(file)?.let {
                            dynamicShireActionService.putAction(file, DynamicShireActionConfig(it.name, it, file))
                            afterUpdater?.invoke(it, file)
                            logger.debug("Shire action[${it.name}] is loaded")
                        }
                    } catch (e: Exception) {
                        logger.warn("An error occurred while parsing shire file: ${file.virtualFile.path}", e)
                    }
                }
            }
            waitingUpdateQueue.clear()
        }
    }

    fun startup() {
        connect ?: synchronized(this) {
            connect ?: ShireUpdater.register { it.invoke(project)?.let { add(it) } }.also { connect = it }
        }

    }

    fun add(file: ShireFile) {
        synchronized(queue) {
            queue.add(file)
        }
        modify(afterUpdater)
    }

    fun dispose() {
        connect?.dispose()
    }

    companion object {

        private val logger = logger<ShireFileModifier>()

    }
}


fun interface ShireUpdater {

    fun onUpdated(processor: (Project) -> ShireFile?)

    companion object {

        @Topic.ProjectLevel
        val TOPIC: Topic<ShireUpdater> = Topic.create("shire file updated", ShireUpdater::class.java)

        val publisher: ShireUpdater
            get() = ApplicationManager.getApplication().messageBus.syncPublisher(TOPIC)

        fun register(subscriber: ShireUpdater): MessageBusConnection {
            val connection = ApplicationManager.getApplication().messageBus.connect()
            connection.subscribe(TOPIC, subscriber)
            return connection
        }
    }
}
