package com.phodal.shirelang.python.util

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.intellij.lang.annotations.Language

class PyTestUtilTest : BasePlatformTestCase() {
    fun testShouldReturnCorrectPythonTestName() {
        @Language("Python")
        val code = """
            def hello():
                pass
        """.trimIndent()

        val psiFile = myFixture.addFileToProject("Hello.py", code)

        val testName = PyTestUtil.getTestNameExample(psiFile.virtualFile)
        assertEquals("test_example.py", testName)
    }
}
