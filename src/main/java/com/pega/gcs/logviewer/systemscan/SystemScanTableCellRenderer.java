/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntry;

public class SystemScanTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -8570520678706612613L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent
	 * (javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		ScanResultHotfixEntry scanResultHotfixEntry = (ScanResultHotfixEntry) value;

		if (scanResultHotfixEntry != null) {

			SystemScanTableModel systemScanTableModel = (SystemScanTableModel) table.getModel();
			
			SystemScanColumn systemScanColumn = systemScanTableModel.getColumn(column);

			String text = systemScanTableModel.getScanResult().getScanResultHotfixEntryData(scanResultHotfixEntry, systemScanColumn);
			
			super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);

			if (!table.isRowSelected(row)) {
				
				boolean searchFound = scanResultHotfixEntry.isSearchFound();

				if (searchFound) {
					setBackground(MyColor.LIGHT_YELLOW);
				} else {
					setBackground(scanResultHotfixEntry.getBackground());
				}
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
