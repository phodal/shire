package com.phodal.shirelang

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirelang.compiler.ShireSyntaxAnalyzer
import com.phodal.shirelang.compiler.ShireTemplateCompiler
import com.phodal.shirelang.compiler.hobbit.execute.PatternActionProcessor
import com.phodal.shirelang.psi.ShireFile
import kotlinx.coroutines.runBlocking
import org.intellij.lang.annotations.Language

class ShirePatternPipelineTest : BasePlatformTestCase() {
    fun testShouldSupportForTee() {
        @Language("Shire")
        val code = """
            ---
            name: Summary
            description: "Generate Summary"
            interaction: AppendCursor
            data: ["a", "b"]
            when: ${'$'}fileName.matches("/.*.java/")
            variables:
              "var2": /.*ple.shire/ { cat | grep("fileName") | sort }
            onStreamingEnd: { append(${'$'}var2) | saveFile("summary.md") }
            ---
            
            Summary webpage: ${'$'}fileName
        """.trimIndent()

        val file = myFixture.addFileToProject("sample.shire", code)

        myFixture.openFileInEditor(file.virtualFile)

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        val hole = compile.config!!

        val context = PostCodeHandleContext(
            genText = "User prompt:\n\n",
        )

        runBlocking {
            val compiledVariables =
                ShireTemplateCompiler(project, hole, compile.variableTable, code).compileVariable(myFixture.editor)

            context.compiledVariables = compiledVariables

            hole.variables.mapValues {
                PatternActionProcessor(project, hole).execute(it.value)
            }

            hole.setupStreamingEndProcessor(project, context = context)
            hole.executeStreamingEndProcessor(project, null, context = context)
        }

        assertEquals("User prompt:\n\n" +
                "  \"var2\": /.*ple.shire/ { cat | grep(\"fileName\") | sort }\n" +
                "Summary webpage: \$fileName\n" +
                "when: \$fileName.matches(\"/.*.java/\")", context.genText)
    }

    fun testShouldSupportAfterStreamingPattern() {
        @Language("Shire")
        val code = """
            ---
            name: Summary
            description: "Generate Summary"
            interaction: AppendCursor
            variables:
              "var2": "sample"
            afterStreaming: { 
                case condition {
                  default { print(${'$'}output) }
                }
            }
            ---
            
            Summary webpage: ${'$'}fileName
        """.trimIndent()

        val file = myFixture.addFileToProject("sample.shire", code)

        myFixture.openFileInEditor(file.virtualFile)

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        val hole = compile.config!!

        val context = PostCodeHandleContext(
            genText = "User prompt:\n\n",
        )

        runBlocking {
            val compiledVariables =
                ShireTemplateCompiler(project, hole, compile.variableTable, code).compileVariable(myFixture.editor)

            context.compiledVariables = compiledVariables

            hole.variables.mapValues {
                PatternActionProcessor(project, hole).execute(it.value)
            }

            hole.setupStreamingEndProcessor(project, context = context)
            hole.executeAfterStreamingProcessor(project, null, context = context)
        }

        assertEquals("User prompt:\n\n", context.lastTaskOutput)
    }
}
