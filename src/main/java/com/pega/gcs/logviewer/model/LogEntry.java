/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import java.awt.Color;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Objects;

import javax.swing.JPanel;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.Identifiable;
import com.pega.gcs.fringecommon.utilities.KnuthMorrisPrattAlgorithm;
import com.pega.gcs.fringecommon.utilities.kyro.KryoSerializer;
import com.pega.gcs.logviewer.LogTableModel;

public abstract class LogEntry implements Identifiable<LogEntryKey>, Serializable {

    private static final long serialVersionUID = -6323090071656803913L;

    private static final Log4j2Helper LOG = new Log4j2Helper(LogEntry.class);

    private LogEntryKey logEntryKey;

    private byte[] compressedLogEntryData;

    public abstract Color getForegroundColor();

    public abstract Color getBackgroundColor();

    public abstract JPanel getDetailsJPanel(LogTableModel logTableModel);

    private boolean searchFound;

    public LogEntry(LogEntryKey logEntryKey, ArrayList<String> logEntryValueList, String logEntryText) {

        super();

        this.logEntryKey = logEntryKey;

        LogEntryData logEntryData = new LogEntryData(logEntryText, logEntryValueList);

        setLogEntryData(logEntryData);
    }

    @Override
    public LogEntryKey getKey() {
        return logEntryKey;
    }

    public LogEntryData getLogEntryData() {

        LogEntryData logEntryData = null;

        try {
            logEntryData = KryoSerializer.decompress(compressedLogEntryData, LogEntryData.class);
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

    public boolean search(String searchStr, Charset charset) {

        searchFound = false;

        String logEntryText = getLogEntryText();

        // logEntryText will null in case of empty or corrupt TE's
        if ((logEntryText != null) && (searchStr != null)) {

            logEntryText = logEntryText.toLowerCase();
            String logSearchStr = searchStr.toLowerCase();

            byte[] pattern = logSearchStr.getBytes(charset);
            byte[] data = logEntryText.getBytes(charset);

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
        return Objects.hash(logEntryKey);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LogEntry)) {
            return false;
        }
        LogEntry other = (LogEntry) obj;
        return Objects.equals(logEntryKey, other.logEntryKey);
    }

    // // Recalculating time when timezone becomes known
    // public void updateLogEntryTime(DateFormat modelDateFormat, int timestampColumnIndex) {
    //
    // LogEntryData logEntryData = getLogEntryData();
    //
    // ArrayList<String> logEntryValueList = logEntryData.getLogEntryValueList();
    //
    // String logEntryDateStr = logEntryValueList.get(timestampColumnIndex);
    //
    // try {
    //
    // Date logEntryDate = modelDateFormat.parse(logEntryDateStr);
    //
    // setLogEntryTime(logEntryDate.getTime());
    //
    // } catch (ParseException pe) {
    // LOG.info("Date parse error: " + logEntryDateStr);
    // }
    //
    // }
}
