package com.phodal.shirelang.python.util

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.intellij.lang.annotations.Language

class PythonPsiUtilTest : BasePlatformTestCase() {
    fun testShouldGetClassWithoutMethod() {
        @Language("Python")
        val code = """
            class MathHelper:
                def addition_with_positive_numbers(self):
                    pass
                def addition_with_negative_numbers(self):
                    pass
        """.trimIndent()

        val file = myFixture.addFileToProject("Hello.py", code)

//        val psiFile = PsiManager.getInstance(project).findFile(file.virtualFile) as PyFile
//
//        val firstClass = psiFile.children[0] as PyClass
//        val firstMethod = firstClass.children[0] as PyFunction
//
//        val testName = PythonPsiUtil.getClassWithoutMethod(firstClass, firstMethod)
//        assertEquals("test_example.py", testName)
    }
}
