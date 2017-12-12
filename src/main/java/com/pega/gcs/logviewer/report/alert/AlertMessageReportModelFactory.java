/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.report.alert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.alert.AlertMessageList.AlertMessage;
import com.pega.gcs.logviewer.model.alert.AlertMessageListProvider;

public class AlertMessageReportModelFactory {

	private static final Log4j2Helper LOG = new Log4j2Helper(AlertMessageReportModelFactory.class);

	private static AlertMessageReportModelFactory _INSTANCE;

	private AlertMessageReportModelFactory() {

	}

	public static AlertMessageReportModelFactory getInstance() {

		if (_INSTANCE == null) {
			_INSTANCE = new AlertMessageReportModelFactory();
		}

		return _INSTANCE;
	}

	public AlertMessageReportModel getAlertMessageReportModel(int alertId, long thresholdKPI,
			AlertLogEntryModel alertLogEntryModel) {

		AlertMessageListProvider alertMessageListProvider = AlertMessageListProvider.getInstance();

		Map<Integer, AlertMessage> alertMessageMap = alertMessageListProvider.getAlertMessageMap();

		AlertMessage alertMessage = alertMessageMap.get(alertId);

		String messageId = alertMessage.getMessageID();
		String kpiUnit = alertMessage.getDSSValueUnit();

		return getAlertMessageReportModel(messageId, thresholdKPI, kpiUnit, alertLogEntryModel);

	}

	public AlertMessageReportModel getAlertMessageReportModel(String messageId, long thresholdKPI, String kpiUnit,
			AlertLogEntryModel alertLogEntryModel) {

		AlertMessageReportModel alertMessageReportModel = null;

		if ("EXCP0001".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new EXCP0001ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0001".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0001ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0002".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0002ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0003".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0003ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0004".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0004ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0005".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0005ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0006".equalsIgnoreCase(messageId)) {
		} else if ("PEGA0007".equalsIgnoreCase(messageId)) {
		} else if ("PEGA0008".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0008ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0009".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0009ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0010".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0010ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0011".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0011ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0012".equalsIgnoreCase(messageId)) {
		} else if ("PEGA0013".equalsIgnoreCase(messageId)) {
		} else if ("PEGA0014".equalsIgnoreCase(messageId)) {
		} else if ("PEGA0015".equalsIgnoreCase(messageId)) {
		} else if ("PEGA0016".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0016ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0017".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0017ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0018".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0018ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0019".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0019ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0020".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0020ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0021".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0021ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0022".equalsIgnoreCase(messageId)) {
		} else if ("PEGA0023".equalsIgnoreCase(messageId)) {
		} else if ("PEGA0024".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0024ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0025".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0025ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0026".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0026ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0027".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0027ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0028".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0028ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0029".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0029ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0030".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0030ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0031".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0031ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0032".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0032ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0033".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0033ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0034".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0034ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0035".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0035ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0036".equalsIgnoreCase(messageId)) {
		} else if ("PEGA0037".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0037ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0038".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0038ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0039".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0039ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0040".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0040ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0041".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0041ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0042".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0042ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0043".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0043ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0044".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0044ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0045".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0045ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0046".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0046ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0047".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0047ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0048".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0048ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0049".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0049ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0050".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0050ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0051".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0051ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0052".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0052ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0053".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0053ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0054".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0054ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0055".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0055ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0056".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0056ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0057".equalsIgnoreCase(messageId)) {
		} else if ("PEGA0058".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0058ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0059".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0059ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0060".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0060ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0061".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0061ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0062".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0062ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0063".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0063ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0064".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0064ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0065".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0065ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0066".equalsIgnoreCase(messageId)) {
			// --
		} else if ("PEGA0067".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0067ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0068".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0068ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0069".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0069ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0070".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0070ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0071".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0071ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0072".equalsIgnoreCase(messageId)) {
		} else if ("PEGA0073".equalsIgnoreCase(messageId)) {
		} else if ("PEGA0074".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0074ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0075".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0075ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0076".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0076ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0077".equalsIgnoreCase(messageId)) {
		} else if ("PEGA0078".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0078ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0079".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0079ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0080".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0080ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0081".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0081ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0082".equalsIgnoreCase(messageId)) {
		} else if ("PEGA0083".equalsIgnoreCase(messageId)) {
		} else if ("PEGA0084".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0084ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0085".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0085ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0086".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0086ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0087".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0087ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0088".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0088ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0089".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0089ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0090".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0090ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0091".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0091ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0092".equalsIgnoreCase(messageId)) {
		} else if ("PEGA0093".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0093ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0094".equalsIgnoreCase(messageId)) {
		} else if ("PEGA0095".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0095ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0096".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new PEGA0096ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("PEGA0097".equalsIgnoreCase(messageId)) {
		} else if ("SECU0001".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new SECU0001ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("SECU0002".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new SECU0002ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("SECU0003".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new SECU0003ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("SECU0004".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new SECU0004ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("SECU0005".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new SECU0005ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("SECU0006".equalsIgnoreCase(messageId)) {
			alertMessageReportModel = new SECU0006ReportModel(thresholdKPI, kpiUnit, alertLogEntryModel);
		} else if ("SECU0007".equalsIgnoreCase(messageId)) {
		} else if ("SECU0008".equalsIgnoreCase(messageId)) {
		} else if ("SECU0009".equalsIgnoreCase(messageId)) {
		} else if ("SECU0010".equalsIgnoreCase(messageId)) {
		} else if ("SECU0011".equalsIgnoreCase(messageId)) {
		} else if ("SECU0012".equalsIgnoreCase(messageId)) {
		} else if ("SECU0013".equalsIgnoreCase(messageId)) {
		} else if ("SECU0014".equalsIgnoreCase(messageId)) {
		}

		return alertMessageReportModel;
	}

	public static void main(String[] args) {
		// test if AlertMessageReportModel are created successfully

		AlertMessageReportModelFactory alertMessageReportModelFactory = AlertMessageReportModelFactory.getInstance();

		List<String> messageIdList = new ArrayList<>(AlertMessageListProvider.getInstance().getMessageIdSet());
		Collections.sort(messageIdList);

		List<String> unImplementedList = new ArrayList<>();

		for (String messageId : messageIdList) {

			AlertMessageReportModel alertMessageReportModel;
			alertMessageReportModel = alertMessageReportModelFactory.getAlertMessageReportModel(messageId, 0, "", null);

			if (alertMessageReportModel != null) {
				LOG.info("Report Model for message id: " + messageId + " " + alertMessageReportModel.toString());
			} else {
				unImplementedList.add(messageId);
			}
		}

		LOG.info("Unimplemented List size: " + unImplementedList.size());

		for (String messageId : unImplementedList) {
			LOG.info("Did not find report Model for message id: " + messageId);
		}
	}
}
