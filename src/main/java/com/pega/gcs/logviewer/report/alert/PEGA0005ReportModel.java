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
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.alert.AlertMessageList.AlertMessage;

public class PEGA0005ReportModel extends AlertMessageReportModel {

    private static final long serialVersionUID = -8889727175209305065L;

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0005ReportModel.class);

    private List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList;

    private Pattern pattern;

    private Pattern opPattern;

    public PEGA0005ReportModel(AlertMessage alertMessage, long thresholdKPI, AlertLogEntryModel alertLogEntryModel,
            Locale locale) {

        super(alertMessage, thresholdKPI, alertLogEntryModel, locale);

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
            int horizontalAlignment;
            boolean filterable;

            // first column data is the key
            displayName = "Alert Subject (\"SQL\")";
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

            alertMessageReportEntryKey = patternMatcher.group(1).trim();

            if ((alertMessageReportEntryKey == null) || ("".equals(alertMessageReportEntryKey))) {

                Matcher opPatternMatcher = opPattern.matcher(dataText);
                matches = opPatternMatcher.find();

                if (matches) {
                    alertMessageReportEntryKey = opPatternMatcher.group(1);
                }
            }

            // generalise the sql query to trim out bind variables
            if (alertMessageReportEntryKey != null) {
                alertMessageReportEntryKey = getInClauseGeneralisedKey(alertMessageReportEntryKey);
            }
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
            LOG.info("PEGA0005ReportModel - Could'nt match - [" + message + "]");
        }

        return alertMessageReportEntryKey;
    }
}
