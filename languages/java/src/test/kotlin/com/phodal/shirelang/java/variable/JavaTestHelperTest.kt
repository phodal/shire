package com.phodal.shirelang.java.variable

import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiMethod
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase

class JavaTestHelperTest : BasePlatformTestCase() {

    val code = """
        import org.junit.Test;

public class MathHelperTest {

    @Test
    public void testAdditionWithPositiveNumbers() {

    }

    @Test
    public void testAdditionWithNegativeNumbers() {

    }

    @Test
    public void testSubtractionWithPositiveNumbers() {

    }

    @Test
    public void testSubtractionWithNegativeNumbers() {

    }

    @Test
    public void testMultiplicationWithPositiveNumbers() {

    }

    @Test
    public void testMultiplicationWithNegativeNumbers() {

    }

    @Test
    public void testDivisionWithPositiveNumbers() {

    }

    @Test
    public void testDivisionWithNegativeNumbers() {

    }
}
    """.trimIndent()

    fun testShouldReturnCorrectJavaMethodName() {
        val code2 = """
            class MathHelper {
                public int AdditionWith(int a, int b) {
                    return a + b;
                }
            }
        """.trimIndent()
        val psiFile = myFixture.addFileToProject("MathHelperTest.java", code)
        val psiFile2 = myFixture.addFileToProject("MathHelper.java", code2) as PsiJavaFile

        val addMethod = psiFile2.classes.first().methods.first() as PsiMethod

        val testCases = JavaTestHelper.findSimilarTestCases(addMethod)
        TestCase.assertEquals(0, testCases.size)
    }
}
