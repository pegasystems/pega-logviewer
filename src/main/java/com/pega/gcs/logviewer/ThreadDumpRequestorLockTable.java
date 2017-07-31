/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer;

import java.awt.Font;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class ThreadDumpRequestorLockTable extends JTable {

	private static final long serialVersionUID = -5904262962391585657L;

	public ThreadDumpRequestorLockTable(ThreadDumpRequestorLockTableModel dm) {

		super(dm);

		setAutoCreateColumnsFromModel(false);

		setRowHeight(22);

		setRowSelectionAllowed(true);

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		JTableHeader tableHeader = getTableHeader();

		tableHeader.setReorderingAllowed(false);

		// bold the header
		Font existingFont = tableHeader.getFont();
		String existingFontName = existingFont.getName();
		int existFontSize = existingFont.getSize();
		Font newFont = new Font(existingFontName, Font.BOLD, existFontSize);
		tableHeader.setFont(newFont);

		DefaultTableCellRenderer dtcr = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();

		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public void setModel(TableModel dataModel) {

		super.setModel(dataModel);

		if ((dataModel != null) && (dataModel instanceof ThreadDumpRequestorLockTableModel)) {

			TableColumnModel columnModel = ((ThreadDumpRequestorLockTableModel) dataModel).getTableColumnModel();

			setColumnModel(columnModel);
		}
	}

}
