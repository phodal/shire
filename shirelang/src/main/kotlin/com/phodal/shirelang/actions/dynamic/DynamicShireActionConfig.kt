package com.phodal.shirelang.actions.dynamic

import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.psi.ShireFile

data class DynamicShireActionConfig(
    val name: String,
    val config: HobbitHole,
    val shireFile: ShireFile
)