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

public class PEGA0011ReportModel extends AlertMessageReportModel {

	private static final long serialVersionUID = -8889727175209305065L;

	private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0011ReportModel.class);

	private List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList;

	private Pattern pattern;

	public PEGA0011ReportModel(long thresholdKPI, String kpiUnit, AlertLogEntryModel alertLogEntryModel) {

		super("PEGA0011", thresholdKPI, kpiUnit, alertLogEntryModel);

		String regex = "(.*?)has exceeded the threshold of";
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
			displayName = "Service Rule";
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
			alertMessageReportEntryKey = patternMatcher.group(1).trim();
		}

		if (alertMessageReportEntryKey == null) {
			LOG.info("PEGA0011ReportModel - Could'nt match - [" + message + "]");
		}

		return alertMessageReportEntryKey;
	}

	public static void main(String[] args) {

		String message = "RULE-CONNECT-HTTP DATA-FIND-SEARCH PYSEARCHHTTPCONNECT #20130919T004245.583 GMT has exceeded the threshold of 1,000 ms: 6,656 ms;pxConnectOutMapReqElapsed=6;pxConnectClientResponseElapsed=6642;pxConnectInMapReqElapsed=5;";

		String serviceRuleRegex = "(.*?)has exceeded the threshold of";
		Pattern serviceRulePattern = Pattern.compile(serviceRuleRegex);

		Matcher serviceRulePatternMatcher = serviceRulePattern.matcher(message);
		boolean matches = serviceRulePatternMatcher.find();
		System.out.println(matches);

		if (matches) {
			System.out.println(serviceRulePatternMatcher.groupCount());
			System.out.println(serviceRulePatternMatcher.group(1));
		}
	}

}
