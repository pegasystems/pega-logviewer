/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import java.awt.Color;

import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.general.SeriesException;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class LogTimeSeries implements LogSeries {

    private static final Log4j2Helper LOG = new Log4j2Helper(LogTimeSeries.class);

    private TimeSeries timeSeries;

    private Color color;

    private ValueMarker thresholdMarker;

    // storing the separate count as JFreechart has precision of millisecond,
    // whereas alert logs have time precision of nano seconds.
    // getting the count from TimeSeries will not be precise.
    private int timeSeriesEntryCount;

    private boolean showCount;

    private boolean defaultShowLogTimeSeries;

    public LogTimeSeries(String timeSeriesName, Color color, ValueMarker thresholdMarker, boolean showCount,
            boolean defaultShowLogTimeSeries) {

        super();

        this.timeSeries = new TimeSeries(timeSeriesName);
        this.color = color;
        this.thresholdMarker = thresholdMarker;
        this.timeSeriesEntryCount = 0;
        this.showCount = showCount;
        this.defaultShowLogTimeSeries = defaultShowLogTimeSeries;
    }

    @Override
    public String getName() {
        return timeSeries.getKey().toString();
    }

    @Override
    public int getCount() {
        return timeSeriesEntryCount;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public boolean isShowCount() {
        return showCount;
    }

    @Override
    public boolean isDefaultShowLogTimeSeries() {
        return defaultShowLogTimeSeries;
    }

    @Override
    public void setDefaultShowLogTimeSeries(boolean defaultShowLogTimeSeries) {
        this.defaultShowLogTimeSeries = defaultShowLogTimeSeries;

    }

    public TimeSeries getTimeSeries() {
        return timeSeries;
    }

    public ValueMarker getThresholdMarker() {
        return thresholdMarker;
    }

    public BoxAndWhiskerItem getBoxAndWhiskerItem() {
        return null;
    }

    public void addTimeSeriesDataItem(TimeSeriesDataItem timeSeriesDataItem) {

        // not using the addOrUpdate method because we cannot disable notify
        try {
            timeSeries.add(timeSeriesDataItem, false);
        } catch (SeriesException se) {
            LOG.debug("duplicate entry set for time period: " + timeSeriesDataItem.getPeriod().toString());
        }

        timeSeriesEntryCount++;
    }
}
