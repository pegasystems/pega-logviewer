/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.dataflow.lifecycleevent;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.message.LifeCycleEventMessage;

public class LifeCycleEventTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 6044590006853481473L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {

        LifeCycleEventMessage lifeCycleEventMessage = (LifeCycleEventMessage) value;

        if (lifeCycleEventMessage != null) {

            LifeCycleEventTableModel ttm = (LifeCycleEventTableModel) table.getModel();
            String text = ttm.getColumnValue(lifeCycleEventMessage, column);

            super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);

            if (!table.isRowSelected(row)) {

                Color foregroundColor = Color.BLACK;
                Color backgroundColor = null;

                boolean searchFound = lifeCycleEventMessage.isSearchFound();

                if (searchFound) {
                    backgroundColor = MyColor.LIGHT_YELLOW;
                } else {
                    foregroundColor = lifeCycleEventMessage.getForegroundColor();
                    backgroundColor = lifeCycleEventMessage.getBackgroundColor();

                }

                setForeground(foregroundColor);
                setBackground(backgroundColor);
            }

            setBorder(new EmptyBorder(1, 3, 1, 1));

            setToolTipText(text);

        } else {
            setBackground(Color.WHITE);
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

        return this;
    }

}
