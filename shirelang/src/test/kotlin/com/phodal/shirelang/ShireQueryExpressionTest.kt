package com.phodal.shirelang

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.phodal.shirelang.compiler.ShireCompiler
import com.phodal.shirelang.compiler.patternaction.PatternActionProcessor
import com.phodal.shirelang.psi.ShireFile

class ShireQueryExpressionTest : BasePlatformTestCase() {
    val javaHelloWorld = """
        public class HelloWorld {
            public static void main(String[] args) {
                System.out.println("Hello, World");
            }
        }
    """.trimIndent()

    fun testShouldGetFromExpression() {
        val code = """
            ---
            variables:
              "allController": {
                from {
                    PsiClass clazz // the class
                }
                where {
                    clazz.extends("org.springframework.web.bind.annotation.RestController") and clazz.getAnAnnotation() == "org.springframework.web.bind.annotation.RequestMapping"
                }
            
                select {
                    clazz.id, clazz.name, "code"
                }
              }
            ---
            
            ${'$'}allController
        """.trimIndent()

        myFixture.addFileToProject("HelloWorld.java", javaHelloWorld)
        val file = myFixture.addFileToProject("sample.shire", code)

        myFixture.openFileInEditor(file.virtualFile)

        val compile = ShireCompiler(project, file as ShireFile, myFixture.editor).compile()
        val hole = compile.config!!
        val editor = myFixture.editor

        val results = hole.variables.mapValues {
            PatternActionProcessor(project, editor, hole).execute(it.value)
        }

//        assertEquals(results["allController"], listOf("HelloWorld", "code"))
    }
}