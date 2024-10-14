package com.phodal.shirelang.compiler.execute.command

class PrintShireCommand(private val value: String) : ShireCommand {
    override suspend fun doExecute(): String {
        return value
    }
}