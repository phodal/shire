package com.phodal.shirelang

import com.intellij.openapi.application.smartReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScope

class ShireActionStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        // check all ShireLanguage
        val searchScope: GlobalSearchScope = ProjectScope.getProjectScope(project)
        smartReadAction(project) {
            val virtualFiles = FileTypeIndex.getFiles(ShireFileType.INSTANCE, searchScope)

            println("Shire Files: ${virtualFiles.size}")
        }
    }
}
