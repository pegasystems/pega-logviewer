
package com.pega.gcs.logviewer.model;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.pega.gcs.logviewer.LogTableModel;

public class JsonLogEntry extends LogEntry {

    private static final long serialVersionUID = -2296850212910350176L;

    public JsonLogEntry(LogEntryKey logEntryKey, ArrayList<String> logEntryValueList, String logEntryText) {

        super(logEntryKey, logEntryValueList, logEntryText);

    }

    @Override
    public Color getForegroundColor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Color getBackgroundColor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JPanel getDetailsPanel(LogTableModel logTableModel) {
        // TODO Auto-generated method stub
        return null;
    }

}
