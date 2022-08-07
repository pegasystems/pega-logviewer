/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JPanel;

import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.ThreadDumpPanel;

public class Log4jLogThreadDumpEntry extends Log4jLogEntry {

    private static final long serialVersionUID = 5176182208095407460L;

    private Integer index;

    private List<Log4jLogRequestorLockEntry> log4jLogRequestorLockEntryList;

    private Object ptdpThreadDump;

    private String generatedReportFile;

    public Log4jLogThreadDumpEntry(int index, LogEntryKey logEntryKey, ArrayList<String> logEntryValueList,
            String logEntryText, boolean sysdateEntry, byte logLevelId) {

        super(logEntryKey, logEntryValueList, logEntryText, sysdateEntry, logLevelId);

        this.index = index;

        log4jLogRequestorLockEntryList = new ArrayList<Log4jLogRequestorLockEntry>();

        generatedReportFile = null;
    }

    public List<Log4jLogRequestorLockEntry> getLog4jLogRequestorLockEntryList() {
        return log4jLogRequestorLockEntryList;
    }

    @Override
    public JPanel getDetailsPanel(LogTableModel logTableModel) {

        JPanel detailsPanel;
        AtomicInteger threadDumpSelectedTab = new AtomicInteger(0);
        detailsPanel = new ThreadDumpPanel(this, logTableModel, threadDumpSelectedTab);

        return detailsPanel;
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

    public String getDisplayString(LogEntryModel logEntryModel) {

        LogEntryKey logEntryKey = getKey();

        String timeText = logEntryModel.getLogEntryTimeDisplayString(logEntryKey);
        int lineNo = logEntryKey.getLineNo();

        StringBuilder sb = new StringBuilder();
        sb.append(index);
        sb.append(". Thread Dump - Time [");
        sb.append(timeText);
        sb.append("] Line No [");
        sb.append(lineNo);
        sb.append("]");

        return sb.toString();
    }
}
