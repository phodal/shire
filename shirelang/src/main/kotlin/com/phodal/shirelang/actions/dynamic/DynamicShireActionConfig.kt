package com.phodal.shirelang.actions.dynamic

import com.intellij.openapi.editor.Editor
import com.phodal.shirelang.compiler.FrontmatterParser
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.psi.ShireFile

data class DynamicShireActionConfig(
    val name: String,
    val hole: HobbitHole ? = null,
    val shireFile: ShireFile,
    val editor: Editor? = null,
) {
    companion object {
        fun from(file: ShireFile): DynamicShireActionConfig {
            val hole = FrontmatterParser.parse(file)
            return DynamicShireActionConfig(file.name, hole, file)
        }
    }
}