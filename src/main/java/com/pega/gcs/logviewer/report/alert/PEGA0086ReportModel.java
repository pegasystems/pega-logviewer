/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.report.alert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.AlertLogEntry;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntryColumn;

public class PEGA0086ReportModel extends AlertMessageReportModel {

	private static final long serialVersionUID = -8889727175209305065L;

	private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0086ReportModel.class);

	private List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList;

	public PEGA0086ReportModel(long thresholdKPI, String kpiUnit, AlertLogEntryModel alertLogEntryModel) {

		super("PEGA0086", thresholdKPI, kpiUnit, alertLogEntryModel);

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
			displayName = "Requestor ID";
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

		int parameterPageIndex = logEntryColumnList.indexOf(LogEntryColumn.PARAMETERPAGEDATA.getColumnId());
		
		String parameterPage = logEntryValueList.get(parameterPageIndex);

		Map<String, String> parameterPageMap = getAlertLogEntryDataValueMap(parameterPage);

		String requestorId = parameterPageMap.get("requestorID");

		if ((requestorId != null) && (!"".equals(requestorId))) {
			alertMessageReportEntryKey = requestorId;
		}

		if (alertMessageReportEntryKey == null) {
			LOG.info("PEGA0086ReportModel - Could'nt match - [" + parameterPage + "]");
		}

		return alertMessageReportEntryKey;
	}

	public static void main(String[] args) {

		long before = System.currentTimeMillis();
		String message1 = "";

		long after = System.currentTimeMillis();

		System.out.println(after - before);
	}

}
