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

public class PEGA0005ReportModel extends AlertMessageReportModel {

	private static final long serialVersionUID = -8889727175209305065L;

	private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0005ReportModel.class);

	private List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList;

	private Pattern pattern;

	private Pattern opPattern;

	public PEGA0005ReportModel(long thresholdKPI, String kpiUnit, AlertLogEntryModel alertLogEntryModel) {

		super("PEGA0005", thresholdKPI, kpiUnit, alertLogEntryModel);

		// http://stackoverflow.com/questions/5254804/regex-optional-word-match
		String regex = "SQL\\:?:\\s*(.*?)\\s*(?:inserts?:\\s*(.*))?$";
		pattern = Pattern.compile(regex);

		String opRegex = "(.*?) took more than the threshold of";
		opPattern = Pattern.compile(opRegex);
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
			displayName = "SQL";
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

			if ((alertMessageReportEntryKey == null) || ("".equals(alertMessageReportEntryKey))) {

				Matcher opPatternMatcher = opPattern.matcher(message);
				matches = opPatternMatcher.find();

				if (matches) {
					alertMessageReportEntryKey = opPatternMatcher.group(1);
				}
			}
		}

		if (alertMessageReportEntryKey == null) {
			LOG.info("PEGA0005ReportModel - Could'nt match - [" + message + "]");
		}

		return alertMessageReportEntryKey;
	}

	public static void main(String[] args) {

		long before = System.currentTimeMillis();
		String message1 = "Database operation took more than the threshold of 500 ms: 518 ms	SQL: select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.pc_work where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.pc_Work_ProcessEmail where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.pfw_nps_survey where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.CPM_WORK where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.pfw_cs_work where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.pca_alert where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.scm_work where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.SCM_WORK where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.pca_work where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.pc_work_accel where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.LOANCONV_WORK where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.CPMFS_Work where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.pca_nps_survey where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.pfw_km_work where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.PC_WORK_DSM_BATCH where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.ABR_DICTIONARY_WORK where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.PCA_QUALITYRVW where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.simplereq_work where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.cpm_work where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.CPM_WORK_VIEWCLIENT where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.paf_work where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.pc_pegatask where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.ACCMGMT_WORK where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.ACCMGMT_MAINTAIN where pxCoverInsKey = ?";
		String message2 = "metadata.getPrimaryKeys took more than the threshold of 500 ms: 1,387 ms SQL:";

		// String regex = ".*SQL:(.+)[inserts:.*]?";
		// http://stackoverflow.com/questions/5254804/regex-optional-word-match
		String regex = "SQL\\:?:\\s*(.*?)\\s*(?:inserts?:\\s*(.*))?$";
		Pattern pattern = Pattern.compile(regex);

		String opRegex = "(.*?) took more than the threshold of";
		Pattern opPattern = Pattern.compile(opRegex);

		Matcher patternMatcher = pattern.matcher(message2);
		boolean matches = patternMatcher.find();
		System.out.println(matches);
		String result = null;

		if (matches) {
			System.out.println(patternMatcher.groupCount());
			System.out.println(result = patternMatcher.group(1));
		}

		if ((result == null) || ("".equals(result))) {
			Matcher opPatternMatcher = opPattern.matcher(message2);
			matches = opPatternMatcher.find();

			if (matches) {
				System.out.println(opPatternMatcher.groupCount());
				System.out.println(opPatternMatcher.group(1));
			}
		}
		long after = System.currentTimeMillis();

		System.out.println(after - before);
	}

}
