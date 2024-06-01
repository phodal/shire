package com.phodal.shirelang.compiler

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
//    var executeAgent: CustomAgentConfig? = null,
    var executeAgent: Any? = null,
    /**
     * Next job to be executed
     */
    var nextJob: ShireFile? = null
)
