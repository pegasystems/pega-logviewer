/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
/**
 * 
 */
package com.pega.gcs.logviewer.report.alert;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import com.pega.gcs.logviewer.model.AlertBoxAndWhiskerCalculator;
import com.pega.gcs.logviewer.model.AlertBoxAndWhiskerItem;

public class AlertMessageReportEntry implements Comparable<AlertMessageReportEntry> {

	private long thresholdKPI;

	private String kpiUnit;

	private String alertMessageReportEntryKey;

	private LinkedHashMap<Integer, Double> alertLogEntryAlertKeyValueMap;

	private boolean dirty;

	private AlertBoxAndWhiskerItem alertBoxAndWhiskerItem;

	public AlertMessageReportEntry(long thresholdKPI, String kpiUnit, String alertMessageReportEntryKey) {
		super();

		this.thresholdKPI = thresholdKPI;
		this.kpiUnit = kpiUnit;
		this.alertMessageReportEntryKey = alertMessageReportEntryKey;

		this.alertLogEntryAlertKeyValueMap = new LinkedHashMap<>();
	}

	public List<Integer> getAlertLogEntryKeyList() {
		ArrayList<Integer> alertLogEntryKeyList = new ArrayList<>(alertLogEntryAlertKeyValueMap.keySet());
		return Collections.unmodifiableList(alertLogEntryKeyList);
	}

	public String getAlertMessageReportEntryKey() {
		return alertMessageReportEntryKey;
	}

	public Double getAlertMessageReportEntryKeyValue(Integer alertLogEntryKey) {
		return alertLogEntryAlertKeyValueMap.get(alertLogEntryKey);
	}

	public void addAlertLogEntryKey(Integer alertLogEntryKey, Double alertLogEntryValue) {
		alertLogEntryAlertKeyValueMap.put(alertLogEntryKey, alertLogEntryValue);
		dirty = true;
	}

	public AlertBoxAndWhiskerItem getAlertBoxAndWhiskerItem() {

		if ((alertBoxAndWhiskerItem == null) || dirty) {

			ArrayList<Double> alertLogEntryAlertValueList = new ArrayList<>(alertLogEntryAlertKeyValueMap.values());

			Collections.sort(alertLogEntryAlertValueList);

			alertBoxAndWhiskerItem = AlertBoxAndWhiskerCalculator.calculateStatistics(alertLogEntryAlertValueList,
					thresholdKPI, kpiUnit);

			dirty = false;
		}

		return alertBoxAndWhiskerItem;
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
	public int compareTo(AlertMessageReportEntry o) {

		AlertBoxAndWhiskerItem thisAlertBoxAndWhiskerItem = getAlertBoxAndWhiskerItem();
		AlertBoxAndWhiskerItem otherAlertBoxAndWhiskerItem = o.getAlertBoxAndWhiskerItem();

		return otherAlertBoxAndWhiskerItem.compareTo(thisAlertBoxAndWhiskerItem);
	}
}
