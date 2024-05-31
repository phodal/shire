package com.phodal.shirelang.compiler.exec

interface ShireCommand {
    suspend fun doExecute(): String?
}

