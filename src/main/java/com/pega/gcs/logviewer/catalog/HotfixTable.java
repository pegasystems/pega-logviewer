
package com.pega.gcs.logviewer.catalog;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import com.pega.gcs.fringecommon.guiutilities.FilterTable;
import com.pega.gcs.logviewer.catalog.model.HotfixEntryKey;

public class HotfixTable extends FilterTable<HotfixEntryKey> {

    private static final long serialVersionUID = 124489163290610807L;

    public HotfixTable(HotfixTableModel hotfixTableModel) {
        this(hotfixTableModel, true);
    }

    public HotfixTable(HotfixTableModel hotfixTableModel, boolean filterColumns) {

        super(hotfixTableModel, filterColumns);

        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        setTransferHandler(new TransferHandler() {

            private static final long serialVersionUID = 3782246634541129167L;

            @Override
            protected Transferable createTransferable(JComponent component) {

                int[] selectedRows = getSelectedRows();

                StringBuilder dataSB = new StringBuilder();

                if (selectedRows != null) {

                    HotfixTableModel hotfixTableModel = (HotfixTableModel) getModel();

                    String selectedRowData = hotfixTableModel.getSelectedRowsData(selectedRows);
                    dataSB.append(selectedRowData);
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
