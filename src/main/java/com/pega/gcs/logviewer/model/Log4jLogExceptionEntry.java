/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

import java.util.ArrayList;
import java.util.Date;

public class Log4jLogExceptionEntry extends Log4jLogEntry {

	private static final long serialVersionUID = -7419303874771737533L;

	public Log4jLogExceptionEntry(int logEntryIndex, Date logEntryDate, ArrayList<String> logEntryValueList,
			String logEntryText, boolean sysdateEntry, byte logLevelId) {

		super(logEntryIndex, logEntryDate, logEntryValueList, logEntryText, sysdateEntry, logLevelId);

	}

}
