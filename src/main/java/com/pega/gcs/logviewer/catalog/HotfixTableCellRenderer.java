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
import com.pega.gcs.logviewer.catalog.model.HotfixEntry;

public class HotfixTableCellRenderer extends DefaultTableCellRenderer {

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

        HotfixEntry hotfixEntry = (HotfixEntry) value;

        if (hotfixEntry != null) {

            HotfixTableModel hotfixTableModel = (HotfixTableModel) table.getModel();

            String text = hotfixTableModel.getColumnValue(hotfixEntry, column);

            super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);

            if (!isSelected) {

                Color background = hotfixEntry.getBackground();

                boolean catalogSearchFound = hotfixEntry.isCatalogSearchFound();

                boolean searchFound = hotfixEntry.isSearchFound();

                if (catalogSearchFound && searchFound) {
                    background = MyColor.ORANGE;
                } else if (catalogSearchFound) {
                    background = MyColor.LIGHT_YELLOW;
                } else if (searchFound) {
                    background = MyColor.LIME;
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
