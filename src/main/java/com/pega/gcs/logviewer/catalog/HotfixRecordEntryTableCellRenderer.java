/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.catalog;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.logviewer.catalog.model.HotfixColumn;
import com.pega.gcs.logviewer.catalog.model.HotfixRecordEntry;

public class HotfixRecordEntryTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = -8570520678706612613L;

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent (javax.swing.JTable, java.lang.Object, boolean,
     * boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {

        HotfixRecordEntry hotfixRecordEntry = (HotfixRecordEntry) value;

        if (hotfixRecordEntry != null) {

            HotfixRecordEntryTableModel hotfixRecordEntryTableModel = (HotfixRecordEntryTableModel) table.getModel();

            HotfixColumn hotfixColumn = hotfixRecordEntryTableModel.getVisibleColumnList().get(column);

            int columnIndex = hotfixRecordEntryTableModel.getHotfixColumnList().indexOf(hotfixColumn);

            String text = hotfixRecordEntry.getHotfixRecordEntryData(hotfixColumn, columnIndex);

            super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);

            if (!table.isRowSelected(row)) {

                boolean catalogSearchFound = hotfixRecordEntry.isCatalogSearchFound(columnIndex);

                boolean searchFound = hotfixRecordEntry.isSearchFound(columnIndex);

                Color background = MyColor.LIGHTEST_LIGHT_GRAY;

                if (catalogSearchFound) {
                    background = MyColor.LIGHT_YELLOW;
                } else if (searchFound) {
                    setBackground(MyColor.LIME);
                }

                setBackground(background);
            }

            setBorder(new EmptyBorder(1, 10, 1, 4));

            setToolTipText(text);

        } else {
            setBackground(Color.WHITE);
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

        return this;
    }

}
