package com.phodal.shirelang.actions

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.AsyncFileListener
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent
import com.intellij.psi.PsiManager
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.psi.ShireFile

/**
 * It is used after the project is started
 * and can use [shireFileModifier] to handle shire file change events.
 *
 * @author lk
 */
@Service(Service.Level.PROJECT)
class ShireFileChangesProvider(val project: Project) : Disposable {

    @Volatile
    var shireFileModifier: ShireFileModifier? = null

    fun startup(afterUpdater: (HobbitHole, ShireFile) -> Unit) {
        (shireFileModifier ?: synchronized(this) {
            shireFileModifier ?: ShireFileModifier(project, afterUpdater).also { shireFileModifier = it }
        }).startup()

    }

    fun onUpdated(file: ShireFile) {
        ShireUpdater.publisher.onUpdated { file }
    }

    companion object {

        fun getInstance(project: Project): ShireFileChangesProvider {
            return project.getService(ShireFileChangesProvider::class.java)
        }

    }

    override fun dispose() {
        shireFileModifier?.dispose()
    }
}

internal class ShireFileModifcationListener : FileDocumentManagerListener, DocumentListener, ShireFileListener {

    fun onUpdated(document: Document) {
        FileDocumentManager.getInstance().getFile(document).let { onUpdated(it) }
    }

    override fun documentChanged(event: DocumentEvent) {
        onUpdated(event.document)
    }

    override fun bulkUpdateFinished(document: Document) {
        onUpdated(document)
    }

    override fun unsavedDocumentDropped(document: Document) {
        onUpdated(document)
    }

}

internal class AsyncShireFileListener : AsyncFileListener, ShireFileListener {

    override fun prepareChange(events: MutableList<out VFileEvent>): AsyncFileListener.ChangeApplier {

        val beforeChangedEvents = mutableListOf<VFileEvent>()
        val afterChangedEvents = mutableListOf<VFileEvent>()
        for (event in events) {
            if (event is VFileDeleteEvent) beforeChangedEvents.add(event)
            if (event is VFileCopyEvent || event is VFileMoveEvent) afterChangedEvents.add(event)
        }

        return object : AsyncFileListener.ChangeApplier {
            override fun beforeVfsChange() {
                beforeChangedEvents.forEach { onUpdated(it.file) }
            }

            override fun afterVfsChange() {
                afterChangedEvents.forEach {
                    when (it) {
                        is VFileCopyEvent -> {
                            onUpdated(it.findCreatedFile())
                        }

                        else -> {
                            onUpdated(it.file)
                        }
                    }
                }
            }
        }

    }


}

/**
 * Only handle events related to shire file
 */
interface ShireFileListener {
    fun onUpdated(file: VirtualFile?) {
        if (file == null || !file.isValid) return
        ShireUpdater.publisher.onUpdated {
            PsiManager.getInstance(it).findFile(file) as? ShireFile
        }
    }
}