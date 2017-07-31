/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

import java.awt.Color;

import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.time.TimeSeries;

public class LogTimeSeries implements LogSeries {

	private TimeSeries timeSeries;

	private BoxAndWhiskerItem boxAndWhiskerItem;

	private Color color;

	private ValueMarker thresholdMarker;

	// storing the separate count as JFreechart has precision of millisecond,
	// whereas alert logs have time precision of nano seconds.
	// getting the count from TimeSeries will not be precise.
	private int timeSeriesEntryCount;

	private boolean showCount;

	private boolean defaultShowLogTimeSeries;

	public LogTimeSeries(TimeSeries timeSeries, BoxAndWhiskerItem boxAndWhiskerItem, Color color,
			ValueMarker thresholdMarker, int timeSeriesEntryCount, boolean showCount,
			boolean defaultShowLogTimeSeries) {

		super();

		this.timeSeries = timeSeries;
		this.boxAndWhiskerItem = boxAndWhiskerItem;
		this.color = color;
		this.thresholdMarker = thresholdMarker;
		this.timeSeriesEntryCount = timeSeriesEntryCount;
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

	public BoxAndWhiskerItem getBoxAndWhiskerItem() {
		return boxAndWhiskerItem;
	}

	public ValueMarker getThresholdMarker() {
		return thresholdMarker;
	}

}
