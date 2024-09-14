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
import java.awt.Component
import javax.swing.AbstractCellEditor
import javax.swing.Icon
import javax.swing.JTable
import javax.swing.event.ChangeEvent
import javax.swing.table.TableCellEditor

open class IconButtonTableCellEditor(value: Any, icon: Icon, tooltipText: String) : AbstractCellEditor(),
    TableCellEditor {
    protected var myValue: Any = value
    protected val myButton: IconButton = IconButton(icon).apply {
        setOpaque(true)
        setToolTipText(tooltipText)
    }

    @get:VisibleForTesting
    var changeEvent: ChangeEvent?
        get() = changeEvent
        set(changeEvent) {
            super.changeEvent = changeEvent
        }

    override fun getTableCellEditorComponent(
        table: JTable,
        value: Any,
        selected: Boolean,
        viewRowIndex: Int,
        viewColumnIndex: Int,
    ): Component {
        // I'd pass selected instead of hard coding true but the selection is changed after the cell is edited. selected is false when I expect
        // it to be true.
        return myButton.getTableCellComponent(table, true, true)
    }

    override fun getCellEditorValue(): Any {
        checkNotNull(myValue)
        return myValue as Any
    }
}
