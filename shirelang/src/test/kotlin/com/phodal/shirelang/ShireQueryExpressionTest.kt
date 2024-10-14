package com.phodal.shirelang

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.rd.util.first
import com.phodal.shirelang.compiler.parser.ShireSyntaxAnalyzer
import com.phodal.shirelang.compiler.patternaction.PatternActionFunc
import com.phodal.shirelang.compiler.hobbit.execute.PatternActionProcessor
import com.phodal.shirelang.psi.ShireFile
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.intellij.lang.annotations.Language

class ShireQueryExpressionTest : BasePlatformTestCase() {
    fun testShouldGetFromExpression() {
        val sampleText = """HelloWorld.txt""".trimIndent()

        @Language("Shire")
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

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        val hole = compile.config!!

        val patternActionFuncs = hole.variables.first().value.patternActionFuncs
        val whereDisplay = (patternActionFuncs[1] as PatternActionFunc.Where).statement.display()
        val selectDisplay = (patternActionFuncs[2] as PatternActionFunc.Select).statements.map { it.display() }

        assertEquals(whereDisplay, "clazz.text == \"HelloWorld.txt\"")
        assertEquals(selectDisplay, listOf("clazz.toString", "\"code\""))

        val results = runBlocking {
            hole.variables.mapValues {
                PatternActionProcessor(project, hole, mutableMapOf()).execute(it.value)
            }
        }

        assertEquals(results["allController"], """
            PsiFile(plain text):HelloWorld.txt
            "code"
            """.trimIndent())
    }

    fun testShouldTestForDayNow() {
        @Language("Shire")
        val code = """
            ---
            variables:
              "dayNow": {
                from {
                    Date date
                }
                where {
                    date.dayOfWeek() != 8
                }
                select {
                    date.toString()
                }
              }
            ---
            
            ${'$'}dayNow
        """.trimIndent()

        val file = myFixture.addFileToProject("sample.shire", code)

        myFixture.openFileInEditor(file.virtualFile)

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        val hole = compile.config!!

        val results = runBlocking {
            hole.variables.mapValues {
                PatternActionProcessor(project, hole, mutableMapOf()).execute(it.value)
            }
        }

        val nowDate = results["dayNow"]
        TestCase.assertTrue(nowDate!!.startsWith("ShireDate(date="))
    }
}
