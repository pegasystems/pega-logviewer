/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import java.awt.Font;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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

    private ArrayList<LogEntryColumn> logEntryColumnList;

    private List<LogEntryKey> logEntryKeyList;

    // large files cause hanging during search, because of getIndex call, hence building a map to store these
    private HashMap<LogEntryKey, Integer> keyIndexMap;

    private Map<LogEntryKey, LogEntry> logEntryMap;

    private Map<LogEntryColumn, Integer> logEntryColumnIndexMap;

    private Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<LogEntryKey>>> columnFilterMap;

    private ArrayList<Integer> visibleColumnIndexList;

    private LogEntryKey lastQueriedLogEntryKey;

    private ArrayList<String> lastQueriedLogEntryValueList;

    private Map<Long, LogEntryKey> timeLogEntryKeyMap;

    private Map<Integer, LogEntryKey> lineNoLogEntryKeyMap;

    private int timestampColumnIndex;

    private DateAxis domainAxis;

    private long lowerDomainRange;

    private long upperDomainRange;

    private boolean rebuildLogTimeSeriesCollectionSet;

    public abstract void rebuildLogTimeSeriesCollectionSet(Locale locale) throws Exception;

    public abstract Set<LogSeriesCollection> getLogTimeSeriesCollectionSet(boolean filtered, Locale locale)
            throws Exception;

    public abstract Set<LogIntervalMarker> getLogIntervalMarkerSet();

    public abstract LogEntryColumn[] getReportTableColumns();

    public abstract String getTypeName();

    protected abstract void postProcess(LogEntry logEntry, ArrayList<String> logEntryValueList, Charset charset,
            Locale locale);

    public LogEntryModel(DateFormat dateFormat) {
        this(dateFormat, dateFormat.getTimeZone());
    }

    public LogEntryModel(DateFormat dateFormat, TimeZone displayTimezone) {

        this.modelDateFormat = dateFormat;
        this.lowerDomainRange = -1;
        this.upperDomainRange = -1;
        this.rebuildLogTimeSeriesCollectionSet = false;

        logEntryColumnList = new ArrayList<>();

        displayDateFormat = new SimpleDateFormat(DateTimeUtilities.DATEFORMAT_ISO8601);

        if (displayTimezone == null) {
            displayTimezone = dateFormat.getTimeZone();
        }

        displayDateFormat.setTimeZone(displayTimezone);

        updateDomainAxis(displayTimezone, null);

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

    public TimeZone getDisplayDateFormatTimeZone() {

        DateFormat displayDateFormat = getDisplayDateFormat();

        TimeZone displayDateFormatTimeZone = displayDateFormat.getTimeZone();

        return displayDateFormatTimeZone;
    }

    public void setDisplayDateFormatTimeZone(TimeZone displayTimeZone) {

        DateFormat displayDateFormat = getDisplayDateFormat();

        displayDateFormat.setTimeZone(displayTimeZone);

        updateDomainAxis(displayTimeZone, null);

        // rebuild graphs
        setRebuildLogTimeSeriesCollectionSet(true);
    }

    public List<LogEntryColumn> getLogEntryColumnList() {
        return logEntryColumnList;
    }

    public void updateLogEntryColumnList(List<LogEntryColumn> lecList) {
        logEntryColumnList.clear();
        logEntryColumnList.addAll(lecList);
        initialiseVisibleFilterableColumnIndexList();
    }

    public List<LogEntryKey> getLogEntryKeyList() {

        if (logEntryKeyList == null) {
            logEntryKeyList = new ArrayList<LogEntryKey>();
        }

        return logEntryKeyList;
    }

    public HashMap<LogEntryKey, Integer> getKeyIndexMap() {

        if (keyIndexMap == null) {
            keyIndexMap = new HashMap<>();
        }

        return keyIndexMap;
    }

    public Map<LogEntryKey, LogEntry> getLogEntryMap() {

        if (logEntryMap == null) {
            logEntryMap = new HashMap<LogEntryKey, LogEntry>();
        }

        return logEntryMap;
    }

    public ArrayList<Integer> getVisibleColumnIndexList() {

        if (visibleColumnIndexList == null) {
            visibleColumnIndexList = new ArrayList<Integer>();
        }

        return visibleColumnIndexList;
    }

    public Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<LogEntryKey>>> getColumnFilterMap() {

        if (columnFilterMap == null) {
            columnFilterMap = new TreeMap<FilterColumn, List<CheckBoxMenuItemPopupEntry<LogEntryKey>>>();
        }

        return columnFilterMap;
    }

    private Map<Long, LogEntryKey> getTimeLogEntryKeyMap() {

        if (timeLogEntryKeyMap == null) {
            timeLogEntryKeyMap = new HashMap<>();
        }

        return timeLogEntryKeyMap;
    }

    private Map<Integer, LogEntryKey> getLineNoLogEntryKeyMap() {

        if (lineNoLogEntryKeyMap == null) {
            lineNoLogEntryKeyMap = new HashMap<>();
        }

        return lineNoLogEntryKeyMap;
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
        getKeyIndexMap().clear();
        getLogEntryKeyList().clear();
        getLogEntryMap().clear();
        getVisibleColumnIndexList().clear();
        getColumnFilterMap().clear();
        getTimeLogEntryKeyMap().clear();
        getLineNoLogEntryKeyMap().clear();
        getLogEntryColumnIndexMap().clear();

        // TimeZone displayDateFormatTimeZone = getDisplayDateFormatTimeZone();
        // Locale locale = getLocale();
        // updateDomainAxis(displayDateFormatTimeZone, locale);
    }

    public void addLogEntry(LogEntry logEntry, ArrayList<String> logEntryValueList, Charset charset, Locale locale) {

        LogEntryKey logEntryKey = logEntry.getKey();
        int lineNo = logEntryKey.getLineNo();
        long logEntryTime = logEntryKey.getTimestamp();

        List<LogEntryKey> logEntryKeyList = getLogEntryKeyList();
        logEntryKeyList.add(logEntryKey);

        // removing because timestamp sorting changes the key order.
        // HashMap<LogEntryKey, Integer> keyIndexMap = getKeyIndexMap();
        // keyIndexMap.put(logEntryKey, logEntryKeyList.size() - 1);

        Map<LogEntryKey, LogEntry> logEntryMap = getLogEntryMap();
        logEntryMap.put(logEntryKey, logEntry);

        Map<Long, LogEntryKey> timeLogEntryKeyMap = getTimeLogEntryKeyMap();
        timeLogEntryKeyMap.put(logEntryTime, logEntryKey);

        Map<Integer, LogEntryKey> lineNoLogEntryKeyMap = getLineNoLogEntryKeyMap();
        lineNoLogEntryKeyMap.put(lineNo, logEntryKey);

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

        postProcess(logEntry, logEntryValueList, charset, locale);
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

    protected boolean isRebuildLogTimeSeriesCollectionSet() {
        return rebuildLogTimeSeriesCollectionSet;
    }

    public void setRebuildLogTimeSeriesCollectionSet(boolean rebuildLogTimeSeriesCollectionSet) {
        this.rebuildLogTimeSeriesCollectionSet = rebuildLogTimeSeriesCollectionSet;
    }

    private void initialiseVisibleFilterableColumnIndexList() {

        Map<LogEntryColumn, Integer> logEntryColumnIndexMap = getLogEntryColumnIndexMap();
        logEntryColumnIndexMap.clear();

        ArrayList<Integer> visibleColumnIndexList = getVisibleColumnIndexList();
        visibleColumnIndexList.clear();

        Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<LogEntryKey>>> columnFilterMap = getColumnFilterMap();
        columnFilterMap.clear();

        List<LogEntryColumn> lecList = getLogEntryColumnList();

        int columnIndex = 0;

        for (LogEntryColumn logEntryColumn : lecList) {

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

            if (logEntryColumn.equals(LogEntryColumn.TIMESTAMP)) {
                timestampColumnIndex = columnIndex;
            }

            columnIndex++;
        }

    }

    private void updateColumnFilterMap(LogEntryKey logEntryKey, ArrayList<String> logEntryValueList) {

        if (logEntryValueList != null) {

            Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<LogEntryKey>>> columnFilterMap = getColumnFilterMap();

            Iterator<FilterColumn> fcIterator = columnFilterMap.keySet().iterator();

            while (fcIterator.hasNext()) {

                FilterColumn filterColumn = fcIterator.next();
                List<CheckBoxMenuItemPopupEntry<LogEntryKey>> columnFilterEntryList = columnFilterMap.get(filterColumn);

                if (columnFilterEntryList == null) {
                    columnFilterEntryList = new ArrayList<CheckBoxMenuItemPopupEntry<LogEntryKey>>();
                    columnFilterMap.put(filterColumn, columnFilterEntryList);
                }

                int columnIndex = filterColumn.getIndex();

                String logEntryValue = logEntryValueList.get(columnIndex);

                if (logEntryValue == null) {
                    logEntryValue = FilterTableModel.NULL_STR;
                } else if ("".equals(logEntryValue)) {
                    logEntryValue = FilterTableModel.EMPTY_STR;
                }

                CheckBoxMenuItemPopupEntry<LogEntryKey> columnFilterEntry;

                CheckBoxMenuItemPopupEntry<LogEntryKey> searchKey = new CheckBoxMenuItemPopupEntry<LogEntryKey>(
                        logEntryValue);

                int index = columnFilterEntryList.indexOf(searchKey);

                if (index == -1) {
                    columnFilterEntry = new CheckBoxMenuItemPopupEntry<LogEntryKey>(logEntryValue);
                    columnFilterEntryList.add(columnFilterEntry);
                } else {
                    columnFilterEntry = columnFilterEntryList.get(index);
                }

                columnFilterEntry.addRowIndex(logEntryKey);
            }
        }
    }

    public int getTotalRowCount() {
        return logEntryMap.size();
    }

    public LogEntry getLogEntry(LogEntryKey logEntryKey) {
        return logEntryMap.get(logEntryKey);
    }

    public String getLogEntryColumnName(int columnIndex) {

        List<LogEntryColumn> lecList = getLogEntryColumnList();

        return lecList.get(columnIndex).getColumnId();
    }

    public int getLogEntryColumnIndex(LogEntryColumn logEntryColumn) {

        Map<LogEntryColumn, Integer> logEntryColumnIndexMap = getLogEntryColumnIndexMap();

        return logEntryColumnIndexMap.get(logEntryColumn);
    }

    public String getFormattedLogEntryValue(LogEntry logEntry, LogEntryColumn logEntryColumn) {

        int logEntryColumnIndex = getLogEntryColumnIndex(logEntryColumn);

        return getFormattedLogEntryValue(logEntry, logEntryColumnIndex);

    }

    public String getFormattedLogEntryValue(LogEntry logEntry, int column) {

        LogEntryKey logEntryKey = logEntry.getKey();

        String text = null;

        if (column >= 0) {

            try {

                if (column == timestampColumnIndex) {

                    long logEntryTime = logEntryKey.getTimestamp();

                    if (logEntryTime != -1) {

                        DateFormat displayDateFormat = getDisplayDateFormat();

                        text = displayDateFormat.format(new Date(logEntryTime));
                    }
                } else {

                    ArrayList<String> logEntryValueList = null;

                    if ((lastQueriedLogEntryKey == null) || (!lastQueriedLogEntryKey.equals(logEntryKey))
                            || (lastQueriedLogEntryValueList == null)) {

                        logEntryValueList = logEntry.getLogEntryValueList();

                    } else {

                        logEntryValueList = lastQueriedLogEntryValueList;
                    }

                    lastQueriedLogEntryKey = logEntryKey;
                    lastQueriedLogEntryValueList = logEntryValueList;

                    text = logEntryValueList.get(column);
                }
            } catch (Exception e) {
                LOG.error("Error in getting formatted log entry value for index: " + logEntryKey, e);
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

    public String getLogEntryTimeDisplayString(LogEntryKey logEntryKey) {

        String logEntryTimeDisplayString = null;

        long logEntryTime = logEntryKey.getTimestamp();

        if (logEntryTime != -1) {

            DateFormat displayDateFormat = getDisplayDateFormat();

            logEntryTimeDisplayString = displayDateFormat.format(new Date(logEntryTime));
        }

        return logEntryTimeDisplayString;
    }

    public void clearLogEntrySearchResults() {

        Map<LogEntryKey, LogEntry> logEntryMap = getLogEntryMap();

        if (logEntryMap != null) {

            for (Map.Entry<LogEntryKey, LogEntry> entry : logEntryMap.entrySet()) {
                LogEntry logEntry = entry.getValue();
                logEntry.setSearchFound(false);
            }
        }
    }

    public LogEntryKey getClosestLogEntryKeyForTime(long time) {

        LogEntryKey logEntryKey = null;

        Map<Long, LogEntryKey> timeLogEntryKeyMap = getTimeLogEntryKeyMap();

        List<Long> timeKeyList = new ArrayList<>(timeLogEntryKeyMap.keySet());

        Collections.sort(timeKeyList);

        long firstTime = timeKeyList.get(0);

        long lastTime = timeKeyList.get(timeKeyList.size() - 1);

        if ((time >= firstTime) && (time <= lastTime)) {

            int index = Collections.binarySearch(timeKeyList, time);
            Long timeKey = null;

            if (index < 0) {
                index = (index * -1) - 1;

                // find closest index
                if (index > 0) {

                    int prevIndex = index - 1;
                    long prevtimeKey = timeKeyList.get(prevIndex);
                    timeKey = timeKeyList.get(index);

                    long diff1 = time - prevtimeKey;
                    long diff2 = timeKey - time;

                    if (diff1 < diff2) {
                        timeKey = prevtimeKey;
                    }

                } else {
                    timeKey = timeKeyList.get(index);
                }
            } else {
                // exact match
                timeKey = timeKeyList.get(index);
            }

            logEntryKey = timeLogEntryKeyMap.get(timeKey);

        }

        return logEntryKey;
    }

    public LogEntryKey getClosestLogEntryKeyForLineNo(int lineNo) {

        LogEntryKey logEntryKey = null;

        Map<Integer, LogEntryKey> lineNoLogEntryKeyMap = getLineNoLogEntryKeyMap();

        List<Integer> lineNoKeyList = new ArrayList<>(lineNoLogEntryKeyMap.keySet());

        Collections.sort(lineNoKeyList);

        int firstLineNo = lineNoKeyList.get(0);

        int lastLineNo = lineNoKeyList.get(lineNoKeyList.size() - 1);

        if ((lineNo >= firstLineNo) && (lineNo <= lastLineNo)) {

            int index = Collections.binarySearch(lineNoKeyList, lineNo);
            Integer lineNoKey = null;

            if (index < 0) {
                index = (index * -1) - 1;

                // find closest index
                if (index > 0) {

                    int prevIndex = index - 1;
                    int prevLineNoKey = lineNoKeyList.get(prevIndex);
                    lineNoKey = lineNoKeyList.get(index);

                    int diff1 = lineNo - prevLineNoKey;
                    int diff2 = lineNoKey - lineNo;

                    if (diff1 < diff2) {
                        lineNoKey = prevLineNoKey;
                    }

                } else {
                    lineNoKey = lineNoKeyList.get(index);
                }
            } else {
                // exact match
                lineNoKey = lineNoKeyList.get(index);
            }

            logEntryKey = lineNoLogEntryKeyMap.get(lineNoKey);

        }

        return logEntryKey;
    }
}
