package com.phodal.shirelang.actions.dynamic

import com.phodal.shirelang.compiler.frontmatter.FrontMatterShireConfig
import com.phodal.shirelang.psi.ShireFile

data class DynamicShireActionConfig(
    val name: String,
    val config: FrontMatterShireConfig,
    val file: ShireFile
)