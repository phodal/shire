package com.phodal.shirelang.compiler

import com.phodal.shirecore.agent.CustomAgent
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.variable.VariableTable
import com.phodal.shirelang.psi.ShireFile

data class ShireCompiledResult(
    /**
     * The origin Shire content
     */
    var sourceCode: String = "",

    /**
     * Output String of a compiler result, not the final result
     */
    var shireOutput: String = "",

    /**
     * Is local command only
     */
    var isLocalCommand: Boolean = false,

    /**
     * Has error
     */
    var hasError: Boolean = false,

    /**
     * Execute agent
     */
    var executeAgent: CustomAgent? = null,

    /**
     * Next job file to be executed
     */
    var nextJob: ShireFile? = null,

    /**
     * The frontmatter of the file, which contains the configuration of Shire
     */
    var config: HobbitHole? = null,

    /**
     * Symbol table for all variables
     */
    var variableTable: VariableTable = VariableTable(),
)
