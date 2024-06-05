package com.phodal.shirelang

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.smartReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScope

class ShireActionStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        // check all ShireLanguage
        val searchScope: GlobalSearchScope = ProjectScope.getProjectScope(project)
        smartReadAction(project) {
            getEditorConfigFiles(project).forEach { _ ->
                // do something
            }
        }
    }

    fun getEditorConfigFiles(project: Project): Collection<VirtualFile> {
        // Not allowed during indexing to prevent reentrant indexing (IDEA-277028)
        ApplicationManager.getApplication().assertReadAccessAllowed()
        val allScope = GlobalSearchScope.allScope(project)
        val filesScope = GlobalSearchScope.getScopeRestrictedByFileTypes(allScope, ShireFileType.INSTANCE)
        return FileTypeIndex.getFiles(ShireFileType.INSTANCE, filesScope)
    }

}
