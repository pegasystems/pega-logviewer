/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.catalog;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import com.pega.gcs.fringecommon.guiutilities.FilterTable;
import com.pega.gcs.fringecommon.utilities.GeneralUtilities;
import com.pega.gcs.logviewer.catalog.model.HotfixColumn;
import com.pega.gcs.logviewer.catalog.model.HotfixRecordEntry;

public class HotfixRecordEntryTable extends FilterTable<Integer> {

    private static final long serialVersionUID = 7882619873210948802L;

    public HotfixRecordEntryTable(HotfixRecordEntryTableModel hotfixRecordEntryTableModel) {

        super(hotfixRecordEntryTableModel);

        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        setTransferHandler(new TransferHandler() {

            private static final long serialVersionUID = 5316455238872189390L;

            @Override
            protected Transferable createTransferable(JComponent component) {

                int[] selectedRows = getSelectedRows();

                StringBuilder dataSB = new StringBuilder();

                if (selectedRows != null) {

                    HotfixRecordEntryTableModel hotfixRecordEntryTableModel = (HotfixRecordEntryTableModel) getModel();

                    List<HotfixColumn> hotfixColumnList = hotfixRecordEntryTableModel.getHotfixColumnList();

                    List<String> hotfixColumnNameList = HotfixColumn.getColumnNameList(hotfixColumnList);

                    String scanResultHotfixInfoColumnListCSV = GeneralUtilities
                            .getCollectionAsSeperatedValues(hotfixColumnNameList, null, true);

                    dataSB.append(scanResultHotfixInfoColumnListCSV);
                    dataSB.append(System.getProperty("line.separator"));

                    for (int selectedRow : selectedRows) {

                        HotfixRecordEntry hotfixRecordEntry = (HotfixRecordEntry) hotfixRecordEntryTableModel
                                .getValueAt(selectedRow, 0);

                        List<String> recordDataList = hotfixRecordEntry.getRecordDataList();

                        String recordDataListCSV = GeneralUtilities.getCollectionAsSeperatedValues(recordDataList, null,
                                true);

                        dataSB.append(recordDataListCSV);
                        dataSB.append(System.getProperty("line.separator"));
                    }

                }

                return new StringSelection(dataSB.toString());
            }

            @Override
            public int getSourceActions(JComponent component) {
                return TransferHandler.COPY;
            }
        });

    }

}
