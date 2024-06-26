package com.phodal.shirelang

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.middleware.PostProcessor
import com.phodal.shirelang.compiler.ShireCompiler
import com.phodal.shirelang.psi.ShireFile

class ShireLifecycleTest: BasePlatformTestCase() {
    fun testShouldHandleWhenStreamingEnd() {
        val code = """
            ---
            onStreamingEnd:  { verifyCode | runCode }
            ---
            
            ${'$'}allController
        """.trimIndent()

        val file = myFixture.addFileToProject("sample.shire", code)

        myFixture.openFileInEditor(file.virtualFile)

        val compile = ShireCompiler(project, file as ShireFile, myFixture.editor).compile()
        val hole = compile.config!!

        val funcNode = hole.onStreamingEnd

        assertEquals(funcNode.size, 2)
        assertEquals(funcNode[0].funName, "verifyCode")
        assertEquals(funcNode[1].funName, "runCode")

        val handleContext = PostCodeHandleContext(file, ShireLanguage.INSTANCE.displayName, file)
        PostProcessor.execute(project, funcNode, handleContext)
    }

    fun testShouldHandleWhenAfterStreaming() {
        val code = """
            ---
            afterStreaming: {
              condition {
                "variable-success" { ${"$"}selection.length > 1 }
                "jsonpath-success" { jsonpath("$['store']['book'][0]['title']") }
              }
              case condition {
                "variable-success" { done }
                "jsonpath-success" { task() }
                default { task() }
              }
            }
            ---
            
            ${'$'}allController
        """.trimIndent()

        val file = myFixture.addFileToProject("sample.shire", code)

        myFixture.openFileInEditor(file.virtualFile)

        val compile = ShireCompiler(project, file as ShireFile, myFixture.editor).compile()
        val hole = compile.config!!

        val funcNode = hole.afterStreaming

        println(funcNode)
    }
}