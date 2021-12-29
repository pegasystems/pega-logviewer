/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import java.util.ArrayList;

public class Log4jLogExceptionEntry extends Log4jLogEntry {

    private static final long serialVersionUID = -7419303874771737533L;

    public Log4jLogExceptionEntry(LogEntryKey logEntryKey, ArrayList<String> logEntryValueList, String logEntryText,
            boolean sysdateEntry, byte logLevelId) {

        super(logEntryKey, logEntryValueList, logEntryText, sysdateEntry, logLevelId);

    }

}
