/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JPanel;

import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.ThreadDumpPanel;

public class Log4jLogThreadDumpEntry extends Log4jLogEntry {

	private static final long serialVersionUID = 5176182208095407460L;

	private List<Log4jLogRequestorLockEntry> log4jLogRequestorLockEntryList;

	private Object ptdpThreadDump;

	private String generatedReportFile;

	public Log4jLogThreadDumpEntry(int logEntryIndex, Date logEntryDate, ArrayList<String> logEntryValueList,
			String logEntryText, boolean sysdateEntry, byte logLevelId) {

		super(logEntryIndex, logEntryDate, logEntryValueList, logEntryText, sysdateEntry, logLevelId);

		log4jLogRequestorLockEntryList = new ArrayList<Log4jLogRequestorLockEntry>();

		generatedReportFile = null;
	}

	public List<Log4jLogRequestorLockEntry> getLog4jLogRequestorLockEntryList() {
		return log4jLogRequestorLockEntryList;
	}

	@Override
	public JPanel getDetailsJPanel(LogTableModel logTableModel) {

		JPanel detailsJPanel = null;
		AtomicInteger threadDumpSelectedTab = new AtomicInteger(0);
		detailsJPanel = new ThreadDumpPanel(this, logTableModel, threadDumpSelectedTab);

		return detailsJPanel;
	}

	public Object getPtdpThreadDump() {
		return ptdpThreadDump;
	}

	public void setPtdpThreadDump(Object ptdpThreadDump) {
		this.ptdpThreadDump = ptdpThreadDump;
	}

	public String getGeneratedReportFile() {
		return generatedReportFile;
	}

	public void setGeneratedReportFile(String generatedReportFile) {
		this.generatedReportFile = generatedReportFile;
	}

}
