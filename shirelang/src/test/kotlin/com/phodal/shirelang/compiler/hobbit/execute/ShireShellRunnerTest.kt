package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.intellij.lang.annotations.Language

class ShireShellRunnerTest: BasePlatformTestCase() {
    fun testFill() {
        @Language("JSON")
        val jsonEnv = """
            {
              "development": {
                "name": "Phodal"
              }
            }
             """.trimIndent()

        myFixture.addFileToProject("demo.shireEnv.json", jsonEnv)

        @Language("Shell Script")
        val content = """
            echo "Hello ${'$'}{name}, my name is ${'$'}{myName}!"
        """.trimIndent()

        val file = myFixture.addFileToProject("demo.seh", content)

        val fill = ShireShellRunner.fill(
            project, file.virtualFile, mapOf(
                "myName" to "Shire"
            )
        )

        assertEquals("echo \"Hello Phodal, my name is Shire!\"", fill)
    }
}
