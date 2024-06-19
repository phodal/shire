package com.phodal.shirelang.compiler.variable

interface VariableResolver {
    fun resolve(): Map<String, Any>
}
