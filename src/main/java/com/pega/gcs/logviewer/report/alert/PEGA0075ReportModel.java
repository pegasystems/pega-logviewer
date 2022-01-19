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

public class PEGA0075ReportModel extends AlertMessageReportModel {

    private static final long serialVersionUID = -8889727175209305065L;

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0075ReportModel.class);

    private List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList;

    private List<Pattern> patternList;

    public PEGA0075ReportModel(AlertMessage alertMessage, long thresholdKPI, AlertLogEntryModel alertLogEntryModel,
            Locale locale) {

        super(alertMessage, thresholdKPI, alertLogEntryModel, locale);

        patternList = new ArrayList<>();

        String regex;
        Pattern pattern;

        // pre 7.3
        regex = "for column family(.*?)with keys";
        pattern = Pattern.compile(regex);
        patternList.add(pattern);

        // 7.3 - CassandraAlertingLatencyTracker
        regex = "Cassandra interaction above threshold on(.*?): query(.*?)execution time was ";
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
            displayName = "Alert Subject (\"Cassandra Query\")";
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

                // different approach

                String colfamily = null;

                // for post 7.3 query
                if (patternMatcher.groupCount() > 1) {

                    String queryStr = patternMatcher.group(2).trim();

                    queryStr = queryStr.toLowerCase().trim();

                    if ((queryStr.startsWith("insert")) || (queryStr.startsWith("begin batch"))) {

                        int beginIndex = queryStr.indexOf("insert into ");
                        int endIndex = queryStr.indexOf("(");

                        if ((beginIndex != -1) && (endIndex != -1)) {
                            colfamily = queryStr.substring(beginIndex + 12, endIndex);
                        }
                    } else if (queryStr.startsWith("select")) {

                        int beginIndex = queryStr.indexOf("from ");
                        int endIndex = queryStr.indexOf(" where");

                        if ((beginIndex != -1) && (endIndex != -1)) {
                            colfamily = queryStr.substring(beginIndex + 5, endIndex);
                        }
                    }
                }

                if ((colfamily == null) || ("".equals(colfamily))) {
                    // original approach
                    StringBuilder sb = new StringBuilder();
                    boolean first = true;

                    for (int i = 1; i <= patternMatcher.groupCount(); i++) {

                        String groupStr = patternMatcher.group(i).trim();

                        if (!first) {
                            sb.append(" - ");
                        }

                        first = false;

                        sb.append(groupStr);
                    }

                    alertMessageReportEntryKey = sb.toString();
                } else {
                    alertMessageReportEntryKey = colfamily;
                }
            }
        }

        return alertMessageReportEntryKey;

    }

    @Override
    public String getAlertMessageReportEntryKey(ArrayList<String> logEntryValueList) {

        String alertMessageReportEntryKey = null;

        AlertLogEntryModel alertLogEntryModel = getAlertLogEntryModel();

        List<String> logEntryColumnList = alertLogEntryModel.getLogEntryColumnList();

        int messageIndex = logEntryColumnList.indexOf(LogEntryColumn.MESSAGE.getColumnId());
        String message = logEntryValueList.get(messageIndex);

        alertMessageReportEntryKey = getAlertMessageReportEntryKey(message);

        if (alertMessageReportEntryKey == null) {
            LOG.info("PEGA0075ReportModel - Could'nt match - [" + message + "]");
        }

        return alertMessageReportEntryKey;
    }

}
