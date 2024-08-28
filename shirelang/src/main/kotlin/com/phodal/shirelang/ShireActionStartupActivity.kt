package com.phodal.shirelang

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.smartReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.phodal.shirecore.config.InteractionType
import com.phodal.shirelang.actions.base.DynamicShireActionConfig
import com.phodal.shirelang.actions.base.DynamicShireActionService
import com.phodal.shirelang.actions.copyPaste.PasteManagerService
import com.phodal.shirelang.actions.copyPaste.PasteProcessorConfig
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.parser.HobbitHoleParser
import com.phodal.shirelang.psi.ShireFile


class ShireActionStartupActivity : ProjectActivity {
    private val logger = logger<ShireActionStartupActivity>()

    override suspend fun execute(project: Project) {
        bindingShireActions(project)
    }

    private suspend fun bindingShireActions(project: Project) {
        smartReadAction(project) {
            obtainShireFiles(project).forEach {
                val shireConfig = try {
                    HobbitHoleParser.parse(it)
                } catch (e: Exception) {
                    logger.warn("parse shire config error for file: ${it.virtualFile.path}", e)
                    return@forEach
                } ?: return@forEach

                val shireActionConfig = DynamicShireActionConfig(shireConfig.name, shireConfig, it)
                DynamicShireActionService.getInstance().putAction(shireConfig.name, shireActionConfig)

                attachCopyPasteAction(shireConfig, it)
            }

            attachTerminalAction()
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
