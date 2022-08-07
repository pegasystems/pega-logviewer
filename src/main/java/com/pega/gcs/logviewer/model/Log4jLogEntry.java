/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.logviewer.LogEntryPanel;
import com.pega.gcs.logviewer.LogTableModel;

public class Log4jLogEntry extends LogEntry {

    private static final long serialVersionUID = 3587369391174048988L;

    private boolean sysdateEntry;

    private byte logLevelId;

    public Log4jLogEntry(LogEntryKey logEntryKey, ArrayList<String> logEntryValueList, String logEntryText,
            boolean sysdateEntry, byte logLevelId) {

        super(logEntryKey, logEntryValueList, logEntryText);

        this.sysdateEntry = sysdateEntry;
        this.logLevelId = logLevelId;

    }

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

    @Override
    public JPanel getDetailsPanel(LogTableModel logTableModel) {

        JPanel detailsPanel;

        detailsPanel = new LogEntryPanel(getLogEntryText(), logTableModel.getCharset());

        return detailsPanel;
    }
}
