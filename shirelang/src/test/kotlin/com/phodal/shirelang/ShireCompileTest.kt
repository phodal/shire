package com.phodal.shirelang

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirecore.agent.InteractionType
import com.phodal.shirelang.compiler.FrontmatterParser
import com.phodal.shirelang.compiler.ShireCompiler
import com.phodal.shirelang.compiler.hobbit.FrontMatterType
import com.phodal.shirelang.compiler.hobbit.ShirePatternAction
import com.phodal.shirelang.psi.ShireFile
import junit.framework.TestCase

class ShireCompileTest: BasePlatformTestCase() {
    fun testNormalString() {
        val code = "Normal String /"
        val file = myFixture.configureByText("test.shire", code)

        val compile = ShireCompiler(project, file as ShireFile, myFixture.editor).compile()
        assertEquals("Normal String /", compile.output)
    }

    fun testWithFrontmatter() {
        val code = """
            ---
            name: Summary
            description: "Generate Summary"
            interaction: AppendCursor
            actionLocation: ContextMenu
            ---
            
            Summary webpage:
            
        """.trimIndent()

        val file = myFixture.configureByText("test.shire", code)

        val compile = ShireCompiler(project, file as ShireFile, myFixture.editor).compile()
        assertEquals("\n\nSummary webpage:\n", compile.output)
        compile.config!!.let {
            assertEquals("Summary", it.name)
            assertEquals("Generate Summary", it.description)
            assertEquals(InteractionType.AppendCursor, it.interaction)
            assertEquals(ShireActionLocation.CONTEXT_MENU, it.actionLocation)
        }
    }

    fun testWithFrontMatterArray() {
        val code = """
            ---
            name: Summary
            description: "Generate Summary"
            interaction: AppendCursor
            data: ["a", "b"]
            ---
            
            Summary webpage:
            
        """.trimIndent()

        val file = myFixture.configureByText("test.shire", code)

        val compile = ShireCompiler(project, file as ShireFile, myFixture.editor).compile()
        assertEquals("\n\nSummary webpage:\n", compile.output)
    }

    fun testShouldCheckFile() {
        val code = """
            ---
            name: Summary
            description: "Generate Summary"
            interaction: AppendCursor
            data: ["a", "b"]
            ---
            
            Summary webpage:
            
        """.trimIndent()

        val file = myFixture.configureByText("test.shire", code)

        val isFrontMatterPresent = FrontmatterParser.hasFrontMatter(file as ShireFile)
        assertTrue(isFrontMatterPresent)
    }

    fun testShouldHandleForObject() {
        val code = """
            ---
            name: Summary
            description: "Generate Summary"
            interaction: AppendCursor
            data: ["a", "b"]
            filenameRules: 
              "/**.java/": "You should thinking in best Kotlin way."
            ---
            
            Summary webpage:
        """.trimIndent()

        val file = myFixture.configureByText("test.shire", code)

        val compile = ShireCompiler(project, file as ShireFile, myFixture.editor).compile()
        assertEquals("\n\nSummary webpage:", compile.output)
        val filenameRules = compile.config!!.filenameRules

        assertEquals(1, filenameRules.size)
        assertEquals("/**.java/", filenameRules[0].pattern)
    }

    fun testShouldHandleForPatternAction() {
        val code = """
            ---
            name: Summary
            description: "Generate Summary"
            interaction: AppendCursor
            data: ["a", "b"]
            variables:
              "var1": "demo"
              "var2": /**.java/ { grep("error.log") | sort | xargs("rm")}
            ---
            
            Summary webpage:
        """.trimIndent()

        val file = myFixture.configureByText("test.shire", code)

        val compile = ShireCompiler(project, file as ShireFile, myFixture.editor).compile()
        assertEquals("\n\nSummary webpage:", compile.output)
        val map = compile.config!!.variables


        val var2 = map["var2"]!!

        assertEquals(3, var2.size)
      }
}
