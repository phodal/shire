package com.phodal.shirelang.compiler.execute.command

interface ShireCommand {
    fun isApplicable(): Boolean {
        return true
    }

    suspend fun doExecute(): String?
}

