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

public class PEGA0018ReportModel extends AlertMessageReportModel {

	private static final long serialVersionUID = -8889727175209305065L;

	private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0018ReportModel.class);

	private List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList;

	private Pattern pattern;

	public PEGA0018ReportModel(long thresholdKPI, String kpiUnit, AlertLogEntryModel alertLogEntryModel) {

		super("PEGA0018", thresholdKPI, kpiUnit, alertLogEntryModel);

		String regex = "The requestor(.*?)has exceeded the PRThread count threshold by creating the PRThread";
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
			displayName = "Requestor";
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
			LOG.info("PEGA0018ReportModel - Could'nt match - [" + message + "]");
		}

		return alertMessageReportEntryKey;
	}

	public static void main(String[] args) {

		long before = System.currentTimeMillis();
		String message1 = "The requestor H60B9CB64B025F3EC4755F1F6A70D5340 has exceeded the PRThread count threshold by creating the PRThread pyNS_CPMPortal11_CPMWorkThread; there are now 21 PRThreads in the requestor. [pyNS_CPMPortal0_HomeTab, pyNS_CPMPortal2_CPMWorkThread, pyNS_CPMPortal3_CPMWorkThread, pyNS_CPMPortal4_CPMWorkThread, pyNS_CPMPortal20_CPMWorkThread, pyNS_CPMPortal10_CPMWorkThread, pyNS_CPMPortal5_CPMWorkThread, pyNS_CPMPortal21_CPMWorkThread, pyNS_CPMPortal11_CPMWorkThread, pyNS_CPMPortal6_CPMWorkThread, pyNS_CPMPortal22_CPMWorkThread, pyNS_CPMPortal7_CPMWorkThread, pyNS_CPMPortal23_CPMWorkThread, pyNS_CPMPortal8_CPMWorkThread, pyNS_CPMPortal24_CPMWorkThread, pyNS_CPMPortal9_CPMWorkThread, pyNS_CPMPortal25_CPMWorkThread, pyNS_CPMPortal26_CPMWorkThread, pyNS_CPMPortal27_CPMWorkThread, STANDARD, pyNS_CPMPortal19_CPMWorkThread]";

		String regex = "The requestor(.*?)has exceeded the PRThread count threshold by creating the PRThread";

		Pattern pattern = Pattern.compile(regex);

		Matcher patternMatcher = pattern.matcher(message1);
		boolean matches = patternMatcher.find();
		System.out.println(matches);

		if (matches) {
			System.out.println(patternMatcher.groupCount());
			System.out.println(patternMatcher.group(1));
		}
		long after = System.currentTimeMillis();

		System.out.println(after - before);
	}

}
