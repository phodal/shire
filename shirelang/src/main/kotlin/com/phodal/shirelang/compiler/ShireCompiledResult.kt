package com.phodal.shirelang.compiler

import com.phodal.shirecore.agent.CustomAgent
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.psi.ShireFile

data class ShireCompiledResult(
    /**
     * The origin DevIns content
     */
    var input: String = "",
    /**
     * Output String of a compile result
     */
    var output: String = "",
    var isLocalCommand: Boolean = false,
    var hasError: Boolean = false,

    /**
     * Execute agent
     */
    var executeAgent: CustomAgent? = null,
    /**
     * Next job to be executed
     */
    var nextJob: ShireFile? = null,

    var config: HobbitHole? = null,

    /**
     * Variables for lazy resolve
     */
    var symbolTable: SymbolTable = SymbolTable(),
)
