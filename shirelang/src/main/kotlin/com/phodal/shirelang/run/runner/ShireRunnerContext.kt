package com.phodal.shirelang.run.runner

import com.intellij.openapi.editor.Editor
import com.phodal.shirelang.compiler.ShireCompiledResult
import com.phodal.shirelang.compiler.hobbit.HobbitHole

data class ShireRunnerContext(
    val hole: HobbitHole?,
    val editor: Editor?,
    val compileResult: ShireCompiledResult,
    val finalPrompt: String = "",
    val hasError: Boolean,
)
