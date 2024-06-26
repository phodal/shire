package com.phodal.shirelang

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.middleware.PostProcessor
import com.phodal.shirelang.compiler.ShireCompiler
import com.phodal.shirelang.psi.ShireFile
import junit.framework.TestCase

class ShireLifecycleTest : BasePlatformTestCase() {
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

        val handleContext = PostCodeHandleContext(currentLanguage = ShireLanguage.INSTANCE)
        PostProcessor.execute(project, funcNode, handleContext, null)
    }

    fun testShouldHandleWhenAfterStreaming() {
//        val code2 = "hi"
        val code = """
            ---
            afterStreaming: {
                condition {
                  "error"       { output.length < 1 }
                  "success"     { output.length > 1 }
                  "json-result" { jsonpath("${'$'}.store.*") }
                }
                case condition {
                  "error"       { notify("Failed to Generate JSON") }
                  "success"     { notify("Success to Generate JSON") }
                  "json-result" { execute("sample2.shire") }
                  default       { notify("Failed to Generate JSON") /* mean nothing */ }
                }
              }
            ---
            
            ${'$'}allController
        """.trimIndent()

//        myFixture.addFileToProject("sample2.shire", code2)
        val file = myFixture.addFileToProject("sample.shire", code)

        myFixture.openFileInEditor(file.virtualFile)

        val compile = ShireCompiler(project, file as ShireFile, myFixture.editor).compile()
        val hole = compile.config!!

        val funcNode = hole.afterStreaming!!

        TestCase.assertEquals(funcNode.conditions.size, 3)
        TestCase.assertEquals(funcNode.conditions[0].conditionKey, "\"error\"")

        assertEquals(funcNode.conditions[2].valueExpression.display(), "jsonpath(\"${'$'}.store.*\")")

        TestCase.assertEquals(funcNode.cases.size, 4)
        TestCase.assertEquals(funcNode.cases[0].caseKey, "\"error\"")

        val genJson = """
            {
                "store": {
                    "book": [
                        {
                            "category": "reference",
                            "author": "Nigel Rees",
                            "title": "Sayings of the Century",
                            "price": 8.95
                        }
                    ]
                }
            }
           """.trimIndent()
        val handleContext = PostCodeHandleContext(
            currentLanguage = ShireLanguage.INSTANCE,
            genText = genJson
        )

        val matchedCase = hole.afterStreaming?.execute(myFixture.project, handleContext, hole)
        assertEquals(matchedCase?.size, 2)

        assertEquals(matchedCase?.get(0)?.caseKey, "\"success\"")
        assertEquals(matchedCase?.get(1)?.caseKey, "\"json-result\"")
    }
}