/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import java.util.ArrayList;
import java.util.List;

public class SystemStart implements Comparable<SystemStart> {

    private Integer index;

    private LogEntryKey beginKey;

    private List<LogEntryKey> logEntryKeyList;

    private boolean abruptStop;

    public SystemStart(Integer index, LogEntryKey beginKey) {
        super();
        this.index = index;
        this.beginKey = beginKey;
        this.logEntryKeyList = new ArrayList<>();
        this.logEntryKeyList.add(beginKey);
    }

    public Integer getIndex() {
        return index;
    }

    public LogEntryKey getBeginKey() {
        return beginKey;
    }

    public List<LogEntryKey> getLogEntryKeyList() {
        return logEntryKeyList;
    }

    public void addLogEntryKey(LogEntryKey logEntryKey) {
        logEntryKeyList.add(logEntryKey);
    }

    public boolean isAbruptStop() {
        return abruptStop;
    }

    public void setAbruptStop(boolean abruptStop) {
        this.abruptStop = abruptStop;
    }

    public String getDisplayString(LogEntryModel logEntryModel) {

        String timeText = logEntryModel.getLogEntryTimeDisplayString(beginKey);
        int lineNo = beginKey.getLineNo();

        StringBuilder sb = new StringBuilder();
        sb.append(index);
        sb.append(". System Start - Time [");
        sb.append(timeText);
        sb.append("] Line No [");
        sb.append(lineNo);
        sb.append("]");
        if (abruptStop) {
            sb.append(" - Abruptly stopped");
        }

        return sb.toString();
    }

    @Override
    public int compareTo(SystemStart other) {

        return getIndex().compareTo(other.getIndex());
    }

}
