package com.phodal.shirelang.compiler.exec

import com.phodal.shirelang.compiler.exec.InsCommand

class PrintInsCommand(private val value: String) : InsCommand {
    override suspend fun execute(): String {
        return value
    }
}