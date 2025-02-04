package com.phodal.shirelang.compiler.execute.command

import com.phodal.shirelang.completion.dataprovider.BuiltinCommand

interface ShireCommand {
    val commandName: BuiltinCommand

    fun isApplicable(): Boolean = true

    suspend fun doExecute(): String?
}

