/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.logviewer.model.LogEntry;

public class LogTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -5768343434033636406L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent
	 * (javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		LogEntry le = (LogEntry) value;

		if (le != null) {

			LogTableModel ttm = (LogTableModel) table.getModel();
			String text = ttm.getFormattedLogEntryValue(le, column);

			super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);

			if (!table.isRowSelected(row)) {

				Color foregroundColor = Color.BLACK;
				Color backgroundColor = null;

				boolean searchFound = le.isSearchFound();

				if (searchFound) {
					backgroundColor = MyColor.LIGHT_YELLOW;
				} else {
					foregroundColor = le.getForegroundColor();
					backgroundColor = le.getBackgroundColor();

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
