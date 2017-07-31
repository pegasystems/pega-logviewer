/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.logfile;

public class LogFileType {

	public enum LogType {
		PEGA_ALERT, PEGA_RULES, WAS, WLS, JBOSS
	}

	private LogType logType;

	private LogPattern logPattern;

	// for kryo
	private LogFileType() {
		super();
	}

	/**
	 * @param aLogType
	 * @param aLogPattern
	 */
	public LogFileType(LogType aLogType, LogPattern aLogPattern) {
		super();
		logType = aLogType;
		logPattern = aLogPattern;
	}

	public LogType getLogType() {
		return logType;
	}

	public LogPattern getLogPattern() {
		return logPattern;
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append(logType.name());
		sb.append(" ");

		if (logPattern != null) {
			sb.append(logPattern.toString());
		}

		return sb.toString();
	}

}
