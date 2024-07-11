package com.phodal.shirelang.java.impl

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase


class JavaPsiQLInterpreterTest: BasePlatformTestCase() {

    fun testShouldSuccessGetClassName() {
        val javaClassCode = """
            public class TestClass extends Object implements Runnable {
                public String getName() {
                    return "TestClass";
                }
            }
        """.trimIndent()

        val psiFile = myFixture.addFileToProject("TestClass.java", javaClassCode) as PsiJavaFile
        val psiClass = psiFile.classes[0]

        val interpreter = JavaPsiQLInterpreter()
        val result = interpreter.resolveCall(psiClass, "getName", emptyList())
        val extendsClassName = interpreter.resolveCall(psiClass, "extends", emptyList())
        // implements
        val implementsClassName = interpreter.resolveCall(psiClass, "implements", emptyList())

        assertEquals("TestClass", result)
        assertEquals(listOf("Object"), extendsClassName)
        assertEquals(listOf("Runnable"), implementsClassName)
    }

    fun testShouldResolveParentOf() {
        val javaClassCode = """
            public class TestClass extends ShireObject implements Runnable {
                public String getName() {
                    return "TestClass";
                }
            }
        """.trimIndent()

        val shireObjectCode = """
            public class ShireObject {
                public String getName() {
                    return "ShireObject";
                }
            }
        """.trimIndent()

        val psiFile = myFixture.addFileToProject("TestClass.java", javaClassCode) as PsiJavaFile
        myFixture.addFileToProject("ShireObject.java", shireObjectCode)

        val psiClass = psiFile.classes[0]

        val interpreter = JavaPsiQLInterpreter()
        val result = interpreter.resolveCall(psiClass, "superclassOf", listOf("TestClass"))
        // extendsOf
        val extendsClasses = interpreter.resolveOfTypedCall(project, "subclassesOf", listOf("ShireObject"))

        assertEquals("ShireObject", (result as PsiClass).name)
        assertEquals("TestClass", (extendsClasses as List<PsiClass>).first().name)
    }
}
