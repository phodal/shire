package com.phodal.shirelang.regression

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.phodal.shirecore.middleware.post.PostProcessorContext
import com.phodal.shirelang.compiler.parser.ShireSyntaxAnalyzer
import com.phodal.shirelang.compiler.template.ShireTemplateCompiler
import com.phodal.shirelang.psi.ShireFile
import kotlinx.coroutines.runBlocking
import org.intellij.lang.annotations.Language

class ShireTokenizerTest : BasePlatformTestCase() {
    val javaHelloController = """
            package com.phodal.shirelang.controller;
            
            import org.springframework.web.bind.annotation.GetMapping;
            import org.springframework.web.bind.annotation.RestController;
            
            @RestController
            public class HelloController {
                @GetMapping("/hello")
                public String hello() {
                    return "Hello, World!";
                }
            }
        """.trimIndent()

    fun testShouldReturnControllerCodeWithFindCat() {
        myFixture.addFileToProject(
            "HelloController.java",
            javaHelloController
        )

        @Language("Shire")
        val code = """
            ---
            name: "类图分析"
            variables:
              "controllers": /.*.java/ { cat }
              "tokens": /any/ { tokenizer(${'$'}controllers, "word") }
              "chinese": /any/ { tokenizer("孩子上了幼儿园 安全防拐教育要做好", "jieba") }
            ---
            
            ${'$'}controllers
        """.trimIndent()

        val file = myFixture.configureByText("test.shire", code)
        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parseAndExecuteLocalCommand()
        val hole = compile.config!!

        val context = PostProcessorContext(
            genText = "User prompt:\n\n",
        )

        runBlocking {
            val templateCompiler = ShireTemplateCompiler(project, hole, compile.variableTable, code, myFixture.editor)
            val compiledVariables =
                templateCompiler.compileVariable(myFixture.editor, mutableMapOf())

            context.compiledVariables = compiledVariables
        }

        assertEquals(
            """[package, com, phodal, shirelang, controller, import, org, springframework, web, bind, annotation, GetMapping, RestController, public, class, HelloController, hello, String, return, Hello, World]""",
            context.compiledVariables["tokens"]
        )
        assertEquals(
            listOf("孩子", "上", "了", "幼儿园",  "安全", "防拐", "教育", "要", "做好").toString(),
            context.compiledVariables["chinese"].toString()
        )
    }
}
