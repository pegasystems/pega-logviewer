/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.report;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.model.LogEntry;
import com.pega.gcs.logviewer.model.LogEntryColumn;

public class LogReportTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -1734090615883541481L;

	private LogEntryColumn[] reportTableColumns;

	private List<Integer> logEventKeyList;

	private LogTableModel logTableModel;

	public LogReportTableModel(List<Integer> logEventKeyList, LogTableModel logTableModel) {
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

		Integer logEventKey = getLogEntryKey(rowIndex);

		LogEntry logEntry = logTableModel.getEventForKey(logEventKey);

		return logEntry;
	}

	public LogEntryColumn getColumn(int columnIndex) {
		return reportTableColumns[columnIndex];
	}

	public String getColumnValue(LogEntry logEntry, int columnIndex) {

		String columnValue = null;

		LogEntryColumn logEntryColumn = getColumn(columnIndex);

		columnValue = logTableModel.getLogEntryModel().getFormattedLogEntryValue(logEntry, logEntryColumn);

		return columnValue;
	}

	public Integer getLogEntryKey(int rowIndex) {
		Integer logEntryKey = logEventKeyList.get(rowIndex);
		return logEntryKey;
	}
}
