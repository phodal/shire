package com.phodal.shirelang

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.phodal.shirecore.middleware.ShireRunVariableContext
import com.phodal.shirecore.middleware.PostProcessor
import com.phodal.shirecore.middleware.PostProcessor.Companion.handler
import com.phodal.shirecore.middleware.PostProcessorNode
import com.phodal.shirelang.compiler.ShireSyntaxAnalyzer
import com.phodal.shirelang.psi.ShireFile
import junit.framework.TestCase
import org.intellij.lang.annotations.Language
import org.jetbrains.annotations.TestOnly

class ShireLifecycleTest : BasePlatformTestCase() {
    fun testShouldHandleWhenStreamingEnd() {
        @Language("Shire")
        val code = """
            ---
            onStreamingEnd:  { parseCode | saveFile("api.py") | verifyCode | runCode }
            ---
            
            ${'$'}allController
        """.trimIndent()

        val file = myFixture.addFileToProject("sample.shire", code)

        myFixture.openFileInEditor(file.virtualFile)

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        val hole = compile.config!!

        val funcNode = hole.onStreamingEnd

        assertEquals(funcNode.size, 4)
        assertEquals(funcNode[0].funName, "parseCode")
        assertEquals(funcNode[0].args.size, 0)

        assertEquals(funcNode[1].funName, "saveFile")
        assertEquals(funcNode[1].args[0], "api.py")

        assertEquals(funcNode[2].funName, "verifyCode")
        assertEquals(funcNode[3].funName, "runCode")

        val handleContext = ShireRunVariableContext(currentLanguage = ShireLanguage.INSTANCE, editor = null)
        execute(project, funcNode, handleContext, null)
    }

    @TestOnly
    fun execute(project: Project, funcNodes: List<PostProcessorNode>, handleContext: ShireRunVariableContext, console: ConsoleView?) {
        funcNodes.forEach { funNode ->
            val handler = handler(funNode.funName)
            if (handler != null) {
                handler.setup(handleContext)
                handler.execute(project, handleContext, console, funNode.args)
                handler.finish(handleContext)
            }
        }
    }

    fun testShouldHandleWhenAfterStreaming() {
        @Language("Shire")
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

        val file = myFixture.addFileToProject("sample.shire", code)

        myFixture.openFileInEditor(file.virtualFile)

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
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
        val handleContext = ShireRunVariableContext(
            currentLanguage = ShireLanguage.INSTANCE,
            genText = genJson,
            editor = null
        )

        assertThrows(RuntimeException::class.java) {
            hole.afterStreaming?.execute(myFixture.project, handleContext, hole)
        }
    }
}