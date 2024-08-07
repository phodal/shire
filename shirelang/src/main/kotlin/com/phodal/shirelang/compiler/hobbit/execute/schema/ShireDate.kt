package com.phodal.shirelang.compiler.hobbit.execute.schema

import java.util.*

class ShireDate : ShireQLSchema {
    private val date: Long = System.currentTimeMillis()

    fun now(): Long {
        return date
    }

    fun dayOfWeek(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    }

    fun year(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    fun month(): Int {
        return Calendar.getInstance().get(Calendar.MONTH) + 1
    }

    fun dayOfMonth(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    }

    override fun toString(): String {
        return "ShireDate(date=$date)"
    }
}