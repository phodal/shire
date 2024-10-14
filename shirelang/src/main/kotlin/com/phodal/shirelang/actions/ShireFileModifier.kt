package com.phodal.shirelang.actions

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.messages.MessageBusConnection
import com.intellij.util.messages.Topic
import com.phodal.shirelang.actions.base.DynamicActionService
import com.phodal.shirelang.actions.base.DynamicShireActionConfig
import com.phodal.shirelang.compiler.ast.hobbit.HobbitHole
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
class ShireFileModifier(val context: ShireFileModificationContext) {

    private val dynamicActionService: DynamicActionService

    private val scope: CoroutineScope

    init {
        dynamicActionService = context.dynamicActionService
        scope = context.scope
    }

    private val queue: MutableSet<ShireFile> = mutableSetOf()

    private val waitingUpdateQueue: ArrayDeque<ShireFile> = ArrayDeque()

    private val delayTime: Long = TimeUnit.SECONDS.toMillis(3)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = Dispatchers.IO.limitedParallelism(1)

    @Volatile
    var connect: MessageBusConnection? = null

    private fun modify(afterUpdater: ((HobbitHole, ShireFile) -> Unit)?) {
        scope.launch(dispatcher) {
            delay(delayTime)
            synchronized(queue) {
                waitingUpdateQueue.addAll(queue)
                queue.clear()
            }
            runBlocking {
                runReadAction {
                    waitingUpdateQueue.forEach { file ->
                        if (!file.isValid) {
                            dynamicActionService.removeAction(file)
                            logger.debug("Shire file[${file.name}] is deleted")
                            file.virtualFile.takeIf { it.isValid }?.run { context.convertor.invoke(this)?.let { println("reload.")
                                loadShireAction(it, afterUpdater) } }
                            return@forEach
                        }
                        if (!file.isPhysical) return@forEach
                        loadShireAction(file, afterUpdater)
                    }
                }
                waitingUpdateQueue.clear()
            }
        }
    }

    private fun loadShireAction(file: ShireFile, afterUpdater: ((HobbitHole, ShireFile) -> Unit)?) {
        try {
            HobbitHoleParser.parse(file).let {
                dynamicActionService.putAction(file, DynamicShireActionConfig(it?.name ?: file.name, it, file))
                if (it != null) afterUpdater?.invoke(it, file)
                logger.debug("Shire file[${file.virtualFile.path}] is loaded")
            }
        } catch (e: Exception) {
            logger.error("An error occurred while parsing shire file: ${file.virtualFile.path}", e)
        }
    }

    fun startup(predicate: (VirtualFile) -> Boolean) {
        connect ?: synchronized(this) {
            connect ?: ShireUpdater.register { it.takeIf(predicate)?.let(context.convertor)?.let { add(it) } }.also { connect = it }
        }

    }

    private fun add(file: ShireFile) {
        synchronized(queue) {
            queue.add(file)
        }
        modify(context.afterUpdater)
    }

    fun dispose() {
        connect?.dispose()
    }

    companion object {

        private val logger = logger<ShireFileModifier>()

    }
}


fun interface ShireUpdater {

    fun onUpdated(file: VirtualFile)

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

data class ShireFileModificationContext(
    val dynamicActionService: DynamicActionService,
    val afterUpdater: ((HobbitHole, ShireFile) -> Unit)?,
    val scope: CoroutineScope,
    val convertor: (VirtualFile) -> ShireFile?
)
