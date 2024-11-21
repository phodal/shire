package com.phodal.shirelang

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.Constraints
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.smartReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.ProjectScope
import com.phodal.shirecore.config.InteractionType
import com.phodal.shirelang.actions.GlobalShireFileChangesProvider
import com.phodal.shirelang.actions.ShireFileChangesProvider
import com.phodal.shirelang.actions.base.DynamicShireActionService
import com.phodal.shirelang.actions.copyPaste.PasteManagerService
import com.phodal.shirelang.compiler.ast.hobbit.HobbitHole
import com.phodal.shirelang.psi.ShireFile


class ShireActionStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        bindingShireActions(project)
    }

    private suspend fun bindingShireActions(project: Project) {
        GlobalShireFileChangesProvider.getInstance().startup(::attachCopyPasteAction)
        val changesProvider = ShireFileChangesProvider.getInstance(project)
        smartReadAction(project) {
            changesProvider.startup(::attachCopyPasteAction)
            obtainShireFiles(project).forEach {
                changesProvider.onUpdated(it)
            }

            attachTerminalAction()
            attachDatabaseAction(project)
        }
    }

    private fun attachCopyPasteAction(shireConfig: HobbitHole, shireFile: ShireFile) {
        if (shireConfig.interaction == InteractionType.OnPaste) {
            PasteManagerService.getInstance()
                .registerPasteProcessor(shireConfig, shireFile)
        }
    }

    /**
     * We make terminal plugin optional, so can't add to `TerminalToolwindowActionGroup` the plugin.xml.
     * So we add it manually here, if terminal plugin is not enabled, this action will not be shown.
     */
    private fun attachTerminalAction() {
        val actionManager = ActionManager.getInstance()
        val toolsMenu = actionManager.getAction("TerminalToolwindowActionGroup") as? DefaultActionGroup ?: return

        val action = actionManager.getAction("ShireTerminalAction")
        if (!toolsMenu.containsAction(action)) {
            toolsMenu.add(action)
        }
    }

    private fun attachDatabaseAction(project: Project) {
        val actionManager = ActionManager.getInstance()
        val toolsMenu = actionManager.getAction("DatabaseViewPopupMenu") as? DefaultActionGroup ?: return

        val action = actionManager.getAction("ShireDatabaseAction")
        if (!toolsMenu.containsAction(action)) {
            toolsMenu.add(action, Constraints.LAST)
        }
    }

    companion object {
        private fun obtainShireFiles(project: Project): List<ShireFile> {
            ApplicationManager.getApplication().assertReadAccessAllowed()
            val projectShire = obtainProjectShires(project).map {
                PsiManager.getInstance(project).findFile(it) as ShireFile
            }

            return projectShire
        }

        private fun obtainProjectShires(project: Project): List<VirtualFile> {
            val scope = ProjectScope.getContentScope(project)
            val projectShire = FileTypeIndex.getFiles(ShireFileType.INSTANCE, scope).mapNotNull {
                it
            }

            return projectShire
        }

        fun findShireFile(project: Project, filename: String): ShireFile? {
            return DynamicShireActionService.getInstance(project).getAllActions().map {
                it.shireFile
            }.firstOrNull {
                it.name == filename
            }
        }

    }
}
