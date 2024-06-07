package com.phodal.shirelang.actions.dynamic

import com.phodal.shirelang.compiler.frontmatter.HobbitHole
import com.phodal.shirelang.psi.ShireFile

data class DynamicShireActionConfig(
    val name: String,
    val config: HobbitHole,
    val file: ShireFile
)