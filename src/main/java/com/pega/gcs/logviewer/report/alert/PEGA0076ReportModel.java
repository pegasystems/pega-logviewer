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

public class PEGA0076ReportModel extends AlertMessageReportModel {

	private static final long serialVersionUID = -8889727175209305065L;

	private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0076ReportModel.class);

	private List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList;

	private Pattern pattern;

	public PEGA0076ReportModel(long thresholdKPI, String kpiUnit, AlertLogEntryModel alertLogEntryModel) {

		super("PEGA0076", thresholdKPI, kpiUnit, alertLogEntryModel);

		String regex = "Node (.*?) of service (.*?) became UNREACHABLE";
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
			displayName = "NodeId - Service";
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
			String nodeId = patternMatcher.group(1).trim();
			String service = patternMatcher.group(2).trim();

			StringBuffer sb = new StringBuffer();
			sb.append(nodeId);
			sb.append(" - ");
			sb.append(service);

			alertMessageReportEntryKey = sb.toString();
		}

		if (alertMessageReportEntryKey == null) {
			LOG.info("PEGA0076ReportModel - Could'nt match - [" + message + "]");
		}

		return alertMessageReportEntryKey;
	}

	public static void main(String[] args) {

		long before = System.currentTimeMillis();
		String message = "Node <node name> of service <service name> became UNREACHABLE";

		String regex = "Node (.*?) of service (.*?) became UNREACHABLE";

		Pattern pattern = Pattern.compile(regex);

		Matcher patternMatcher = pattern.matcher(message);
		boolean matches = patternMatcher.find();
		System.out.println(matches);

		if (matches) {
			System.out.println(patternMatcher.groupCount());
			System.out.println(patternMatcher.group(1));
			System.out.println(patternMatcher.group(2));
		}

		long after = System.currentTimeMillis();

		System.out.println(after - before);
	}

}
