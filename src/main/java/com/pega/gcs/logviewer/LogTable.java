/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import com.pega.gcs.fringecommon.guiutilities.FilterTable;

public class LogTable extends FilterTable<Integer> {

	private static final long serialVersionUID = -3752172566327398734L;

	public LogTable(LogTableModel logTableModel) {

		super(logTableModel);

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		setTransferHandler(new TransferHandler() {

			private static final long serialVersionUID = 2454653839935814799L;

			@Override
			protected Transferable createTransferable(JComponent c) {

				int[] selectedRows = getSelectedRows();

				StringBuffer dataSB = new StringBuffer();

				if (selectedRows != null) {

					LogTableModel logTableModel = (LogTableModel) getModel();

					String selectedRowData = logTableModel.getSelectedRowsData(selectedRows);
					dataSB.append(selectedRowData);
				}

				return new StringSelection(dataSB.toString());
			}

			@Override
			public int getSourceActions(JComponent c) {
				return TransferHandler.COPY;
			}

		});
	}

	public void updateLogTableColumnModel() {

		LogTableModel logTableModel = (LogTableModel) getModel();

		setColumnModel(logTableModel.getTableColumnModel());
	}

	public void logTableScrollToIndex(Integer logEntryIndex) {

		LogTableModel ltm = (LogTableModel) getModel();

		List<Integer> ltmIndexList = ltm.getFtmEntryKeyList();

		int rowNumber = Collections.binarySearch(ltmIndexList, logEntryIndex);

		if (rowNumber < 0) {
			rowNumber = (rowNumber * -1) - 1;
		}

		// changeSelection(rowNumber, 0, false, false);
		setRowSelectionInterval(rowNumber, rowNumber);
		scrollRowToVisible(rowNumber);
	}

}
