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

public class PEGA0048ReportModel extends AlertMessageReportModel {

    private static final long serialVersionUID = -8889727175209305065L;

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0048ReportModel.class);

    private List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList;

    private List<Pattern> patternList;

    public PEGA0048ReportModel(AlertMessage alertMessage, long thresholdKPI, AlertLogEntryModel alertLogEntryModel,
            Locale locale) {

        super(alertMessage, thresholdKPI, alertLogEntryModel, locale);

        patternList = new ArrayList<>();

        String regex;
        Pattern pattern;

        // pre 7.2.2
        regex = "for data page(.*?)having pzInskey";
        pattern = Pattern.compile(regex);
        patternList.add(pattern);

        // 7.2.2 - ADPInputData
        regex = "for data page(.*?)having pzInsKey";
        pattern = Pattern.compile(regex);
        patternList.add(pattern);

        // 7.2.2 - AsyncActivityServiceInputData
        regex = "for activity(.*?)in class";
        pattern = Pattern.compile(regex);
        patternList.add(pattern);

        // 7.2.2 - AsyncChannelInputData
        regex = "for channel(.*?)in user thread";
        pattern = Pattern.compile(regex);
        patternList.add(pattern);
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
            displayName = "Alert Subject (\"Data Page\")";
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

        for (Pattern pattern : patternList) {

            Matcher patternMatcher = pattern.matcher(dataText);
            boolean matches = patternMatcher.find();

            if (matches) {
                alertMessageReportEntryKey = patternMatcher.group(1).trim();
                break;
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
            LOG.info("PEGA0048ReportModel - Could'nt match - [" + message + "]");
        }

        return alertMessageReportEntryKey;
    }

}
