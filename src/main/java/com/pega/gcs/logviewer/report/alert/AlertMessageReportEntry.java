/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.report.alert;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeriesDataItem;

import com.pega.gcs.logviewer.model.AlertBoxAndWhiskerItem;
import com.pega.gcs.logviewer.model.AlertLogEntry;
import com.pega.gcs.logviewer.model.AlertLogTimeSeries;
import com.pega.gcs.logviewer.model.LogEntryKey;

public class AlertMessageReportEntry implements Comparable<AlertMessageReportEntry> {

    private String alertMessageReportEntryKey;

    private LinkedHashMap<LogEntryKey, Double> alertLogEntryAlertKeyValueMap;

    private AlertLogTimeSeries alertLogTimeSeries;

    public AlertMessageReportEntry(long thresholdKPI, String kpiUnit, String alertMessageReportEntryKey, Color color) {
        super();

        this.alertMessageReportEntryKey = alertMessageReportEntryKey;

        this.alertLogEntryAlertKeyValueMap = new LinkedHashMap<>();
        this.alertLogTimeSeries = new AlertLogTimeSeries("", color, null, false, false, thresholdKPI, kpiUnit);

    }

    public List<LogEntryKey> getAlertLogEntryKeyList() {
        ArrayList<LogEntryKey> alertLogEntryKeyList = new ArrayList<>(alertLogEntryAlertKeyValueMap.keySet());
        return Collections.unmodifiableList(alertLogEntryKeyList);
    }

    public String getAlertMessageReportEntryKey() {
        return alertMessageReportEntryKey;
    }

    public Double getAlertMessageReportEntryKeyValue(LogEntryKey alertLogEntryKey) {
        return alertLogEntryAlertKeyValueMap.get(alertLogEntryKey);
    }

    public void addAlertLogEntry(AlertLogEntry alertLogEntry, TimeZone modelTimezone, Locale locale) {

        LogEntryKey logEntryKey = alertLogEntry.getKey();
        double alertLogEntryValue = alertLogEntry.getObservedKPI();

        alertLogEntryAlertKeyValueMap.put(logEntryKey, alertLogEntryValue);

        long logEntryTime = logEntryKey.getTimestamp();

        RegularTimePeriod regularTimePeriod;
        regularTimePeriod = new Millisecond(new Date(logEntryTime), modelTimezone, locale);

        TimeSeriesDataItem timeSeriesDataItem;
        timeSeriesDataItem = new TimeSeriesDataItem(regularTimePeriod, alertLogEntryValue);

        alertLogTimeSeries.addTimeSeriesDataItem(timeSeriesDataItem);
    }

    public AlertBoxAndWhiskerItem getAlertBoxAndWhiskerItem() {
        return alertLogTimeSeries.getBoxAndWhiskerItem();
    }

    public Object getColumnValue(AlertBoxAndWhiskerReportColumn alertBoxAndWhiskerReportColumn, NumberFormat nf) {

        Object columnValue = null;

        AlertBoxAndWhiskerItem alertBoxAndWhiskerItem = getAlertBoxAndWhiskerItem();

        if (AlertBoxAndWhiskerReportColumn.KEY.equals(alertBoxAndWhiskerReportColumn.getColumnId())) {
            columnValue = getAlertMessageReportEntryKey();
        } else if (AlertBoxAndWhiskerReportColumn.COUNT.equals(alertBoxAndWhiskerReportColumn)) {
            columnValue = alertBoxAndWhiskerItem.getCount();
        } else if (AlertBoxAndWhiskerReportColumn.TOTAL.equals(alertBoxAndWhiskerReportColumn)) {
            columnValue = nf.format(alertBoxAndWhiskerItem.getTotal());
        } else if (AlertBoxAndWhiskerReportColumn.MEAN.equals(alertBoxAndWhiskerReportColumn)) {
            columnValue = nf.format(alertBoxAndWhiskerItem.getMean());
        } else if (AlertBoxAndWhiskerReportColumn.MEDIAN.equals(alertBoxAndWhiskerReportColumn)) {
            columnValue = nf.format(alertBoxAndWhiskerItem.getMedian());
        } else if (AlertBoxAndWhiskerReportColumn.Q1.equals(alertBoxAndWhiskerReportColumn)) {
            columnValue = nf.format(alertBoxAndWhiskerItem.getQ1());
        } else if (AlertBoxAndWhiskerReportColumn.Q3.equals(alertBoxAndWhiskerReportColumn)) {
            columnValue = nf.format(alertBoxAndWhiskerItem.getQ3());
        } else if (AlertBoxAndWhiskerReportColumn.MINREGULARVALUE.equals(alertBoxAndWhiskerReportColumn)) {
            columnValue = nf.format(alertBoxAndWhiskerItem.getMinRegularValue());
        } else if (AlertBoxAndWhiskerReportColumn.MAXREGULARVALUE.equals(alertBoxAndWhiskerReportColumn)) {
            columnValue = nf.format(alertBoxAndWhiskerItem.getMaxRegularValue());
        } else if (AlertBoxAndWhiskerReportColumn.MINOUTLIER.equals(alertBoxAndWhiskerReportColumn)) {
            columnValue = nf.format(alertBoxAndWhiskerItem.getMinOutlier());
        } else if (AlertBoxAndWhiskerReportColumn.MAXOUTLIER.equals(alertBoxAndWhiskerReportColumn)) {
            columnValue = nf.format(alertBoxAndWhiskerItem.getMaxOutlier());
        } else if (AlertBoxAndWhiskerReportColumn.IQR.equals(alertBoxAndWhiskerReportColumn)) {
            columnValue = nf.format(alertBoxAndWhiskerItem.getIQR());
        } else if (AlertBoxAndWhiskerReportColumn.OUTLIERS.equals(alertBoxAndWhiskerReportColumn)) {
            columnValue = nf.format(alertBoxAndWhiskerItem.getOutliers().size());
        }

        return columnValue;
    }

    @Override
    public int compareTo(AlertMessageReportEntry other) {

        AlertBoxAndWhiskerItem thisAlertBoxAndWhiskerItem = getAlertBoxAndWhiskerItem();
        AlertBoxAndWhiskerItem otherAlertBoxAndWhiskerItem = other.getAlertBoxAndWhiskerItem();

        // sort in decending order
        return otherAlertBoxAndWhiskerItem.compareTo(thisAlertBoxAndWhiskerItem);
    }

    public AlertLogTimeSeries getAlertLogTimeSeries() {
        return alertLogTimeSeries;
    }

}
