package com.phodal.shirelang.java.complexity

import com.intellij.psi.PsiJavaFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.intellij.lang.annotations.Language

class JavaComplexityProviderTest: BasePlatformTestCase() {
    fun testShouldCalculateComplexitySize2() {
        @Language("Java")
        val code = """
            public class TestClass {
                @Complexity(4)
                public void parenthesisInCenterSplitTheGroup() {
                    if (                          // +1 if
                        a || b ||                 // +1 OR
                            !(c || d)             // +1 OR separate
                                || e || f) {      // +1 new OR
                        return;
                    }
                }

            }
        """.trimIndent()

        val psiFile = myFixture.addFileToProject("TestClass.java", code) as PsiJavaFile
        val psiClass = psiFile.classes[0]

        val complexityProvider = JavaComplexityProvider()
        val result = complexityProvider.process(psiClass)
        assertEquals(4, result)
    }
}
