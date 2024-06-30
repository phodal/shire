package com.phodal.shirelang

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.smartReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.phodal.shirelang.actions.dynamic.DynamicShireActionConfig
import com.phodal.shirelang.actions.dynamic.DynamicShireActionService
import com.phodal.shirelang.compiler.FrontmatterParser
import com.phodal.shirelang.psi.ShireFile

class ShireActionStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        smartReadAction(project) {
            obtainShireFiles(project).forEach {
                val shireConfig = FrontmatterParser.parse(it) ?: return@forEach

                val configName = shireConfig.name

                val shireActionConfig = DynamicShireActionConfig(configName, shireConfig, it)
                DynamicShireActionService.getInstance().putAction(configName, shireActionConfig)
            }
        }
    }

    companion object {
        fun obtainShireFiles(project: Project): List<ShireFile> {
            ApplicationManager.getApplication().assertReadAccessAllowed()
            val allScope = GlobalSearchScope.allScope(project)
            val filesScope = GlobalSearchScope.getScopeRestrictedByFileTypes(allScope, ShireFileType.INSTANCE)

            return FileTypeIndex.getFiles(ShireFileType.INSTANCE, filesScope).mapNotNull {
                PsiManager.getInstance(project).findFile(it) as? ShireFile
            }
        }
    }
}
