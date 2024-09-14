/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.phodal.shire.marketplace

import com.google.common.annotations.VisibleForTesting
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.scale.JBUIScale.scale
import com.intellij.util.IconUtil
import com.intellij.util.ui.JBDimension
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.image.RGBImageFilter
import java.util.*
import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.IntStream
import javax.swing.Icon
import javax.swing.JButton
import javax.swing.JTable
import javax.swing.UIManager
import javax.swing.border.Border
import javax.swing.table.TableCellRenderer
import javax.swing.table.TableColumn
import kotlin.math.max

fun generateColoredIcon(icon: Icon, color: Color): Icon = IconLoader.createLazy(object : Supplier<Icon> {
    private val cache = mutableMapOf<Int, Icon>()

    override fun get(): Icon =
        cache.getOrPut(color.rgb) {
            IconUtil.filterIcon(icon, {
                object : RGBImageFilter() {
                    override fun filterRGB(x: Int, y: Int, rgb: Int) = (rgb or 0xffffff) and color.rgb
                }
            }, null)
        }
})

object Tables {
    fun getBackground(table: JTable, selected: Boolean): Color {
        if (selected) {
            return table.selectionBackground
        }

        return table.background
    }

    fun getBorder(selected: Boolean, focused: Boolean): Border? {
        return getBorder(
            selected, focused
        ) { key: Any -> UIManager.getBorder(key) }
    }

    @VisibleForTesting
    fun getBorder(selected: Boolean, focused: Boolean, getBorder: Function<Any, Border?>): Border? {
        if (!focused) {
            return getBorder.apply("Table.cellNoFocusBorder")
        }

        if (selected) {
            return getBorder.apply("Table.focusSelectedCellHighlightBorder")
        }

        return getBorder.apply("Table.focusCellHighlightBorder")
    }

    fun getForeground(table: JTable, selected: Boolean): Color {
        if (selected) {
            return table.selectionForeground
        }

        return table.foreground
    }

    fun getIcon(table: JTable, selected: Boolean, icon: Icon): Icon {
        if (selected) {
            return generateColoredIcon(icon, table.selectionForeground)
        }

        return icon
    }

    fun setWidths(column: TableColumn, width: Int) {
        column.minWidth = width
        column.maxWidth = width
        column.preferredWidth = width
    }

    fun setWidths(column: TableColumn, width: Int, minWidth: Int) {
        column.minWidth = minWidth
        column.maxWidth = width
        column.preferredWidth = width
    }

    fun getPreferredColumnWidth(table: JTable, viewColumnIndex: Int, minPreferredWidth: Int): Int {
        val width = IntStream.range(-1, table.rowCount)
            .map { viewRowIndex: Int ->
                getPreferredCellWidth(
                    table,
                    viewRowIndex,
                    viewColumnIndex
                )
            }
            .max()

        if (!width.isPresent) {
            return minPreferredWidth
        }

        return max(width.asInt.toDouble(), minPreferredWidth.toDouble()).toInt()
    }

    private fun getPreferredCellWidth(table: JTable, viewRowIndex: Int, viewColumnIndex: Int): Int {
        val component: Component

        if (viewRowIndex == -1) {
            val renderer = table.tableHeader.defaultRenderer
            val value = table.columnModel.getColumn(viewColumnIndex).headerValue

            component = renderer.getTableCellRendererComponent(table, value, false, false, -1, viewColumnIndex)
        } else {
            component = table.prepareRenderer(
                table.getCellRenderer(viewRowIndex, viewColumnIndex),
                viewRowIndex,
                viewColumnIndex
            )
        }

        return component.preferredSize.width + scale(8)
    }
}

interface IconTableCell {
    fun getTableCellComponent(table: JTable, selected: Boolean, focused: Boolean): Component {
        setBackground(Tables.getBackground(table, selected))
        setBorder(Tables.getBorder(selected, focused))

        val icon = defaultIcon
            .map { i: Icon -> Tables.getIcon(table, selected, i) }
            .orElse(null)

        setIcon(icon)
        return this as Component
    }

    val defaultIcon: Optional<Icon>

    fun setBackground(background: Color?)

    fun setBorder(border: Border?)

    fun setIcon(icon: Icon?)
}

class IconButton(defaultIcon: Icon) : JButton(defaultIcon), IconTableCell {
    private var myDefaultIcon: Icon

    init {
        val size: Dimension = JBDimension(22, 22)

        border = null
        isContentAreaFilled = false
        maximumSize = size
        minimumSize = size
        preferredSize = size

        myDefaultIcon = defaultIcon
    }

    override var defaultIcon: Optional<Icon>
        get() = Optional.ofNullable(myDefaultIcon)
        set(defaultIcon) {
            myDefaultIcon = defaultIcon.orElse(null)
        }

    fun setDefaultIcon(defaultIcon: Icon) {
        myDefaultIcon = defaultIcon
    }
}

open class IconButtonTableCellRenderer(icon: Icon? = null, tooltipText: String? = null) : TableCellRenderer {
    protected val myButton: IconButton = IconButton(icon!!)

    init {
        myButton.toolTipText = tooltipText
    }

    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any,
        selected: Boolean,
        focused: Boolean,
        viewRowIndex: Int,
        viewColumnIndex: Int,
    ): Component {
        return myButton.getTableCellComponent(table, selected, focused)
    }

    companion object {
        fun getPreferredWidth(table: JTable, c: Class<*>): Int {
            val renderer = table.getDefaultRenderer(c) as IconButtonTableCellRenderer
            return renderer.myButton.getTableCellComponent(table, false, false).preferredSize.width + scale(8)
        }
    }
}