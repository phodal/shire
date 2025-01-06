package com.phodal.shirelang.debugger

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

@Service(Service.Level.PROJECT)
class TimeTravelService() {
    private val snapshots = mutableListOf<ShireFileSnapshot>()

    fun createSnapshot(file: VirtualFile, variables: Map<String, ResolvableVariableSnapshot>): ShireFileSnapshot {
        val snapshot = ShireFileSnapshot(file, System.nanoTime().toInt(), variables)
        snapshots.add(snapshot)
        return snapshot
    }

    fun replayTo(timestamp: Long) {
        snapshots.forEach { snapshot ->
            snapshot.takeSnapshot()
        }
    }

    fun getAllSnapshots(): List<ShireFileSnapshot> = snapshots.toList()

    companion object {
        fun getInstance(project: Project): TimeTravelService {
            return project.getService(TimeTravelService::class.java)
        }
    }
}
