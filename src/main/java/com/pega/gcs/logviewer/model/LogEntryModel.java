/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

import java.awt.Font;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import org.jfree.chart.axis.DateAxis;

import com.pega.gcs.fringecommon.guiutilities.CheckBoxMenuItemPopupEntry;
import com.pega.gcs.fringecommon.guiutilities.FilterColumn;
import com.pega.gcs.fringecommon.guiutilities.FilterTableModel;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.DateTimeUtilities;

public abstract class LogEntryModel {

	private static final Log4j2Helper LOG = new Log4j2Helper(LogEntryModel.class);

	private static final int TABLE_DATA_LENGTH = 800;

	private DateFormat modelDateFormat;

	private DateFormat displayDateFormat;

	private Locale locale;

	private NumberFormat numberFormat;

	private ArrayList<String> logEntryColumnList;

	private List<Integer> logEntryIndexList;

	private Map<Integer, LogEntry> logEntryMap;

	private Map<LogEntryColumn, Integer> logEntryColumnIndexMap;

	private Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<Integer>>> columnFilterMap;

	private ArrayList<Integer> visibleColumnIndexList;

	private int lastQueriedLogEntryIndex;

	private ArrayList<String> lastQueriedLogEntryValueList;

	private Map<Long, Integer> timeLogEntryKeyMap;

	private int timestampColumnIndex;

	private DateAxis domainAxis;

	private long lowerDomainRange;

	private long upperDomainRange;

	public abstract Set<LogSeriesCollection> getLogTimeSeriesCollectionSet(boolean filtered) throws Exception;

	public abstract Set<LogIntervalMarker> getLogIntervalMarkerSet();

	public abstract LogEntryColumn[] getReportTableColumns();

	public abstract String getTypeName();

	protected abstract void postProcess(LogEntry logEntry, ArrayList<String> logEntryValueList);

	public LogEntryModel(DateFormat dateFormat, Locale locale) {

		this.modelDateFormat = dateFormat;
		this.lowerDomainRange = -1;
		this.upperDomainRange = -1;

		TimeZone tz = dateFormat.getTimeZone();

		displayDateFormat = new SimpleDateFormat(DateTimeUtilities.DATEFORMAT_ISO8601);
		displayDateFormat.setTimeZone(tz);

		setLocale(locale);

		resetModel();

	}

	public DateFormat getModelDateFormat() {
		return modelDateFormat;
	}

	public void setModelDateFormatTimeZone(TimeZone modelTimeZone) {

		DateFormat modelDateFormat = getModelDateFormat();

		modelDateFormat.setTimeZone(modelTimeZone);

	}

	public DateFormat getDisplayDateFormat() {
		return displayDateFormat;
	}

	public void setDisplayDateFormatTimeZone(TimeZone displayTimeZone) {

		DateFormat displayDateFormat = getDisplayDateFormat();

		displayDateFormat.setTimeZone(displayTimeZone);

		updateDomainAxis(displayTimeZone, null);
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
		numberFormat = NumberFormat.getInstance(locale);

		updateDomainAxis(null, locale);

	}

	public NumberFormat getNumberFormat() {
		return numberFormat;
	}

	public ArrayList<String> getLogEntryColumnList() {

		if (logEntryColumnList == null) {
			logEntryColumnList = new ArrayList<String>();
		}

		return logEntryColumnList;
	}

	public void setLogEntryColumnList(ArrayList<String> logEntryColumnList) {

		ArrayList<String> leColumnList = getLogEntryColumnList();
		leColumnList.clear();
		leColumnList.addAll(logEntryColumnList);
		initialiseVisibleFilterableColumnIndexList();
	}

	public List<Integer> getLogEntryIndexList() {

		if (logEntryIndexList == null) {
			logEntryIndexList = new ArrayList<Integer>();
		}

		return logEntryIndexList;
	}

	public Map<Integer, LogEntry> getLogEntryMap() {

		if (logEntryMap == null) {
			logEntryMap = new HashMap<Integer, LogEntry>();
		}

		return logEntryMap;
	}

	public ArrayList<Integer> getVisibleColumnIndexList() {

		if (visibleColumnIndexList == null) {
			visibleColumnIndexList = new ArrayList<Integer>();
		}

		return visibleColumnIndexList;
	}

	public Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<Integer>>> getColumnFilterMap() {

		if (columnFilterMap == null) {
			columnFilterMap = new TreeMap<FilterColumn, List<CheckBoxMenuItemPopupEntry<Integer>>>();
		}

		return columnFilterMap;
	}

	public Map<Long, Integer> getTimeLogEntryKeyMap() {

		if (timeLogEntryKeyMap == null) {
			timeLogEntryKeyMap = new HashMap<Long, Integer>();
		}

		return timeLogEntryKeyMap;
	}

	public Map<LogEntryColumn, Integer> getLogEntryColumnIndexMap() {

		if (logEntryColumnIndexMap == null) {
			logEntryColumnIndexMap = new HashMap<LogEntryColumn, Integer>();
		}

		return logEntryColumnIndexMap;
	}

	public DateAxis getDomainAxis() {

		if (domainAxis == null) {

			domainAxis = new DateAxis();
			domainAxis.setLowerMargin(0.02);
			domainAxis.setUpperMargin(0.02);

			Font labelFont = new Font("Arial", Font.PLAIN, 10);
			domainAxis.setLabelFont(labelFont);
		}

		return domainAxis;
	}

	public void resetModel() {

		timestampColumnIndex = -1;
		getLogEntryColumnList().clear();
		getLogEntryIndexList().clear();
		getLogEntryMap().clear();
		getVisibleColumnIndexList().clear();
		getColumnFilterMap().clear();
		getTimeLogEntryKeyMap().clear();
		getLogEntryColumnIndexMap().clear();

		TimeZone displayTimeZone = displayDateFormat.getTimeZone();
		Locale locale = getLocale();
		updateDomainAxis(displayTimeZone, locale);
	}

	public void addLogEntry(LogEntry logEntry, ArrayList<String> logEntryValueList) {

		Integer logEntryKey = logEntry.getKey();
		Date logEntryDate = logEntry.getLogEntryDate();
		long logEntryTime = logEntryDate.getTime();

		List<Integer> logEntryIndexList = getLogEntryIndexList();
		logEntryIndexList.add(logEntry.getKey());

		Map<Integer, LogEntry> logEntryMap = getLogEntryMap();
		logEntryMap.put(logEntryKey, logEntry);

		Map<Long, Integer> timeLogEntryKeyMap = getTimeLogEntryKeyMap();
		timeLogEntryKeyMap.put(logEntryTime, logEntryKey);

		updateColumnFilterMap(logEntry.getKey(), logEntryValueList);

		long lowerDomainRange = getLowerDomainRange();
		long upperDomainRange = getUpperDomainRange();

		if (lowerDomainRange == -1) {
			lowerDomainRange = logEntryTime - 1;
		} else {
			lowerDomainRange = Math.min(lowerDomainRange, logEntryTime);
		}

		if (upperDomainRange == -1) {
			upperDomainRange = logEntryTime;
		} else {
			upperDomainRange = Math.max(upperDomainRange, logEntryTime);
		}

		setLowerDomainRange(lowerDomainRange);
		setUpperDomainRange(upperDomainRange);

		postProcess(logEntry, logEntryValueList);
	}

	public long getLowerDomainRange() {
		return lowerDomainRange;
	}

	protected void setLowerDomainRange(long lowerDomainRange) {
		this.lowerDomainRange = lowerDomainRange;
	}

	public long getUpperDomainRange() {
		return upperDomainRange;
	}

	protected void setUpperDomainRange(long upperDomainRange) {
		this.upperDomainRange = upperDomainRange;
	}

	private void initialiseVisibleFilterableColumnIndexList() {

		Map<LogEntryColumn, Integer> logEntryColumnIndexMap = getLogEntryColumnIndexMap();
		logEntryColumnIndexMap.clear();

		ArrayList<Integer> visibleColumnIndexList = getVisibleColumnIndexList();
		visibleColumnIndexList.clear();

		Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<Integer>>> columnFilterMap = getColumnFilterMap();
		columnFilterMap.clear();

		ArrayList<String> logEntryColumnList = getLogEntryColumnList();

		int size = logEntryColumnList.size();

		for (int columnIndex = 0; columnIndex < size; columnIndex++) {

			String column = logEntryColumnList.get(columnIndex);

			LogEntryColumn logEntryColumn = null;

			logEntryColumn = LogEntryColumn.getTableColumnById(column);

			if (logEntryColumn != null) {

				logEntryColumnIndexMap.put(logEntryColumn, columnIndex);

				if (logEntryColumn.isVisibleColumn()) {
					visibleColumnIndexList.add(columnIndex);

					// preventing unnecessary buildup of filter map
					if (logEntryColumn.isFilterable()) {

						FilterColumn fc = new FilterColumn(columnIndex);

						fc.setColumnFilterEnabled(true);

						columnFilterMap.put(fc, null);
					}
				}

			} else {
				// unknown column name, default is show it
				visibleColumnIndexList.add(columnIndex);
			}
		}

		timestampColumnIndex = logEntryColumnIndexMap.get(LogEntryColumn.TIMESTAMP);
	}

	private void updateColumnFilterMap(Integer logEntryIndex, ArrayList<String> logEntryValueList) {

		if (logEntryValueList != null) {

			Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<Integer>>> columnFilterMap = getColumnFilterMap();

			Iterator<FilterColumn> fcIterator = columnFilterMap.keySet().iterator();

			while (fcIterator.hasNext()) {

				FilterColumn filterColumn = fcIterator.next();
				List<CheckBoxMenuItemPopupEntry<Integer>> columnFilterEntryList = columnFilterMap.get(filterColumn);

				if (columnFilterEntryList == null) {
					columnFilterEntryList = new ArrayList<CheckBoxMenuItemPopupEntry<Integer>>();
					columnFilterMap.put(filterColumn, columnFilterEntryList);
				}

				int columnIndex = filterColumn.getIndex();

				String logEntryValue = logEntryValueList.get(columnIndex);

				if (logEntryValue == null) {
					logEntryValue = FilterTableModel.NULL_STR;
				} else if ("".equals(logEntryValue)) {
					logEntryValue = FilterTableModel.EMPTY_STR;
				}

				CheckBoxMenuItemPopupEntry<Integer> columnFilterEntry;

				CheckBoxMenuItemPopupEntry<Integer> searchKey = new CheckBoxMenuItemPopupEntry<Integer>(logEntryValue);

				int index = columnFilterEntryList.indexOf(searchKey);

				if (index == -1) {
					columnFilterEntry = new CheckBoxMenuItemPopupEntry<Integer>(logEntryValue);
					columnFilterEntryList.add(columnFilterEntry);
				} else {
					columnFilterEntry = columnFilterEntryList.get(index);
				}

				columnFilterEntry.addRowIndex(logEntryIndex);
			}
		}
	}

	public int getTotalRowCount() {
		return logEntryMap.size();
	}

	public LogEntry getLogEntry(Integer logEntryIndex) {
		return logEntryMap.get(logEntryIndex);
	}

	public String getLogEntryColumn(int columnIndex) {
		return logEntryColumnList.get(columnIndex);
	}

	public String getFormattedLogEntryValue(LogEntry logEntry, LogEntryColumn logEntryColumn) {

		Map<LogEntryColumn, Integer> logEntryColumnIndexMap = getLogEntryColumnIndexMap();

		int columnIndex = -1;

		Integer logEntryColumnIndex = logEntryColumnIndexMap.get(logEntryColumn);

		if (logEntryColumnIndex != null) {
			columnIndex = logEntryColumnIndex.intValue();
		}

		return getFormattedLogEntryValue(logEntry, columnIndex);

	}

	public String getFormattedLogEntryValue(LogEntry logEntry, int column) {

		int logEntryIndex = logEntry.getKey();

		String text = null;

		if (column >= 0) {

			try {

				if (column == timestampColumnIndex) {

					Date logEntryDate = logEntry.getLogEntryDate();

					if (logEntryDate != null) {
						text = displayDateFormat.format(logEntryDate);
					}
				} else {

					ArrayList<String> logEntryValueList = null;

					if ((lastQueriedLogEntryIndex != logEntryIndex) || (lastQueriedLogEntryValueList == null)) {

						logEntryValueList = logEntry.getLogEntryValueList();

					} else {

						logEntryValueList = lastQueriedLogEntryValueList;
					}

					lastQueriedLogEntryIndex = logEntryIndex;
					lastQueriedLogEntryValueList = logEntryValueList;

					text = logEntryValueList.get(column);
				}
			} catch (Exception e) {
				LOG.error("Error in getting formatted log entry value for index: " + logEntryIndex, e);
			}

			// only show data of limited length in the table column
			if ((text != null) && (text.length() > TABLE_DATA_LENGTH)) {
				text = text.substring(0, TABLE_DATA_LENGTH);
				text = text + "...";
			}
		}

		return text;
	}

	private void updateDomainAxis(TimeZone displayTimeZone, Locale locale) {

		DateAxis domainAxis = getDomainAxis();

		if (displayTimeZone != null) {
			String abbrTimeZoneStr = displayTimeZone.getDisplayName(displayTimeZone.useDaylightTime(), TimeZone.SHORT);

			String label = "Time (" + abbrTimeZoneStr + ")";

			domainAxis.setLabel(label);
			domainAxis.setTimeZone(displayTimeZone);
		}

		if (locale != null) {
			domainAxis.setLocale(locale);
		}

	}
}
