/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import com.pega.gcs.fringecommon.guiutilities.FilterTable;
import com.pega.gcs.fringecommon.utilities.GeneralUtilities;
import com.pega.gcs.logviewer.systemscan.model.ScanResult;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixChangeEntry;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntry;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntryKey;

public class ScanResultHotfixTable extends FilterTable<Integer> {

	private static final long serialVersionUID = 7882619873210948802L;

	public ScanResultHotfixTable(ScanResultHotfixTableModel scanResultHotfixTableModel) {

		super(scanResultHotfixTableModel);

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		setTransferHandler(new TransferHandler() {

			private static final long serialVersionUID = 5316455238872189390L;

			@Override
			protected Transferable createTransferable(JComponent c) {

				int[] selectedRows = getSelectedRows();

				StringBuffer dataSB = new StringBuffer();

				if (selectedRows != null) {

					ScanResultHotfixTableModel scanResultHotfixTableModel = (ScanResultHotfixTableModel) getModel();

					ScanResult scanResult = scanResultHotfixTableModel.getScanResult();

					List<String> hotfixColumnList = scanResult.getHotfixColumnList();

					String scanResultHotfixInfoColumnListCSV = GeneralUtilities
							.getListAsSeperatedValues(hotfixColumnList, null);

					dataSB.append(scanResultHotfixInfoColumnListCSV);
					dataSB.append(System.getProperty("line.separator"));

					ScanResultHotfixEntryKey scanResultHotfixEntryKey = scanResultHotfixTableModel
							.getScanResultHotfixEntryKey();

					for (int selectedRow : selectedRows) {

						ScanResultHotfixEntry scanResultHotfixEntry;
						scanResultHotfixEntry = scanResult.getScanResultHotfixEntry(scanResultHotfixEntryKey);

						List<ScanResultHotfixChangeEntry> scanResultHotfixChangeEntryList;
						scanResultHotfixChangeEntryList = scanResultHotfixEntry.getScanResultHotfixChangeEntryList();

						if (scanResultHotfixChangeEntryList != null) {

							ScanResultHotfixChangeEntry scanResultHotfixChangeEntry = scanResultHotfixChangeEntryList
									.get(selectedRow);

							List<String> recordDataList = scanResultHotfixChangeEntry.getRecordDataList();

							String recordDataListCSV = GeneralUtilities.getListAsSeperatedValues(recordDataList, null);

							dataSB.append(recordDataListCSV);
							dataSB.append(System.getProperty("line.separator"));
						}

						dataSB.append(System.getProperty("line.separator"));
					}

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
