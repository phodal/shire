package com.phodal.shirelang.run

import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.context.ActionLocationEditor
import com.phodal.shirelang.compiler.ShireParsedResult
import com.phodal.shirelang.compiler.ShireSyntaxAnalyzer
import com.phodal.shirelang.psi.ShireFile

fun preAnalysisSyntax(shireFile: ShireFile, project: Project): ShireParsedResult {
    val syntaxAnalyzer = ShireSyntaxAnalyzer(project, shireFile, ActionLocationEditor.defaultEditor(project))
    val parsedResult = syntaxAnalyzer.parse()
    return parsedResult
}