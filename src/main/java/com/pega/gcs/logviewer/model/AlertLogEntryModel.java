/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

import java.awt.Color;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import org.jfree.data.time.TimeSeries;
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

	public AlertLogEntryModel(DateFormat dateFormat, Locale locale) {
		super(dateFormat, locale);

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
	protected void postProcess(LogEntry logEntry, ArrayList<String> logEntryValueList) {

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

		// report models
		Map<Integer, AlertMessageReportModel> alertMessageReportModelMap = getAlertMessageReportModelMap();

		AlertMessageReportModel alertMessageReportModel = alertMessageReportModelMap.get(alertId);

		if (alertMessageReportModel == null) {

			AlertMessageReportModelFactory alertMessageReportModelFactory = AlertMessageReportModelFactory
					.getInstance();

			try {
				alertMessageReportModel = alertMessageReportModelFactory.getAlertMessageReportModel(alertId,
						alertMessageThresholdKPI, this);

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

	@Override
	public Set<LogSeriesCollection> getLogTimeSeriesCollectionSet(boolean filtered) throws Exception {

		Map<Integer, LogEntry> logEntryMap = getLogEntryMap();

		Iterator<Integer> logEntryIndexIterator = null;

		if (filtered) {

			List<Integer> logEntryIndexList = getLogEntryIndexList();
			logEntryIndexIterator = logEntryIndexList.iterator();

		} else {

			Set<Integer> logEntryIndexSet = new TreeSet<Integer>(logEntryMap.keySet());
			logEntryIndexIterator = logEntryIndexSet.iterator();

		}

		Map<Integer, List<TimeSeriesDataItem>> alertIdTimeSeriesDataItemMap = new HashMap<Integer, List<TimeSeriesDataItem>>();
		Map<Integer, List<Double>> alertIdObservedKPIMap = new HashMap<Integer, List<Double>>();

		DateFormat modelDateFormat = getModelDateFormat();
		Locale locale = getLocale();

		TimeZone timeZone = modelDateFormat.getTimeZone();

		while (logEntryIndexIterator.hasNext()) {

			Integer logEntryKey = logEntryIndexIterator.next();

			AlertLogEntry alertLogEntry = (AlertLogEntry) logEntryMap.get(logEntryKey);

			Integer alertId = alertLogEntry.getAlertId();
			double observedKPI = alertLogEntry.getObservedKPI();

			Date logEntryDate = alertLogEntry.getLogEntryDate();

			if (logEntryDate != null) {

				List<TimeSeriesDataItem> alertMessageList = alertIdTimeSeriesDataItemMap.get(alertId);

				if (alertMessageList == null) {
					alertMessageList = new ArrayList<TimeSeriesDataItem>();
					alertIdTimeSeriesDataItemMap.put(alertId, alertMessageList);
				}

				RegularTimePeriod regularTimePeriod;
				regularTimePeriod = new Millisecond(logEntryDate, timeZone, locale);

				TimeSeriesDataItem tsdi = new TimeSeriesDataItem(regularTimePeriod, observedKPI);

				alertMessageList.add(tsdi);

			}

			List<Double> alertMessageKPIList = alertIdObservedKPIMap.get(alertId);

			if (alertMessageKPIList == null) {
				alertMessageKPIList = new ArrayList<Double>();
				alertIdObservedKPIMap.put(alertId, alertMessageKPIList);
			}

			alertMessageKPIList.add(observedKPI);

		}

		Map<Integer, AlertMessage> alertMessageMap = AlertMessageListProvider.getInstance().getAlertMessageMap();

		Set<LogSeriesCollection> logTimeSeriesCollectionSet = new TreeSet<LogSeriesCollection>();

		List<LogTimeSeries> criticalLogTimeSeriesList = new ArrayList<>();

		List<LogTimeSeries> normalLogTimeSeriesList = new ArrayList<>();

		for (Integer alertId : alertIdTimeSeriesDataItemMap.keySet()) {

			int timeSeriesEntryCount = 0;
			AlertMessage alertMessage = alertMessageMap.get(alertId);

			String messageIdStr = alertMessage.getMessageID();
			String kpiUnit = alertMessage.getDSSValueUnit();

			TimeSeries timeSeries = new TimeSeries(messageIdStr);

			List<TimeSeriesDataItem> tsdiList = alertIdTimeSeriesDataItemMap.get(alertId);

			for (TimeSeriesDataItem tsdi : tsdiList) {
				// JFreechart only have millisecond precision whereas Alert log
				// entries have nanosecond precision
				timeSeries.addOrUpdate(tsdi);
				timeSeriesEntryCount++;
			}

			List<Double> alertMessageObservedKPIList = alertIdObservedKPIMap.get(alertId);
			Collections.sort(alertMessageObservedKPIList);

			Long alertMessageThresholdKPI = alertIdThresholdKPIMap.get(alertId);

			AlertBoxAndWhiskerItem alertBoxAndWhiskerItem = AlertBoxAndWhiskerCalculator
					.calculateStatistics(alertMessageObservedKPIList, alertMessageThresholdKPI, kpiUnit);

			String chartColor = alertMessage.getChartColor();
			Color color = null;

			if (chartColor == null) {
				color = Color.BLACK;
			} else {
				color = MyColor.getColor(chartColor);
			}

			ValueMarker valueMarker = new ValueMarker(alertMessageThresholdKPI);

			// default display is set to false, except for critical alerts. will
			// set to true later down.
			boolean isCritical = Severity.CRITICAL.equals(alertMessage.getSeverity());

			LogTimeSeries alertLogTimeSeries = new LogTimeSeries(timeSeries, alertBoxAndWhiskerItem, color, valueMarker,
					timeSeriesEntryCount, true, isCritical);

			LogSeriesCollection ltsc = new LogSeriesCollection(messageIdStr);

			ltsc.addLogSeries(alertLogTimeSeries);

			logTimeSeriesCollectionSet.add(ltsc);

			if (isCritical) {
				criticalLogTimeSeriesList.add(alertLogTimeSeries);
			} else {
				normalLogTimeSeriesList.add(alertLogTimeSeries);
			}
		}

		// process the alertLogTimeSeries so that only 10 alert message chart
		// are visible by default
		int visibleCount = criticalLogTimeSeriesList.size();

		if (visibleCount < 10) {

			Comparator<LogTimeSeries> logTimeSeriesComparator = new Comparator<LogTimeSeries>() {

				@Override
				public int compare(LogTimeSeries o1, LogTimeSeries o2) {
					Integer o1Count = o1.getCount();
					Integer o2Count = o2.getCount();

					return o2Count.compareTo(o1Count);
				}
			};

			Collections.sort(normalLogTimeSeriesList, logTimeSeriesComparator);

			Iterator<LogTimeSeries> normalLogTimeSeriesIterator = normalLogTimeSeriesList.iterator();

			while (normalLogTimeSeriesIterator.hasNext()) {

				LogTimeSeries normalLogTimeSeries = normalLogTimeSeriesIterator.next();

				if (visibleCount < 10) {
					normalLogTimeSeries.setDefaultShowLogTimeSeries(true);
					visibleCount++;
				} else {
					break;
				}
			}
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

		LogEntryColumn[] reportTableColumns = new LogEntryColumn[8];

		reportTableColumns[0] = LogEntryColumn.LINE;
		reportTableColumns[1] = LogEntryColumn.TIMESTAMP;
		reportTableColumns[2] = LogEntryColumn.VERSION;
		reportTableColumns[3] = LogEntryColumn.MESSAGEID;
		reportTableColumns[4] = LogEntryColumn.OBSERVEDKPI;
		reportTableColumns[5] = LogEntryColumn.THRESHOLDKPI;
		reportTableColumns[6] = LogEntryColumn.NODEID;
		reportTableColumns[7] = LogEntryColumn.REQUESTORID;

		return reportTableColumns;
	}

}
