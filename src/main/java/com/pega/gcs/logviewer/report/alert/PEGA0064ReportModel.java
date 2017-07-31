/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.report.alert;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.AlertLogEntry;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntryColumn;

public class PEGA0064ReportModel extends AlertMessageReportModel {

	private static final long serialVersionUID = -8889727175209305065L;

	private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0064ReportModel.class);

	private List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList;

	private Pattern pattern;

	public PEGA0064ReportModel(long thresholdKPI, String kpiUnit, AlertLogEntryModel alertLogEntryModel) {

		super("PEGA0064", thresholdKPI, kpiUnit, alertLogEntryModel);

		String regex = "For strategy \\[(.*?)\\] component \\[(.*?)\\] in data flow \\[(.*?)\\]";
		pattern = Pattern.compile(regex);
	}

	@Override
	protected List<AlertBoxAndWhiskerReportColumn> getAlertMessageReportColumnList() {

		if (alertMessageReportColumnList == null) {
			alertMessageReportColumnList = new ArrayList<AlertBoxAndWhiskerReportColumn>();

			String displayName;
			int prefColWidth;
			int hAlignment;
			boolean filterable;
			AlertBoxAndWhiskerReportColumn amReportColumn = null;

			// first column data is the key
			displayName = "Component [Strategy] [Data Flow]";
			prefColWidth = 500;
			hAlignment = SwingConstants.LEFT;
			filterable = true;
			amReportColumn = new AlertBoxAndWhiskerReportColumn(AlertBoxAndWhiskerReportColumn.KEY, displayName, prefColWidth, hAlignment, filterable);

			alertMessageReportColumnList.add(amReportColumn);

			List<AlertBoxAndWhiskerReportColumn> defaultAlertMessageReportColumnList = AlertBoxAndWhiskerReportColumn.getDefaultAlertMessageReportColumnList();

			alertMessageReportColumnList.addAll(defaultAlertMessageReportColumnList);
		}

		return alertMessageReportColumnList;
	}

	@Override
	public String getAlertMessageReportEntryKey(AlertLogEntry alertLogEntry, ArrayList<String> logEntryValueList) {

		String alertMessageReportEntryKey = null;

		AlertLogEntryModel alertLogEntryModel = getAlertLogEntryModel();

		List<String> logEntryColumnList = alertLogEntryModel.getLogEntryColumnList();

		int messageIndex = logEntryColumnList.indexOf(LogEntryColumn.MESSAGE.getColumnId());
		String message = logEntryValueList.get(messageIndex);

		Matcher patternMatcher = pattern.matcher(message);
		boolean matches = patternMatcher.find();

		if (matches) {

			String component = patternMatcher.group(2).trim();
			String strategyShape = patternMatcher.group(1).trim();
			String dataflow = patternMatcher.group(3).trim();

			StringBuffer sb = new StringBuffer();
			sb.append(component);
			sb.append(" [");
			sb.append(strategyShape);
			sb.append("] [");
			sb.append(dataflow);
			sb.append("]");

			alertMessageReportEntryKey = sb.toString();
		}

		if (alertMessageReportEntryKey == null) {
			LOG.info("PEGA0064ReportModel - Could'nt match - [" + message + "]");
		}

		return alertMessageReportEntryKey;
	}

	public static void main(String[] args) {

		long before = System.currentTimeMillis();
		String message1 = "Strategy component generated more rows than the threshold of 10000 rows: 10001 rows. For strategy [componentWithManyRows.strategyName] component [componentWithManyRows.componentName] in data flow [alertParameters.getDataFlowName()]";

		String regex = "For strategy \\[(.*?)\\] component \\[(.*?)\\] in data flow \\[(.*?)\\]";

		Pattern pattern = Pattern.compile(regex);

		Matcher patternMatcher = pattern.matcher(message1);
		boolean matches = patternMatcher.find();
		System.out.println(matches);

		if (matches) {
			System.out.println(patternMatcher.groupCount());
			System.out.println(patternMatcher.group(1));
			System.out.println(patternMatcher.group(2));
			System.out.println(patternMatcher.group(3));
		}
		long after = System.currentTimeMillis();

		System.out.println(after - before);
	}

}
