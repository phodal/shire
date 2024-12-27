package com.phodal.shirelang.javascript.codeedit

import com.intellij.execution.configurations.RunProfile
import com.intellij.lang.javascript.buildTools.npm.rc.NpmRunConfiguration
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.ecmal4.JSImportStatement
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import com.phodal.shirecore.provider.TestingService
import com.phodal.shirecore.provider.codemodel.model.ClassStructure
import com.phodal.shirecore.variable.toolchain.unittest.AutoTestingPromptContext
import com.phodal.shirelang.javascript.JSTypeResolver
import com.phodal.shirelang.javascript.codemodel.JavaScriptClassStructureProvider
import com.phodal.shirelang.javascript.codemodel.JavaScriptMethodStructureProvider
import com.phodal.shirelang.javascript.util.JSPsiUtil
import com.phodal.shirelang.javascript.util.LanguageApplicableUtil
import kotlin.io.path.Path

class JSAutoTestingService : TestingService() {
    private val log = logger<JSAutoTestingService>()
    override fun runConfigurationClass(project: Project): Class<out RunProfile> = NpmRunConfiguration::class.java

    override fun isApplicable(element: PsiElement): Boolean {
        val sourceFile: PsiFile = element.containingFile ?: return false
        return LanguageApplicableUtil.isWebChatCreationContextSupported(sourceFile)
    }

    override fun isApplicable(project: Project, file: VirtualFile): Boolean {
        val psiFile = PsiManager.getInstance(project).findFile(file) as? JSFile ?: return false
        return LanguageApplicableUtil.isWebChatCreationContextSupported(psiFile)
    }

    override fun findOrCreateTestFile(
        sourceFile: PsiFile,
        project: Project,
        psiElement: PsiElement,
    ): AutoTestingPromptContext? {
        val language = sourceFile.language
        val testFilePath = JSPsiUtil.getTestFilePath(psiElement)?.toString()
        if (testFilePath == null) {
            log.warn("Failed to find test file path for: $psiElement")
            return null
        }

        val elementToTest = runReadAction { JSPsiUtil.getElementToTest(psiElement) }
        if (elementToTest == null) {
            log.warn("Failed to find element to test for: ${psiElement}, check your function is exported.")
            return null
        }

        val elementName = JSPsiUtil.elementName(elementToTest)
        if (elementName == null) {
            log.warn("Failed to find element name for: $psiElement")
            return null
        }

        var testFile = LocalFileSystem.getInstance().findFileByPath(testFilePath)
        if (testFile != null) {
            return AutoTestingPromptContext(false, testFile, emptyList(), null, language, null)
        }

        WriteCommandAction.writeCommandAction(sourceFile.project).withName("Generate Unit Tests")
            .compute<Unit, Throwable> {
                val parentDir = VfsUtil.createDirectoryIfMissing(Path(testFilePath).parent.toString())
                testFile = parentDir?.createChildData(this, Path(testFilePath).fileName.toString())
            }

        val underTestObj = ReadAction.compute<String, Throwable> {
            val underTestObj = JavaScriptClassStructureProvider()
                .build(elementToTest, false)?.format()

            if (underTestObj == null) {
                val funcObj = JavaScriptMethodStructureProvider()
                    .build(elementToTest, false, false)?.format()

                return@compute funcObj ?: ""
            } else {
                return@compute underTestObj
            }
        }

        val imports: List<String> = (sourceFile as? JSFile)?.let {
            PsiTreeUtil.findChildrenOfType(it, JSImportStatement::class.java)
        }?.map {
            it.text
        } ?: emptyList()

        return AutoTestingPromptContext(true, testFile!!, emptyList(), elementName, language, underTestObj, imports)
    }

    override fun lookupRelevantClass(project: Project, element: PsiElement): List<ClassStructure> {
        return JSTypeResolver.resolveByElement(element).mapNotNull {
            JavaScriptClassStructureProvider().build(it, false)
        }
    }
}