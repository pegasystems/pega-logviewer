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
import com.pega.gcs.logviewer.AlertLogEntryPanel;
import com.pega.gcs.logviewer.LogTableModel;

public class AlertLogEntry extends LogEntry {

    private static final long serialVersionUID = -6395874651333188874L;

    private int version;

    private int alertId;

    private long observedKPI;

    private boolean criticalAlertEntry;

    private Number[] palDataValueArray;

    public AlertLogEntry(LogEntryKey logEntryKey, ArrayList<String> logEntryValueList, String logEntryText, int version,
            int alertId, long observedKPI, boolean criticalAlertEntry, Number[] palDataValueArray) {

        super(logEntryKey, logEntryValueList, logEntryText);

        this.version = version;
        this.alertId = alertId;
        this.observedKPI = observedKPI;
        this.criticalAlertEntry = criticalAlertEntry;
        this.palDataValueArray = palDataValueArray;

    }

    public int getVersion() {
        return version;
    }

    public int getAlertId() {
        return alertId;
    }

    public long getObservedKPI() {
        return observedKPI;
    }

    public boolean isCriticalAlertEntry() {
        return criticalAlertEntry;
    }

    @Override
    public Color getForegroundColor() {

        Color foregroundColor = null;

        if (isCriticalAlertEntry()) {
            foregroundColor = Color.WHITE;
        }

        return foregroundColor;
    }

    @Override
    public Color getBackgroundColor() {

        Color backgroundColor = null;

        if (isCriticalAlertEntry()) {
            backgroundColor = MyColor.ERROR;
        } else {
            backgroundColor = MyColor.LIGHTEST_LIGHT_GRAY;
        }

        return backgroundColor;
    }

    @Override
    public JPanel getDetailsPanel(LogTableModel logTableModel) {

        JPanel detailsPanel;

        AlertLogEntryModel alertLogEntryModel = (AlertLogEntryModel) logTableModel.getLogEntryModel();

        detailsPanel = new AlertLogEntryPanel(this, alertLogEntryModel, logTableModel.getCharset());

        return detailsPanel;
    }

    public Number getPALDataValue(int column) {
        return palDataValueArray[column];
    }

    @Override
    public String toString() {
        return getKey().getLineNo() + " " + alertId;
    }

}
