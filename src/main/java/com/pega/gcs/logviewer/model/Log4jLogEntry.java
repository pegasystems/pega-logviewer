/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

import java.awt.Color;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JPanel;

import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.LogEntryPanel;
import com.pega.gcs.logviewer.LogTableModel;

public class Log4jLogEntry extends LogEntry {

	private static final long serialVersionUID = 3587369391174048988L;

	private static final Log4j2Helper LOG = new Log4j2Helper(Log4jLogEntry.class);

	private boolean sysdateEntry;

	private byte logLevelId;

	public Log4jLogEntry(int logEntryIndex, Date logEntryDate, ArrayList<String> logEntryValueList, String logEntryText,
			boolean sysdateEntry, byte logLevelId) {

		super(logEntryIndex, logEntryDate, logEntryValueList, logEntryText);

		this.sysdateEntry = sysdateEntry;
		this.logLevelId = logLevelId;

	}

	/**
	 * @return the sysdateEntry
	 */
	public boolean isSysdateEntry() {
		return sysdateEntry;
	}

	@Override
	public Color getForegroundColor() {

		Color foregroundColor = null;

		switch (logLevelId) {

		// case 1:
		// foregroundColor = MyColor.TRACE;
		// break;
		// case 2:
		// foregroundColor = MyColor.DEBUG;
		// break;
		// case 3:
		// foregroundColor = MyColor.INFO;
		// break;
		// case 4:
		// foregroundColor = MyColor.WARN;
		// break;
		case 5:
		case 6:
		case 7:
			foregroundColor = Color.WHITE;
			break;
		default:
			foregroundColor = null;
		}

		return foregroundColor;
	}

	@Override
	public Color getBackgroundColor() {

		Color backgroundColor = null;

		switch (logLevelId) {

		case 1:
			backgroundColor = MyColor.TRACE;
			break;
		case 2:
			backgroundColor = MyColor.DEBUG;
			break;
		case 3:
			backgroundColor = MyColor.INFO;
			break;
		case 4:
			backgroundColor = MyColor.WARN;
			break;
		case 5:
			backgroundColor = MyColor.ALERT;
			break;
		case 6:
			backgroundColor = MyColor.ERROR;
			break;
		case 7:
			backgroundColor = MyColor.FATAL;
			break;
		default:
			backgroundColor = Color.WHITE;
		}

		return backgroundColor;
	}

	public void updateTimestamp(DateFormat modelDateFormat, int timestampColumnIndex, int deltaColumnIndex,
			Log4jLogEntry prevLog4jLogEntry) {

		LogEntryData logEntryData = getLogEntryData();

		ArrayList<String> logEntryValueList = logEntryData.getLogEntryValueList();

		String logEntryDateStr = logEntryValueList.get(timestampColumnIndex);

		String deltaStr = null;

		try {

			Date logEntryDate = modelDateFormat.parse(logEntryDateStr);

			setLogEntryDate(logEntryDate);

			if (prevLog4jLogEntry != null) {

				long logEntryTime = logEntryDate.getTime();

				long prevLogEntryTime = 0;
				prevLogEntryTime = prevLog4jLogEntry.getLogEntryDate().getTime();

				long delta = logEntryTime - prevLogEntryTime;

				deltaStr = Long.toString(delta);
			}
			
		} catch (ParseException pe) {
			LOG.info("Date parse error: " + logEntryDateStr);
		}

		logEntryValueList.set(deltaColumnIndex, deltaStr);

		setLogEntryData(logEntryData);

	}

	@Override
	public JPanel getDetailsJPanel(LogTableModel logTableModel) {

		JPanel detailsJPanel = null;

		detailsJPanel = new LogEntryPanel(getLogEntryText());

		return detailsJPanel;
	}
}
