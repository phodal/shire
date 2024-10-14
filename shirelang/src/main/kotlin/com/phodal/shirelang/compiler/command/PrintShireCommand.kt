package com.phodal.shirelang.compiler.command

class PrintShireCommand(private val value: String) : ShireCommand {
    override suspend fun doExecute(): String {
        return value
    }
}