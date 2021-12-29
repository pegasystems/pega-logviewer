/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.report.alertpal;

import java.io.File;
import java.nio.charset.Charset;

import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;

import com.pega.gcs.logviewer.model.AlertLogEntry;

public class AlertPALTableExportTask extends SwingWorker<Void, Integer> {

    private AlertPALTableModel alertPALTableModel;
    private File tsvFile;

    public AlertPALTableExportTask(AlertPALTableModel alertPALTableModel, File tsvFile) {

        this.alertPALTableModel = alertPALTableModel;
        this.tsvFile = tsvFile;
    }

    @Override
    protected Void doInBackground() throws Exception {

        int rowCount = alertPALTableModel.getRowCount();
        int colCount = alertPALTableModel.getColumnCount();

        int batchSize = 4194304; // 4096KB
        StringBuilder outputStrBatch = new StringBuilder();
        boolean append = false;
        Charset charset = alertPALTableModel.getCharset();

        int row = 0;

        publish(row);

        // StringBuilder dataRecordBuffer = new StringBuilder();
        boolean firstColumn = true;

        // write headers
        for (int col = 0; col < colCount; col++) {

            String columnName = alertPALTableModel.getColumnName(col);

            if (firstColumn) {
                firstColumn = false;
            } else {
                outputStrBatch.append("\t");
            }

            outputStrBatch.append(columnName);
        }

        outputStrBatch.append(System.getProperty("line.separator"));

        for (row = 0; row < rowCount; row++) {

            if (!isCancelled()) {

                firstColumn = true;

                AlertLogEntry alertLogEntry = (AlertLogEntry) alertPALTableModel.getValueAt(row, 0);

                for (int col = 0; col < colCount; col++) {

                    String columnValue = alertPALTableModel.getColumnValue(alertLogEntry, col);

                    if (firstColumn) {
                        firstColumn = false;
                    } else {
                        outputStrBatch.append("\t");
                    }

                    if (columnValue != null) {
                        outputStrBatch.append(columnValue);
                    }
                }

                outputStrBatch.append(System.getProperty("line.separator"));

                int accumulatedSize = outputStrBatch.length();

                if (accumulatedSize > batchSize) {

                    FileUtils.writeStringToFile(tsvFile, outputStrBatch.toString(), charset, append);

                    outputStrBatch = new StringBuilder();

                    if (!append) {
                        append = true;
                    }

                    publish(row);
                }
            }
        }

        if (!isCancelled()) {

            int accumulatedSize = outputStrBatch.length();

            if (accumulatedSize > 0) {
                FileUtils.writeStringToFile(tsvFile, outputStrBatch.toString(), charset, append);
            }

            publish(row);
        }

        return null;
    }
}
