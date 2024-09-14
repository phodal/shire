package com.phodal.shire.marketplace

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.phodal.shire.marketplace.ui.MarketplaceView


class MarketplaceToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        project.getService(MarketplaceView::class.java).initToolWindow(toolWindow)
    }

}
