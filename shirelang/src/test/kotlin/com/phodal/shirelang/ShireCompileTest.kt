package com.phodal.shirelang

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.phodal.shirelang.compiler.ShireCompiler
import com.phodal.shirelang.psi.ShireFile

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
            description: Generate Summary
            interaction: AppendCursor
            actionLocation: ContextMenu
            ---
            
            Summary webpage:
            
            /browse:https://www.phodal.com
        """.trimIndent()

        val file = myFixture.configureByText("test.shire", code)

        val compile = ShireCompiler(project, file as ShireFile, myFixture.editor).compile()
        println(compile.output)
//        assertEquals("Summary webpage:\n\n/browse:https://www.phodal.com", compile.output)
    }
}