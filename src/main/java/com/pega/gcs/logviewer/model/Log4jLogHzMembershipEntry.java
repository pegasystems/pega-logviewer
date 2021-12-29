
package com.pega.gcs.logviewer.model;

import java.util.ArrayList;

public class Log4jLogHzMembershipEntry extends Log4jLogEntry {

    private static final long serialVersionUID = 2256004087868128770L;

    public Log4jLogHzMembershipEntry(LogEntryKey logEntryKey, ArrayList<String> logEntryValueList, String logEntryText,
            boolean sysdateEntry, byte logLevelId) {

        super(logEntryKey, logEntryValueList, logEntryText, sysdateEntry, logLevelId);

    }

}
