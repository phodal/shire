package com.phodal.shirelang.compiler.exec

interface InsCommand {
    suspend fun execute(): String?
}

