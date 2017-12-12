/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import com.pega.gcs.fringecommon.guiutilities.FilterTable;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntryKey;

public class SystemScanTable extends FilterTable<ScanResultHotfixEntryKey> {

	private static final long serialVersionUID = 7717205544218025756L;

	public SystemScanTable(SystemScanTableModel systemScanTableModel) {
		this(systemScanTableModel, true);
	}
	
	public SystemScanTable(SystemScanTableModel systemScanTableModel, boolean filterColumns) {

		super(systemScanTableModel, filterColumns);

		// setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		setTransferHandler(new TransferHandler() {

			private static final long serialVersionUID = 3782246634541129167L;

			@Override
			protected Transferable createTransferable(JComponent c) {

				int[] selectedRows = getSelectedRows();

				StringBuffer dataSB = new StringBuffer();

				if (selectedRows != null) {

					SystemScanTableModel systemScanTableModel = (SystemScanTableModel) getModel();

					String selectedRowData = systemScanTableModel.getSelectedRowsData(selectedRows);
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

}
