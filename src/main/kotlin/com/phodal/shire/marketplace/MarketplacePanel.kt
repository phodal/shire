package com.phodal.shire.marketplace

import com.intellij.CommonBundle
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dualView.TreeTableView
import com.intellij.ui.treeStructure.treetable.ListTreeTableModelOnColumns
import com.intellij.ui.treeStructure.treetable.TreeColumnInfo
import com.intellij.util.ui.AsyncProcessIcon
import com.intellij.util.ui.ColumnInfo
import javax.swing.JFrame
import javax.swing.tree.DefaultMutableTreeNode

class MarketplacePanel: JFrame() {
    @Suppress("unused")
    private val myPlatformLoadingIcon = AsyncProcessIcon(CommonBundle.getLoadingTreeNodeText())
    private val myPlatformSummaryRootNode = PlatformSummaryRootNode()
    private var myPlatformSummaryTable: TreeTableView? = null
    private val myPlatformLoadingLabel: JBLabel = JBLabel("Loading...")

    init {
        val platformSummaryColumns =
            arrayOf<ColumnInfo<*, *>>(
                TreeColumnInfo("Name"),
            )

        myPlatformLoadingLabel.foreground = JBColor.GRAY
        myPlatformSummaryTable =
            TreeTableView(ListTreeTableModelOnColumns(myPlatformSummaryRootNode, platformSummaryColumns))
    }
}

class PlatformSummaryRootNode: DefaultMutableTreeNode() {
    override fun isLeaf(): Boolean {
        return false
    }
}
