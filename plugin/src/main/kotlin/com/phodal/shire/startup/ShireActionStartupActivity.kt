package com.phodal.shire.startup

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScope
import com.phodal.shirelang.ShireFileType

class ShireActionStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        // check all ShireLanguage
        val searchScope: GlobalSearchScope = ProjectScope.getProjectScope(project)
//        val shireFiles = FileTypeIndex.getFiles(ShireFileType, searchScope)

        // check all files
//        println("Shire Files: ${shireFiles.size}")
    }
}
