package com.phodal.shirelang.modify

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.phodal.shirelang.ShireFileType

class ShireModificationListener : BulkFileListener {
    override fun after(events: List<VFileEvent>) {
        if (ApplicationManager.getApplication().isUnitTestMode) {
            return
        }
        var isCodeStylePossiblyAffected = false
        for (event in events) {
            val file = event.file
            if (file == null || file.extension != ShireFileType.INSTANCE.defaultExtension) {
                continue
            }
            for (project in ProjectManager.getInstance().openProjects) {
                if (ProjectRootManager.getInstance(project).fileIndex.isInContent(file)) {
                    when (event) {
                        is VFileCopyEvent, is VFileCreateEvent -> {}
                        // todo: add to new Service
                    }
                    isCodeStylePossiblyAffected = true
                }
            }
        }

        if (isCodeStylePossiblyAffected) {
            ApplicationManager.getApplication().invokeLater {
//                for (editor in EditorFactory.getInstance().allEditors) {
//                    if (editor.isDisposed) continue
//                    (editor as EditorEx).reinitSettings()
//                }
            }
        }
    }
}
