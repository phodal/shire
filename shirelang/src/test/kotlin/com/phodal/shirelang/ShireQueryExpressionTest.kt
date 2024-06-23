package com.phodal.shirelang

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.rd.util.first
import com.phodal.shirelang.compiler.ShireCompiler
import com.phodal.shirelang.compiler.patternaction.PatternActionFunc
import com.phodal.shirelang.compiler.patternaction.PatternActionProcessor
import com.phodal.shirelang.psi.ShireFile

class ShireQueryExpressionTest : BasePlatformTestCase() {
    val javaHelloWorld = """
        public class HelloWorld {
            public static void main(String[] args) {
                System.out.println("Hello, World");
            }
        }
    """.trimIndent()

    fun testShouldGetFromExpression() {
        val code = """
            ---
            variables:
              "allController": {
                from {
                    File clazz // the class
                }
                where {
                    clazz.name == "HelloWorld.txt"
                }
            
                select {
                    clazz.id, clazz.name, "code"
                }
              }
            ---
            
            ${'$'}allController
        """.trimIndent()

        myFixture.addFileToProject("HelloWorld.txt", javaHelloWorld)
        val file = myFixture.addFileToProject("sample.shire", code)

        myFixture.openFileInEditor(file.virtualFile)

        val compile = ShireCompiler(project, file as ShireFile, myFixture.editor).compile()
        val hole = compile.config!!
        val editor = myFixture.editor

        val patternActionFuncs = hole.variables.first().value.patternActionFuncs
        val whereDisplay = (patternActionFuncs[1] as PatternActionFunc.Where).statement.display()
        val selectDisplay = (patternActionFuncs[2] as PatternActionFunc.Select).variable.map { it.display() }


        assertEquals(whereDisplay, "clazz.name == \"HelloWorld.txt\"")
        assertEquals(selectDisplay, listOf("clazz.id", "clazz.name", "\"code\""))

        val results = hole.variables.mapValues {
            PatternActionProcessor(project, editor, hole).execute(it.value)
        }

        println(results)
//        assertEquals(results["allController"], listOf("HelloWorld", "code"))
    }
}