package com.phodal.shirelang.compiler.parser

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.phodal.shirelang.psi.ShireFile
import org.intellij.lang.annotations.Language

class HobbitHoleParserTest : BasePlatformTestCase() {
    fun testShouldParseFunctions() {
        @Language("Shire")
        val code = """
           ---
           functions:
             normal: "defaultOutput.py"(string)
             output: "multipleOutput.py"(string) -> content, size
             special: "accessFunctionIfSupport.py"::resize(string, number, number) -> image
           ---
        """.trimIndent()

        val file = myFixture.configureByText("test.shire", code)
        val hobbitHole = HobbitHoleParser.parse(file as ShireFile)!!

        assertEquals(3, hobbitHole.foreignFunctions.size)
        val firstFunc = hobbitHole.foreignFunctions["normal"]!!
        assertEquals("normal", firstFunc.funcName)
        assertEquals("defaultOutput.py", firstFunc.funcPath)

        val secondFunc = hobbitHole.foreignFunctions["output"]!!
        assertEquals("output", secondFunc.funcName)
        assertEquals("multipleOutput.py", secondFunc.funcPath)
        assertEquals(listOf("content", "size"), secondFunc.returnVars.keys.toList())

        val thirdFunc = hobbitHole.foreignFunctions["special"]!!
        assertEquals("special", thirdFunc.funcName)
        assertEquals("accessFunctionIfSupport.py", thirdFunc.funcPath)
        assertEquals(listOf("image"), thirdFunc.returnVars.keys.toList())
        assertEquals(listOf("string", "number", "number"), thirdFunc.inputTypes)
    }
}