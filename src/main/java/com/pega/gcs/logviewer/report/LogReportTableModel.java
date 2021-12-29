/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.report;

import java.nio.charset.Charset;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.model.LogEntry;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;

public class LogReportTableModel extends AbstractTableModel {

    private static final long serialVersionUID = -1734090615883541481L;

    private LogEntryColumn[] reportTableColumns;

    private List<LogEntryKey> logEventKeyList;

    private LogTableModel logTableModel;

    public LogReportTableModel(List<LogEntryKey> logEventKeyList, LogTableModel logTableModel) {
        super();
        this.logEventKeyList = logEventKeyList;
        this.logTableModel = logTableModel;

        this.reportTableColumns = logTableModel.getReportTableColumns();
    }

    @Override
    public int getRowCount() {
        return logEventKeyList.size();
    }

    @Override
    public int getColumnCount() {
        return reportTableColumns.length;
    }

    @Override
    public String getColumnName(int column) {
        return getColumn(column).getDisplayName();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        LogEntryKey logEventKey = getLogEntryKey(rowIndex);

        LogEntry logEntry = logTableModel.getEventForKey(logEventKey);

        return logEntry;
    }

    public LogEntryColumn getColumn(int columnIndex) {
        return reportTableColumns[columnIndex];
    }

    // passing row, for delta calculation
    public String getColumnValue(LogEntry logEntry, int rowIndex, int columnIndex) {

        String columnValue = null;

        LogEntryColumn logEntryColumn = getColumn(columnIndex);

        if (logEntryColumn.equals(LogEntryColumn.DELTA)) {

            if (rowIndex > 0) {
                int prevRowIndex = rowIndex - 1;

                LogEntryKey prevLogEventKey = getLogEntryKey(prevRowIndex);

                // LogEntry prevLogEntry = logTableModel.getEventForKey(prevLogEventKey);

                long logEntryTime = logEntry.getKey().getTimestamp();
                long prevLogEntryTime = prevLogEventKey.getTimestamp();

                long delta = logEntryTime - prevLogEntryTime;

                columnValue = Long.toString(delta);
            } else {
                columnValue = "0";
            }

        } else {
            columnValue = logTableModel.getLogEntryModel().getFormattedLogEntryValue(logEntry, logEntryColumn);
        }

        return columnValue;
    }

    public LogEntryKey getLogEntryKey(int rowIndex) {
        LogEntryKey logEntryKey = logEventKeyList.get(rowIndex);
        return logEntryKey;
    }

    public Charset getCharset() {
        return logTableModel.getCharset();
    }
}
