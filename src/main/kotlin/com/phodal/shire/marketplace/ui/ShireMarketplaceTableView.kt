package com.phodal.shire.marketplace.ui

import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.icons.AllIcons
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import com.phodal.shire.ShireIdeaIcons
import com.phodal.shire.ShireMainBundle
import com.phodal.shire.marketplace.model.ShirePackage
import com.phodal.shire.marketplace.util.ShireDownloader
import com.phodal.shirecore.ShirelangNotifications
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.awt.Component
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer


private const val SHIRE_MKT_HOST = "https://shire.run/packages.json"

class ShireMarketplaceTableView(val project: Project) {
    private val columns = arrayOf(
        object : ColumnInfo<ShirePackage, String>(ShireMainBundle.message("marketplace.column.name")) {
            override fun valueOf(data: ShirePackage): String = data.title
            override fun getWidth(table: JTable?): Int = 120
        },
        object : ColumnInfo<ShirePackage, String>(ShireMainBundle.message("marketplace.column.description")) {
            override fun valueOf(data: ShirePackage): String = data.description
        },
        object : ColumnInfo<ShirePackage, ShirePackage>("") {
            override fun getWidth(table: JTable?): Int = 40
            override fun valueOf(item: ShirePackage?): ShirePackage? = item
            override fun isCellEditable(item: ShirePackage?): Boolean = true

            override fun getEditor(item: ShirePackage): TableCellEditor {
                return object : IconButtonTableCellEditor(item, ShireIdeaIcons.Download, "Download") {
                    init {
                        myButton.addActionListener {
                            ShirelangNotifications.info(project, "Downloading ${item.title}")
                            ShireDownloader(project, item).downloadAndUnzip()
                            ShirelangNotifications.info(project, "Success Downloaded ${item.title}")

                            invokeLater {
                                project.guessProjectDir()?.refresh(true, true)
                            }

                            fireEditingStopped()
                        }
                    }
                }
            }

            override fun getRenderer(item: ShirePackage?): TableCellRenderer {
                return object : IconButtonTableCellRenderer(ShireIdeaIcons.Download, "Download") {
                    override fun getTableCellRendererComponent(
                        table: JTable,
                        value: Any,
                        selected: Boolean,
                        focused: Boolean,
                        viewRowIndex: Int,
                        viewColumnIndex: Int,
                    ): Component {
                        myButton.isEnabled = true

                        return super.getTableCellRendererComponent(
                            table,
                            value,
                            selected,
                            focused,
                            viewRowIndex,
                            viewColumnIndex
                        )
                    }
                }
            }
        }
    )

    var mainPanel: JPanel
    private val client = OkHttpClient()

    init {
        val tableModel = ListTableModel(columns, listOf<ShirePackage>())
        val tableView = TableView(tableModel)
        val scrollPane = JBScrollPane(tableView)

        val myReloadButton = JButton(AllIcons.Actions.Refresh)

        mainPanel = panel {
            row {
                cell(myReloadButton.apply {
                    addActionListener {
                        tableModel.items = makeApiCall()
                        tableModel.fireTableDataChanged()
                    }
                })
            }

            row {
                cell(scrollPane).align(Align.FILL)
            }.resizableRow()
        }

        tableModel.items = makeApiCall()
        tableModel.fireTableDataChanged()
    }

    private fun makeApiCall(): List<ShirePackage> {
        try {
            val objectMapper = ObjectMapper()

            val request = Request.Builder().url(SHIRE_MKT_HOST).get().build()
            val responses: Response = client.newCall(request).execute()
            val jsonData = responses.body?.string()
            val packages = objectMapper.readValue(jsonData, Array<ShirePackage>::class.java)
            return packages.toList()
        } catch (e: Exception) {
            ShirelangNotifications.error(project, "Failed to fetch data: $e")
            return listOf()
        }
    }
}