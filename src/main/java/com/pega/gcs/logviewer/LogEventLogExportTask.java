
package com.pega.gcs.logviewer;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;

import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.logviewer.model.LogEntry;

public class LogEventLogExportTask extends SwingWorker<Boolean, Integer> {

    private LogTableModel logTableModel;

    private List<Integer> selectedRowList;

    private File exportFile;

    private ModalProgressMonitor modalProgressMonitor;

    public LogEventLogExportTask(LogTableModel logTableModel, List<Integer> selectedRowList, File exportFile,
            ModalProgressMonitor modalProgressMonitor) {
        super();
        this.logTableModel = logTableModel;
        this.selectedRowList = selectedRowList;
        this.exportFile = exportFile;
        this.modalProgressMonitor = modalProgressMonitor;
    }

    @Override
    protected Boolean doInBackground() throws Exception {

        int prevProgress = 0;
        int counter = 0;
        int totalSize = selectedRowList.size();
        double increment = totalSize / 100d;

        int batchSize = 4194304; // 4096KB
        StringBuilder selectedRowsDataSB = new StringBuilder();
        boolean append = false;
        Charset charset = logTableModel.getCharset();

        for (int selectedRow : selectedRowList) {

            if (modalProgressMonitor.isCanceled()) {
                break;
            }

            counter++;

            LogEntry logEntry = (LogEntry) logTableModel.getValueAt(selectedRow, 0);

            if (logEntry != null) {

                selectedRowsDataSB.append(logEntry.getLogEntryText());
                selectedRowsDataSB.append(System.getProperty("line.separator"));

                int progress = (int) Math.round(counter / increment);

                if (progress > prevProgress) {
                    publish((int) progress);
                    prevProgress = progress;
                }

            }

            int accumulatedSize = selectedRowsDataSB.length();

            if (accumulatedSize > batchSize) {

                FileUtils.writeStringToFile(exportFile, selectedRowsDataSB.toString(), charset, append);

                selectedRowsDataSB = new StringBuilder();

                if (!append) {
                    append = true;
                }
            }
        }

        if (!modalProgressMonitor.isCanceled()) {

            int accumulatedSize = selectedRowsDataSB.length();

            if (accumulatedSize > 0) {
                FileUtils.writeStringToFile(exportFile, selectedRowsDataSB.toString(), charset, append);
            }
        }

        Boolean returnValue = Boolean.TRUE;

        if (modalProgressMonitor.isCanceled()) {
            returnValue = Boolean.FALSE;
        }

        return returnValue;
    }

    @Override
    protected void process(List<Integer> chunks) {

        if ((isDone()) || (isCancelled()) || (chunks == null) || (chunks.size() == 0)) {
            return;
        }

        boolean indeterminate = modalProgressMonitor.isIndeterminate();

        if (!indeterminate) {

            Collections.sort(chunks);

            Integer progress = chunks.get(chunks.size() - 1);

            String message = String.format("Exporting (%d%%)", progress);

            modalProgressMonitor.setProgress(progress);
            modalProgressMonitor.setNote(message);
        }
    }

}
