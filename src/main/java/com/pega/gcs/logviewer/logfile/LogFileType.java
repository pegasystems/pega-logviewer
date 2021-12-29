/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.logfile;

public class LogFileType {

    public enum LogType {
        PEGA_ALERT, PEGA_RULES/* Future - , WAS, WLS, JBOSS */
    }

    private LogType logType;

    private LogPattern logPattern;

    // for kryo
    private LogFileType() {
        super();
    }

    public LogFileType(LogType logType, LogPattern logPattern) {
        super();
        this.logType = logType;
        this.logPattern = logPattern;
    }

    public LogType getLogType() {
        return logType;
    }

    public LogPattern getLogPattern() {
        return logPattern;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(logType.name());

        if (logPattern != null) {
            sb.append(" [");
            sb.append(logPattern.toString());
            sb.append("]");
        }

        return sb.toString();
    }

}
