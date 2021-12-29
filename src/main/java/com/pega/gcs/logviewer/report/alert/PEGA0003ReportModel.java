/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.report.alert;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.SwingConstants;

import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.alert.AlertMessageList.AlertMessage;

public class PEGA0003ReportModel extends AlertMessageReportModel {

    private static final long serialVersionUID = -8889727175209305065L;

    private List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList;

    public PEGA0003ReportModel(AlertMessage alertMessage, long thresholdKPI, AlertLogEntryModel alertLogEntryModel,
            Locale locale) {

        super(alertMessage, thresholdKPI, alertLogEntryModel, locale);
    }

    @Override
    protected List<AlertBoxAndWhiskerReportColumn> getAlertMessageReportColumnList() {

        if (alertMessageReportColumnList == null) {
            alertMessageReportColumnList = new ArrayList<AlertBoxAndWhiskerReportColumn>();

            String displayName;
            int prefColWidth;
            int horizontalAlignment;
            boolean filterable;

            // first column data is the key
            displayName = "Alert Subject (\"Last Step\")";
            prefColWidth = 500;
            horizontalAlignment = SwingConstants.LEFT;
            filterable = true;

            AlertBoxAndWhiskerReportColumn amReportColumn;
            amReportColumn = new AlertBoxAndWhiskerReportColumn(AlertBoxAndWhiskerReportColumn.KEY, displayName,
                    prefColWidth, horizontalAlignment, filterable);

            alertMessageReportColumnList.add(amReportColumn);

            List<AlertBoxAndWhiskerReportColumn> defaultAlertMessageReportColumnList = AlertBoxAndWhiskerReportColumn
                    .getDefaultAlertMessageReportColumnList();

            alertMessageReportColumnList.addAll(defaultAlertMessageReportColumnList);
        }

        return alertMessageReportColumnList;
    }

    @Override
    public String getAlertMessageReportEntryKey(String dataText) {
        return null;
    }

    @Override
    public String getAlertMessageReportEntryKey(ArrayList<String> logEntryValueList) {

        String alertMessageReportEntryKey = null;

        AlertLogEntryModel alertLogEntryModel = getAlertLogEntryModel();

        List<String> logEntryColumnList = alertLogEntryModel.getLogEntryColumnList();

        int firstActivityIndex = logEntryColumnList.indexOf(LogEntryColumn.LASTSTEP.getColumnId());

        alertMessageReportEntryKey = logEntryValueList.get(firstActivityIndex).trim();

        return alertMessageReportEntryKey;
    }

}
