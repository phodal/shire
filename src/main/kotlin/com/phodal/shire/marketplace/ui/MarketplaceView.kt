package com.phodal.shire.marketplace.ui

import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.phodal.shirecore.ui.input.ShireChatBoxInput
import javax.swing.JPanel

@State(name = "MarketPlaceView", storages = [Storage(StoragePathMacros.PRODUCT_WORKSPACE_FILE)])
class MarketplaceView(project: Project) {
    private var myToolWindowPanel: JPanel? = null

    private val shirePackageTableComponent = ShireMarketplaceTableView(project)

    init {
        val shireInput = ShireChatBoxInput(project)
        myToolWindowPanel = panel {
            row {
                cell(shirePackageTableComponent.mainPanel).align(Align.FILL)
            }.resizableRow()
            row {
                cell(shireInput).align(Align.FILL)
            }
        }
    }

    fun initToolWindow(toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(myToolWindowPanel, "Shire Marketplace", false)
        toolWindow.contentManager.addContent(content)
    }
}