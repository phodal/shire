package com.phodal.shirelang

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.smartReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirelang.compiler.FrontmatterParser
import com.phodal.shirelang.compiler.frontmatter.FrontMatterShireConfig
import com.phodal.shirelang.psi.ShireFile

class ShireActionStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        smartReadAction(project) {
            val configFiles = obtainShireFiles(project)
            val shireConfigs: List<FrontMatterShireConfig> = configFiles.mapNotNull { it ->
                val psi: ShireFile =
                    PsiManager.getInstance(project).findFile(it) as? ShireFile ?: return@mapNotNull null
                FrontmatterParser.parse(psi)
            }

            shireConfigs.map {
                when(it.actionLocation) {
                    ShireActionLocation.CONTEXT_MENU -> TODO()
                    ShireActionLocation.INTENTION_MENU -> TODO()
                    ShireActionLocation.TERMINAL_MENU -> TODO()
                    ShireActionLocation.COMMIT_MENU -> TODO()
                    ShireActionLocation.RunPanel -> TODO()
                }
            }

            println("Shire Action Startup Activity")
        }
    }

    private fun obtainShireFiles(project: Project): List<VirtualFile> {
        ApplicationManager.getApplication().assertReadAccessAllowed()
        val allScope = GlobalSearchScope.allScope(project)
        val filesScope = GlobalSearchScope.getScopeRestrictedByFileTypes(allScope, ShireFileType.INSTANCE)
        return FileTypeIndex.getFiles(ShireFileType.INSTANCE, filesScope).toList()
    }

}
