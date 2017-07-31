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

public class PEGA0060ReportModel extends AlertMessageReportModel {

	private static final long serialVersionUID = -8889727175209305065L;

	private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0060ReportModel.class);

	private List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList;

	private Pattern pattern;

	public PEGA0060ReportModel(long thresholdKPI, String kpiUnit, AlertLogEntryModel alertLogEntryModel) {

		super("PEGA0060", thresholdKPI, kpiUnit, alertLogEntryModel);

		String regex = "SQL \\:(.*)";
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
			displayName = "IH READ SQL";
			prefColWidth = 500;
			hAlignment = SwingConstants.LEFT;
			filterable = true;
			amReportColumn = new AlertBoxAndWhiskerReportColumn(AlertBoxAndWhiskerReportColumn.KEY, displayName,
					prefColWidth, hAlignment, filterable);

			alertMessageReportColumnList.add(amReportColumn);

			List<AlertBoxAndWhiskerReportColumn> defaultAlertMessageReportColumnList = AlertBoxAndWhiskerReportColumn
					.getDefaultAlertMessageReportColumnList();

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
			LOG.info("PEGA0060ReportModel - Could'nt match - [" + message + "]");
		}

		return alertMessageReportEntryKey;
	}

	public static void main(String[] args) {

		long before = System.currentTimeMillis();
		String message1 = "The number of read interaction history fact records is more than the threshold of 500 rows : 522 rows : SQL :SELECT pxFactID AS \"pxFactID\", pxOutcomeTime AS \"pxOutcomeTime\", pySubjectID AS \"pySubjectID\", pxInteractionID AS \"pxInteractionID\", \"FACTTABLE\".pzActionID AS \"pzActionID\", \"FACTTABLE\".pzContextID AS \"pzContextID\", \"FACTTABLE\".pzOutcomeID AS \"pzOutcomeID\", \"pyAssociationStrength\" AS \"pyAssociationStrength\", \"pyAssociatedID\" AS \"pyAssociatedID\" FROM  (SELECT \"FACTTABLE\".pxFactID, \"FACTTABLE\".pxOutcomeTime, \"FACTTABLE\".pySubjectID, \"FACTTABLE\".pxInteractionID, \"FACTTABLE\".pzActionID, \"FACTTABLE\".pzContextID, \"FACTTABLE\".pzOutcomeID, null AS \"pyAssociationStrength\", null AS \"pyAssociatedID\" FROM {Class:Data-Decision-IH-Fact} \"FACTTABLE\"  WHERE (\"FACTTABLE\".pySubjectID IN ('33660122'))  UNION ALL SELECT \"FACTTABLE\".pxFactID, \"FACTTABLE\".pxOutcomeTime, \"FACTTABLE\".pySubjectID, \"FACTTABLE\".pxInteractionID, \"FACTTABLE\".pzActionID, \"FACTTABLE\".pzContextID, \"FACTTABLE\".pzOutcomeID, \"ASSOCIATIONTABLE\".pyAssociationStrength AS \"pyAssociationStrength\", \"ASSOCIATIONTABLE\".pySubjectID AS \"pyAssociatedID\" FROM {Class:Data-Decision-IH-Fact} \"FACTTABLE\"  JOIN {Class:Data-Decision-IH-Association} \"ASSOCIATIONTABLE\" ON ((\"ASSOCIATIONTABLE\".pyAssociatedID = \"FACTTABLE\".pySubjectID) ) WHERE (\"ASSOCIATIONTABLE\".pySubjectID IN ('33660122')) )  \"FACTTABLE\"  WHERE (\"FACTTABLE\".pxOutcomeTime >= {IHQueryPage.pzStartFrom DateTime})  ORDER BY 2 DESC";
		String regex = "SQL \\:(.*)";

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
