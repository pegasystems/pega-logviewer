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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.alert.AlertMessageList.AlertMessage;

public class PEGA0068ReportModel extends AlertMessageReportModel {

    private static final long serialVersionUID = -8889727175209305065L;

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0068ReportModel.class);

    private List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList;

    private Pattern pattern;

    public PEGA0068ReportModel(AlertMessage alertMessage, long thresholdKPI, AlertLogEntryModel alertLogEntryModel,
            Locale locale) {

        super(alertMessage, thresholdKPI, alertLogEntryModel, locale);

        String regex = "dataset id\\:(.*?)for dataset type\\:(.*?)with error type";
        pattern = Pattern.compile(regex);
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
            displayName = "Alert Subject (\"Social Source [Data Set Id]\")";
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
        String alertMessageReportEntryKey = null;

        Matcher patternMatcher = pattern.matcher(dataText);
        boolean matches = patternMatcher.find();

        if (matches) {

            String dataset = patternMatcher.group(1).trim();
            String socialSource = patternMatcher.group(2).trim();

            StringBuilder sb = new StringBuilder();
            sb.append(socialSource);
            sb.append(" [");
            sb.append(dataset);
            sb.append("]");

            alertMessageReportEntryKey = sb.toString();
        }

        return alertMessageReportEntryKey;

    }

    @Override
    public String getAlertMessageReportEntryKey(ArrayList<String> logEntryValueList) {

        String alertMessageReportEntryKey;

        int messageIndex = getMessageLogEntryColumnIndex();
        String message = logEntryValueList.get(messageIndex);

        alertMessageReportEntryKey = getAlertMessageReportEntryKey(message);

        if (alertMessageReportEntryKey == null) {
            LOG.info("PEGA0068ReportModel - Could'nt match - [" + message + "]");
        }

        return alertMessageReportEntryKey;
    }

}
