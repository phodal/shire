package com.phodal.shire.marketplace

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.panel
import javax.swing.JPanel

class MarketplaceToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        project.getService(MarketPlaceView::class.java).initToolWindow(toolWindow)
    }

}

@State(name = "MarketPlaceView", storages = [Storage(StoragePathMacros.PRODUCT_WORKSPACE_FILE)])
class MarketPlaceView(project: Project) : Disposable {
    private val myProject = project
    private var myToolWindowPanel: JPanel? = null

    init {
        myToolWindowPanel = panel {
            row {
                label("Hello, World!")
            }
        }

        val marketplacePanel = MarketplacePanel()
        marketplacePanel.isVisible = true
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