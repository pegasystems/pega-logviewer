/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import java.awt.Color;
import java.nio.charset.Charset;
import java.text.DateFormat;
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
import java.util.TreeMap;
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

    private Map<Integer, AlertMessageReportModel> alertMessageReportModelMap;

    private Map<String, LogSeriesCollection> overallLogTimeSeriesCollectionMap;

    public AlertLogEntryModel(DateFormat dateFormat) {

        super(dateFormat);

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

    public Map<Integer, AlertMessageReportModel> getAlertMessageReportModelMap() {

        if (alertMessageReportModelMap == null) {
            alertMessageReportModelMap = new TreeMap<Integer, AlertMessageReportModel>();
        }

        return alertMessageReportModelMap;
    }

    @Override
    protected void postProcess(LogEntry logEntry, ArrayList<String> logEntryValueList, Charset charset, Locale locale) {

        if (thresholdKPIIndex == -1) {
            List<String> logEntryColumnList = getLogEntryColumnList();
            thresholdKPIIndex = logEntryColumnList.indexOf(LogEntryColumn.THRESHOLDKPI.getColumnId());
        }

        AlertLogEntry alertLogEntry = (AlertLogEntry) logEntry;

        Integer alertId = alertLogEntry.getAlertId();

        Long alertMessageThresholdKPI = alertIdThresholdKPIMap.get(alertId);

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

        processLogSeriesCollection(overallLogTimeSeriesCollectionMap, alertLogEntry, logEntryValueList, locale);

        // report models
        Map<Integer, AlertMessageReportModel> alertMessageReportModelMap = getAlertMessageReportModelMap();

        AlertMessageReportModel alertMessageReportModel = alertMessageReportModelMap.get(alertId);

        if (alertMessageReportModel == null) {

            AlertMessageReportModelFactory alertMessageReportModelFactory = AlertMessageReportModelFactory
                    .getInstance();

            try {
                alertMessageReportModel = alertMessageReportModelFactory.getAlertMessageReportModel(alertId,
                        alertMessageThresholdKPI, this, locale);

                alertMessageReportModelMap.put(alertId, alertMessageReportModel);
            } catch (Exception e) {
                Map<Integer, AlertMessage> alertMessageMap = AlertMessageListProvider.getInstance()
                        .getAlertMessageMap();
                String alertMessageId = alertMessageMap.get(alertId).getMessageID();
                LOG.error("Error building alert report model for Id: " + alertId + " Alert Id: " + alertMessageId, e);
            }
        }

        if (alertMessageReportModel != null) {
            try {
                alertMessageReportModel.processAlertLogEntry(alertLogEntry, logEntryValueList);
            } catch (Exception e) {
                LOG.error("Error processing Alert Message report for Log Entry: " + alertLogEntry.getKey(), e);
            }
        }

    }

    private void processLogSeriesCollection(Map<String, LogSeriesCollection> logTimeSeriesCollectionMap,
            AlertLogEntry alertLogEntry, ArrayList<String> logEntryValueList, Locale locale) {

        LogEntryKey logEntryKey = alertLogEntry.getKey();

        long logEntryTime = logEntryKey.getTimestamp();

        // logEntryTime can be -1 in case of corrupted log file
        if (logEntryTime != -1) {

            DateFormat modelDateFormat = getModelDateFormat();
            TimeZone timezone = modelDateFormat.getTimeZone();

            boolean showCount = true;

            Integer alertId = alertLogEntry.getAlertId();
            double observedKPI = alertLogEntry.getObservedKPI();

            Map<Integer, AlertMessage> alertMessageMap = AlertMessageListProvider.getInstance().getAlertMessageMap();

            // alertMessage should not be null as AlertLogParser should have added unidentified AlertIds
            AlertMessage alertMessage = alertMessageMap.get(alertId);

            String messageId = alertMessage.getMessageID();
            boolean isCritical = Severity.CRITICAL.equals(alertMessage.getSeverity());

            processAlertLogTimeSeries(logTimeSeriesCollectionMap, alertMessage, messageId, logEntryTime, observedKPI,
                    showCount, isCritical, timezone, locale);

        }
    }

    private void processAlertLogTimeSeries(Map<String, LogSeriesCollection> logTimeSeriesCollectionMap,
            AlertMessage alertMessage, String logSeriesCollectionName, long logEntryTime, double timeSeriesValue,
            boolean showCount, boolean defaultShowLogTimeSeries, TimeZone timezone, Locale locale) {

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

            Long thresholdKPI = alertIdThresholdKPIMap.get(alertId);
            ValueMarker thresholdMarker = new ValueMarker(thresholdKPI);

            String kpiUnit = alertMessage.getDssValueUnit();

            alertLogTimeSeries = new AlertLogTimeSeries(logSeriesCollectionName, color, thresholdMarker, showCount,
                    defaultShowLogTimeSeries, thresholdKPI, kpiUnit);

            logSeriesCollection.addLogSeries(alertLogTimeSeries);
        }

        alertLogTimeSeries.addTimeSeriesDataItem(timeSeriesDataItem);

    }

    @Override
    public Set<LogSeriesCollection> getLogTimeSeriesCollectionSet(boolean filtered, Locale locale) throws Exception {

        if (isRebuildLogTimeSeriesCollectionSet()) {
            rebuildLogTimeSeriesCollectionSet(locale);
        }

        Set<LogSeriesCollection> logTimeSeriesCollectionSet = new TreeSet<>();

        if (filtered) {

            List<LogEntryKey> logEntryKeyList = getLogEntryKeyList();
            Iterator<LogEntryKey> logEntryIndexIterator = logEntryKeyList.iterator();

            Map<String, LogSeriesCollection> logTimeSeriesCollectionMap;
            logTimeSeriesCollectionMap = new HashMap<>();

            Map<LogEntryKey, LogEntry> logEntryMap = getLogEntryMap();

            while (logEntryIndexIterator.hasNext()) {

                LogEntryKey logEntryKey = logEntryIndexIterator.next();
                AlertLogEntry alertLogEntry = (AlertLogEntry) logEntryMap.get(logEntryKey);

                long logEntryTime = alertLogEntry.getKey().getTimestamp();

                // logEntryTime can be -1 in case of corrupted log file
                if (logEntryTime != -1) {
                    processLogSeriesCollection(logTimeSeriesCollectionMap, alertLogEntry, null, locale);
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

        Map<LogEntryKey, LogEntry> logEntryMap = getLogEntryMap();

        for (LogEntry logEntry : logEntryMap.values()) {

            AlertLogEntry alertLogEntry = (AlertLogEntry) logEntry;

            processLogSeriesCollection(overallLogTimeSeriesCollectionMap, alertLogEntry, null, locale);
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
