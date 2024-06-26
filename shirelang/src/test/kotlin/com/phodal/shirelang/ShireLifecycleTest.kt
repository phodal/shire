package com.phodal.shirelang

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.phodal.shirelang.compiler.ShireCompiler
import com.phodal.shirelang.compiler.patternaction.PatternActionProcessor
import com.phodal.shirelang.psi.ShireFile

class ShireLifecycleTest: BasePlatformTestCase() {
    fun testShouldHandleWhenStreamingEnd() {
        val sampleText = """HelloWorld.txt""".trimIndent()

        val code = """
            ---
            onStreamingEnd:  { verifyCode | runCode }
            ---
            
            ${'$'}allController
        """.trimIndent()

        myFixture.addFileToProject("HelloWorld.txt", sampleText)
        val file = myFixture.addFileToProject("sample.shire", code)

        myFixture.openFileInEditor(file.virtualFile)

        val compile = ShireCompiler(project, file as ShireFile, myFixture.editor).compile()
        val hole = compile.config!!

        val postProcessors = hole.onStreamingEnd

        assertEquals(postProcessors.size, 2)
        assertEquals(postProcessors[0].funName, "verifyCode")
        assertEquals(postProcessors[1].funName, "runCode")
    }
}