package com.phodal.shirelang.compiler.hobbit.execute.schema

class ShireDate: ShireQLSchema {
    fun now(): Long {
        return System.currentTimeMillis()
    }
}