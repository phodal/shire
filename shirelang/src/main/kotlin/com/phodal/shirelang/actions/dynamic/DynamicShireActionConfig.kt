package com.phodal.shirelang.actions.dynamic

import com.phodal.shirelang.compiler.FrontmatterParser
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.psi.ShireFile

data class DynamicShireActionConfig(
    val name: String,
    val hole: HobbitHole,
    val shireFile: ShireFile
) {
    companion object {
        fun from(file: ShireFile): DynamicShireActionConfig {
            val hole = FrontmatterParser.parse(file)
            return DynamicShireActionConfig(file.name, hole!!, file)
        }
    }
}