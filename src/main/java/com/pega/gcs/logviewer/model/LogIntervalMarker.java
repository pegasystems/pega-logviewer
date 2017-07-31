/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jfree.chart.plot.ValueMarker;

public class LogIntervalMarker implements LogSeries, Comparable<LogIntervalMarker> {

	private String name;

	private Color color;

	private List<ValueMarker> valueMarkerList;

	private boolean showCount;

	private boolean defaultShowLogTimeSeries;

	public LogIntervalMarker(String name, Color color, boolean showCount, boolean defaultShowLogTimeSeries) {
		super();
		this.name = name;
		this.color = color;
		this.valueMarkerList = new ArrayList<ValueMarker>();
		this.showCount = showCount;
		this.defaultShowLogTimeSeries = defaultShowLogTimeSeries;
	}

	public List<ValueMarker> getValueMarkerList() {
		return Collections.unmodifiableList(valueMarkerList);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getCount() {
		return valueMarkerList.size();
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

	@Override
	public int compareTo(LogIntervalMarker o) {
		return getName().compareTo(o.getName());
	}

	public void addValueMarker(ValueMarker valueMarker) {
		valueMarkerList.add(valueMarker);
	}

}
