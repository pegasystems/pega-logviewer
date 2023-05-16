/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeriesDataItem;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class Log4jLogEntryModel extends LogEntryModel {

    private static final Log4j2Helper LOG = new Log4j2Helper(Log4jLogEntryModel.class);

    private static final String TSC_MEMORY = "Memory (MB)";

    public static final String TS_TOTAL_MEMORY = "Total Memory";

    public static final String TS_FREE_MEMORY = "Free Memory";

    public static final String TS_USED_MEMORY = "Used Memory";

    public static final String TS_SHARED_PAGE_MEMORY = "Shared Page Memory";

    public static final String TS_REQUESTOR_COUNT = "Requestor Count";

    public static final String TS_TOTAL_CPU_SECONDS = "Total CPU Seconds";

    public static final String TS_NUMBER_OF_THREADS = "Number Of Threads";

    public static final String IM_SYSTEM_START = "System Start";

    public static final String IM_THREAD_DUMP = "Thread Dump";

    public static final String IM_EXCEPTIONS = "Exceptions";

    private List<SystemStart> systemStartList;

    private List<HazelcastMembership> hazelcastMembershipList;

    private Map<String, List<LogEntryKey>> exceptionClassLogEntryIndexMap;

    private List<LogEntryKey> threadDumpLogEntryIndexList;

    private Map<String, LogSeriesCollection> overallLogTimeSeriesCollectionMap;

    private Map<String, LogSeriesCollection> overallCustomMeasureLogTimeSeriesCollectionMap;

    private Map<String, LogIntervalMarker> overallLogIntervalMarkerMap;

    private Set<LogIntervalMarker> logIntervalMarkerSet;

    private List<Log4jMeasurePattern> customMeasurePatternList;

    private MasterAgentSystemPattern masterAgentSystemPattern;

    public Log4jLogEntryModel(DateFormat dateFormat, TimeZone displayTimezone) {

        super(dateFormat, displayTimezone);

        systemStartList = new ArrayList<>();
        hazelcastMembershipList = new ArrayList<>();

        exceptionClassLogEntryIndexMap = new HashMap<>();
        threadDumpLogEntryIndexList = new ArrayList<>();

        // log series collection is not build upfront in rules log.
        setRebuildLogTimeSeriesCollectionSet(true);

        CustomMeasurePatternProvider cmpProvider = CustomMeasurePatternProvider.getInstance();

        customMeasurePatternList = cmpProvider.getCustomMeasurePatternList();

        masterAgentSystemPattern = null;
    }

    @Override
    protected void postProcess(LogEntry logEntry, ArrayList<String> logEntryValueList, Charset charset, Locale locale) {

        // for log file, the timezone is set later in the parsing process. this then forces the rebuild for first time
        // getLogTimeSeriesCollectionSet is called, hence removing from below

        // Log4jLogEntry log4jLogEntry = (Log4jLogEntry) logEntry;
        //
        // Map<String, LogSeriesCollection> overallLogTimeSeriesCollectionMap;
        // overallLogTimeSeriesCollectionMap = getOverallLogTimeSeriesCollectionMap();
        //
        // Map<String, LogSeriesCollection> overallCustomMeasureLogTimeSeriesCollectionMap;
        // overallCustomMeasureLogTimeSeriesCollectionMap = getOverallCustomMeasureLogTimeSeriesCollectionMap();
        //
        // Map<String, LogIntervalMarker> overallLogIntervalMarkerMap;
        // overallLogIntervalMarkerMap = getOverallLogIntervalMarkerMap();
        //
        // processLogSeriesCollection(overallLogTimeSeriesCollectionMap, log4jLogEntry, logEntryValueList, locale);
        //
        // processCustomMeasureLogSeriesCollection(overallCustomMeasureLogTimeSeriesCollectionMap, log4jLogEntry,
        // logEntryValueList, locale);
        //
        // processLogIntervalMarker(overallLogIntervalMarkerMap, log4jLogEntry);
    }

    private void processLogSeriesCollection(Map<String, LogSeriesCollection> logTimeSeriesCollectionMap,
            Log4jLogEntry log4jLogEntry, ArrayList<String> logEntryValueList, Locale locale) {

        long logEntryTime = log4jLogEntry.getKey().getTimestamp();

        // logEntryTime can be -1 in case of corrupted log file
        if (logEntryTime != -1) {

            boolean sysdateEntry = log4jLogEntry.isSysdateEntry();

            if (sysdateEntry) {

                DateFormat modelDateFormat = getModelDateFormat();
                TimeZone timezone = modelDateFormat.getTimeZone();

                NumberFormat numberFormat = NumberFormat.getInstance(locale);

                // logEntryValueList will be null in case of reload
                if (logEntryValueList == null) {
                    logEntryValueList = log4jLogEntry.getLogEntryValueList();
                }

                Map<LogEntryColumn, Integer> logEntryColumnIndexMap = getLogEntryColumnIndexMap();
                int messageIndex = logEntryColumnIndexMap.get(LogEntryColumn.MESSAGE);

                String systemStr = logEntryValueList.get(messageIndex);

                // on some systems like GC, there are multiple spaces
                // before '%m%n' pattern, for ex ' - %m%n'.
                // this causes the system string to have a additional space
                // in the front.hence it is required to
                // trim the message to match the entries below
                systemStr = systemStr.trim();

                if (masterAgentSystemPattern == null) {
                    masterAgentSystemPattern = MasterAgentSystemPattern.getMasterAgentSystemPattern(systemStr);
                }

                if (masterAgentSystemPattern != null) {

                    Pattern pattern = masterAgentSystemPattern.getPattern();

                    Matcher matcher = pattern.matcher(systemStr);

                    if (matcher.matches()) {

                        Map<String, Color> logTimeSeriesColorMap = LogTimeSeriesColor.getLogTimeSeriesColorMap();

                        ValueMarker thresholdMarker = null;
                        boolean showCount = false;
                        boolean defaultShowLogTimeSeries = true;

                        Map<String, Integer> fieldNameGroupIndexMap;
                        fieldNameGroupIndexMap = masterAgentSystemPattern.getFieldNameGroupIndexMap();

                        List<String> fieldNameList = new ArrayList<>(fieldNameGroupIndexMap.keySet());
                        List<String> valueStrList = new ArrayList<>();

                        for (String fieldName : fieldNameList) {

                            int groupIndex = fieldNameGroupIndexMap.get(fieldName);
                            String valueStr = matcher.group(groupIndex);
                            valueStrList.add(valueStr);
                        }

                        String timeSeriesName;
                        int fieldIndex;

                        // TOTAL_MEMORY
                        timeSeriesName = TS_TOTAL_MEMORY;
                        fieldIndex = fieldNameList.indexOf(timeSeriesName);

                        double totalMemory = -1;

                        if (fieldIndex != -1) {

                            String totalMemoryStr = null;

                            try {

                                totalMemoryStr = valueStrList.get(fieldIndex);
                                totalMemory = numberFormat.parse(totalMemoryStr).doubleValue();

                                // convert to MB
                                totalMemory = totalMemory / (1024 * 1024);

                                Color color = logTimeSeriesColorMap.get(timeSeriesName);

                                processLogTimeSeries(logTimeSeriesCollectionMap, TSC_MEMORY, logEntryTime, totalMemory,
                                        timeSeriesName, color, thresholdMarker, showCount, defaultShowLogTimeSeries,
                                        timezone, locale);

                            } catch (Exception e) {
                                LOG.error("Error adding total memory to map:" + totalMemoryStr, e);
                            }
                        }

                        // FREE_MEMORY / USED_MEMORY
                        timeSeriesName = TS_FREE_MEMORY;
                        fieldIndex = fieldNameList.indexOf(timeSeriesName);

                        if (fieldIndex != -1) {

                            String freeMemoryStr = null;

                            try {

                                freeMemoryStr = valueStrList.get(fieldIndex);
                                double freeMemory = numberFormat.parse(freeMemoryStr).doubleValue();

                                // convert to MB
                                freeMemory = freeMemory / (1024 * 1024);
                                double timeSeriesValue = -1;

                                if (totalMemory != -1) {
                                    timeSeriesName = TS_USED_MEMORY;
                                    timeSeriesValue = (totalMemory - freeMemory);
                                } else {
                                    timeSeriesValue = freeMemory;
                                }

                                Color color = logTimeSeriesColorMap.get(timeSeriesName);

                                processLogTimeSeries(logTimeSeriesCollectionMap, TSC_MEMORY, logEntryTime,
                                        timeSeriesValue, timeSeriesName, color, thresholdMarker, showCount,
                                        defaultShowLogTimeSeries, timezone, locale);

                            } catch (Exception e) {
                                LOG.error("Error adding free memory to map:" + freeMemoryStr, e);
                            }
                        }

                        // TOTAL_CPU_SECONDS
                        timeSeriesName = TS_TOTAL_CPU_SECONDS;
                        fieldIndex = fieldNameList.indexOf(timeSeriesName);

                        if (fieldIndex != -1) {

                            String totalCPUStr = null;

                            try {

                                totalCPUStr = valueStrList.get(fieldIndex);
                                double totalCPU = numberFormat.parse(totalCPUStr).doubleValue();

                                Color color = logTimeSeriesColorMap.get(timeSeriesName);

                                processLogTimeSeries(logTimeSeriesCollectionMap, timeSeriesName, logEntryTime, totalCPU,
                                        timeSeriesName, color, thresholdMarker, showCount, defaultShowLogTimeSeries,
                                        timezone, locale);

                            } catch (Exception e) {
                                LOG.error("Error adding total cpu to map:" + totalCPUStr, e);
                            }
                        }

                        // REQUESTOR_COUNT
                        timeSeriesName = TS_REQUESTOR_COUNT;
                        fieldIndex = fieldNameList.indexOf(timeSeriesName);

                        if (fieldIndex != -1) {

                            String requestorCountStr = null;

                            try {

                                requestorCountStr = valueStrList.get(fieldIndex);
                                double requestorCount = numberFormat.parse(requestorCountStr).doubleValue();

                                Color color = logTimeSeriesColorMap.get(timeSeriesName);

                                processLogTimeSeries(logTimeSeriesCollectionMap, timeSeriesName, logEntryTime,
                                        requestorCount, timeSeriesName, color, thresholdMarker, showCount,
                                        defaultShowLogTimeSeries, timezone, locale);

                            } catch (Exception e) {
                                LOG.error("Error adding requestor count to map:" + requestorCountStr, e);
                            }
                        }

                        // SHARED_PAGE_MEMORY
                        timeSeriesName = TS_SHARED_PAGE_MEMORY;
                        fieldIndex = fieldNameList.indexOf(timeSeriesName);

                        if (fieldIndex != -1) {

                            String sharedPageMemoryStr = null;

                            try {

                                sharedPageMemoryStr = valueStrList.get(fieldIndex);
                                double sharedPageMemory = numberFormat.parse(sharedPageMemoryStr).doubleValue();

                                Color color = logTimeSeriesColorMap.get(timeSeriesName);

                                processLogTimeSeries(logTimeSeriesCollectionMap, timeSeriesName, logEntryTime,
                                        sharedPageMemory, timeSeriesName, color, thresholdMarker, showCount,
                                        defaultShowLogTimeSeries, timezone, locale);

                            } catch (Exception e) {
                                LOG.error("Error adding shared page memory to map:" + sharedPageMemoryStr, e);
                            }
                        }

                        // NUMBER_OF_THREADS
                        timeSeriesName = TS_NUMBER_OF_THREADS;
                        fieldIndex = fieldNameList.indexOf(timeSeriesName);

                        if (fieldIndex != -1) {

                            String numberOfThreadsStr = null;

                            try {

                                numberOfThreadsStr = valueStrList.get(fieldIndex);
                                double numberOfThreads = numberFormat.parse(numberOfThreadsStr).doubleValue();

                                Color color = logTimeSeriesColorMap.get(timeSeriesName);

                                processLogTimeSeries(logTimeSeriesCollectionMap, timeSeriesName, logEntryTime,
                                        numberOfThreads, timeSeriesName, color, thresholdMarker, showCount,
                                        defaultShowLogTimeSeries, timezone, locale);

                            } catch (Exception e) {
                                LOG.error("Error adding number of threads to map:" + numberOfThreadsStr, e);
                            }
                        }

                    } else {
                        LOG.error("unable to match master agent system string: " + systemStr);
                    }

                } else {
                    LOG.error("unable to get MasterAgentSystemPattern for system string: " + systemStr);
                }
            }
        }
    }

    // TODO possibly have all the pattern externalise as measures?
    private void processCustomMeasureLogSeriesCollection(Map<String, LogSeriesCollection> logTimeSeriesCollectionMap,
            Log4jLogEntry log4jLogEntry, ArrayList<String> logEntryValueList, Locale locale) {

        long logEntryTime = log4jLogEntry.getKey().getTimestamp();

        // logEntryTime can be -1 in case of corrupted log file
        if (logEntryTime != -1) {

            boolean sysdateEntry = log4jLogEntry.isSysdateEntry();
            boolean log4jLogExceptionEntry = (log4jLogEntry instanceof Log4jLogExceptionEntry);
            boolean log4jLogRequestorLockEntry = (log4jLogEntry instanceof Log4jLogRequestorLockEntry);
            boolean log4jLogThreadDumpEntry = (log4jLogEntry instanceof Log4jLogThreadDumpEntry);
            boolean log4jLogSystemStartEntry = (log4jLogEntry instanceof Log4jLogSystemStartEntry);

            if ((!sysdateEntry) && (!log4jLogExceptionEntry) && (!log4jLogRequestorLockEntry)
                    && (!log4jLogThreadDumpEntry) && (!log4jLogSystemStartEntry)) {

                int messageIndex = getLogEntryColumnIndex(LogEntryColumn.MESSAGE);

                DateFormat modelDateFormat = getModelDateFormat();
                TimeZone timezone = modelDateFormat.getTimeZone();

                NumberFormat numberFormat = NumberFormat.getInstance(locale);

                ValueMarker thresholdMarker = null;
                boolean showCount = false;
                boolean defaultShowLogTimeSeries = true;

                // logEntryValueList will be null in case of reload
                if (logEntryValueList == null) {
                    logEntryValueList = log4jLogEntry.getLogEntryValueList();
                }

                // process custom measures
                for (Log4jMeasurePattern log4jMeasurePattern : customMeasurePatternList) {

                    String name = log4jMeasurePattern.getName();
                    Pattern pattern = log4jMeasurePattern.getPattern();

                    String messageStr = logEntryValueList.get(messageIndex);

                    Matcher matcher = pattern.matcher(messageStr);

                    String customMeasureStr = null;

                    if (matcher.matches()) {

                        customMeasureStr = matcher.group(3);

                        if (customMeasureStr != null) {

                            double customMeasure;

                            try {
                                customMeasure = numberFormat.parse(customMeasureStr).doubleValue();

                                Color color = null;

                                processLogTimeSeries(logTimeSeriesCollectionMap, name, logEntryTime, customMeasure,
                                        name, color, thresholdMarker, showCount, defaultShowLogTimeSeries, timezone,
                                        locale);

                            } catch (Exception e) {
                                LOG.error("Error adding custom measure to map:" + customMeasureStr, e);
                            }
                        }
                    }
                }
            }
        }
    }

    private void processLogTimeSeries(Map<String, LogSeriesCollection> logTimeSeriesCollectionMap,
            String logSeriesCollectionName, long logEntryTime, double timeSeriesValue, String timeSeriesName,
            Color color, ValueMarker thresholdMarker, boolean showCount, boolean defaultShowLogTimeSeries,
            TimeZone timezone, Locale locale) {

        LogSeriesCollection logSeriesCollection;
        logSeriesCollection = logTimeSeriesCollectionMap.get(logSeriesCollectionName);

        if (logSeriesCollection == null) {
            logSeriesCollection = new LogSeriesCollection(logSeriesCollectionName);
            logTimeSeriesCollectionMap.put(logSeriesCollectionName, logSeriesCollection);
        }

        RegularTimePeriod regularTimePeriod;
        regularTimePeriod = new Millisecond(new Date(logEntryTime), timezone, locale);

        TimeSeriesDataItem timeSeriesDataItem;
        timeSeriesDataItem = new TimeSeriesDataItem(regularTimePeriod, timeSeriesValue);

        LogTimeSeries logSeries = (LogTimeSeries) logSeriesCollection.getLogSeries(timeSeriesName);

        if (logSeries == null) {

            if (color == null) {
                color = Color.BLACK;
            }

            logSeries = new LogTimeSeries(timeSeriesName, color, thresholdMarker, showCount, defaultShowLogTimeSeries);

            logSeriesCollection.addLogSeries(logSeries);
        }

        logSeries.addTimeSeriesDataItem(timeSeriesDataItem);

    }

    private void processLogIntervalMarker(Map<String, LogIntervalMarker> intervalMarkerMap,
            Log4jLogEntry log4jLogEntry) {

        long logEntryTime = log4jLogEntry.getKey().getTimestamp();

        // logEntryTime can be -1 in case of corrupted log file
        if (logEntryTime != -1) {

            String logIntervalMarkerName = null;

            if (log4jLogEntry instanceof Log4jLogThreadDumpEntry) {

                logIntervalMarkerName = IM_THREAD_DUMP;

                processIntervalMarker(intervalMarkerMap, logIntervalMarkerName, logEntryTime, true);

            } else if (log4jLogEntry instanceof Log4jLogSystemStartEntry) {

                logIntervalMarkerName = IM_SYSTEM_START;

                processIntervalMarker(intervalMarkerMap, logIntervalMarkerName, logEntryTime, true);

            } else if (log4jLogEntry instanceof Log4jLogExceptionEntry) {

                logIntervalMarkerName = IM_EXCEPTIONS;

                processIntervalMarker(intervalMarkerMap, logIntervalMarkerName, logEntryTime, false);
            }
        }
    }

    private void processIntervalMarker(Map<String, LogIntervalMarker> intervalMarkerMap, String logIntervalMarkerName,
            long logEntryTime, boolean defaultShowLogTimeSeries) {

        Map<String, Color> logTimeSeriesColorMap = LogTimeSeriesColor.getLogTimeSeriesColorMap();

        Color intervalMarkerColor = logTimeSeriesColorMap.get(logIntervalMarkerName);

        LogIntervalMarker logIntervalMarker = intervalMarkerMap.get(logIntervalMarkerName);

        if (logIntervalMarker == null) {

            logIntervalMarker = new LogIntervalMarker(logIntervalMarkerName, intervalMarkerColor, true,
                    defaultShowLogTimeSeries);

            intervalMarkerMap.put(logIntervalMarkerName, logIntervalMarker);
        }

        ValueMarker vm = new ValueMarker(logEntryTime, intervalMarkerColor, new BasicStroke(1.0f), intervalMarkerColor,
                new BasicStroke(1.0f), 1.0f);

        logIntervalMarker.addValueMarker(vm);
    }

    @Override
    public void setModelDateFormatTimeZone(TimeZone modelTimeZone) {

        super.setModelDateFormatTimeZone(modelTimeZone);

        LOG.info("Model timezone changed to " + modelTimeZone);

        Map<LogEntryColumn, Integer> logEntryColumnIndexMap = getLogEntryColumnIndexMap();

        int timestampColumnIndex = logEntryColumnIndexMap.get(LogEntryColumn.TIMESTAMP);

        DateFormat modelDateFormat = getModelDateFormat();

        if (timestampColumnIndex != -1) {

            // update existing entries in map.
            Map<LogEntryKey, LogEntry> logEntryMap = getLogEntryMap();

            LOG.info("Updating " + logEntryMap.size() + " records because of model timezone change");

            long lowerDomainRange = -1;
            long upperDomainRange = -1;

            List<LogEntry> updatedLogEntryList = new ArrayList<>();

            for (LogEntry logEntry : logEntryMap.values()) {

                LogEntryKey logEntryKey = logEntry.getKey();

                LogEntryData logEntryData = logEntry.getLogEntryData();

                ArrayList<String> logEntryValueList = logEntryData.getLogEntryValueList();

                String logEntryDateStr = logEntryValueList.get(timestampColumnIndex);

                try {

                    // Recalculating time when timezone becomes known
                    Date logEntryDate = modelDateFormat.parse(logEntryDateStr);

                    long logEntryTime = logEntryDate.getTime();

                    logEntryKey.setTimestamp(logEntryTime);

                    updatedLogEntryList.add(logEntry);

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

                } catch (ParseException pe) {
                    LOG.info("Date parse error: " + logEntryDateStr);
                }

            }

            // recreate the map with updated Keys.
            logEntryMap.clear();

            for (LogEntry logEntry : updatedLogEntryList) {
                logEntryMap.put(logEntry.getKey(), logEntry);
            }

            if ((lowerDomainRange != -1) && (upperDomainRange != -1)) {
                setLowerDomainRange(lowerDomainRange);
                setUpperDomainRange(upperDomainRange);
            }
        }
    }

    @Override
    public Set<LogSeriesCollection> getLogTimeSeriesCollectionSet(boolean filtered, Locale locale) throws Exception {

        if (isRebuildLogTimeSeriesCollectionSet()) {
            rebuildLogTimeSeriesCollectionSet(locale);
        }

        Set<LogSeriesCollection> logTimeSeriesCollectionSet = new TreeSet<>();

        Map<String, LogSeriesCollection> overallLogTimeSeriesCollectionMap;
        overallLogTimeSeriesCollectionMap = getOverallLogTimeSeriesCollectionMap();

        // for log4j, don't filter out master agent data points.
        logTimeSeriesCollectionSet.addAll(overallLogTimeSeriesCollectionMap.values());

        if (filtered) {

            List<LogEntryKey> logEntryKeyList = getLogEntryKeyList();
            Iterator<LogEntryKey> logEntryKeyIterator = logEntryKeyList.iterator();

            Map<String, LogSeriesCollection> customMeasureLogTimeSeriesCollectionMap;
            customMeasureLogTimeSeriesCollectionMap = new HashMap<>();

            Map<String, LogIntervalMarker> logIntervalMarkerMap;
            logIntervalMarkerMap = new HashMap<>();

            Map<LogEntryKey, LogEntry> logEntryMap = getLogEntryMap();

            while (logEntryKeyIterator.hasNext()) {

                LogEntryKey logEntryKey = logEntryKeyIterator.next();
                Log4jLogEntry log4jLogEntry = (Log4jLogEntry) logEntryMap.get(logEntryKey);

                long logEntryTime = logEntryKey.getTimestamp();

                // logEntryTime can be -1 in case of corrupted log file
                if (logEntryTime != -1) {

                    processCustomMeasureLogSeriesCollection(customMeasureLogTimeSeriesCollectionMap, log4jLogEntry,
                            null, locale);

                    processLogIntervalMarker(logIntervalMarkerMap, log4jLogEntry);
                }
            }

            logTimeSeriesCollectionSet.addAll(customMeasureLogTimeSeriesCollectionMap.values());

            Set<LogIntervalMarker> logIntervalMarkerSet = getLogIntervalMarkerSet();
            logIntervalMarkerSet.clear();

            logIntervalMarkerSet.addAll(logIntervalMarkerMap.values());

        } else {

            Map<String, LogSeriesCollection> overallCustomMeasureLogTimeSeriesCollectionMap;
            overallCustomMeasureLogTimeSeriesCollectionMap = getOverallCustomMeasureLogTimeSeriesCollectionMap();

            logTimeSeriesCollectionSet.addAll(overallCustomMeasureLogTimeSeriesCollectionMap.values());

            Map<String, LogIntervalMarker> overallLogIntervalMarkerMap;
            overallLogIntervalMarkerMap = getOverallLogIntervalMarkerMap();

            Set<LogIntervalMarker> logIntervalMarkerSet = getLogIntervalMarkerSet();
            logIntervalMarkerSet.clear();

            logIntervalMarkerSet.addAll(overallLogIntervalMarkerMap.values());
        }

        return logTimeSeriesCollectionSet;
    }

    private Map<String, LogSeriesCollection> getOverallLogTimeSeriesCollectionMap() {

        if (overallLogTimeSeriesCollectionMap == null) {
            overallLogTimeSeriesCollectionMap = new HashMap<>();
        }

        return overallLogTimeSeriesCollectionMap;
    }

    private Map<String, LogSeriesCollection> getOverallCustomMeasureLogTimeSeriesCollectionMap() {

        if (overallCustomMeasureLogTimeSeriesCollectionMap == null) {
            overallCustomMeasureLogTimeSeriesCollectionMap = new HashMap<>();
        }

        return overallCustomMeasureLogTimeSeriesCollectionMap;
    }

    private Map<String, LogIntervalMarker> getOverallLogIntervalMarkerMap() {

        if (overallLogIntervalMarkerMap == null) {
            overallLogIntervalMarkerMap = new HashMap<>();
        }

        return overallLogIntervalMarkerMap;
    }

    @Override
    public Set<LogIntervalMarker> getLogIntervalMarkerSet() {

        // logIntervalMarkerCollectionSet is populated in
        // getLogTimeSeriesCollectionSet
        // in order to save additional looping
        if (logIntervalMarkerSet == null) {
            logIntervalMarkerSet = new TreeSet<>();
        }

        return logIntervalMarkerSet;
    }

    @Override
    public String getTypeName() {
        return "Log4j";
    }

    public List<SystemStart> getSystemStartList() {
        return systemStartList;
    }

    public List<HazelcastMembership> getHazelcastMembershipList() {
        return hazelcastMembershipList;
    }

    public Map<String, List<LogEntryKey>> getExceptionClassLogEntryIndexMap() {
        return exceptionClassLogEntryIndexMap;
    }

    public List<LogEntryKey> getThreadDumpLogEntryKeyList() {
        return threadDumpLogEntryIndexList;
    }

    @Override
    public LogEntryColumn[] getReportTableColumns() {

        LogEntryColumn[] reportTableColumns = new LogEntryColumn[10];

        reportTableColumns[0] = LogEntryColumn.LINE;
        reportTableColumns[1] = LogEntryColumn.TIMESTAMP;
        reportTableColumns[2] = LogEntryColumn.DELTA;
        reportTableColumns[3] = LogEntryColumn.THREAD;
        reportTableColumns[4] = LogEntryColumn.APP;
        reportTableColumns[5] = LogEntryColumn.LOGGER;
        reportTableColumns[6] = LogEntryColumn.LEVEL;
        reportTableColumns[7] = LogEntryColumn.STACK;
        reportTableColumns[8] = LogEntryColumn.USERID;
        reportTableColumns[9] = LogEntryColumn.MESSAGE;

        return reportTableColumns;
    }

    @Override
    public void rebuildLogTimeSeriesCollectionSet(Locale locale) throws Exception {

        Map<String, LogSeriesCollection> overallLogTimeSeriesCollectionMap;
        overallLogTimeSeriesCollectionMap = getOverallLogTimeSeriesCollectionMap();
        overallLogTimeSeriesCollectionMap.clear();

        Map<String, LogSeriesCollection> overallCustomMeasureLogTimeSeriesCollectionMap;
        overallCustomMeasureLogTimeSeriesCollectionMap = getOverallCustomMeasureLogTimeSeriesCollectionMap();
        overallCustomMeasureLogTimeSeriesCollectionMap.clear();

        Map<String, LogIntervalMarker> overallLogIntervalMarkerMap;
        overallLogIntervalMarkerMap = getOverallLogIntervalMarkerMap();
        overallLogIntervalMarkerMap.clear();

        Map<LogEntryKey, LogEntry> logEntryMap = getLogEntryMap();

        for (LogEntry logEntry : logEntryMap.values()) {

            Log4jLogEntry log4jLogEntry = (Log4jLogEntry) logEntry;

            processLogSeriesCollection(overallLogTimeSeriesCollectionMap, log4jLogEntry, null, locale);

            processCustomMeasureLogSeriesCollection(overallCustomMeasureLogTimeSeriesCollectionMap, log4jLogEntry, null,
                    locale);

            processLogIntervalMarker(overallLogIntervalMarkerMap, log4jLogEntry);
        }

        // Not doing sorting anymore
        // Set<Integer> logEntryKeySet = new TreeSet<Integer>(logEntryMap.keySet());
        // Iterator<Integer> logEntryKeyIterator = logEntryKeySet.iterator();
        //
        // while (logEntryKeyIterator.hasNext()) {
        //
        // Integer logEntryKey = logEntryKeyIterator.next();
        // Log4jLogEntry log4jLogEntry = (Log4jLogEntry) logEntryMap.get(logEntryKey);
        //
        // processLogSeriesCollection(overallLogTimeSeriesCollectionMap, log4jLogEntry, null, locale);
        //
        // processCustomMeasureLogSeriesCollection(overallCustomMeasureLogTimeSeriesCollectionMap, log4jLogEntry, null,
        // locale);
        //
        // processLogIntervalMarker(overallLogIntervalMarkerMap, log4jLogEntry);
        // }

        // reset the flag
        setRebuildLogTimeSeriesCollectionSet(false);
    }
}
