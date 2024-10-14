package com.phodal.shirelang.compiler.execute.command

interface ShireCommand {
    suspend fun doExecute(): String?
}

