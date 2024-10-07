package com.phodal.shirelang.python.provider

import com.intellij.execution.RunManager
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfile
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.jetbrains.python.PythonLanguage
import com.jetbrains.python.psi.PyFile
import com.jetbrains.python.run.PythonRunConfiguration
import com.jetbrains.python.run.PythonRunConfigurationProducer
import com.phodal.shirecore.codemodel.model.ClassStructure
import com.phodal.shirecore.provider.TestingService
import com.phodal.shirecore.variable.toolchain.unittest.AutoTestingPromptContext
import com.phodal.shirelang.python.util.PyTestUtil

class ShirePythonAutoTesting : TestingService() {
    override fun isApplicable(element: PsiElement): Boolean = element.language.displayName == "Python"
    override fun isApplicable(project: Project, file: VirtualFile): Boolean = file.extension == "py"

    override fun runConfigurationClass(project: Project): Class<out RunProfile> = PythonRunConfiguration::class.java

    override fun createConfiguration(project: Project, virtualFile: VirtualFile): RunConfiguration? {
        val psiFile: PyFile = PsiManager.getInstance(project).findFile(virtualFile) as? PyFile ?: return null
        val runManager = RunManager.getInstance(project)

        val context = ConfigurationContext(psiFile)
        val configProducer = RunConfigurationProducer.getInstance(
            PythonRunConfigurationProducer::class.java
        )
        var settings = configProducer.findExistingConfiguration(context)

        if (settings == null) {
            val fromContext = configProducer.createConfigurationFromContext(context) ?: return null
            settings = fromContext.configurationSettings
            runManager.setTemporaryConfiguration(settings)
        }
        val configuration = settings.configuration as PythonRunConfiguration
        return configuration
    }

    override fun findOrCreateTestFile(sourceFile: PsiFile, project: Project, psiElement: PsiElement): AutoTestingPromptContext? {
        val testFileName = PyTestUtil.getTestNameExample(sourceFile.virtualFile)
        val testDir = PyTestUtil.getTestsDirectory(sourceFile.virtualFile, project)
        val testFile = WriteAction.computeAndWait<VirtualFile?, Throwable> {
            testDir.findOrCreateChildData(this, PyTestUtil.toTestFileName(testFileName, sourceFile.name))
        }

        return AutoTestingPromptContext(true, testFile, listOf(), "", PythonLanguage.INSTANCE)
    }

    override fun lookupRelevantClass(project: Project, element: PsiElement): List<ClassStructure> {
        return listOf()
    }

}
