/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.statistics.BoxAndWhiskerCalculator;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class Log4jLogEntryModel extends LogEntryModel {

	private static final Log4j2Helper LOG = new Log4j2Helper(Log4jLogEntryModel.class);

	private static final String TSC_MEMORY = "Memory (MB)";

	public static final String TS_TOTAL_MEMORY = "Total Memory";

	public static final String TS_FREE_MEMORY = "Free Memory";

	public static final String TS_USED_MEMORY = "Used Memory";

	public static final String TS_SHARED_PAGE_MEMORY = "Shared Page Memory";

	public static final String TS_REQUESTOR_COUNT = "Requestor Count";

	public static final String TS_TOTAL_CPU_SECONDS = "Total CPU Seconds";

	public static final String TS_NUMBER_OF_THREADS = "Number Of Threads";

	public static final String IM_SYSTEM_START = "System Start";

	public static final String IM_THREAD_DUMP = "Thread Dump";

	public static final String IM_EXCEPTIONS = "Exceptions";

	private List<SystemStart> systemStartList;

	private Map<String, List<Integer>> errorLogEntryIndexMap;

	private List<Integer> threadDumpLogEntryIndexList;

	private int lastProcessedIndex;

	private Set<LogIntervalMarker> logIntervalMarkerSet;

	private List<Log4jMeasurePattern> customMeasurePatternList;

	private MasterAgentSystemPattern masterAgentSystemPattern;

	public Log4jLogEntryModel(DateFormat dateFormat, Locale locale, TimeZone displayTimezone) {

		super(dateFormat, locale);

		systemStartList = new ArrayList<SystemStart>();
		errorLogEntryIndexMap = new HashMap<String, List<Integer>>();
		threadDumpLogEntryIndexList = new ArrayList<Integer>();
		lastProcessedIndex = -1;

		if (displayTimezone != null) {
			setDisplayDateFormatTimeZone(displayTimezone);
		}

		CustomMeasurePatternProvider cmpProvider = CustomMeasurePatternProvider.getInstance();

		customMeasurePatternList = cmpProvider.getCustomMeasurePatternList();

		masterAgentSystemPattern = null;
	}

	@Override
	protected void postProcess(LogEntry logEntry, ArrayList<String> logEntryValueList) {

	}

	@Override
	public void setModelDateFormatTimeZone(TimeZone modelTimeZone) {

		super.setModelDateFormatTimeZone(modelTimeZone);

		LOG.info("Model timezone changed to " + modelTimeZone);

		Map<LogEntryColumn, Integer> logEntryColumnIndexMap = getLogEntryColumnIndexMap();

		int timestampColumnIndex = logEntryColumnIndexMap.get(LogEntryColumn.TIMESTAMP);

		int deltaColumnIndex = logEntryColumnIndexMap.get(LogEntryColumn.DELTA);

		DateFormat modelDateFormat = getModelDateFormat();

		if (timestampColumnIndex != -1) {

			// update existing entries in map.
			Map<Integer, LogEntry> logEntryMap = getLogEntryMap();

			LOG.info("Updating " + logEntryMap.size() + " records because of model timezone change");

			long lowerDomainRange = -1;
			long upperDomainRange = -1;

			Set<Integer> logEntryIndexSet = new TreeSet<Integer>(logEntryMap.keySet());

			Iterator<Integer> logEntryIndexIterator = logEntryIndexSet.iterator();

			Log4jLogEntry prevLog4jLogEntry = null;

			while (logEntryIndexIterator.hasNext()) {

				Integer logEntryKey = logEntryIndexIterator.next();

				Log4jLogEntry log4jLogEntry = (Log4jLogEntry) logEntryMap.get(logEntryKey);

				log4jLogEntry.updateTimestamp(modelDateFormat, timestampColumnIndex, deltaColumnIndex,
						prevLog4jLogEntry);

				prevLog4jLogEntry = log4jLogEntry;

				long logEntryTime = log4jLogEntry.getLogEntryDate().getTime();

				if (lowerDomainRange == -1) {
					lowerDomainRange = logEntryTime - 1;
				} else {
					lowerDomainRange = Math.min(lowerDomainRange, logEntryTime);
				}

				if (upperDomainRange == -1) {
					upperDomainRange = logEntryTime;
				} else {
					upperDomainRange = Math.max(upperDomainRange, logEntryTime);
				}

			}

			if ((lowerDomainRange != -1) && (upperDomainRange != -1)) {
				setLowerDomainRange(lowerDomainRange);
				setUpperDomainRange(upperDomainRange);
			}
		}
	}

	private void addToMap(Map<String, List<TimeSeriesDataItem>> log4jMessageMap, String key, Date logEntryDate,
			TimeZone timezone, Locale locale, double value) {

		List<TimeSeriesDataItem> logMessageList = log4jMessageMap.get(key);

		if (logMessageList == null) {
			logMessageList = new ArrayList<TimeSeriesDataItem>();
			log4jMessageMap.put(key, logMessageList);
		}

		RegularTimePeriod regularTimePeriod;

		regularTimePeriod = new Millisecond(logEntryDate, timezone, locale);

		TimeSeriesDataItem tsdi = new TimeSeriesDataItem(regularTimePeriod, value);

		logMessageList.add(tsdi);

	}

	private void processSystemStr(String tsKey, List<String> fieldNameList, List<String> valueStrList,
			Date logEntryDate, Map<String, List<TimeSeriesDataItem>> log4jMessageMap,
			Map<String, List<Double>> log4jMessageDataMap) {

		int fieldIndex = fieldNameList.indexOf(tsKey);

		if (fieldIndex != -1) {

			DateFormat modelDateFormat = getModelDateFormat();
			Locale locale = getLocale();
			NumberFormat numberFormat = getNumberFormat();

			TimeZone timeZone = modelDateFormat.getTimeZone();

			String valueStr = null;

			try {

				valueStr = valueStrList.get(fieldIndex);
				double value = numberFormat.parse(valueStr).doubleValue();

				addToMap(log4jMessageMap, tsKey, logEntryDate, timeZone, locale, value);

				List<Double> log4jMessageDataList = log4jMessageDataMap.get(tsKey);

				if (log4jMessageDataList == null) {
					log4jMessageDataList = new LinkedList<Double>();
					log4jMessageDataMap.put(tsKey, log4jMessageDataList);
				}

				log4jMessageDataList.add(value);

			} catch (Exception e) {
				LOG.error("Error adding to map:" + valueStr, e);
			}
		} else {
			LOG.error("could not find key" + tsKey);
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

		Map<String, List<TimeSeriesDataItem>> log4jMessageMap = new HashMap<String, List<TimeSeriesDataItem>>();
		Map<String, List<Double>> log4jMessageDataMap = new HashMap<String, List<Double>>();

		List<String> logEntryColumnList = getLogEntryColumnList();

		int messageIndex = logEntryColumnList.indexOf(LogEntryColumn.MESSAGE.getColumnId());

		DateFormat modelDateFormat = getModelDateFormat();
		Locale locale = getLocale();
		NumberFormat numberFormat = getNumberFormat();

		TimeZone timeZone = modelDateFormat.getTimeZone();

		Set<LogIntervalMarker> logIntervalMarkerSet = getLogIntervalMarkerSet();
		logIntervalMarkerSet.clear();

		Map<String, Color> logTimeSeriesColorMap = LogTimeSeriesColor.getLogTimeSeriesColorMap();

		Color systemStartColor = logTimeSeriesColorMap.get(IM_SYSTEM_START);
		Color threadDumpColor = logTimeSeriesColorMap.get(IM_THREAD_DUMP);
		Color exceptionsColor = logTimeSeriesColorMap.get(IM_EXCEPTIONS);

		LogIntervalMarker ssLogIntervalMarker = null;
		LogIntervalMarker tdLogIntervalMarker = null;
		LogIntervalMarker exLogIntervalMarker = null;

		while (logEntryIndexIterator.hasNext()) {

			Integer logEntryKey = logEntryIndexIterator.next();
			Log4jLogEntry log4jLogEntry = (Log4jLogEntry) logEntryMap.get(logEntryKey);

			Date logEntryDate = log4jLogEntry.getLogEntryDate();

			// date can be null in case of corrupted log file
			if (logEntryDate != null) {

				long logEntryTime = logEntryDate.getTime();

				boolean sysdateEntry = log4jLogEntry.isSysdateEntry();

				ArrayList<String> logEntryValueList = log4jLogEntry.getLogEntryValueList();

				if (sysdateEntry) {

					// ArrayList<String> logEntryValueList =
					// log4jLogEntry.getLogEntryValueList();

					String systemStr = logEntryValueList.get(messageIndex);

					// TODO: on some systems like GC, there are multiple spaces
					// before '%m%n' pattern, for ex ' - %m%n'.
					// this causes the system string to have a additional space
					// in the front.hence it is required to
					// trim the message to match the entries below
					systemStr = systemStr.trim();

					if (masterAgentSystemPattern == null) {
						masterAgentSystemPattern = MasterAgentSystemPattern.getMasterAgentSystemPattern(systemStr);
					}

					if (masterAgentSystemPattern != null) {

						Pattern pattern = masterAgentSystemPattern.getPattern();

						Matcher matcher = pattern.matcher(systemStr);

						if (matcher.matches()) {

							Map<String, Integer> fieldNameGroupIndexMap;
							fieldNameGroupIndexMap = masterAgentSystemPattern.getFieldNameGroupIndexMap();

							List<String> fieldNameList = new ArrayList<>(fieldNameGroupIndexMap.keySet());
							List<String> valueStrList = new ArrayList<>();

							for (String fieldName : fieldNameList) {

								int groupIndex = fieldNameGroupIndexMap.get(fieldName);
								String valueStr = matcher.group(groupIndex);
								valueStrList.add(valueStr);
							}

							String tsKey;
							int fieldIndex;

							// TOTAL_MEMORY
							tsKey = TS_TOTAL_MEMORY;
							fieldIndex = fieldNameList.indexOf(tsKey);

							double totalMemory = -1;

							if (fieldIndex != -1) {

								String totalMemoryStr = null;

								try {
									totalMemoryStr = valueStrList.get(fieldIndex);
									totalMemory = numberFormat.parse(totalMemoryStr).doubleValue();

									// change to MB
									totalMemory = totalMemory / (1024 * 1024);
									addToMap(log4jMessageMap, tsKey, logEntryDate, timeZone, locale, totalMemory);
								} catch (Exception e) {
									LOG.error("Error adding total memory to map:" + totalMemoryStr, e);
								}
							}

							tsKey = TS_FREE_MEMORY;
							fieldIndex = fieldNameList.indexOf(tsKey);

							if (fieldIndex != -1) {

								String freeMemoryStr = null;

								try {

									freeMemoryStr = valueStrList.get(fieldIndex);
									double freeMemory = numberFormat.parse(freeMemoryStr).doubleValue();

									freeMemory = freeMemory / (1024 * 1024);

									if (totalMemory != -1) {
										tsKey = TS_USED_MEMORY;
										addToMap(log4jMessageMap, tsKey, logEntryDate, timeZone, locale,
												(totalMemory - freeMemory));
									} else {
										addToMap(log4jMessageMap, tsKey, logEntryDate, timeZone, locale, freeMemory);
									}

									List<Double> log4jMessageDataList = log4jMessageDataMap.get(tsKey);

									if (log4jMessageDataList == null) {
										log4jMessageDataList = new ArrayList<Double>();
										log4jMessageDataMap.put(tsKey, log4jMessageDataList);
									}

									log4jMessageDataList.add(freeMemory);

								} catch (Exception e) {
									LOG.error("Error adding free memory to map:" + freeMemoryStr, e);
								}
							}

							// TOTAL_CPU_SECONDS
							tsKey = TS_TOTAL_CPU_SECONDS;
							fieldIndex = fieldNameList.indexOf(tsKey);

							if (fieldIndex != -1) {

								String totalCPUStr = null;

								try {

									totalCPUStr = valueStrList.get(fieldIndex);
									double totalCPU = numberFormat.parse(totalCPUStr).doubleValue();

									addToMap(log4jMessageMap, tsKey, logEntryDate, timeZone, locale, totalCPU);

									List<Double> log4jMessageDataList = log4jMessageDataMap.get(tsKey);

									if (log4jMessageDataList == null) {
										log4jMessageDataList = new ArrayList<Double>();
										log4jMessageDataMap.put(tsKey, log4jMessageDataList);
									}

									log4jMessageDataList.add(totalCPU);

								} catch (Exception e) {
									LOG.error("Error adding total cpu to map:" + totalCPUStr, e);
								}
							}

							// REQUESTOR_COUNT
							tsKey = TS_REQUESTOR_COUNT;
							fieldIndex = fieldNameList.indexOf(tsKey);

							if (fieldIndex != -1) {

								String requestorCountStr = null;

								try {

									requestorCountStr = valueStrList.get(fieldIndex);
									double requestorCount = numberFormat.parse(requestorCountStr).doubleValue();

									addToMap(log4jMessageMap, tsKey, logEntryDate, timeZone, locale, requestorCount);

									List<Double> log4jMessageDataList = log4jMessageDataMap.get(tsKey);

									if (log4jMessageDataList == null) {
										log4jMessageDataList = new LinkedList<Double>();
										log4jMessageDataMap.put(tsKey, log4jMessageDataList);
									}

									log4jMessageDataList.add(requestorCount);

								} catch (Exception e) {
									LOG.error("Error adding requestor count to map:" + requestorCountStr, e);
								}
							}

							// SHARED_PAGE_MEMORY
							tsKey = TS_SHARED_PAGE_MEMORY;
							fieldIndex = fieldNameList.indexOf(tsKey);

							if (fieldIndex != -1) {

								String sharedPageMemoryStr = null;

								try {

									sharedPageMemoryStr = valueStrList.get(fieldIndex);
									double sharedPageMemory = numberFormat.parse(sharedPageMemoryStr).doubleValue();

									addToMap(log4jMessageMap, tsKey, logEntryDate, timeZone, locale, sharedPageMemory);

									List<Double> log4jMessageDataList = log4jMessageDataMap.get(tsKey);

									if (log4jMessageDataList == null) {
										log4jMessageDataList = new LinkedList<Double>();
										log4jMessageDataMap.put(tsKey, log4jMessageDataList);
									}

									log4jMessageDataList.add(sharedPageMemory);

								} catch (Exception e) {
									LOG.error("Error adding shared page memory to map:" + sharedPageMemoryStr, e);
								}
							}

							// NUMBER_OF_THREADS
							tsKey = TS_NUMBER_OF_THREADS;
							fieldIndex = fieldNameList.indexOf(tsKey);

							if (fieldIndex != -1) {

								String numberOfThreadsStr = null;

								try {

									numberOfThreadsStr = valueStrList.get(fieldIndex);
									double numberOfThreads = numberFormat.parse(numberOfThreadsStr).doubleValue();

									addToMap(log4jMessageMap, tsKey, logEntryDate, timeZone, locale, numberOfThreads);

									List<Double> log4jMessageDataList = log4jMessageDataMap.get(tsKey);

									if (log4jMessageDataList == null) {
										log4jMessageDataList = new LinkedList<Double>();
										log4jMessageDataMap.put(tsKey, log4jMessageDataList);
									}

									log4jMessageDataList.add(numberOfThreads);

								} catch (Exception e) {
									LOG.error("Error adding number of threads to map:" + numberOfThreadsStr, e);
								}
							}
						} else {
							LOG.error("unable to match master agent system string: " + systemStr);
						}
					} else {
						LOG.error("unable to get MasterAgentSystemPattern for system string: " + systemStr);
					}

				}

				// process custom measures
				for (Log4jMeasurePattern cmp : customMeasurePatternList) {

					String name = cmp.getName();
					Pattern pattern = cmp.getPattern();

					String messageStr = logEntryValueList.get(messageIndex);

					Matcher matcher = pattern.matcher(messageStr);

					String customMeasureStr = null;

					if (matcher.matches()) {
						customMeasureStr = matcher.group(3);

						if (customMeasureStr != null) {

							double customMeasure;
							try {
								customMeasure = numberFormat.parse(customMeasureStr).doubleValue();

								addToMap(log4jMessageMap, name, logEntryDate, timeZone, locale, customMeasure);

								List<Double> log4jMessageDataList = log4jMessageDataMap.get(name);

								if (log4jMessageDataList == null) {
									log4jMessageDataList = new ArrayList<Double>();
									log4jMessageDataMap.put(name, log4jMessageDataList);
								}

								log4jMessageDataList.add(customMeasure);

							} catch (Exception e) {
								LOG.error("Error adding custom measure to map:" + customMeasureStr, e);
							}
						}
					}
				}

				// setup interval markers
				if (log4jLogEntry instanceof Log4jLogThreadDumpEntry) {

					if (tdLogIntervalMarker == null) {

						tdLogIntervalMarker = new LogIntervalMarker(IM_THREAD_DUMP, threadDumpColor, true, true);

						logIntervalMarkerSet.add(tdLogIntervalMarker);
					}

					ValueMarker vm = new ValueMarker(logEntryTime, threadDumpColor, new BasicStroke(1.0f),
							threadDumpColor, new BasicStroke(1.0f), 1.0f);

					tdLogIntervalMarker.addValueMarker(vm);

				} else if (log4jLogEntry instanceof Log4jLogSystemStartEntry) {

					if (ssLogIntervalMarker == null) {

						ssLogIntervalMarker = new LogIntervalMarker(IM_SYSTEM_START, systemStartColor, true, true);

						logIntervalMarkerSet.add(ssLogIntervalMarker);
					}

					ValueMarker vm = new ValueMarker(logEntryTime, systemStartColor, new BasicStroke(1.0f),
							systemStartColor, new BasicStroke(1.0f), 1.0f);

					ssLogIntervalMarker.addValueMarker(vm);

				} else if (log4jLogEntry instanceof Log4jLogExceptionEntry) {

					if (exLogIntervalMarker == null) {

						exLogIntervalMarker = new LogIntervalMarker(IM_EXCEPTIONS, exceptionsColor, true, true);

						logIntervalMarkerSet.add(exLogIntervalMarker);
					}

					ValueMarker vm = new ValueMarker(logEntryTime, exceptionsColor, new BasicStroke(1.0f),
							exceptionsColor, new BasicStroke(1.0f), 1.0f);

					exLogIntervalMarker.addValueMarker(vm);

				}
			}
		}

		LogSeriesCollection ltsc;
		ValueMarker valueMarker = null;
		BoxAndWhiskerItem boxAndWhiskerItem = null;
		List<Double> l4jmdList;

		Set<LogSeriesCollection> logTimeSeriesCollectionSet = new TreeSet<LogSeriesCollection>();

		List<TimeSeriesDataItem> tsdiList = log4jMessageMap.get(TS_TOTAL_MEMORY);

		if ((tsdiList != null) && (tsdiList.size() > 0)) {

			ltsc = new LogSeriesCollection(TSC_MEMORY);
			logTimeSeriesCollectionSet.add(ltsc);

			int timeSeriesEntryCount = 0;
			TimeSeries tsTotalMemory = new TimeSeries(TS_TOTAL_MEMORY);

			for (TimeSeriesDataItem tsdi : tsdiList) {
				tsTotalMemory.add(tsdi);
				timeSeriesEntryCount++;
			}

			Color color = logTimeSeriesColorMap.get(TS_TOTAL_MEMORY);

			LogTimeSeries logTimeSeries = new LogTimeSeries(tsTotalMemory, null, color, valueMarker,
					timeSeriesEntryCount, false, true);

			ltsc.addLogSeries(logTimeSeries);

			tsdiList = log4jMessageMap.get(TS_USED_MEMORY);

			if ((tsdiList != null) && (tsdiList.size() > 0)) {

				timeSeriesEntryCount = 0;
				TimeSeries tsFreeMemory = new TimeSeries(TS_USED_MEMORY);

				for (TimeSeriesDataItem tsdi : tsdiList) {
					tsFreeMemory.add(tsdi);
					timeSeriesEntryCount++;
				}

				l4jmdList = log4jMessageDataMap.get(TS_USED_MEMORY);

				if ((l4jmdList != null) && (l4jmdList.size() > 0)) {
					boxAndWhiskerItem = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(l4jmdList);
				}

				color = logTimeSeriesColorMap.get(TS_USED_MEMORY);

				logTimeSeries = new LogTimeSeries(tsFreeMemory, boxAndWhiskerItem, color, valueMarker,
						timeSeriesEntryCount, false, true);

				ltsc.addLogSeries(logTimeSeries);

			}
		}

		tsdiList = log4jMessageMap.get(TS_TOTAL_CPU_SECONDS);

		if ((tsdiList != null) && (tsdiList.size() > 0)) {

			ltsc = new LogSeriesCollection(TS_TOTAL_CPU_SECONDS);
			logTimeSeriesCollectionSet.add(ltsc);

			l4jmdList = null;
			boxAndWhiskerItem = null;

			int timeSeriesEntryCount = 0;
			TimeSeries tsTotalCPU = new TimeSeries(TS_TOTAL_CPU_SECONDS);

			for (TimeSeriesDataItem tsdi : tsdiList) {
				tsTotalCPU.add(tsdi);
				timeSeriesEntryCount++;
			}

			l4jmdList = log4jMessageDataMap.get(TS_TOTAL_CPU_SECONDS);

			if ((l4jmdList != null) && (l4jmdList.size() > 0)) {
				boxAndWhiskerItem = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(l4jmdList);
			}

			Color color = logTimeSeriesColorMap.get(TS_TOTAL_CPU_SECONDS);

			LogTimeSeries logTimeSeries = new LogTimeSeries(tsTotalCPU, boxAndWhiskerItem, color, valueMarker,
					timeSeriesEntryCount, false, true);

			ltsc.addLogSeries(logTimeSeries);

		}

		tsdiList = log4jMessageMap.get(TS_REQUESTOR_COUNT);

		if ((tsdiList != null) && (tsdiList.size() > 0)) {

			ltsc = new LogSeriesCollection(TS_REQUESTOR_COUNT);
			logTimeSeriesCollectionSet.add(ltsc);

			l4jmdList = null;
			boxAndWhiskerItem = null;

			int timeSeriesEntryCount = 0;
			TimeSeries tsRequestorCount = new TimeSeries(TS_REQUESTOR_COUNT);

			for (TimeSeriesDataItem tsdi : tsdiList) {
				tsRequestorCount.add(tsdi);
				timeSeriesEntryCount++;
			}

			l4jmdList = log4jMessageDataMap.get(TS_REQUESTOR_COUNT);

			if ((l4jmdList != null) && (l4jmdList.size() > 0)) {
				boxAndWhiskerItem = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(l4jmdList);
			}

			Color color = logTimeSeriesColorMap.get(TS_REQUESTOR_COUNT);

			LogTimeSeries logTimeSeries = new LogTimeSeries(tsRequestorCount, boxAndWhiskerItem, color, valueMarker,
					timeSeriesEntryCount, false, true);

			ltsc.addLogSeries(logTimeSeries);

		}

		tsdiList = log4jMessageMap.get(TS_SHARED_PAGE_MEMORY);

		if ((tsdiList != null) && (tsdiList.size() > 0)) {

			ltsc = new LogSeriesCollection(TS_SHARED_PAGE_MEMORY);
			logTimeSeriesCollectionSet.add(ltsc);

			l4jmdList = null;
			boxAndWhiskerItem = null;

			int timeSeriesEntryCount = 0;
			TimeSeries tsSharedPageMemory = new TimeSeries(TS_SHARED_PAGE_MEMORY);

			for (TimeSeriesDataItem tsdi : tsdiList) {
				tsSharedPageMemory.add(tsdi);
				timeSeriesEntryCount++;
			}

			l4jmdList = log4jMessageDataMap.get(TS_SHARED_PAGE_MEMORY);

			if ((l4jmdList != null) && (l4jmdList.size() > 0)) {
				boxAndWhiskerItem = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(l4jmdList);
			}

			Color color = logTimeSeriesColorMap.get(TS_SHARED_PAGE_MEMORY);

			LogTimeSeries logTimeSeries = new LogTimeSeries(tsSharedPageMemory, boxAndWhiskerItem, color, valueMarker,
					timeSeriesEntryCount, false, false);

			ltsc.addLogSeries(logTimeSeries);

		}

		tsdiList = log4jMessageMap.get(TS_NUMBER_OF_THREADS);

		if ((tsdiList != null) && (tsdiList.size() > 0)) {

			ltsc = new LogSeriesCollection(TS_NUMBER_OF_THREADS);
			logTimeSeriesCollectionSet.add(ltsc);

			l4jmdList = null;
			boxAndWhiskerItem = null;

			int timeSeriesEntryCount = 0;
			TimeSeries tsSharedPageMemory = new TimeSeries(TS_NUMBER_OF_THREADS);

			for (TimeSeriesDataItem tsdi : tsdiList) {
				tsSharedPageMemory.add(tsdi);
				timeSeriesEntryCount++;
			}

			l4jmdList = log4jMessageDataMap.get(TS_NUMBER_OF_THREADS);

			if ((l4jmdList != null) && (l4jmdList.size() > 0)) {
				boxAndWhiskerItem = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(l4jmdList);
			}

			Color color = logTimeSeriesColorMap.get(TS_NUMBER_OF_THREADS);

			LogTimeSeries logTimeSeries = new LogTimeSeries(tsSharedPageMemory, boxAndWhiskerItem, color, valueMarker,
					timeSeriesEntryCount, false, true);

			ltsc.addLogSeries(logTimeSeries);

		}

		// TODO need a smart way to do this. possibly have all the pattern
		// externalise as measures?

		for (Log4jMeasurePattern cmp : customMeasurePatternList) {

			String cmpName = cmp.getName();

			tsdiList = log4jMessageMap.get(cmpName);

			if ((tsdiList != null) && (tsdiList.size() > 0)) {

				ltsc = new LogSeriesCollection(cmpName);
				logTimeSeriesCollectionSet.add(ltsc);

				l4jmdList = null;
				boxAndWhiskerItem = null;

				int timeSeriesEntryCount = 0;
				TimeSeries tsCustomMeasure = new TimeSeries(cmpName);

				for (TimeSeriesDataItem tsdi : tsdiList) {
					tsCustomMeasure.addOrUpdate(tsdi);
					timeSeriesEntryCount++;
				}

				l4jmdList = log4jMessageDataMap.get(cmpName);

				if ((l4jmdList != null) && (l4jmdList.size() > 0)) {
					boxAndWhiskerItem = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(l4jmdList);
				}

				Color color = Color.BLACK;

				LogTimeSeries logTimeSeries = new LogTimeSeries(tsCustomMeasure, boxAndWhiskerItem, color, valueMarker,
						timeSeriesEntryCount, false, false);

				ltsc.addLogSeries(logTimeSeries);

			}
		}

		return logTimeSeriesCollectionSet;
	}

	@Override
	public Set<LogIntervalMarker> getLogIntervalMarkerSet() {

		// logIntervalMarkerCollectionSet is populated in
		// getLogTimeSeriesCollectionSet
		// in order to save additional looping
		if (logIntervalMarkerSet == null) {
			logIntervalMarkerSet = new TreeSet<>();
		}

		return logIntervalMarkerSet;
	}

	@Override
	public String getTypeName() {
		return "Log4j";
	}

	/**
	 * @return the systemStartList
	 */
	public List<SystemStart> getSystemStartList() {
		return systemStartList;
	}

	public Map<String, List<Integer>> getErrorLogEntryIndexMap() {
		return errorLogEntryIndexMap;
	}

	/**
	 * @return the threadDumpLogEntryIndexList
	 */
	public List<Integer> getThreadDumpLogEntryIndexList() {
		return threadDumpLogEntryIndexList;
	}

	@Override
	public LogEntryColumn[] getReportTableColumns() {

		LogEntryColumn[] reportTableColumns = new LogEntryColumn[8];

		reportTableColumns[0] = LogEntryColumn.LINE;
		reportTableColumns[1] = LogEntryColumn.TIMESTAMP;
		reportTableColumns[2] = LogEntryColumn.THREAD;
		reportTableColumns[3] = LogEntryColumn.APP;
		reportTableColumns[4] = LogEntryColumn.LOGGER;
		reportTableColumns[5] = LogEntryColumn.LEVEL;
		reportTableColumns[6] = LogEntryColumn.STACK;
		reportTableColumns[7] = LogEntryColumn.USERID;

		return reportTableColumns;
	}

}
