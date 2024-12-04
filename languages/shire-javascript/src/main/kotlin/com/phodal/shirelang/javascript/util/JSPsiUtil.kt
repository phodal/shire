package com.phodal.shirelang.javascript.util

import com.intellij.lang.ecmascript6.psi.ES6ExportDeclaration
import com.intellij.lang.ecmascript6.psi.ES6ExportDefaultAssignment
import com.intellij.lang.javascript.frameworks.commonjs.CommonJSUtil
import com.intellij.lang.javascript.psi.*
import com.intellij.lang.javascript.psi.ecma6.TypeScriptGenericOrMappedTypeParameter
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeListOwner
import com.intellij.lang.javascript.psi.ecmal4.JSClass
import com.intellij.lang.javascript.psi.ecmal4.JSQualifiedNamedElement
import com.intellij.lang.javascript.psi.resolve.JSResolveResult
import com.intellij.lang.javascript.psi.stubs.JSImplicitElement
import com.intellij.lang.javascript.psi.util.JSDestructuringUtil
import com.intellij.lang.javascript.psi.util.JSStubBasedPsiTreeUtil
import com.intellij.lang.javascript.psi.util.JSUtils
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parents
import com.phodal.shirecore.project.isInProject
import java.io.File
import java.nio.file.Path

object JSPsiUtil {
    fun resolveReference(node: JSReferenceExpression, scope: PsiElement): PsiElement? {
        val resolveReference = JSResolveResult.resolveReference(node)
        var resolved = resolveReference.firstOrNull() as? JSImplicitElement

        if (resolved != null) {
            resolved = resolved.parent as? JSImplicitElement
        }

        if (resolved is JSFunction && resolved.isConstructor) {
            resolved = JSUtils.getMemberContainingClass(resolved) as? JSImplicitElement
        }

        if (resolved == null || skipDeclaration(resolved)) {
            return null
        }

        val virtualFile = resolved.containingFile?.virtualFile

        if (virtualFile == null ||
            !node.project.isInProject(virtualFile) ||
            ProjectFileIndex.getInstance(node.project).isInLibrary(virtualFile)
        ) {
            return JSStubBasedPsiTreeUtil.resolveReferenceLocally(node as PsiPolyVariantReference, node.referenceName)
        }

        val jSImplicitElement = resolved

        return if (jSImplicitElement.textLength == 0 || !PsiTreeUtil.isAncestor(scope, jSImplicitElement, true)) {
            jSImplicitElement
        } else {
            null
        }
    }

    private fun skipDeclaration(element: PsiElement): Boolean {
        return when (element) {
            is JSParameter, is TypeScriptGenericOrMappedTypeParameter -> true
            is JSField -> {
                element.initializerOrStub !is JSFunctionExpression
            }

            is JSVariable -> {
                var initializer = JSDestructuringUtil.getNearestDestructuringInitializer(element)
                if (initializer == null) {
                    initializer = element.initializerOrStub ?: return true
                }

                !(initializer is JSCallExpression
                        || initializer is JSFunctionExpression
                        || initializer is JSObjectLiteralExpression
                        )
            }

            else -> false
        }
    }

    fun isExportedFileFunction(element: PsiElement): Boolean {
        when (val parent = element.parent) {
            is JSFile, is JSEmbeddedContent -> {
                return when (element) {
                    is JSVarStatement -> {
                        val variables = element.variables
                        val variable = variables.firstOrNull() ?: return false
                        variable.initializerOrStub is JSFunction && exported(variable)
                    }

                    is JSFunction -> exported(element)
                    else -> false
                }
            }

            is JSVariable -> {
                val varStatement = parent.parent as? JSVarStatement ?: return false
                return varStatement.parent is JSFile && exported(parent)
            }

            else -> {
                return parent is ES6ExportDefaultAssignment
            }
        }
    }

    fun isExportedClass(elementForTests: PsiElement?): Boolean {
        return elementForTests is JSClass && elementForTests.isExported
    }

    fun isExportedClassPublicMethod(psiElement: PsiElement): Boolean {
        val jsClass = PsiTreeUtil.getParentOfType(psiElement, JSClass::class.java, true) ?: return false
        if (!exported(jsClass as PsiElement)) return false

        val parentElement = psiElement.parents(true).firstOrNull() ?: return false
        if (isPrivateMember(parentElement)) return false

        return when (parentElement) {
            is JSFunction -> !parentElement.isConstructor
            is JSVarStatement -> {
                val variables = parentElement.variables
                val jSVariable = variables.firstOrNull()
                (jSVariable?.initializerOrStub as? JSFunction) != null
            }

            else -> false
        }
    }

    private fun exported(element: PsiElement): Boolean {
        if (element !is JSElementBase) return false

        if (element.isExported || element.isExportedWithDefault) {
            return true
        }

        if (element is JSPsiElementBase && CommonJSUtil.isExportedWithModuleExports(element)) {
            return true
        }

        val containingFile = element.containingFile ?: return false
        val exportDeclarations =
            PsiTreeUtil.getChildrenOfTypeAsList(containingFile, ES6ExportDeclaration::class.java)

        return exportDeclarations.any { exportDeclaration ->
            exportDeclaration.exportSpecifiers
                .asSequence()
                .any { it.alias?.findAliasedElement() == element }
        }
    }

    fun elementName(psiElement: PsiElement): String? {
        if (psiElement !is JSVarStatement) {
            if (psiElement !is JSNamedElement) return null

            return psiElement.name
        }

        val jSVariable = psiElement.variables.firstOrNull() ?: return null
        return jSVariable.name
    }

    /**
     * Determines whether the given [element] is a private member.
     *
     * @param element the PSI element to check
     * @return `true` if the element is a private member, `false` otherwise
     */
    private fun isPrivateMember(element: PsiElement): Boolean {
        if (element is JSQualifiedNamedElement && element.isPrivateName) {
            return true
        }

        if (element !is JSAttributeListOwner) return false

        val attributeList = element.attributeList
        return attributeList?.accessType == JSAttributeList.AccessType.PRIVATE
    }

    /**
     * In JavaScript/TypeScript a testable element is a function, a class or a variable.
     *
     * Function:
     * ```javascript
     * function testableFunction() {}
     * export testableFunction
     * ```
     *
     * Class:
     * ```javascript
     * export class TestableClass {}
     * ```
     *
     * Variable:
     * ```javascript
     * var functionA = function() {}
     * export functionA
     * ```
     */
    fun getElementToTest(psiElement: PsiElement): PsiElement? {
        if (psiElement is JSFile) return psiElement
        if (psiElement is JSClass) return psiElement

        val jsFunc = PsiTreeUtil.getParentOfType(psiElement, JSFunction::class.java, false)
        val jsVarStatement = PsiTreeUtil.getParentOfType(psiElement, JSVarStatement::class.java, false)
        val jsClazz = PsiTreeUtil.getParentOfType(psiElement, JSClass::class.java, false)

        val elementForTests: PsiElement? = when {
            jsFunc != null -> jsFunc
            jsVarStatement != null -> jsVarStatement
            jsClazz != null -> jsClazz
            else -> null
        }

        if (elementForTests == null) return null

        return when {
            isExportedClassPublicMethod(elementForTests) -> elementForTests
            isExportedFileFunction(elementForTests) -> elementForTests
            isExportedClass(elementForTests) -> elementForTests
            else -> {
                null
            }
        }
    }

    fun getTestFilePath(element: PsiElement): Path? {
        val testDirectory = suggestTestDirectory(element)
        if (testDirectory == null) {
            logger<JSPsiUtil>().warn("Failed to find test directory for: $element")
            return null
        }

        val containingFile: PsiFile = runReadAction { element.containingFile } ?: return null
        val extension = containingFile.virtualFile?.extension ?: return null
        val elementName = elementName(element) ?: return null
        val testFile: Path = generateUniqueTestFile(elementName, containingFile, testDirectory, extension).toPath()
        return testFile
    }

    /**
     * Todo: since in JavaScript has different test framework, we need to find the test directory by the framework.
     */
    fun suggestTestDirectory(element: PsiElement): PsiDirectory? =
        ReadAction.compute<PsiDirectory?, Throwable> {
            val project: Project = element.project
            val elementDirectory = element.containingFile

            val parentDir = elementDirectory?.virtualFile?.parent ?: return@compute null
            val psiManager = PsiManager.getInstance(project)

            val findDirectory = psiManager.findDirectory(parentDir)
            if (findDirectory != null) {
                return@compute findDirectory
            }

            val createChildDirectory = parentDir.createChildDirectory(this, "test")
            return@compute psiManager.findDirectory(createChildDirectory)
        }

    fun generateUniqueTestFile(
        elementName: String?,
        containingFile: PsiFile,
        testDirectory: PsiDirectory,
        extension: String,
    ): File {
        val testPath = testDirectory.virtualFile.path
        val prefix = elementName ?: containingFile.name.substringBefore('.', "")
        val nameCandidate = "$prefix.test.$extension"
        var testFile = File(testPath, nameCandidate)

        var i = 1
        while (testFile.exists()) {
            val nameCandidateWithIndex = "$prefix${i}.test.$extension"
            i++
            testFile = File(testPath, nameCandidateWithIndex)
        }

        return testFile
    }
}
