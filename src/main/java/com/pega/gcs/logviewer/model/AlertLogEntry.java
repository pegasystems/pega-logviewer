/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;

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

	public AlertLogEntry(int logEntryIndex, Date logEntryDate, ArrayList<String> logEntryValueList, String logEntryText,
			int version, int alertId, long observedKPI, boolean criticalAlertEntry, Number[] palDataValueArray) {

		super(logEntryIndex, logEntryDate, logEntryValueList, logEntryText);

		this.version = version;
		this.alertId = alertId;
		this.observedKPI = observedKPI;
		this.criticalAlertEntry = criticalAlertEntry;
		this.palDataValueArray = palDataValueArray;

	}

	public int getVersion() {
		return version;
	}

	/**
	 * @return the alertId
	 */
	public int getAlertId() {
		return alertId;
	}

	/**
	 * @return the observedKPI
	 */
	public long getObservedKPI() {
		return observedKPI;
	}

	/**
	 * @return the criticalAlertEntry
	 */
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
	public JPanel getDetailsJPanel(LogTableModel logTableModel) {

		JPanel detailsJPanel = null;

		detailsJPanel = new AlertLogEntryPanel(this, logTableModel.getLogEntryModel());

		return detailsJPanel;
	}

	public Number getPALDataValue(int column) {
		return palDataValueArray[column];
	}

	@Override
	public String toString() {
		return getKey() + " " + alertId + " " + getLogEntryDate();
	}

}
