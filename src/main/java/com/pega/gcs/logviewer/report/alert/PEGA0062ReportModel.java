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

public class PEGA0062ReportModel extends AlertMessageReportModel {

	private static final long serialVersionUID = -8889727175209305065L;

	private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0062ReportModel.class);

	private List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList;

	private Pattern pattern;

	public PEGA0062ReportModel(long thresholdKPI, String kpiUnit, AlertLogEntryModel alertLogEntryModel) {

		super("PEGA0062", thresholdKPI, kpiUnit, alertLogEntryModel);

		String regex = "For data flow \\[(.*?)\\]";
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
			displayName = "Data Flow";
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
			LOG.info("PEGA0062ReportModel - Could'nt match - [" + message + "]");
		}

		return alertMessageReportEntryKey;
	}

	public static void main(String[] args) {

		long before = System.currentTimeMillis();
		String message1 = "Data flow record processing time exceeds threshold of 10000 ms: 10071 ms. For data flow [GetCustomerAndSave]. Data flow metrics are: {\"stageMetrics\":[{\"stageId\":\"Source5\",\"dataFlowKeys\":{\"className\":\"RBSMKT-Data-Customer\",\"name\":\"GetCustomer\"},\"stageName\":\"Data-Decision-IH-Fact-Aggregate\",\"recordsIn\":13,\"recordsOut\":13,\"errorsCount\":0,\"lastRecordTimestamp\":1465300810975,\"executionTime\":994419},{\"stageId\":\"Source4\",\"dataFlowKeys\":{\"className\":\"RBSMKT-Data-Customer\",\"name\":\"GetCustomer\"},\"stageName\":\"RBSMKT-Data-Customer-AccountEvent\",\"recordsIn\":0,\"recordsOut\":0,\"errorsCount\":0,\"lastRecordTimestamp\":0,\"executionTime\":0},{\"stageId\":\"Source3\",\"dataFlowKeys\":{\"className\":\"RBSMKT-Data-Customer\",\"name\":\"GetCustomer\"},\"stageName\":\"RBSMKT-Data-Customer-Event\",\"recordsIn\":2,\"recordsOut\":2,\"errorsCount\":0,\"lastRecordTimestamp\":1465300810908,\"executionTime\":173930},{\"stageId\":\"Source2\",\"dataFlowKeys\":{\"className\":\"RBSMKT-Data-Customer\",\"name\":\"GetCustomer\"},\"stageName\":\"RBSMKT-Data-Customer-ProductHolding\",\"recordsIn\":6,\"recordsOut\":6,\"errorsCount\":0,\"lastRecordTimestamp\":1465300810797,\"executionTime\":4286921},{\"stageId\":\"Source1\",\"dataFlowKeys\":{\"className\":\"RBSMKT-Data-Customer\",\"name\":\"GetCustomer\"},\"stageName\":\"RBSMKT-Data-Customer\",\"recordsIn\":1,\"recordsOut\":1,\"errorsCount\":0,\"lastRecordTimestamp\":1465300801096,\"executionTime\":0},{\"stageId\":\"Source\",\"dataFlowKeys\":{\"className\":\"RBSMKT-Data-Customer\",\"name\":\"GetCustomer\"},\"stageName\":\"RBSMKT-Data-Customer\",\"recordsIn\":1,\"recordsOut\":1,\"errorsCount\":0,\"lastRecordTimestamp\":1465300800934,\"executionTime\":10071325214},{\"stageId\":\"Merge1\",\"dataFlowKeys\":{\"className\":\"RBSMKT-Data-Customer\",\"name\":\"GetCustomer\"},\"stageName\":\"RBSMKT-Data-Customer with RBSMKT-Data-Customer\",\"recordsIn\":1,\"recordsOut\":1,\"errorsCount\":0,\"lastRecordTimestamp\":1465300801115,\"executionTime\":10071323288},{\"stageId\":\"Compose1\",\"dataFlowKeys\":{\"className\":\"RBSMKT-Data-Customer\",\"name\":\"GetCustomer\"},\"stageName\":\"RBSMKT-Data-Customer with RBSMKT-Data-Customer with RBSMKT-Data-Customer-ProductHolding\",\"recordsIn\":1,\"recordsOut\":1,\"errorsCount\":0,\"lastRecordTimestamp\":1465300810847,\"executionTime\":9889741244},{\"stageId\":\"Compose2\",\"dataFlowKeys\":{\"className\":\"RBSMKT-Data-Customer\",\"name\":\"GetCustomer\"},\"stageName\":\"RBSMKT-Data-Customer with RBSMKT-Data-Customer with RBSMKT-Data-Customer-ProductHolding with RBSMKT-Data-Customer-Event\",\"recordsIn\":1,\"recordsOut\":1,\"errorsCount\":0,\"lastRecordTimestamp\":1465300810931,\"executionTime\":158452377},{\"stageId\":\"Compose3\",\"dataFlowKeys\":{\"className\":\"RBSMKT-Data-Customer\",\"name\":\"GetCustomer\"},\"stageName\":\"RBSMKT-Data-Customer with RBSMKT-Data-Customer with RBSMKT-Data-Customer-ProductHolding with RBSMKT-Data-Customer-Event with RBSMKT-Data-Customer-AccountEvent\",\"recordsIn\":1,\"recordsOut\":1,\"errorsCount\":0,\"lastRecordTimestamp\":1465300810959,\"executionTime\":74103479},{\"stageId\":\"Compose4\",\"dataFlowKeys\":{\"className\":\"RBSMKT-Data-Customer\",\"name\":\"GetCustomer\"},\"stageName\":\"RBSMKT-Data-Customer with RBSMKT-Data-Customer with RBSMKT-Data-Customer-ProductHolding with RBSMKT-Data-Customer-Event with RBSMKT-Data-Customer-AccountEvent with Data-Decision-IH-Fact-Aggregate\",\"recordsIn\":1,\"recordsOut\":1,\"errorsCount\":0,\"lastRecordTimestamp\":1465300810977,\"executionTime\":45922392},{\"stageId\":\"Destination\",\"dataFlowKeys\":{\"className\":\"RBSMKT-Data-Customer\",\"name\":\"GetCustomer\"},\"stageName\":\"RBSMKT-Data-Customer\",\"recordsIn\":1,\"recordsOut\":1,\"errorsCount\":0,\"lastRecordTimestamp\":1465300810977,\"executionTime\":28624700},{\"stageId\":\"Source\",\"dataFlowKeys\":{\"className\":\"RBSMKT-Data-Customer\",\"name\":\"GetCustomerAndSave\"},\"stageName\":\"GetCustomer\",\"recordsIn\":1,\"recordsOut\":1,\"errorsCount\":0,\"lastRecordTimestamp\":1465300810977,\"executionTime\":28623376},{\"stageId\":\"Output1\",\"dataFlowKeys\":{\"className\":\"RBSMKT-Data-Customer\",\"name\":\"GetCustomerAndSave\"},\"stageName\":\"CustomerDDS\",\"recordsIn\":1,\"recordsOut\":0,\"errorsCount\":0,\"lastRecordTimestamp\":0,\"executionTime\":28616477},{\"stageId\":\"Destination\",\"dataFlowKeys\":{\"className\":\"RBSMKT-Data-Customer\",\"name\":\"GetCustomerAndSave\"},\"stageName\":\"RBSMKT-Data-Customer\",\"recordsIn\":1,\"recordsOut\":1,\"errorsCount\":0,\"lastRecordTimestamp\":1465300811005,\"executionTime\":3454}],\"throughput\":0,\"recordsProcessed\":1,\"timer\":{\"startTime\":1465300800934,\"endTime\":1465300811005},\"status\":\"New\",\"errorsCount\":0}";

		String regex = "For data flow \\[(.*?)\\]";

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
