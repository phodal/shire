package com.phodal.shirelang

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.rd.util.first
import com.phodal.shirelang.compiler.ShireCompiler
import com.phodal.shirelang.compiler.patternaction.PatternActionFunc
import com.phodal.shirelang.compiler.hobbit.execute.PatternActionProcessor
import com.phodal.shirelang.psi.ShireFile

class ShireQueryExpressionTest : BasePlatformTestCase() {
    fun testShouldGetFromExpression() {
        val sampleText = """HelloWorld.txt""".trimIndent()

        val code = """
            ---
            variables:
              "allController": {
                from {
                    File clazz // the class
                }
                where {
                    clazz.text == "HelloWorld.txt"
                }
            
                select {
                    clazz.toString(), "code"
                }
              }
            ---
            
            ${'$'}allController
        """.trimIndent()

        myFixture.addFileToProject("HelloWorld.txt", sampleText)
        val file = myFixture.addFileToProject("sample.shire", code)

        myFixture.openFileInEditor(file.virtualFile)

        val compile = ShireCompiler(project, file as ShireFile, myFixture.editor).compile()
        val hole = compile.config!!

        val patternActionFuncs = hole.variables.first().value.patternActionFuncs
        val whereDisplay = (patternActionFuncs[1] as PatternActionFunc.Where).statement.display()
        val selectDisplay = (patternActionFuncs[2] as PatternActionFunc.Select).statements.map { it.display() }


        assertEquals(whereDisplay, "clazz.text == \"HelloWorld.txt\"")
        assertEquals(selectDisplay, listOf("clazz.toString", "\"code\""))

        val results = hole.variables.mapValues {
            PatternActionProcessor(project, hole).execute(it.value)
        }

        assertEquals(results["allController"], """
            PsiFile(plain text):HelloWorld.txt
            "code"
            """.trimIndent())
    }
}