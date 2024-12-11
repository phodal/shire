package com.phodal.shire.marketplace.ui

import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
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
import com.phodal.shirecore.provider.sketch.LanguageSketchProvider
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

            row {
                val patchContent = """
                    以下是使用 `PATCH` 方法来完成删除博客的代码补丁。这里提供了两个补丁，分别用于更新 `BlogController` 和 `BlogService`。

                    ### BlogController 的补丁
                    
                    ```patch
                    --- a/Users/phodal/IdeaProjects/shire-demo/src/main/java/com/phodal/shire/demo/controller/BlogController.java
                    +++ b/Users/phodal/IdeaProjects/shire-demo/src/main/java/com/phodal/shire/demo/controller/BlogController.java
                    @@ -44,6 +44,13 @@ public class BlogController {
                         }
                    
                         /**
                    +     * Delete a blog post by id.
                    +     *
                    +     * @param id The id of the blog post to delete
                    +     */
                    +    @PatchMapping("/{id}")
                    +    public void deleteBlog(@PathVariable Long id) {
                    +        blogService.deleteBlog(id);
                         }
                     }
                    ```
                    
                    ### BlogService 的补丁
                    
                    实际上，根据你提供的 `BlogService` 代码，删除方法已经存在，所以这里不需要补丁。但如果你的项目需要遵循 RESTful API 的最佳实践，通常删除操作会使用 `DELETE` 方法而不是 `PATCH`。不过，如果你确实想使用 `PATCH` 来删除博客，以下是补丁示例：
                    
                    ```patch
                    --- a/Users/phodal/IdeaProjects/shire-demo/src/main/java/com/phodal/shire/demo/service/BlogService.java
                    +++ b/Users/phodal/IdeaProjects/shire-demo/src/main/java/com/phodal/shire/demo/service/BlogService.java
                    @@ -20,6 +20,9 @@ public class BlogService {
                         public void deleteBlog(Long id) {
                             blogRepository.deleteById(id);
                         }
                    +
                    +    /**
                    +     * Alternative method to delete blog using PATCH, if required.
                    +     */
                    +    public void patchDeleteBlog(Long id) {
                    +        blogRepository.deleteById(id);
                    +    }
                     }
                    ```
                    
                    请注意，通常 `PATCH` 方法用于部分更新资源，而 `DELETE` 方法用于删除资源。如果你确实想使用 `PATCH` 来删除资源，你可能需要考虑设计你的 API 以支持这一行为。
                    
                    将上述代码块复制到你的代码编辑器中，并应用这些补丁，即可实现通过 `PATCH` 方法删除博客的功能。
                """.trimIndent()
                cell(
                    LanguageSketchProvider.provide("patch")!!.createSketch(project, patchContent).getComponent()
                ).align(Align.FILL)
            }
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