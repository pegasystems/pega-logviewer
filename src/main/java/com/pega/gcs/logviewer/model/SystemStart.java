/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

import java.text.DateFormat;
import java.util.Date;

public class SystemStart implements Comparable<SystemStart> {

	private Integer index;

	private DateFormat displayDateFormat;

	Log4jLogEntry log4jLogEntry;

	private Integer endIndex;

	private boolean abruptStop;

	public SystemStart(Integer index, DateFormat displayDateFormat, Log4jLogEntry log4jLogEntry) {
		super();
		this.index = index;
		this.displayDateFormat = displayDateFormat;
		this.log4jLogEntry = log4jLogEntry;
		this.endIndex = null;
	}

	/**
	 * @return the index
	 */
	public Integer getIndex() {
		return index;
	}

	/**
	 * @return the beginIndex
	 */
	public Integer getBeginIndex() {
		return log4jLogEntry.getKey();
	}

	/**
	 * @return the systemStartEndIndex
	 */
	public Integer getEndIndex() {
		return endIndex;
	}

	/**
	 * @param endIndex
	 *            the endIndex to set
	 */
	public void setEndIndex(Integer endIndex) {
		this.endIndex = endIndex;
	}

	public boolean isAbruptStop() {
		return abruptStop;
	}

	public void setAbruptStop(boolean abruptStop) {
		this.abruptStop = abruptStop;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		Date beginDate = log4jLogEntry.getLogEntryDate();
		String timeText = displayDateFormat.format(beginDate);
		int beginIndex = log4jLogEntry.getKey();

		StringBuffer sb = new StringBuffer();
		sb.append(index);
		sb.append(". System Start - Time [");
		sb.append(timeText);
		sb.append("] Line No [");
		sb.append(beginIndex);
		sb.append("]");
		if (abruptStop) {
			sb.append(" - Abruptly stopped");
		}

		return sb.toString();
	}

	@Override
	public int compareTo(SystemStart o) {

		return getIndex().compareTo(o.getIndex());
	}

}
