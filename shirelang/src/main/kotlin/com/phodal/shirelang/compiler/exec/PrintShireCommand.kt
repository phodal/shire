package com.phodal.shirelang.compiler.exec

class PrintShireCommand(private val value: String) : ShireCommand {
    override suspend fun doExecute(): String {
        return value
    }
}