/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.parser;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.logfile.AlertLogPattern;
import com.pega.gcs.logviewer.model.AlertLogEntry;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogEntryModel;
import com.pega.gcs.logviewer.model.PALStatisticName;
import com.pega.gcs.logviewer.model.alert.AlertMessageListProvider;

public class AlertLogParser extends LogParser {

    private static final Log4j2Helper LOG = new Log4j2Helper(AlertLogParser.class);

    private int alertVersion;

    private StringBuilder fullLogEntryTextSB;

    private int capturedColumnCount;

    // store the index of 'messageid' column so as to identify colour
    private int messageIDIndex;

    private int timestampIndex;

    private int observedKPIIndex;

    private int palDataIndex;

    private AlertLogEntryModel alertLogEntryModel;

    private Pattern alertDatePattern;

    public AlertLogParser(AlertLogPattern alertLogPattern, Charset charset, Locale locale) {

        super(alertLogPattern, charset, locale);

        alertVersion = -1;

        fullLogEntryTextSB = new StringBuilder();
        capturedColumnCount = 0;

        getTimeStampFormat("%d{}{GMT}");

        String threadRegEx = "(\\d{4}-\\d{2}-\\d{2}[ ]\\d{2}:\\d{2}:\\d{2},\\d{3}[ ]GMT)";
        alertDatePattern = Pattern.compile(threadRegEx);

        alertLogEntryModel = new AlertLogEntryModel(getDateFormat());
    }

    @Override
    public String toString() {
        return "AlertLogParser [" + alertVersion + "]";
    }

    @Override
    protected void parseV2(String line) {

        Map<String, Object> fieldMap = getCloudKFieldMap(line);

        if (fieldMap != null) {

            String message = (String) fieldMap.get("message");

            setupLogEntryColumnList(message);

            List<LogEntryColumn> logEntryColumnList = getLogEntryColumnList();

            if (logEntryColumnList != null) {

                fullLogEntryTextSB.append(message);

                buildLogEntry();
            }
        }
    }

    @Override
    protected void parseV1(String line) {

        line = getLineFromCloudK(line);

        setupLogEntryColumnList(line);

        List<LogEntryColumn> logEntryColumnList = getLogEntryColumnList();

        if (logEntryColumnList != null) {

            String[] fields = null;

            int logEntryColumnListSize = logEntryColumnList.size();

            // logEntryColumnListSize has additional Line column
            if ((fullLogEntryTextSB.length() > 0) && (capturedColumnCount < (logEntryColumnListSize - 1))) {

                fullLogEntryTextSB.append(line);
                fields = fullLogEntryTextSB.toString().split("\\*");
                capturedColumnCount = fields.length;

            } else {

                buildLogEntry();

                fullLogEntryTextSB.append(line);
                fields = fullLogEntryTextSB.toString().split("\\*");
                capturedColumnCount = fields.length;
            }
        } else {
            LOG.info("discarding empty line in the begining");
        }
    }

    private void setupLogEntryColumnList(String line) {

        List<LogEntryColumn> logEntryColumnList = getLogEntryColumnList();

        if ((line != null) && (!line.isEmpty()) && (logEntryColumnList == null)) {

            int oldStyleIndex = 0;

            String[] fields = line.split("\\*");

            int fieldsLen = fields.length;

            // 22 is the lowest count of fields, for Alert Version 4.
            if (fieldsLen >= 22) {

                alertVersion = Integer.parseInt(fields[1]);

                // // 37 is max column count for v8 alert
                // if (fieldsLen > 37) {
                // oldStyleIndex = 12;
                // }

                switch (alertVersion) {

                case 4:
                    logEntryColumnList = getAlertColumnListV4();
                    timestampIndex = oldStyleIndex + 0;
                    messageIDIndex = oldStyleIndex + 2;
                    observedKPIIndex = oldStyleIndex + 3;
                    palDataIndex = oldStyleIndex + 20;
                    break;

                case 5:
                    logEntryColumnList = getAlertColumnListV5();
                    timestampIndex = oldStyleIndex + 0;
                    messageIDIndex = oldStyleIndex + 2;
                    observedKPIIndex = oldStyleIndex + 3;
                    palDataIndex = oldStyleIndex + 20;
                    break;

                case 6:
                    logEntryColumnList = getAlertColumnListV6();
                    timestampIndex = oldStyleIndex + 0;
                    messageIDIndex = oldStyleIndex + 2;
                    observedKPIIndex = oldStyleIndex + 3;
                    palDataIndex = oldStyleIndex + 22;
                    break;

                case 7:
                    logEntryColumnList = getAlertColumnListV7();
                    timestampIndex = oldStyleIndex + 0;
                    messageIDIndex = oldStyleIndex + 2;
                    observedKPIIndex = oldStyleIndex + 3;
                    palDataIndex = oldStyleIndex + 24;
                    break;

                case 8:
                    logEntryColumnList = getAlertColumnListV8();
                    timestampIndex = oldStyleIndex + 0;
                    messageIDIndex = oldStyleIndex + 2;
                    observedKPIIndex = oldStyleIndex + 3;
                    palDataIndex = oldStyleIndex + 29;
                    break;

                default:
                    // set to v6 alert.
                    logEntryColumnList = getAlertColumnListV6();
                    timestampIndex = oldStyleIndex + 0;
                    messageIDIndex = oldStyleIndex + 2;
                    observedKPIIndex = oldStyleIndex + 3;
                    palDataIndex = oldStyleIndex + 22;
                    break;
                }

                LOG.info("logEntryColumnList: " + logEntryColumnList);

                setLogEntryColumnList(logEntryColumnList);
                alertLogEntryModel.updateLogEntryColumnList(logEntryColumnList);
            } else {
                LOG.info("discarding line: " + line);
            }
        }

    }

    @Override
    public void parseFinalInternal() {

        buildLogEntry();

        alertLogEntryModel.processAlertMessageReportModels();
    }

    @Override
    public LogEntryModel getLogEntryModel() {
        return alertLogEntryModel;
    }

    private AlertLogEntry buildLogEntry() {

        AlertLogEntry alertLogEntry = null;

        if (fullLogEntryTextSB.length() > 0) {

            LogEntryModel logEntryModel = getLogEntryModel();
            AtomicInteger logEntryIndex = getLogEntryIndex();
            List<LogEntryColumn> logEntryColumnList = getLogEntryColumnList();

            try {

                logEntryIndex.incrementAndGet();

                int logEntryColumnListSize = logEntryColumnList.size();

                // adding trim() as some alert logs are copied from IU and has a space after the last '*'.
                String logEntryText = fullLogEntryTextSB.toString().trim();
                String[] fields = logEntryText.split("\\*");

                // logEntryColumnListSize has additional LINE column
                if (fields.length != (logEntryColumnListSize - 1)) {
                    LOG.info("Problem - found additional * in the alert entry: " + logEntryIndex);

                    // try fixing the alert
                    logEntryText = logEntryText.replaceAll("\\*/", "ASTERIX/");

                    fields = logEntryText.split("\\*");
                }

                DateFormat modelDateFormat = logEntryModel.getModelDateFormat();
                String timestampStr = fields[timestampIndex];
                long logEntryTime = -1;

                try {
                    Date logEntryDate = modelDateFormat.parse(timestampStr);
                    logEntryTime = logEntryDate.getTime();
                } catch (ParseException pe) {

                    LOG.error("Error parsing line: [" + logEntryIndex + "] logentry: [" + logEntryText + "]", pe);

                    // possibly a older style alert. extract the date string and
                    // parse it again
                    Matcher alertDateMatcher = alertDatePattern.matcher(timestampStr);

                    if (alertDateMatcher.find()) {
                        int count = alertDateMatcher.groupCount();
                        timestampStr = alertDateMatcher.group(count);

                        try {
                            Date logEntryDate = modelDateFormat.parse(timestampStr);
                            logEntryTime = logEntryDate.getTime();
                        } catch (ParseException pe2) {
                            LOG.error("not able to parse [" + timestampStr + "]", pe2);
                        }
                    }

                }

                ArrayList<String> logEntryColumnValueList;

                logEntryColumnValueList = new ArrayList<String>();
                logEntryColumnValueList.add(String.valueOf(logEntryIndex));
                logEntryColumnValueList.addAll(Arrays.asList(fields));

                String messageIdStr = fields[messageIDIndex];

                AlertMessageListProvider alertMessageListProvider = AlertMessageListProvider.getInstance();

                Integer alertId = alertMessageListProvider.getAlertId(messageIdStr);

                String observedKPIStr = fields[observedKPIIndex];
                long observedKPI = Long.parseLong(observedKPIStr);

                boolean criticalAlertEntry = false;

                List<String> criticalAlertList = alertMessageListProvider.getCriticalAlertList();

                if (criticalAlertList.contains(messageIdStr)) {
                    criticalAlertEntry = true;
                }

                // parse PAL data
                String palDataStr = fields[palDataIndex];
                Number[] palDataValueArray = parsePALData(palDataStr);

                LogEntryKey logEntryKey = new LogEntryKey(logEntryIndex.intValue(), logEntryTime);

                alertLogEntry = new AlertLogEntry(logEntryKey, logEntryColumnValueList, logEntryText, alertVersion,
                        alertId, observedKPI, criticalAlertEntry, palDataValueArray);

                alertLogEntryModel.addLogEntry(alertLogEntry, logEntryColumnValueList, getCharset(), getLocale());

                // update the processed counter
                incrementAndGetProcessedCount();

                fullLogEntryTextSB = new StringBuilder();
            } catch (Exception e) {
                LOG.error("Error parsing Log text: \n" + fullLogEntryTextSB.toString());
                LOG.error("Error parsing Log index: " + logEntryIndex, e);

                // discard the previous accumulated text
                fullLogEntryTextSB = new StringBuilder();
            }
        }

        return alertLogEntry;
    }

    private Number[] parsePALData(String palDataStr) {

        Number[] palDataValueArray = new Number[PALStatisticName.values().length];

        NumberFormat nf = NumberFormat.getInstance(getLocale());

        if ((palDataStr != null) && (!"".equals(palDataStr)) && (!"NA".equals(palDataStr))) {

            String[] palStatArray = palDataStr.split(";", 0);

            for (String palStat : palStatArray) {

                String[] palStatNameValue = palStat.split("=", 2);

                String palStatName = palStatNameValue[0];
                String palStatValue = null;

                PALStatisticName palStatisticName = null;

                try {
                    palStatisticName = PALStatisticName.valueOf(palStatName);

                    if (palStatNameValue.length == 2) {

                        alertLogEntryModel.addPALStatisticColumn(palStatisticName);

                        palStatValue = palStatNameValue[1];

                        try {
                            Number parseValue = nf.parse(palStatValue);

                            int index = palStatisticName.ordinal();

                            palDataValueArray[index] = parseValue;
                        } catch (ParseException pe) {
                            LOG.error("Unable to parse PAL statistic: " + palStatisticName + " palStatValue: "
                                    + palStatValue, pe);
                        }
                    }

                } catch (Exception e) {
                    // such stat name doesn't exist, ignore for now.
                    LOG.error("palStatisticName: " + palStatName + " not found in the list", e);
                }
            }
        }

        return palDataValueArray;
    }

    private static List<LogEntryColumn> getAlertColumnListV4() {

        List<LogEntryColumn> alertColumnList = new ArrayList<>();

        alertColumnList.add(LogEntryColumn.LINE);
        alertColumnList.add(LogEntryColumn.TIMESTAMP);
        alertColumnList.add(LogEntryColumn.VERSION);
        alertColumnList.add(LogEntryColumn.MESSAGEID);
        alertColumnList.add(LogEntryColumn.OBSERVEDKPI);
        alertColumnList.add(LogEntryColumn.THRESHOLDKPI);
        alertColumnList.add(LogEntryColumn.NODEID);
        alertColumnList.add(LogEntryColumn.REQUESTORID);
        alertColumnList.add(LogEntryColumn.USERID);
        alertColumnList.add(LogEntryColumn.WORKPOOL);
        alertColumnList.add(LogEntryColumn.ENCODEDRULESET);
        alertColumnList.add(LogEntryColumn.PERSONALRULESETYN);
        alertColumnList.add(LogEntryColumn.INTERACTIONNUMBER);
        alertColumnList.add(LogEntryColumn.ALERTNUMBER);
        alertColumnList.add(LogEntryColumn.THREAD);
        alertColumnList.add(LogEntryColumn.LOGGER);
        alertColumnList.add(LogEntryColumn.STACK);
        alertColumnList.add(LogEntryColumn.LASTINPUT);
        alertColumnList.add(LogEntryColumn.FIRSTACTIVITY);
        alertColumnList.add(LogEntryColumn.LASTSTEP);
        alertColumnList.add(LogEntryColumn.TRACELIST);
        alertColumnList.add(LogEntryColumn.PALDATA);
        alertColumnList.add(LogEntryColumn.MESSAGE);

        return alertColumnList;
    }

    private static List<LogEntryColumn> getAlertColumnListV5() {

        List<LogEntryColumn> alertColumnList = new ArrayList<>();

        alertColumnList.add(LogEntryColumn.LINE);
        alertColumnList.add(LogEntryColumn.TIMESTAMP);
        alertColumnList.add(LogEntryColumn.VERSION);
        alertColumnList.add(LogEntryColumn.MESSAGEID);
        alertColumnList.add(LogEntryColumn.OBSERVEDKPI);
        alertColumnList.add(LogEntryColumn.THRESHOLDKPI);
        alertColumnList.add(LogEntryColumn.NODEID);
        alertColumnList.add(LogEntryColumn.REQUESTORID);
        alertColumnList.add(LogEntryColumn.USERID);
        alertColumnList.add(LogEntryColumn.WORKPOOL);
        alertColumnList.add(LogEntryColumn.ENCODEDRULESET);
        alertColumnList.add(LogEntryColumn.PERSONALRULESETYN);
        alertColumnList.add(LogEntryColumn.INTERACTIONNUMBER);
        alertColumnList.add(LogEntryColumn.ALERTNUMBER);
        alertColumnList.add(LogEntryColumn.THREAD);
        alertColumnList.add(LogEntryColumn.LOGGER);
        alertColumnList.add(LogEntryColumn.STACK);
        alertColumnList.add(LogEntryColumn.LASTINPUT);
        alertColumnList.add(LogEntryColumn.FIRSTACTIVITY);
        alertColumnList.add(LogEntryColumn.LASTSTEP);
        alertColumnList.add(LogEntryColumn.TRACELIST);
        alertColumnList.add(LogEntryColumn.PALDATA);
        alertColumnList.add(LogEntryColumn.PRIMARYPAGECLASS);
        alertColumnList.add(LogEntryColumn.PRIMARYPAGENAME);
        alertColumnList.add(LogEntryColumn.STEPPAGECLASS);
        alertColumnList.add(LogEntryColumn.STEPPAGENAME);
        alertColumnList.add(LogEntryColumn.PRSTACKTRACE);
        alertColumnList.add(LogEntryColumn.PARAMETERPAGEDATA);
        alertColumnList.add(LogEntryColumn.MESSAGE);

        return alertColumnList;
    }

    private static List<LogEntryColumn> getAlertColumnListV6() {

        List<LogEntryColumn> alertColumnList = new ArrayList<>();

        alertColumnList.add(LogEntryColumn.LINE);
        alertColumnList.add(LogEntryColumn.TIMESTAMP);
        alertColumnList.add(LogEntryColumn.VERSION);
        alertColumnList.add(LogEntryColumn.MESSAGEID);
        alertColumnList.add(LogEntryColumn.OBSERVEDKPI);
        alertColumnList.add(LogEntryColumn.THRESHOLDKPI);
        alertColumnList.add(LogEntryColumn.NODEID);
        alertColumnList.add(LogEntryColumn.REQUESTORID);
        alertColumnList.add(LogEntryColumn.USERID);
        alertColumnList.add(LogEntryColumn.WORKPOOL);
        alertColumnList.add(LogEntryColumn.RULEAPPNAMEANDVERSION);
        alertColumnList.add(LogEntryColumn.ENCODEDRULESET);
        alertColumnList.add(LogEntryColumn.PERSONALRULESETYN);
        alertColumnList.add(LogEntryColumn.INTERACTIONNUMBER);
        alertColumnList.add(LogEntryColumn.ALERTNUMBER);
        alertColumnList.add(LogEntryColumn.THREAD);
        alertColumnList.add(LogEntryColumn.PEGATHREAD);
        alertColumnList.add(LogEntryColumn.LOGGER);
        alertColumnList.add(LogEntryColumn.STACK);
        alertColumnList.add(LogEntryColumn.LASTINPUT);
        alertColumnList.add(LogEntryColumn.FIRSTACTIVITY);
        alertColumnList.add(LogEntryColumn.LASTSTEP);
        alertColumnList.add(LogEntryColumn.TRACELIST);
        alertColumnList.add(LogEntryColumn.PALDATA);
        alertColumnList.add(LogEntryColumn.PRIMARYPAGECLASS);
        alertColumnList.add(LogEntryColumn.PRIMARYPAGENAME);
        alertColumnList.add(LogEntryColumn.STEPPAGECLASS);
        alertColumnList.add(LogEntryColumn.STEPPAGENAME);
        alertColumnList.add(LogEntryColumn.PRSTACKTRACE);
        alertColumnList.add(LogEntryColumn.PARAMETERPAGEDATA);
        alertColumnList.add(LogEntryColumn.MESSAGE);

        return alertColumnList;
    }

    private static List<LogEntryColumn> getAlertColumnListV7() {

        List<LogEntryColumn> alertColumnList = new ArrayList<>();

        alertColumnList.add(LogEntryColumn.LINE);
        alertColumnList.add(LogEntryColumn.TIMESTAMP);
        alertColumnList.add(LogEntryColumn.VERSION);
        alertColumnList.add(LogEntryColumn.MESSAGEID);
        alertColumnList.add(LogEntryColumn.OBSERVEDKPI);
        alertColumnList.add(LogEntryColumn.THRESHOLDKPI);
        alertColumnList.add(LogEntryColumn.NODEID);
        alertColumnList.add(LogEntryColumn.TENANTID);
        alertColumnList.add(LogEntryColumn.TENANTIDHASH);
        alertColumnList.add(LogEntryColumn.REQUESTORID);
        alertColumnList.add(LogEntryColumn.USERID);
        alertColumnList.add(LogEntryColumn.WORKPOOL);
        alertColumnList.add(LogEntryColumn.RULEAPPNAMEANDVERSION);
        alertColumnList.add(LogEntryColumn.ENCODEDRULESET);
        alertColumnList.add(LogEntryColumn.PERSONALRULESETYN);
        alertColumnList.add(LogEntryColumn.INTERACTIONNUMBER);
        alertColumnList.add(LogEntryColumn.ALERTNUMBER);
        alertColumnList.add(LogEntryColumn.THREAD);
        alertColumnList.add(LogEntryColumn.PEGATHREAD);
        alertColumnList.add(LogEntryColumn.LOGGER);
        alertColumnList.add(LogEntryColumn.STACK);
        alertColumnList.add(LogEntryColumn.LASTINPUT);
        alertColumnList.add(LogEntryColumn.FIRSTACTIVITY);
        alertColumnList.add(LogEntryColumn.LASTSTEP);
        alertColumnList.add(LogEntryColumn.TRACELIST);
        alertColumnList.add(LogEntryColumn.PALDATA);
        alertColumnList.add(LogEntryColumn.PRIMARYPAGECLASS);
        alertColumnList.add(LogEntryColumn.PRIMARYPAGENAME);
        alertColumnList.add(LogEntryColumn.STEPPAGECLASS);
        alertColumnList.add(LogEntryColumn.STEPPAGENAME);
        alertColumnList.add(LogEntryColumn.PRSTACKTRACE);
        alertColumnList.add(LogEntryColumn.PARAMETERPAGEDATA);
        alertColumnList.add(LogEntryColumn.MESSAGE);

        return alertColumnList;
    }

    private static List<LogEntryColumn> getAlertColumnListV8() {

        List<LogEntryColumn> alertColumnList = new ArrayList<>();

        alertColumnList.add(LogEntryColumn.LINE);
        alertColumnList.add(LogEntryColumn.TIMESTAMP);
        alertColumnList.add(LogEntryColumn.VERSION);
        alertColumnList.add(LogEntryColumn.MESSAGEID);
        alertColumnList.add(LogEntryColumn.OBSERVEDKPI);
        alertColumnList.add(LogEntryColumn.THRESHOLDKPI);
        alertColumnList.add(LogEntryColumn.NODEID);
        alertColumnList.add(LogEntryColumn.TENANTID);
        alertColumnList.add(LogEntryColumn.TENANTIDHASH);
        alertColumnList.add(LogEntryColumn.REQUESTORID);
        alertColumnList.add(LogEntryColumn.USERID);
        alertColumnList.add(LogEntryColumn.WORKPOOL);
        alertColumnList.add(LogEntryColumn.RULEAPPNAMEANDVERSION);
        alertColumnList.add(LogEntryColumn.ENCODEDRULESET);
        alertColumnList.add(LogEntryColumn.PERSONALRULESETYN);
        alertColumnList.add(LogEntryColumn.INTERACTIONNUMBER);
        alertColumnList.add(LogEntryColumn.CORRELATIONID);
        alertColumnList.add(LogEntryColumn.ALERTNUMBER);
        alertColumnList.add(LogEntryColumn.THREAD);
        alertColumnList.add(LogEntryColumn.PEGATHREAD);
        alertColumnList.add(LogEntryColumn.LOGGER);
        alertColumnList.add(LogEntryColumn.STACK);
        alertColumnList.add(LogEntryColumn.LASTINPUT);
        alertColumnList.add(LogEntryColumn.FIRSTACTIVITY);
        alertColumnList.add(LogEntryColumn.LASTSTEP);
        alertColumnList.add(LogEntryColumn.CLIENTPAGELOADID);
        alertColumnList.add(LogEntryColumn.ISSTATELESSAPP);
        alertColumnList.add(LogEntryColumn.CLIENTREQUESTID);
        alertColumnList.add(LogEntryColumn.FUTURE3);
        alertColumnList.add(LogEntryColumn.TRACELIST);
        alertColumnList.add(LogEntryColumn.PALDATA);
        alertColumnList.add(LogEntryColumn.PRIMARYPAGECLASS);
        alertColumnList.add(LogEntryColumn.PRIMARYPAGENAME);
        alertColumnList.add(LogEntryColumn.STEPPAGECLASS);
        alertColumnList.add(LogEntryColumn.STEPPAGENAME);
        alertColumnList.add(LogEntryColumn.PRSTACKTRACE);
        alertColumnList.add(LogEntryColumn.PARAMETERPAGEDATA);
        alertColumnList.add(LogEntryColumn.MESSAGE);

        return alertColumnList;
    }

}
