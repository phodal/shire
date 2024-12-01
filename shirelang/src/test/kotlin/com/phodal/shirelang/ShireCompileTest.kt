package com.phodal.shirelang

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirecore.config.InteractionType
import com.phodal.shirelang.compiler.parser.HobbitHoleParser
import com.phodal.shirelang.compiler.parser.ShireSyntaxAnalyzer
import com.phodal.shirelang.compiler.ast.LogicalExpression
import com.phodal.shirelang.compiler.ast.patternaction.PatternActionFunc
import com.phodal.shirelang.compiler.execute.PatternActionProcessor
import com.phodal.shirelang.psi.ShireFile
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.intellij.lang.annotations.Language

class ShireCompileTest : BasePlatformTestCase() {
    fun testNormalString() {
        val code = "Normal String /"
        val file = myFixture.configureByText("test.shire", code)

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        assertEquals("Normal String /", compile.shireOutput)
    }

    fun testWithFrontmatter() {
        @Language("Shire")
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

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        assertEquals("\n\nSummary webpage:\n", compile.shireOutput)
        compile.config!!.let {
            assertEquals("Summary", it.name)
            assertEquals("Generate Summary", it.description)
            assertEquals(InteractionType.AppendCursor, it.interaction)
            assertEquals(ShireActionLocation.CONTEXT_MENU, it.actionLocation)
        }
    }

    fun testWithFrontMatterArray() {
        @Language("Shire")
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

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        assertEquals("\n\nSummary webpage:\n", compile.shireOutput)
    }

    fun testShouldCheckFile() {
        @Language("Shire")
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

        val isFrontMatterPresent = HobbitHoleParser.hasFrontMatter(file as ShireFile)
        assertTrue(isFrontMatterPresent)
    }

    fun testShouldHandleForPatternAction() {
        @Language("Shire")
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

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        assertEquals("\n\nSummary webpage:", compile.shireOutput)
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
        @Language("Shire")
        val code = """
            ---
            when: ${'$'}selection.length >= 1 && ${'$'}selection.first() == 'p'
            ---
            
            Summary webpage:
        """.trimIndent()

        val file = myFixture.configureByText("test.shire", code)

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        assertEquals("\n\nSummary webpage:", compile.shireOutput)
        val when_ = compile.config?.when_

        assertEquals(when_!!.display(), "\$selection.length >= 1 && \$selection.first == \"p\"")

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

    fun testShouldHandleForWhenConditionInVariableExpr() {
        @Language("Shire")
        val code = """
            ---
            when: { ${'$'}selection.length >= 1 && ${'$'}selection.first() == 'p' }
            ---
            
            Summary webpage:
        """.trimIndent()

        val file = myFixture.configureByText("test.shire", code)

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        assertEquals("\n\nSummary webpage:", compile.shireOutput)
        val when_ = compile.config?.when_

        assertEquals(when_!!.display(), "\$selection.length >= 1 && \$selection.first == \"p\"")

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
        @Language("Shire")
        val code = """
            ---
            when: ${'$'}fileName.contains(".java") && ${'$'}filePath.contains("src/main/java")
            ---
            
            Summary webpage:
        """.trimIndent()

        val file = myFixture.configureByText("test.shire", code)

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        assertEquals("\n\nSummary webpage:", compile.shireOutput)
        val when_ = compile.config?.when_

        assertEquals(when_!!.display(), "\$fileName.contains(\".java\") && \$filePath.contains(\"src/main/java\")")
    }

    fun testShouldHandleForWhenConditionForPattern() {
        @Language("Shire")
        val code = """
            ---
            when: ${'$'}fileName.matches("/.*.java/")
            ---
            
            Summary webpage:
        """.trimIndent()

        val file = myFixture.configureByText("test.shire", code)

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        assertEquals("\n\nSummary webpage:", compile.shireOutput)
        val when_ = compile.config?.when_

        assertEquals(when_!!.display(), "\$fileName.matches(\"/.*.java/\")")
    }

    fun testShouldGetSymbolTableValueFromCompileResult() {
        @Language("Shire")
        val code = """
            ---
            name: Summary
            description: "Generate Summary"
            interaction: AppendCursor
            data: ["a", "b"]
            when: ${'$'}fileName.matches("/.*.java/")
            variables:
              "var1": "demo"
              "var2": /.*.java/ { print("hello") | sort }
            ---
            
            Summary webpage: ${'$'}fileName
        """.trimIndent()

        val file = myFixture.configureByText("test.shire", code)

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        val table = compile.variableTable

        val hole = compile.config!!

        assertEquals(1, table.getAllVariables().size)
        assertEquals(11, table.getVariable("fileName").lineDeclared)

        val results = runBlocking {
            hole.variables.mapValues {
                PatternActionProcessor(project, hole, mutableMapOf()).execute(it.value)
            }
        }

        assertEquals("demo", results["var1"])
        assertEquals("hello", results["var2"].toString())
    }

    fun testShouldLoadFile() {
        @Language("Shire")
        val code = """
            ---
            name: Summary
            description: "Generate Summary"
            interaction: AppendCursor
            data: ["a", "b"]
            when: ${'$'}fileName.matches("/.*.java/")
            variables:
              "var2": /.*ple.shire/ { cat | find("fileName") | sort }
            ---
            
            Summary webpage: ${'$'}fileName
        """.trimIndent()

        val file = myFixture.addFileToProject("sample.shire", code)

        myFixture.openFileInEditor(file.virtualFile)

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        val hole = compile.config!!

        val results = runBlocking {
            hole.variables.mapValues {
                PatternActionProcessor(project, hole, mutableMapOf()).execute(it.value)
            }
        }

        assertEquals(
            "  \"var2\": /.*ple.shire/ { cat | find(\"fileName\") | sort }\n" +
                    "Summary webpage: \$fileName\n" +
                    "when: \$fileName.matches(\"/.*.java/\")", results["var2"].toString()
        )
    }

    fun testShouldComputePatterCaseResult() {
        @Language("Shire")
        val code = """
            ---
            variables:
              "var1": /.*.shire/ {
                case "${'$'}0" {
                  "error" { grep("ERROR") | sort | xargs("notify_admin") }
                  "warn" { grep("WARN") | sort | xargs("notify_admin") }
                  "info" { grep("INFO") | sort | xargs("notify_user") }
                  default  { grep("(.*).shire") | sort }
                }
              }
            ---

            ${'$'}var1
            """.trimIndent()

        val file = myFixture.configureByText("test.shire", code)

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        val hole = compile.config!!

        TestCase.assertEquals(1, hole.variables.size)

        val results = runBlocking {
            hole.variables.mapValues {
                PatternActionProcessor(project, hole, mutableMapOf()).execute(it.value)
            }
        }

        assertEquals("/src/test", results["var1"])
    }

    fun testShouldSupportCorrectGrep() {
        @Language("Shire")
        val code = """
            ---
            name: Summary
            description: "Generate Summary"
            interaction: AppendCursor
            data: ["a", "b"]
            when: ${'$'}fileName.matches("/.*.java/")
            variables:
              "phoneNumber": "086-1234567890"
              "phoneNumber2": "088-1234567890"
              "var2": /.*ple.shire/ { cat | grep("([0-9]{3}-[0-9]{10})") }
            ---
            
            Summary webpage: ${'$'}fileName
        """.trimIndent()

        val file = myFixture.addFileToProject("sample.shire", code)

        myFixture.openFileInEditor(file.virtualFile)

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        val hole = compile.config!!

        val results = runBlocking {
            hole.variables.mapValues {
                PatternActionProcessor(project, hole, mutableMapOf()).execute(it.value)
            }
        }

        assertEquals("086-1234567890\n088-1234567890", results["var2"].toString())
    }

    fun testShouldHandleForDataRedact() {
        @Language("Shire")
        val code = """
            ---
            name: Summary
            description: "Generate Summary"
            interaction: AppendCursor
            data: ["a", "b"]
            when: ${'$'}fileName.matches("/.*.java/")
            variables:
              "phoneNumber": "086-1234567890"
              "var2": /.*ple.shire/ { cat | redact }
            ---
            
            Summary webpage: ${'$'}fileName
        """.trimIndent()

        val file = myFixture.addFileToProject("sample.shire", code)

        myFixture.openFileInEditor(file.virtualFile)

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        val hole = compile.config!!

        val results = runBlocking {
            hole.variables.mapValues {
                PatternActionProcessor(project, hole, mutableMapOf()).execute(it.value)
            }
        }

        assertEquals(
            """---
name: Summary
description: "Generate Summary"
interaction: AppendCursor
data: ["a", "b"]
when: ${'$'}fileName.matches("/.*.java/")
variables:
  "phoneNumber": "****"
  "var2": /.*ple.shire/ { cat | redact }
---

Summary webpage: ${'$'}fileName""", results["var2"].toString()
        )
    }

    fun testShouldConvertSourceCode() {
        @Language("Shire")
        val code = """
            ---
            name: "添加测试"
            actionLocation: ContextMenu
            variables:
              "sourceCode": /any/ { print(${'$'}filePath) | sed("src/test", "src/main") | sed("sample.shire", "sampleTest.shire") | print }
            onStreamingEnd: { parseCode | patch(${'$'}filePath, ${'$'}output) }
            ---
        """.trimIndent()

        val file = myFixture.addFileToProject("sample.shire", code)

        myFixture.openFileInEditor(file.virtualFile)

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        val hole = compile.config!!

        val results = runBlocking {
            hole.variables.mapValues {
                PatternActionProcessor(
                    project, hole, mutableMapOf(
                        "filePath" to "src/test/resources/sample.shire"
                    )
                ).execute(it.value)
            }
        }

        assertEquals(results["sourceCode"], "src/main/resources/sampleTest.shire")
    }

    fun testShouldSupportForeignFunction() {
        @Language("JavaScript")
        val jsMainWithArgs = """
            const args = process.argv.slice(2);
            console.log("hello, world");
            console.log(args[0]);
            
            process.exit(0);
        """.trimIndent()

        myFixture.addFileToProject("hello.js", jsMainWithArgs)

        @Language("Shire")
        val code = """
            ---
            name: Summary
            description: "Generate Summary"
            interaction: AppendCursor
            functions:
              normal: "hello.js"(string)
            variables:
              "var2": /.*ple.shire/ { normal }
            ---
            
            Summary webpage: ${'$'}fileName
        """.trimIndent()
        val file = myFixture.addFileToProject("sample.shire", code)
        myFixture.openFileInEditor(file.virtualFile)

        val compile = ShireSyntaxAnalyzer(project, file as ShireFile, myFixture.editor).parse()
        val hole = compile.config!!

        val results = runBlocking {
            hole.variables.mapValues {
                PatternActionProcessor(project, hole, mutableMapOf()).execute(it.value)
            }
        }

        assertEquals("", results["var2"].toString())
    }
}
