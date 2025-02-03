package com.phodal.shirelang.compiler.execute.command

import com.phodal.shirelang.completion.dataprovider.BuiltinCommand

class PrintShireCommand(private val value: String) : ShireCommand {
    override val commandName = BuiltinCommand.FILE_FUNC

    override suspend fun doExecute(): String {
        return value
    }
}