package com.phodal.shirecore.ui.input

import com.intellij.codeInsight.lookup.LookupManagerListener
import com.intellij.icons.AllIcons
import com.intellij.lang.Language
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.provider.psi.RelatedClassesProvider
import com.phodal.shirecore.provider.shire.FileCreateService
import com.phodal.shirecore.provider.shire.FileRunService
import com.phodal.shirecore.relativePath
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class ShireInput(val project: Project) : JPanel(BorderLayout()), Disposable {
    private var scratchFile: VirtualFile? = null
    private val listModel = DefaultListModel<PsiElement>()
    private val elementsList = JBList(listModel)
    private var inputSection: ShireInputSection

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

                val virtualFile = createShireFile(prompt)
                this@ShireInput.scratchFile = virtualFile

                FileRunService.provider(project, virtualFile!!)
                    ?.runFile(project, virtualFile, null)

                listModel.clear()
                elementsList.clearSelection()
            }
        })
        this.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        this.add(inputSection, BorderLayout.CENTER)
        this.add(elementsList, BorderLayout.NORTH)

        setupEditorListener()
        setupRelatedListener()
    }

    private fun setupEditorListener() {
        project.messageBus.connect(this).subscribe(
            FileEditorManagerListener.FILE_EDITOR_MANAGER,
            object : FileEditorManagerListener {
                override fun selectionChanged(event: FileEditorManagerEvent) {
                    val file = event.newFile ?: return
                    val psiFile = PsiManager.getInstance(project).findFile(file) ?: return
                    RelatedClassesProvider.provide(psiFile.language) ?: return
                    ApplicationManager.getApplication().invokeLater {
                        listModel.addIfAbsent(psiFile)
                    }
                }
            }
        )
    }

    private fun setupRelatedListener() {
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
        elementsList.visibleRowCount = 2
        elementsList.cellRenderer = ElementListCellRenderer()
        
        // 创建一个可滚动的面板
        val scrollPane = JBScrollPane(elementsList)
        scrollPane.preferredSize = Dimension(-1, 80)
        scrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
        scrollPane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED

        elementsList.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val list = e.source as JBList<*>
                val index = list.locationToIndex(e.point)
                if (index != -1) {
                    val element = listModel.getElementAt(index) as PsiElement
                    val cellBounds = list.getCellBounds(index, index)

                    // 计算关闭按钮的区域
                    val closeButtonWidth = 20  // 关闭按钮的大约宽度
                    val isClickOnCloseButton = e.x > cellBounds.x + cellBounds.width - closeButtonWidth

                    if (isClickOnCloseButton) {
                        listModel.remove(index)
                        e.consume()  // 阻止事件继续传播
                        return
                    }

                    // 只有在非关闭按钮区域的点击才处理文件路径添加
                    element.containingFile?.let { psiFile ->
                        val relativePath = psiFile.virtualFile.relativePath(project)
                        inputSection.appendText("\n/" + "file" + ":${relativePath}")

                        listModel.remove(index)

                        val relatedElements = RelatedClassesProvider.provide(psiFile.language)?.lookup(psiFile)
                        updateElements(relatedElements)
                    }
                }
            }
        })

        add(scrollPane, BorderLayout.NORTH)
    }

    private fun updateElements(elements: List<PsiElement>?) {
        elements?.forEach { listModel.addIfAbsent(it) }
    }

    private fun createShireFile(prompt: String): VirtualFile? {
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

private fun <E> DefaultListModel<E>.addIfAbsent(psiFile: E) {
    if (!contains(psiFile)) {
        addElement(psiFile)
    }
}

private class ElementListCellRenderer : ListCellRenderer<PsiElement> {
    override fun getListCellRendererComponent(
        list: JList<out PsiElement>,
        value: PsiElement,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean,
    ): Component {
        val panel = JPanel(FlowLayout(FlowLayout.LEFT, 3, 0))
        panel.accessibleContext.accessibleName = "Element Panel"

        panel.border = JBUI.Borders.empty(2, 5)

        val iconLabel = JLabel(value.containingFile?.fileType?.icon ?: AllIcons.FileTypes.Unknown)
        panel.add(iconLabel)

        val nameLabel = JLabel(value.containingFile?.name ?: "Unknown")
        panel.add(nameLabel)

        val closeLabel = JLabel(AllIcons.Actions.Close)
        closeLabel.border = JBUI.Borders.empty()
        closeLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val model = list.model as DefaultListModel<*>
                model.remove(index)
            }
        })
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