/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.report.alert;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import com.pega.gcs.logviewer.model.AlertBoxAndWhiskerItem;
import com.pega.gcs.logviewer.model.LogSeries;
import com.pega.gcs.logviewer.model.LogSeriesCollection;
import com.pega.gcs.logviewer.model.LogTimeSeries;
import com.pega.gcs.logviewer.model.alert.AlertMessageListProvider;

public class AlertBoxAndWhiskerTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -2033481761765641385L;

	private List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList;

	private List<String> alertMessageIdList;

	private Map<String, AlertBoxAndWhiskerItem> alertBoxAndWhiskerItemMap;

	private NumberFormat numberFormat;

	private Map<String, List<String>> alertTypeMap;

	public AlertBoxAndWhiskerTableModel(Set<LogSeriesCollection> logTimeSeriesCollectionSet,
			NumberFormat numberFormat) {

		super();

		alertMessageIdList = new ArrayList<>();
		alertBoxAndWhiskerItemMap = new HashMap<>();
		alertTypeMap = new HashMap<>();

		AlertMessageListProvider alertMessageListProvider = AlertMessageListProvider.getInstance();
		Map<String, List<String>> alertMessageTypeMap = alertMessageListProvider.getAlertMessageTypeMap();

		for (LogSeriesCollection logSeriesCollection : logTimeSeriesCollectionSet) {

			String alertMessageId = logSeriesCollection.getName();
			alertMessageIdList.add(alertMessageId);

			// currently we have one LogSeries per LogSeriesCollection for
			// Alerts
			for (LogSeries logSeries : logSeriesCollection.getLogSeriesList()) {

				LogTimeSeries logTimeSeries = (LogTimeSeries) logSeries;

				AlertBoxAndWhiskerItem alertBoxAndWhiskerItem;
				alertBoxAndWhiskerItem = (AlertBoxAndWhiskerItem) logTimeSeries.getBoxAndWhiskerItem();

				alertBoxAndWhiskerItemMap.put(alertMessageId, alertBoxAndWhiskerItem);
			}

			String alertMessageType = null;

			for (String amType : alertMessageTypeMap.keySet()) {

				List<String> alertMessageIdList = alertMessageTypeMap.get(amType);

				if (alertMessageIdList.contains(alertMessageId)) {
					alertMessageType = amType;
					break;
				}

			}

			if ((alertMessageType == null) || ("".equals(alertMessageType))) {
				alertMessageType = "OTHERS";
			}

			List<String> alertMessageIdList = alertTypeMap.get(alertMessageType);

			if (alertMessageIdList == null) {
				alertMessageIdList = new ArrayList<>();
				alertTypeMap.put(alertMessageType, alertMessageIdList);
			}

			alertMessageIdList.add(alertMessageId);
		}

		this.numberFormat = numberFormat;

		Collections.sort(alertMessageIdList);
	}

	@Override
	public int getRowCount() {
		return alertMessageIdList.size();
	}

	@Override
	public int getColumnCount() {
		return getAlertMessageReportColumnList().size();
	}

	@Override
	public String getColumnName(int column) {
		return getColumn(column).getDisplayName();
	}

	public AlertBoxAndWhiskerReportColumn getColumn(int column) {

		List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList = getAlertMessageReportColumnList();

		return alertMessageReportColumnList.get(column);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		String value = null;

		String key = alertMessageIdList.get(rowIndex);

		AlertBoxAndWhiskerItem abawi = alertBoxAndWhiskerItemMap.get(key);
		AlertBoxAndWhiskerReportColumn alertBoxAndWhiskerReportColumn = getColumn(columnIndex);

		if (AlertBoxAndWhiskerReportColumn.ALERT_MESSAGE.equals(alertBoxAndWhiskerReportColumn)) {
			value = key;
		} else if (AlertBoxAndWhiskerReportColumn.KPI_THRESHOLD.equals(alertBoxAndWhiskerReportColumn)) {
			value = (abawi != null) ? numberFormat.format(abawi.getThresholdKPI()) : null;
		} else if (AlertBoxAndWhiskerReportColumn.KPI_UNIT.equals(alertBoxAndWhiskerReportColumn)) {
			value = (abawi != null) ? abawi.getKpiUnit() : null;
		} else if (AlertBoxAndWhiskerReportColumn.COUNT.equals(alertBoxAndWhiskerReportColumn)) {
			value = (abawi != null) ? numberFormat.format(abawi.getCount()) : null;
		} else if (AlertBoxAndWhiskerReportColumn.MEAN.equals(alertBoxAndWhiskerReportColumn)) {
			value = (abawi != null) ? numberFormat.format(abawi.getMean()) : null;
		} else if (AlertBoxAndWhiskerReportColumn.MEDIAN.equals(alertBoxAndWhiskerReportColumn)) {
			value = (abawi != null) ? numberFormat.format(abawi.getMedian()) : null;
		} else if (AlertBoxAndWhiskerReportColumn.Q1.equals(alertBoxAndWhiskerReportColumn)) {
			value = (abawi != null) ? numberFormat.format(abawi.getQ1()) : null;
		} else if (AlertBoxAndWhiskerReportColumn.Q3.equals(alertBoxAndWhiskerReportColumn)) {
			value = (abawi != null) ? numberFormat.format(abawi.getQ3()) : null;
		} else if (AlertBoxAndWhiskerReportColumn.MINOUTLIER.equals(alertBoxAndWhiskerReportColumn)) {
			value = (abawi != null) ? numberFormat.format(abawi.getMinOutlier()) : null;
		} else if (AlertBoxAndWhiskerReportColumn.MAXOUTLIER.equals(alertBoxAndWhiskerReportColumn)) {
			value = (abawi != null) ? numberFormat.format(abawi.getMaxOutlier()) : null;
		} else if (AlertBoxAndWhiskerReportColumn.IQR.equals(alertBoxAndWhiskerReportColumn)) {
			value = (abawi != null) ? numberFormat.format(abawi.getIQR()) : null;
		} else if (AlertBoxAndWhiskerReportColumn.OUTLIERS.equals(alertBoxAndWhiskerReportColumn)) {
			value = (abawi != null) ? numberFormat.format(abawi.getOutliers().size()) : null;
		}

		return value;
	}

	private List<AlertBoxAndWhiskerReportColumn> getAlertMessageReportColumnList() {

		if (alertMessageReportColumnList == null) {
			alertMessageReportColumnList = new ArrayList<>();

			alertMessageReportColumnList.add(AlertBoxAndWhiskerReportColumn.ALERT_MESSAGE);
			alertMessageReportColumnList.add(AlertBoxAndWhiskerReportColumn.KPI_THRESHOLD);
			alertMessageReportColumnList.add(AlertBoxAndWhiskerReportColumn.KPI_UNIT);
			alertMessageReportColumnList.add(AlertBoxAndWhiskerReportColumn.COUNT);
			alertMessageReportColumnList.add(AlertBoxAndWhiskerReportColumn.MINOUTLIER);
			alertMessageReportColumnList.add(AlertBoxAndWhiskerReportColumn.Q1);
			alertMessageReportColumnList.add(AlertBoxAndWhiskerReportColumn.MEDIAN);
			alertMessageReportColumnList.add(AlertBoxAndWhiskerReportColumn.MEAN);
			alertMessageReportColumnList.add(AlertBoxAndWhiskerReportColumn.Q3);
			alertMessageReportColumnList.add(AlertBoxAndWhiskerReportColumn.MAXOUTLIER);
			alertMessageReportColumnList.add(AlertBoxAndWhiskerReportColumn.IQR);
			alertMessageReportColumnList.add(AlertBoxAndWhiskerReportColumn.OUTLIERS);

		}

		return alertMessageReportColumnList;
	}

	public Set<String> getAlertMessageTypeSet() {
		return Collections.unmodifiableSet(alertTypeMap.keySet());
	}

	public void applyFilter(List<String> alertMessageTypeFilterList) {

		if ((alertMessageTypeFilterList != null) && (alertMessageTypeFilterList.size() > 0)) {

			List<String> filteredAlertMessageIdList = new ArrayList<>();

			for (String alertMessageType : alertMessageTypeFilterList) {
				List<String> amList = alertTypeMap.get(alertMessageType);
				filteredAlertMessageIdList.addAll(amList);
			}

			alertMessageIdList.clear();
			alertMessageIdList.addAll(filteredAlertMessageIdList);

			Collections.sort(alertMessageIdList);

		} else {
			alertMessageIdList.clear();
			alertMessageIdList.addAll(alertBoxAndWhiskerItemMap.keySet());

			Collections.sort(alertMessageIdList);
		}

		fireTableDataChanged();
	}

}
