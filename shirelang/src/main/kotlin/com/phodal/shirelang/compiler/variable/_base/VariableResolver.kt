package com.phodal.shirelang.compiler.variable._base

/**
 * The `VariableResolver` interface is designed to provide a mechanism for resolving variables.
 */
interface VariableResolver {
    fun resolve(): Map<String, Any>
}
