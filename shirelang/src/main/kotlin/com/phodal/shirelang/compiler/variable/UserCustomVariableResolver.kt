package com.phodal.shirelang.compiler.variable

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.patternaction.PatternActionProcessor

class UserCustomVariableResolver(
    val project: Project,
    val editor: Editor,
    val hole: HobbitHole?
) : VariableResolver {
    override fun resolve() : Map<String, String> {
        if (hole == null) {
            return emptyMap()
        }

        return hole.variables.mapValues {
            PatternActionProcessor(project, editor, hole).execute(it.value)
        }
    }
}