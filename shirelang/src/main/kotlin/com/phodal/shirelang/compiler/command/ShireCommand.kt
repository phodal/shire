package com.phodal.shirelang.compiler.command

interface ShireCommand {
    suspend fun doExecute(): String?
}

