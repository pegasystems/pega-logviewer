/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JPanel;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.Identifiable;
import com.pega.gcs.fringecommon.utilities.KnuthMorrisPrattAlgorithm;
import com.pega.gcs.fringecommon.utilities.kyro.KryoSerializer;
import com.pega.gcs.logviewer.LogTableModel;

public abstract class LogEntry implements Identifiable<Integer>, Serializable {

	private static final long serialVersionUID = -6323090071656803913L;

	private static final Log4j2Helper LOG = new Log4j2Helper(LogEntry.class);

	private int logEntryIndex;

	private Date logEntryDate;

	private byte[] compressedLogEntryData;

	public abstract Color getForegroundColor();

	public abstract Color getBackgroundColor();

	public abstract JPanel getDetailsJPanel(LogTableModel logTableModel);

	private boolean searchFound;

	public LogEntry(int logEntryIndex, Date logEntryDate, ArrayList<String> logEntryValueList, String logEntryText) {

		super();

		this.logEntryIndex = logEntryIndex;
		this.logEntryDate = logEntryDate;

		LogEntryData logEntryData = new LogEntryData(logEntryText, logEntryValueList);

		setLogEntryData(logEntryData);
	}

	@Override
	public Integer getKey() {
		return logEntryIndex;
	}

	public Date getLogEntryDate() {
		return logEntryDate;
	}

	protected void setLogEntryDate(Date newDate) {
		logEntryDate = newDate;
	}

	public LogEntryData getLogEntryData() {

		LogEntryData logEntryData = null;

		try {
			logEntryData = (LogEntryData) KryoSerializer.decompress(compressedLogEntryData, LogEntryData.class);
		} catch (Exception e) {
			LOG.error("Error decompressing log entry data", e);
		}

		return logEntryData;
	}

	protected void setLogEntryData(LogEntryData logEntryData) {
		try {
			compressedLogEntryData = KryoSerializer.compress(logEntryData);
		} catch (Exception e) {
			LOG.error("Error compressing log entry data", e);
		}
	}

	/**
	 * @return the logEntryValueList
	 */
	public ArrayList<String> getLogEntryValueList() {

		ArrayList<String> logEntryValueList = null;

		LogEntryData logEntryData = getLogEntryData();

		if (logEntryData != null) {
			logEntryValueList = logEntryData.getLogEntryValueList();
		}

		return logEntryValueList;
	}

	public String getLogEntryText() {

		String logEntryText = null;

		LogEntryData logEntryData = getLogEntryData();

		if (logEntryData != null) {
			logEntryText = logEntryData.getLogEntryText();
		}

		return logEntryText;

	}

	public boolean search(String searchStr) {

		searchFound = false;

		String traceEventStr = getLogEntryText();

		// traceEventStr will null in case of empty or corrupt TE's
		if ((traceEventStr != null) && (searchStr != null)) {

			traceEventStr = traceEventStr.toLowerCase();
			String traceSearchStr = searchStr.toLowerCase();

			byte[] pattern = traceSearchStr.getBytes();
			byte[] data = traceEventStr.getBytes();

			int index = KnuthMorrisPrattAlgorithm.indexOf(data, pattern);

			if (index != -1) {
				searchFound = true;
			}
		}

		return searchFound;

	}

	public boolean isSearchFound() {
		return searchFound;
	}

	public void setSearchFound(boolean searchFound) {
		this.searchFound = searchFound;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + logEntryIndex;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogEntry other = (LogEntry) obj;
		if (logEntryIndex != other.logEntryIndex)
			return false;
		return true;
	}

}
