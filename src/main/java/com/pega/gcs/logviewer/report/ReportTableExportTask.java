
package com.pega.gcs.logviewer.report;

import java.io.File;
import java.nio.charset.Charset;

import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;

import com.pega.gcs.logviewer.model.LogEntry;

public class ReportTableExportTask extends SwingWorker<Void, Integer> {

    private LogReportTableModel logReportTableModel;
    private File tsvFile;

    public ReportTableExportTask(LogReportTableModel logReportTableModel, File tsvFile) {

        this.logReportTableModel = logReportTableModel;
        this.tsvFile = tsvFile;
    }

    @Override
    protected Void doInBackground() throws Exception {

        int rowCount = logReportTableModel.getRowCount();
        int colCount = logReportTableModel.getColumnCount();

        int batchSize = 4194304; // 4096KB
        StringBuilder outputStrBatch = new StringBuilder();
        boolean append = false;
        Charset charset = logReportTableModel.getCharset();

        int row = 0;

        publish(row);

        boolean firstColumn = true;

        // write headers
        for (int col = 0; col < colCount; col++) {

            String columnName = logReportTableModel.getColumnName(col);

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

                LogEntry logEntry = (LogEntry) logReportTableModel.getValueAt(row, 0);

                for (int col = 0; col < colCount; col++) {

                    String columnValue = logReportTableModel.getColumnValue(logEntry, row, col);

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
