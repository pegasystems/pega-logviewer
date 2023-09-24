/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import java.awt.Color;
import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeriesDataItem;

import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.alert.AlertMessageList.AlertMessage;
import com.pega.gcs.logviewer.model.alert.AlertMessageListProvider;
import com.pega.gcs.logviewer.model.alert.Severity;
import com.pega.gcs.logviewer.report.alert.AlertMessageReportModel;
import com.pega.gcs.logviewer.report.alert.AlertMessageReportModelFactory;

public class AlertLogEntryModel extends LogEntryModel {

    private static final Log4j2Helper LOG = new Log4j2Helper(AlertLogEntryModel.class);

    private int thresholdKPIIndex;

    Map<Integer, Long> alertIdThresholdKPIMap;

    private TreeSet<PALStatisticName> palStatisticColumnSet;

    // hold the overall report models, not for filtered case
    private Map<Integer, AlertMessageReportModel> alertMessageReportModelMap;

    private Map<String, LogSeriesCollection> overallLogTimeSeriesCollectionMap;

    public AlertLogEntryModel(DateTimeFormatter modelDateTimeFormatter, ZoneId modelZoneId, ZoneId displayZoneId) {

        super(modelDateTimeFormatter, modelZoneId, displayZoneId);

        Comparator<PALStatisticName> comparator = new Comparator<PALStatisticName>() {

            @Override
            public int compare(PALStatisticName o1, PALStatisticName o2) {

                Integer o1Ordinal = o1.ordinal();
                Integer o2Ordinal = o2.ordinal();

                return o1Ordinal.compareTo(o2Ordinal);
            }
        };

        thresholdKPIIndex = -1;

        alertIdThresholdKPIMap = new HashMap<Integer, Long>();

        palStatisticColumnSet = new TreeSet<PALStatisticName>(comparator);
    }

    private Map<Integer, AlertMessageReportModel> getAlertMessageReportModelMap() {

        if (alertMessageReportModelMap == null) {
            alertMessageReportModelMap = new HashMap<Integer, AlertMessageReportModel>();
        }

        return alertMessageReportModelMap;
    }

    @Override
    protected void postProcess(LogEntry logEntry, ArrayList<String> logEntryValueList, Charset charset, Locale locale) {

        if (thresholdKPIIndex == -1) {
            thresholdKPIIndex = getLogEntryColumnIndex(LogEntryColumn.THRESHOLDKPI);
        }

        AlertLogEntry alertLogEntry = (AlertLogEntry) logEntry;

        Integer alertId = alertLogEntry.getAlertId();

        Long alertMessageThresholdKPI = getAlertMessageThresholdKPI(alertId);

        if (alertMessageThresholdKPI == null) {

            String thresholdKPI = logEntryValueList.get(thresholdKPIIndex);

            try {
                alertMessageThresholdKPI = Long.parseLong(thresholdKPI);
            } catch (NumberFormatException nfe) {
                LOG.error("Error parsing thresholdKPI: " + thresholdKPI, nfe);
            }

            alertIdThresholdKPIMap.put(alertId, alertMessageThresholdKPI);

        }

        // graphs
        Map<String, LogSeriesCollection> overallLogTimeSeriesCollectionMap;
        overallLogTimeSeriesCollectionMap = getOverallLogTimeSeriesCollectionMap();

        processLogSeriesCollection(overallLogTimeSeriesCollectionMap, alertLogEntry, locale);

        // report models
        Map<Integer, AlertMessageReportModel> alertMessageReportModelMap = getAlertMessageReportModelMap();

        processAlertMessageReportModelMap(alertMessageReportModelMap, alertLogEntry, locale, alertMessageThresholdKPI,
                logEntryValueList);

    }

    private Long getAlertMessageThresholdKPI(Integer alertId) {
        return alertIdThresholdKPIMap.get(alertId);
    }

    private void processLogSeriesCollection(Map<String, LogSeriesCollection> logTimeSeriesCollectionMap,
            AlertLogEntry alertLogEntry, Locale locale) {

        LogEntryKey logEntryKey = alertLogEntry.getKey();

        long logEntryTime = logEntryKey.getTimestamp();

        // logEntryTime can be -1 in case of corrupted log file
        if (logEntryTime != -1) {

            ZoneId modelZoneId = getModelZoneId();
            TimeZone modelTimeZone = TimeZone.getTimeZone(modelZoneId);

            boolean showCount = true;

            Integer alertId = alertLogEntry.getAlertId();
            double observedKPI = alertLogEntry.getObservedKPI();

            AlertMessage alertMessage = AlertMessageListProvider.getInstance().getAlertMessage(alertId);

            String messageId = alertMessage.getMessageID();
            boolean isCritical = Severity.CRITICAL.equals(alertMessage.getSeverity());

            processAlertLogTimeSeries(logTimeSeriesCollectionMap, alertMessage, messageId, logEntryTime, observedKPI,
                    showCount, isCritical, modelTimeZone, locale);

        }
    }

    private void processAlertLogTimeSeries(Map<String, LogSeriesCollection> logTimeSeriesCollectionMap,
            AlertMessage alertMessage, String logSeriesCollectionName, long logEntryTime, double timeSeriesValue,
            boolean showCount, boolean defaultShowLogTimeSeries, TimeZone modelTimeZone, Locale locale) {

        LogSeriesCollection logSeriesCollection;
        logSeriesCollection = logTimeSeriesCollectionMap.get(logSeriesCollectionName);

        if (logSeriesCollection == null) {
            logSeriesCollection = new LogSeriesCollection(logSeriesCollectionName);
            logTimeSeriesCollectionMap.put(logSeriesCollectionName, logSeriesCollection);
        }

        RegularTimePeriod regularTimePeriod;
        regularTimePeriod = new Millisecond(new Date(logEntryTime), modelTimeZone, locale);

        TimeSeriesDataItem timeSeriesDataItem;
        timeSeriesDataItem = new TimeSeriesDataItem(regularTimePeriod, timeSeriesValue);

        AlertLogTimeSeries alertLogTimeSeries = (AlertLogTimeSeries) logSeriesCollection
                .getLogSeries(logSeriesCollectionName);

        if (alertLogTimeSeries == null) {

            Integer alertId = alertMessage.getId();
            String chartColor = alertMessage.getChartColor();

            Color color = null;

            if (chartColor == null) {
                color = Color.BLACK;
            } else {
                color = MyColor.getColor(chartColor);
            }

            if (color == null) {
                color = Color.BLACK;
            }

            Long thresholdKPI = getAlertMessageThresholdKPI(alertId);
            ValueMarker thresholdMarker = new ValueMarker(thresholdKPI);

            String kpiUnit = alertMessage.getDssValueUnit();

            alertLogTimeSeries = new AlertLogTimeSeries(logSeriesCollectionName, color, thresholdMarker, showCount,
                    defaultShowLogTimeSeries, thresholdKPI, kpiUnit);

            logSeriesCollection.addLogSeries(alertLogTimeSeries);
        }

        alertLogTimeSeries.addTimeSeriesDataItem(timeSeriesDataItem);

    }

    private void processAlertMessageReportModelMap(Map<Integer, AlertMessageReportModel> alertMessageReportModelMap,
            AlertLogEntry alertLogEntry, Locale locale, Long alertMessageThresholdKPI,
            ArrayList<String> logEntryValueList) {

        Integer alertId = alertLogEntry.getAlertId();

        AlertMessageReportModel alertMessageReportModel = alertMessageReportModelMap.get(alertId);

        if (alertMessageReportModel == null) {

            AlertMessageReportModelFactory alertMessageReportModelFactory = AlertMessageReportModelFactory
                    .getInstance();

            try {
                alertMessageReportModel = alertMessageReportModelFactory.getAlertMessageReportModel(alertId,
                        alertMessageThresholdKPI, this, locale);

                alertMessageReportModelMap.put(alertId, alertMessageReportModel);

            } catch (Exception e) {

                AlertMessage alertMessage = AlertMessageListProvider.getInstance().getAlertMessage(alertId);
                String alertMessageId = (alertMessage != null) ? alertMessage.getMessageID() : null;

                LOG.error("Error building alert report model for Id: " + alertId + " Alert Id: " + alertMessageId, e);
            }
        }

        if (alertMessageReportModel != null) {
            try {
                alertMessageReportModel.processAlertLogEntry(alertLogEntry, logEntryValueList);
            } catch (Exception e) {
                LOG.error("Error processing alert message report model for log entry: " + alertLogEntry.getKey(), e);
            }
        }

    }

    @Override
    public Set<LogSeriesCollection> getLogTimeSeriesCollectionSet(boolean filtered, Locale locale) throws Exception {

        // rebuild is done when timezone updated
        if (isRebuildLogTimeSeriesCollectionSet()) {
            rebuildLogTimeSeriesCollectionSet(locale);
        }

        Set<LogSeriesCollection> logTimeSeriesCollectionSet = new TreeSet<>();

        if (filtered) {

            Map<String, LogSeriesCollection> logTimeSeriesCollectionMap;
            logTimeSeriesCollectionMap = new HashMap<>();

            List<LogEntryKey> logEntryKeyList = getLogEntryKeyList();

            Iterator<LogEntryKey> logEntryIndexIterator = logEntryKeyList.iterator();

            Map<LogEntryKey, LogEntry> logEntryMap = getLogEntryMap();

            while (logEntryIndexIterator.hasNext()) {

                LogEntryKey logEntryKey = logEntryIndexIterator.next();
                AlertLogEntry alertLogEntry = (AlertLogEntry) logEntryMap.get(logEntryKey);

                long logEntryTime = alertLogEntry.getKey().getTimestamp();

                // logEntryTime can be -1 in case of corrupted log file
                if (logEntryTime != -1) {
                    processLogSeriesCollection(logTimeSeriesCollectionMap, alertLogEntry, locale);
                }
            }

            logTimeSeriesCollectionSet.addAll(logTimeSeriesCollectionMap.values());

        } else {

            Map<String, LogSeriesCollection> overallLogTimeSeriesCollectionMap;
            overallLogTimeSeriesCollectionMap = getOverallLogTimeSeriesCollectionMap();

            logTimeSeriesCollectionSet.addAll(overallLogTimeSeriesCollectionMap.values());

        }

        return logTimeSeriesCollectionSet;

    }

    // TODO avoid duplicate looping
    public Map<Integer, AlertMessageReportModel> getFilteredAlertMessageReportModelMap(boolean filtered,
            Locale locale) {

        Map<Integer, AlertMessageReportModel> alertMessageReportModelMap = null;

        if (filtered) {

            alertMessageReportModelMap = new HashMap<>();

            List<LogEntryKey> logEntryKeyList = getLogEntryKeyList();

            Iterator<LogEntryKey> logEntryIndexIterator = logEntryKeyList.iterator();

            Map<LogEntryKey, LogEntry> logEntryMap = getLogEntryMap();

            while (logEntryIndexIterator.hasNext()) {

                LogEntryKey logEntryKey = logEntryIndexIterator.next();
                AlertLogEntry alertLogEntry = (AlertLogEntry) logEntryMap.get(logEntryKey);

                long logEntryTime = alertLogEntry.getKey().getTimestamp();

                // logEntryTime can be -1 in case of corrupted log file
                if (logEntryTime != -1) {
                    Integer alertId = alertLogEntry.getAlertId();

                    Long alertMessageThresholdKPI = getAlertMessageThresholdKPI(alertId);

                    // logEntryValueList: null - going to be slow :(
                    processAlertMessageReportModelMap(alertMessageReportModelMap, alertLogEntry, locale,
                            alertMessageThresholdKPI, null);

                }
            }
        } else {
            alertMessageReportModelMap = getAlertMessageReportModelMap();
        }

        return alertMessageReportModelMap;
    }

    @Override
    public Set<LogIntervalMarker> getLogIntervalMarkerSet() {
        return null;
    }

    @Override
    public String getTypeName() {
        return "Alert";
    }

    public TreeSet<PALStatisticName> getPalStatisticColumnSet() {
        return palStatisticColumnSet;
    }

    public void addPALStatisticColumn(PALStatisticName palStatisticName) {
        palStatisticColumnSet.add(palStatisticName);
    }

    @Override
    public LogEntryColumn[] getReportTableColumns() {

        LogEntryColumn[] reportTableColumns = new LogEntryColumn[10];

        reportTableColumns[0] = LogEntryColumn.LINE;
        reportTableColumns[1] = LogEntryColumn.TIMESTAMP;
        reportTableColumns[2] = LogEntryColumn.DELTA;
        reportTableColumns[3] = LogEntryColumn.VERSION;
        reportTableColumns[4] = LogEntryColumn.MESSAGEID;
        reportTableColumns[5] = LogEntryColumn.OBSERVEDKPI;
        reportTableColumns[6] = LogEntryColumn.THRESHOLDKPI;
        reportTableColumns[7] = LogEntryColumn.NODEID;
        reportTableColumns[8] = LogEntryColumn.REQUESTORID;
        reportTableColumns[9] = LogEntryColumn.MESSAGE;

        return reportTableColumns;
    }

    private Map<String, LogSeriesCollection> getOverallLogTimeSeriesCollectionMap() {

        if (overallLogTimeSeriesCollectionMap == null) {
            overallLogTimeSeriesCollectionMap = new HashMap<>();
        }

        return overallLogTimeSeriesCollectionMap;
    }

    @Override
    public void rebuildLogTimeSeriesCollectionSet(Locale locale) throws Exception {

        Map<String, LogSeriesCollection> overallLogTimeSeriesCollectionMap;
        overallLogTimeSeriesCollectionMap = getOverallLogTimeSeriesCollectionMap();
        overallLogTimeSeriesCollectionMap.clear();

        Map<Integer, AlertMessageReportModel> alertMessageReportModelMap;
        alertMessageReportModelMap = getAlertMessageReportModelMap();
        alertMessageReportModelMap.clear();

        Map<LogEntryKey, LogEntry> logEntryMap = getLogEntryMap();

        for (LogEntry logEntry : logEntryMap.values()) {

            AlertLogEntry alertLogEntry = (AlertLogEntry) logEntry;

            long logEntryTime = alertLogEntry.getKey().getTimestamp();

            // logEntryTime can be -1 in case of corrupted log file
            if (logEntryTime != -1) {

                processLogSeriesCollection(overallLogTimeSeriesCollectionMap, alertLogEntry, locale);

                Integer alertId = alertLogEntry.getAlertId();

                Long alertMessageThresholdKPI = getAlertMessageThresholdKPI(alertId);

                // logEntryValueList: null - going to be slow :(
                processAlertMessageReportModelMap(alertMessageReportModelMap, alertLogEntry, locale,
                        alertMessageThresholdKPI, null);

            }
        }

        // Not doing sorting anymore
        // Set<LogEntryKey> logEntryKeySet = new TreeSet<>(logEntryMap.keySet());
        // Iterator<LogEntryKey> logEntryIndexIterator = logEntryKeySet.iterator();
        //
        // while (logEntryIndexIterator.hasNext()) {
        //
        // Integer logEntryKey = logEntryIndexIterator.next();
        // AlertLogEntry alertLogEntry = (AlertLogEntry) logEntryMap.get(logEntryKey);
        //
        // processLogSeriesCollection(overallLogTimeSeriesCollectionMap, alertLogEntry, null, locale);
        //
        // }

        // reset the flag
        setRebuildLogTimeSeriesCollectionSet(false);
    }

    public void processAlertMessageReportModels() {

        Map<Integer, AlertMessageReportModel> alertMessageReportModelMap = getAlertMessageReportModelMap();

        for (AlertMessageReportModel alertMessageReportModel : alertMessageReportModelMap.values()) {

            // alertMessageReportModel can be null for new alertid thats not yet developed in to this tool
            if (alertMessageReportModel != null) {
                alertMessageReportModel.postProcess();
            }
        }
    }
}
