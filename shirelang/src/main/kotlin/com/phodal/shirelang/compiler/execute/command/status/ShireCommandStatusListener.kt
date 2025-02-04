package com.phodal.shirelang.compiler.execute.command.status

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.messages.Topic
import com.phodal.shirelang.compiler.execute.command.ShireCommand

/**
 * Provide for listening to the status of InsCommand
 */
interface ShireCommandStatusListener {
    fun onFinish(command: ShireCommand, status: ShireCommandStatus, file: VirtualFile?)

    companion object {
        val TOPIC = Topic.create("shire.command.status", ShireCommandStatusListener::class.java)

        fun notify(command: ShireCommand, status: ShireCommandStatus, file: VirtualFile?) {
            ApplicationManager.getApplication().messageBus
                .syncPublisher(TOPIC)
                .onFinish(command, status, file)
        }
    }
}