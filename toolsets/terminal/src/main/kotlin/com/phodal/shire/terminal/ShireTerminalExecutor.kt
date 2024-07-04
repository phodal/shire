package com.phodal.shire.terminal

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys
import com.phodal.shirecore.provider.action.TerminalLocationExecutor
import com.phodal.shirecore.provider.action.terminal.TerminalHandler
import java.awt.Component

class ShireTerminalExecutor : TerminalLocationExecutor {
    override fun getComponent(e: AnActionEvent): Component? {
        return e.getData(PlatformCoreDataKeys.CONTEXT_COMPONENT)
    }

    override fun buildBundler(): TerminalHandler? {
        TODO("Not yet implemented")
    }
}
