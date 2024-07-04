package com.phodal.shirelang

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.smartReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.keymap.KeymapManager
import com.intellij.openapi.keymap.impl.KeymapImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.phodal.shirelang.actions.dynamic.DynamicShireActionConfig
import com.phodal.shirelang.actions.dynamic.DynamicShireActionService
import com.phodal.shirelang.compiler.parser.FrontmatterParser
import com.phodal.shirelang.psi.ShireFile
import javax.swing.KeyStroke


class ShireActionStartupActivity : ProjectActivity {
    private val logger = logger<ShireActionStartupActivity>()

    override suspend fun execute(project: Project) {
        smartReadAction(project) {
            obtainShireFiles(project).forEach {
                val shireConfig = try {
                    FrontmatterParser.parse(it)
                } catch (e: Exception) {
                    logger.warn("parse shire config error for file: ${it.virtualFile.path}", e)
                    return@forEach
                } ?: return@forEach

                val shireActionConfig = DynamicShireActionConfig(shireConfig.name, shireConfig, it)
                DynamicShireActionService.getInstance().putAction(shireConfig.name, shireActionConfig)
            }
        }
    }

    fun setKeymapShortcut(actionId: String, keyCode: Int, modifiers: Int) {
        val actionManager: ActionManager = ActionManager.getInstance()
        val action: AnAction = actionManager.getAction(actionId) ?: return

        val keymapManager = KeymapManager.getInstance()
        val activeKeymap = keymapManager.activeKeymap

        if (activeKeymap is KeymapImpl) {
            val newShortcut = KeyboardShortcut(KeyStroke.getKeyStroke(keyCode, modifiers), null)
            activeKeymap.removeAllActionShortcuts(actionId)
            activeKeymap.addShortcut(actionId, newShortcut)
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
