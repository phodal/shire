package com.phodal.shirelang

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirecore.agent.InteractionType
import com.phodal.shirelang.compiler.FrontmatterParser
import com.phodal.shirelang.compiler.ShireCompiler
import com.phodal.shirelang.compiler.hobbit.LogicalExpression
import com.phodal.shirelang.compiler.hobbit.patternaction.PatternActionFunc
import com.phodal.shirelang.compiler.hobbit.patternaction.PatternActionProcessor
import com.phodal.shirelang.psi.ShireFile

class ShireCompileTest : BasePlatformTestCase() {
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
        val filenameRules = compile.config!!.preFilter

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

        val patterns = var2.patternActionFuncs
        assertEquals(3, patterns.size)
        assertEquals("grep", patterns[0].funcName)
        assertEquals("error.log", (patterns[0] as PatternActionFunc.Grep).patterns[0])
        assertEquals("sort", patterns[1].funcName)
        assertEquals("xargs", patterns[2].funcName)
    }

    fun testShouldHandleForWhenCondition() {
        val code = """
            ---
            when: ${'$'}selection.length >= 1 && ${'$'}selection.first() == 'p'
            ---
            
            Summary webpage:
        """.trimIndent()

        val file = myFixture.configureByText("test.shire", code)

        val compile = ShireCompiler(project, file as ShireFile, myFixture.editor).compile()
        assertEquals("\n\nSummary webpage:", compile.output)
        val when_ = compile.config?.when_

        assertEquals(when_!!.display(), "\$selection.length() >= 1 && \$selection.first() == \"p\"")

        val variables: Map<String, String> = mapOf(
            "selection" to """public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World");
    }
}"""
        )

        val result = (when_.value as LogicalExpression).evaluate(variables)
        assertTrue(result)
    }

    fun testShouldHandleForWhenConditionForContains() {
        val code = """
            ---
            when: ${'$'}fileName.contains(".java") && ${'$'}filePath.contains("src/main/java")
            ---
            
            Summary webpage:
        """.trimIndent()

        val file = myFixture.configureByText("test.shire", code)

        val compile = ShireCompiler(project, file as ShireFile, myFixture.editor).compile()
        assertEquals("\n\nSummary webpage:", compile.output)
        val when_ = compile.config?.when_

        assertEquals(when_!!.display(), "\$fileName.contains(\".java\") && \$filePath.contains(\"src/main/java\")")
    }

    fun testShouldHandleForWhenConditionForPattern() {
        val code = """
            ---
            when: ${'$'}fileName.matches("/.*.java/")
            ---
            
            Summary webpage:
        """.trimIndent()

        val file = myFixture.configureByText("test.shire", code)

        val compile = ShireCompiler(project, file as ShireFile, myFixture.editor).compile()
        assertEquals("\n\nSummary webpage:", compile.output)
        val when_ = compile.config?.when_

        assertEquals(when_!!.display(), "\$fileName.matches(\"/.*.java/\")")
    }

    fun testShouldGetSymbolTableValueFromCompileResult() {
        val code = """
            ---
            name: Summary
            description: "Generate Summary"
            interaction: AppendCursor
            data: ["a", "b"]
            when: ${'$'}fileName.matches("/.*.java/")
            variables:
              "var1": "demo"
              "var2": /**.java/ { print("hello") | sort }
            ---
            
            Summary webpage: ${'$'}fileName
        """.trimIndent()

        val file = myFixture.configureByText("test.shire", code)

        val compile = ShireCompiler(project, file as ShireFile, myFixture.editor).compile()
        val table = compile.symbolTable

        val hole = compile.config!!

        assertEquals(1, table.getAllVariables().size)
        assertEquals(11, table.getVariable("fileName").lineDeclared)


        val editor = myFixture.editor

        val results = hole.variables.mapValues {
            PatternActionProcessor(project, editor, hole).execute(it.value)
        }

        assertEquals("demo", results["var1"])
        assertEquals("hello", results["var2"].toString())
    }
}
