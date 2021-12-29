/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import java.awt.Font;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;

public class LogSeriesCollection implements Comparable<LogSeriesCollection> {

    private String name;

    private Map<String, LogSeries> logSeriesMap;

    public LogSeriesCollection(String name) {
        this.name = name;
        this.logSeriesMap = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public Collection<LogSeries> getLogSeriesList() {
        return Collections.unmodifiableCollection(logSeriesMap.values());
    }

    public LogSeries getLogSeries(String logSeriesName) {

        LogSeries logSeries = logSeriesMap.get(logSeriesName);

        return logSeries;
    }

    public void addLogSeries(LogSeries logSeries) {

        String logSeriesName = logSeries.getName();

        logSeriesMap.put(logSeriesName, logSeries);
    }

    @Override
    public int compareTo(LogSeriesCollection other) {
        return getName().compareTo(other.getName());
    }

    private ValueAxis getNewDomainAxis(DateFormat displayDateFormat, Locale locale) {

        TimeZone displayTimeZone = displayDateFormat.getTimeZone();
        String abbrTimeZoneStr = displayTimeZone.getDisplayName(displayTimeZone.useDaylightTime(), TimeZone.SHORT);

        String label = "Time (" + abbrTimeZoneStr + ")";

        DateAxis domainAxis = new DateAxis(label, displayTimeZone, locale);
        domainAxis.setLowerMargin(0.02);
        domainAxis.setUpperMargin(0.02);

        Font labelFont = new Font("Arial", Font.PLAIN, 10);
        domainAxis.setLabelFont(labelFont);

        return domainAxis;
    }

    public boolean isShowCount() {

        boolean showCount = false;

        for (LogSeries logSeries : logSeriesMap.values()) {

            showCount = showCount || logSeries.isShowCount();

            if (showCount) {
                break;
            }
        }

        return showCount;
    }

    public boolean isDefaultShowLogTimeSeries() {

        boolean defaultShowLogTimeSeries = false;

        for (LogSeries logSeries : logSeriesMap.values()) {

            defaultShowLogTimeSeries = defaultShowLogTimeSeries || logSeries.isDefaultShowLogTimeSeries();

            if (defaultShowLogTimeSeries) {
                break;
            }
        }

        return defaultShowLogTimeSeries;
    }
}
