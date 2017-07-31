/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

import java.awt.Font;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;

public class LogSeriesCollection implements Comparable<LogSeriesCollection> {

	private String name;

	private List<LogSeries> logSeriesList;

	public LogSeriesCollection(String name) {
		this.name = name;
		this.logSeriesList = new ArrayList<>();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public List<LogSeries> getLogSeriesList() {
		return Collections.unmodifiableList(logSeriesList);
	}

	public void addLogSeries(LogSeries logSeries) {
		logSeriesList.add(logSeries);
	}

	@Override
	public int compareTo(LogSeriesCollection o) {
		return getName().compareTo(o.getName());
	}

	public ValueAxis getNewDomainAxis(DateFormat displayDateFormat, Locale locale) {

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

		for (LogSeries logSeries : logSeriesList) {

			showCount = showCount || logSeries.isShowCount();

			if (showCount) {
				break;
			}
		}

		return showCount;
	}

	public boolean isDefaultShowLogTimeSeries() {

		boolean defaultShowLogTimeSeries = false;

		for (LogSeries logSeries : logSeriesList) {

			defaultShowLogTimeSeries = defaultShowLogTimeSeries || logSeries.isDefaultShowLogTimeSeries();

			if (defaultShowLogTimeSeries) {
				break;
			}
		}

		return defaultShowLogTimeSeries;
	}
}
