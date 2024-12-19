package com.phodal.shire.marketplace.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.phodal.shirecore.ui.ShireInputSection
import javax.swing.JPanel

@State(name = "MarketPlaceView", storages = [Storage(StoragePathMacros.PRODUCT_WORKSPACE_FILE)])
class MarketplaceView(val project: Project) : Disposable {
    private var myToolWindowPanel: JPanel? = null

    private val shirePackageTableComponent = ShireMarketplaceTableView(project)

    init {
        val inputSection = ShireInputSection(project, this)
        myToolWindowPanel = panel {
            row {
                cell(shirePackageTableComponent.mainPanel).align(Align.FILL)
            }.resizableRow()
            row {
                cell(inputSection).align(Align.FILL)
            }
        }
    }

    fun initToolWindow(toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(myToolWindowPanel, "Shire Marketplace", false)
        toolWindow.contentManager.addContent(content)
    }

    override fun dispose() {
        // TODO("Not yet implemented")
    }
}