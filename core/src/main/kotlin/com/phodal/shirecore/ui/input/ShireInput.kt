package com.phodal.shirecore.ui.input

import com.intellij.codeInsight.lookup.LookupManagerListener
import com.intellij.lang.Language
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.ui.components.JBList
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.provider.psi.RelatedClassesProvider
import com.phodal.shirecore.provider.shire.FileCreateService
import com.phodal.shirecore.provider.shire.FileRunService
import java.awt.BorderLayout
import javax.swing.BorderFactory
import javax.swing.DefaultListModel
import javax.swing.JPanel
import javax.swing.ListSelectionModel
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.*
import com.intellij.icons.AllIcons
import com.intellij.ui.components.JBScrollPane
import javax.swing.border.EmptyBorder
import com.intellij.util.ui.JBUI
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class ShireInput(val project: Project) : JPanel(BorderLayout()), Disposable {
    private var scratchFile: VirtualFile? = null
    private val listModel = DefaultListModel<PsiElement>()
    private val elementsList = JBList(listModel)
    private lateinit var inputSection: ShireInputSection

    init {
        setupElementsList()
        inputSection = ShireInputSection(project, this)
        inputSection.addListener(object : ShireInputListener {
            override fun onStop(component: ShireInputSection) {
                inputSection.showSendButton()
            }

            override fun onSubmit(component: ShireInputSection, trigger: ShireInputTrigger) {
                val prompt = component.text
                component.text = ""

                if (prompt.isEmpty() || prompt.isBlank()) {
                    component.showTooltip(ShireCoreBundle.message("chat.input.empty.tips"))
                    return
                }

                val virtualFile = getVirtualFile(prompt)
                this@ShireInput.scratchFile = virtualFile

                FileRunService.provider(project, virtualFile!!)
                    ?.runFile(project, virtualFile, null)
            }
        })
        this.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        this.add(inputSection, BorderLayout.CENTER)
        this.add(elementsList, BorderLayout.NORTH)

        project.messageBus.connect(this)
            .subscribe(LookupManagerListener.TOPIC, ShireInputLookupManagerListener(project) {
                ApplicationManager.getApplication().invokeLater {
                    val relatedElements = RelatedClassesProvider.provide(it.language)?.lookup(it)
                    updateElements(relatedElements)
                }
            })
    }

    private fun setupElementsList() {
        elementsList.selectionMode = ListSelectionModel.SINGLE_SELECTION
        elementsList.layoutOrientation = JList.HORIZONTAL_WRAP
        elementsList.visibleRowCount = 1
        
        val scrollPane = JBScrollPane(elementsList)
        scrollPane.preferredSize = Dimension(-1, 40)
        
        elementsList.cellRenderer = ElementListCellRenderer()
        elementsList.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val list = e.source as JBList<*>
                val index = list.locationToIndex(e.point)
                if (index != -1) {
                    val element = listModel.getElementAt(index) as PsiElement
                    val cellBounds = list.getCellBounds(index, index)
                    
                    if (e.x > cellBounds.x + cellBounds.width - 10) {
                        listModel.remove(index)
                    } else {
                        element.containingFile?.let { psiFile ->
                            val virtualFile = psiFile.virtualFile
                            val projectBasePath = project.basePath
                            val filePath = virtualFile.path
                            val relativePath = if (projectBasePath != null && filePath.startsWith(projectBasePath)) {
                                filePath.substring(projectBasePath.length + 1)
                            } else {
                                filePath
                            }
                            inputSection.appendText("\n/file:${relativePath}")
                        }
                    }
                }
            }
        })

        add(scrollPane, BorderLayout.NORTH)
    }

    fun updateElements(elements: List<PsiElement>?) {
        listModel.clear()
        elements?.forEach { listModel.addElement(it) }
    }

    private fun getVirtualFile(prompt: String): VirtualFile? {
        val findLanguageByID = Language.findLanguageByID("Shire")
            ?: throw IllegalStateException("Shire language not found")
        val provide = FileCreateService.provide(findLanguageByID)
            ?: throw IllegalStateException("FileCreateService not found")

        return provide.createFile(prompt, project)
    }

    override fun dispose() {
        scratchFile?.delete(this)
    }
}

private class ElementListCellRenderer : ListCellRenderer<PsiElement> {
    override fun getListCellRendererComponent(
        list: JList<out PsiElement>,
        value: PsiElement,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        val panel = JPanel(FlowLayout(FlowLayout.LEFT, 3, 0))
        panel.border = EmptyBorder(2, 5, 2, 5)
        
        // 添加文件类型图标
        val iconLabel = JLabel(value.containingFile?.fileType?.icon ?: AllIcons.FileTypes.Unknown)
        panel.add(iconLabel)
        
        // 添加文件名
        val nameLabel = JLabel(value.containingFile?.name ?: "Unknown")
        panel.add(nameLabel)
        
        // 添加关闭按钮
        val closeLabel = JLabel(AllIcons.Actions.Close)
        closeLabel.border = JBUI.Borders.empty(0, 0)
        panel.add(closeLabel)
        
        if (isSelected) {
            panel.background = list.selectionBackground
            nameLabel.foreground = list.selectionForeground
        } else {
            panel.background = list.background
            nameLabel.foreground = list.foreground
        }
        
        return panel
    }
}