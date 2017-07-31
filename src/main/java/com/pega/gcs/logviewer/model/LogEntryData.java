/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

import java.io.Serializable;
import java.util.ArrayList;

public class LogEntryData implements Serializable {

	private static final long serialVersionUID = 3956161860913344066L;

	private String logEntryText;

	private ArrayList<String> logEntryValueList;

	// default constructor required for kyro
	public LogEntryData() {

	}

	public LogEntryData(String logEntryText, ArrayList<String> logEntryValueList) {
		super();
		this.logEntryText = logEntryText;
		this.logEntryValueList = logEntryValueList;
	}

	/**
	 * @return the logEntryText
	 */
	public String getLogEntryText() {
		return logEntryText;
	}

	/**
	 * @return the logEntryValueList
	 */
	public ArrayList<String> getLogEntryValueList() {
		return logEntryValueList;
	}

}
