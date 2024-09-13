package com.phodal.shire.marketplace

import com.intellij.ide.IdeBundle.message
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.options.UnnamedConfigurable
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.FileColorManager
import com.intellij.ui.LayeredIcon
import com.intellij.ui.ToolbarDecorator.createDecorator
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.hover.TableHoverListener
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.EditableModel
import com.phodal.shire.ShireMainBundle
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.table.AbstractTableModel

class MarketplaceToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        project.getService(MarketPlaceView::class.java).initToolWindow(toolWindow)
    }

}

@State(name = "MarketPlaceView", storages = [Storage(StoragePathMacros.PRODUCT_WORKSPACE_FILE)])
class MarketPlaceView(project: Project) : Disposable {
    private val myProject = project
    private var myToolWindowPanel: JPanel? = null
    private val packageModel = ShirePackageModel()

    init {
        myToolWindowPanel = panel {
            row {
                cell(packageModel.createComponent())
                    .align(Align.FILL)
                    .comment(ShireMainBundle.message("marketplace.table.comment"))
                    .onIsModified { packageModel.isModified }
                    .onReset { packageModel.reset() }
                    .onApply { packageModel.apply() }
            }.resizableRow()
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

private class Column(private val key: String, val type: Class<*>, val editable: Boolean) {
    val name: String
        get() = ShireMainBundle.message(key)
}

private val columns = arrayOf(
    Column("marketplace.column.name", String::class.java, false),
    Column("marketplace.column.description", String::class.java, true),
    Column("marketplace.column.version", String::class.java, true)
)

private class ShirePackageModel() : AbstractTableModel(), UnnamedConfigurable {
    override fun getColumnCount() = columns.size

    override fun getColumnName(column: Int) = columns[column].name

    override fun getColumnClass(column: Int) = columns[column].type

    override fun isCellEditable(row: Int, column: Int) = columns[column].editable

    override fun getRowCount() = 0

    override fun getValueAt(row: Int, column: Int): Any? {
        return null
    }

    override fun createComponent(): JComponent {
        val table = JBTable(this)
        table.setShowGrid(false)
        table.emptyText.text = ShireMainBundle.message("marketplace.empty.text")

        return createDecorator(table)
            .createPanel()
    }

    override fun isModified(): Boolean {
        return false
    }

    override fun apply() {
//        TODO("Not yet implemented")
    }
}
