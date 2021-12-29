/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import java.util.ArrayList;

public class Log4jLogRequestorLockEntry extends Log4jLogEntry {

    private static final long serialVersionUID = 5940780183062945527L;

    private String requestorId;

    private Integer timeInterval;

    private String thisThreadName;

    private String originalLockThreadName;

    private String finallyLockThreadName;

    public Log4jLogRequestorLockEntry(LogEntryKey logEntryKey, ArrayList<String> logEntryValueList, String logEntryText,
            boolean sysdateEntry, byte logLevelId) {

        super(logEntryKey, logEntryValueList, logEntryText, sysdateEntry, logLevelId);

        this.requestorId = null;
        this.timeInterval = null;
        this.thisThreadName = null;
        this.originalLockThreadName = null;
        this.finallyLockThreadName = null;

    }

    public String getRequestorId() {
        return requestorId;
    }

    public void setRequestorId(String requestorId) {
        this.requestorId = requestorId;
    }

    public Integer getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(Integer timeInterval) {
        this.timeInterval = timeInterval;
    }

    public String getThisThreadName() {
        return thisThreadName;
    }

    public void setThisThreadName(String thisThreadName) {
        this.thisThreadName = thisThreadName;
    }

    public String getOriginalLockThreadName() {
        return originalLockThreadName;
    }

    public void setOriginalLockThreadName(String originalLockThreadName) {
        this.originalLockThreadName = originalLockThreadName;
    }

    public String getFinallyLockThreadName() {
        return finallyLockThreadName;
    }

    public void setFinallyLockThreadName(String finallyLockThreadName) {
        this.finallyLockThreadName = finallyLockThreadName;
    }

}
