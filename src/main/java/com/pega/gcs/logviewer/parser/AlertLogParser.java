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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.logfile.LogFileType;
import com.pega.gcs.logviewer.model.AlertLogEntry;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogEntryModel;
import com.pega.gcs.logviewer.model.PALStatisticName;
import com.pega.gcs.logviewer.model.alert.AlertMessageList.AlertMessage;
import com.pega.gcs.logviewer.model.alert.AlertMessageListProvider;

public class AlertLogParser extends LogParser {

    private static final Log4j2Helper LOG = new Log4j2Helper(AlertLogParser.class);

    private int alertVersion;

    private ArrayList<String> logEntryColumnList;

    private StringBuilder fullLogEntryTextSB;

    private int capturedColumnCount;

    private int logEntryIndex;

    // store the index of 'messageid' column so as to identify colour
    private int messageIDIndex;

    private int timestampIndex;

    private int observedKPIIndex;

    private int palDataIndex;

    private AlertLogEntryModel alertLogEntryModel;

    private Pattern alertDatePattern;

    public AlertLogParser(LogFileType logFileType, Charset charset, Locale locale) {

        super(logFileType, charset, locale);

        alertVersion = -1;
        logEntryColumnList = null;
        fullLogEntryTextSB = new StringBuilder();
        capturedColumnCount = 0;
        logEntryIndex = 0;

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
    public void parse(String line) {

        String[] fields = null;

        if ((line != null) && (!line.isEmpty()) && (logEntryColumnList == null)) {

            int oldStyleIndex = 0;

            fields = line.split("\\*");

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

                alertLogEntryModel.setLogEntryColumnList(logEntryColumnList);
            } else {
                LOG.info("discarding line: " + line);
            }
        }

        if (logEntryColumnList != null) {

            int logEntryColumnListSize = logEntryColumnList.size();

            // logEntryColumnListSize has additional Line column
            if ((fullLogEntryTextSB.length() > 0) && (capturedColumnCount < (logEntryColumnListSize - 1))) {

                fullLogEntryTextSB.append(line);
                fields = fullLogEntryTextSB.toString().split("\\*");
                capturedColumnCount = fields.length;

            } else {

                buildLogEntry();

                logEntryIndex++;

                fullLogEntryTextSB.append(line);
                fields = fullLogEntryTextSB.toString().split("\\*");
                capturedColumnCount = fields.length;
            }
        } else {
            LOG.info("discarding empty line in the begining");
        }
    }

    @Override
    public void parseFinalInternal() {

        buildLogEntry();

        alertLogEntryModel.processAlertMessageReportModels();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.fringe.logviewer.LogParser#getLogEntryModel()
     */
    @Override
    public LogEntryModel getLogEntryModel() {
        return alertLogEntryModel;
    }

    private AlertLogEntry buildLogEntry() {

        AlertLogEntry alertLogEntry = null;

        if (fullLogEntryTextSB.length() > 0) {

            try {

                LogEntryModel logEntryModel = getLogEntryModel();

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

                Map<String, Integer> messageIdAlertIdMap = alertMessageListProvider.getMessageIdAlertIdMap();

                Integer alertId = messageIdAlertIdMap.get(messageIdStr);

                if (alertId == null) {
                    // get an arbitrary id.
                    alertId = alertMessageListProvider.getNextAdhocAlertId();

                    AlertMessage alertMessage = new AlertMessage();
                    alertMessage.setId(alertId);
                    alertMessage.setMessageID(messageIdStr);
                    alertMessageListProvider.addNewAlertMessage(alertMessage);

                }

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

                LogEntryKey logEntryKey = new LogEntryKey(logEntryIndex, logEntryTime);

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

    private static ArrayList<String> getAlertColumnListV4() {

        ArrayList<String> alertColumnList = new ArrayList<String>();

        alertColumnList.add(LogEntryColumn.LINE.getColumnId());
        alertColumnList.add(LogEntryColumn.TIMESTAMP.getColumnId());
        alertColumnList.add(LogEntryColumn.VERSION.getColumnId());
        alertColumnList.add(LogEntryColumn.MESSAGEID.getColumnId());
        alertColumnList.add(LogEntryColumn.OBSERVEDKPI.getColumnId());
        alertColumnList.add(LogEntryColumn.THRESHOLDKPI.getColumnId());
        alertColumnList.add(LogEntryColumn.NODEID.getColumnId());
        alertColumnList.add(LogEntryColumn.REQUESTORID.getColumnId());
        alertColumnList.add(LogEntryColumn.USERID.getColumnId());
        alertColumnList.add(LogEntryColumn.WORKPOOL.getColumnId());
        alertColumnList.add(LogEntryColumn.ENCODEDRULESET.getColumnId());
        alertColumnList.add(LogEntryColumn.PERSONALRULESETYN.getColumnId());
        alertColumnList.add(LogEntryColumn.INTERACTIONNUMBER.getColumnId());
        alertColumnList.add(LogEntryColumn.ALERTNUMBER.getColumnId());
        alertColumnList.add(LogEntryColumn.THREAD.getColumnId());
        alertColumnList.add(LogEntryColumn.LOGGER.getColumnId());
        alertColumnList.add(LogEntryColumn.STACK.getColumnId());
        alertColumnList.add(LogEntryColumn.LASTINPUT.getColumnId());
        alertColumnList.add(LogEntryColumn.FIRSTACTIVITY.getColumnId());
        alertColumnList.add(LogEntryColumn.LASTSTEP.getColumnId());
        alertColumnList.add(LogEntryColumn.TRACELIST.getColumnId());
        alertColumnList.add(LogEntryColumn.PALDATA.getColumnId());
        alertColumnList.add(LogEntryColumn.MESSAGE.getColumnId());

        return alertColumnList;
    }

    private static ArrayList<String> getAlertColumnListV5() {

        ArrayList<String> alertColumnList = new ArrayList<String>();

        alertColumnList.add(LogEntryColumn.LINE.getColumnId());
        alertColumnList.add(LogEntryColumn.TIMESTAMP.getColumnId());
        alertColumnList.add(LogEntryColumn.VERSION.getColumnId());
        alertColumnList.add(LogEntryColumn.MESSAGEID.getColumnId());
        alertColumnList.add(LogEntryColumn.OBSERVEDKPI.getColumnId());
        alertColumnList.add(LogEntryColumn.THRESHOLDKPI.getColumnId());
        alertColumnList.add(LogEntryColumn.NODEID.getColumnId());
        alertColumnList.add(LogEntryColumn.REQUESTORID.getColumnId());
        alertColumnList.add(LogEntryColumn.USERID.getColumnId());
        alertColumnList.add(LogEntryColumn.WORKPOOL.getColumnId());
        alertColumnList.add(LogEntryColumn.ENCODEDRULESET.getColumnId());
        alertColumnList.add(LogEntryColumn.PERSONALRULESETYN.getColumnId());
        alertColumnList.add(LogEntryColumn.INTERACTIONNUMBER.getColumnId());
        alertColumnList.add(LogEntryColumn.ALERTNUMBER.getColumnId());
        alertColumnList.add(LogEntryColumn.THREAD.getColumnId());
        alertColumnList.add(LogEntryColumn.LOGGER.getColumnId());
        alertColumnList.add(LogEntryColumn.STACK.getColumnId());
        alertColumnList.add(LogEntryColumn.LASTINPUT.getColumnId());
        alertColumnList.add(LogEntryColumn.FIRSTACTIVITY.getColumnId());
        alertColumnList.add(LogEntryColumn.LASTSTEP.getColumnId());
        alertColumnList.add(LogEntryColumn.TRACELIST.getColumnId());
        alertColumnList.add(LogEntryColumn.PALDATA.getColumnId());
        alertColumnList.add(LogEntryColumn.PRIMARYPAGECLASS.getColumnId());
        alertColumnList.add(LogEntryColumn.PRIMARYPAGENAME.getColumnId());
        alertColumnList.add(LogEntryColumn.STEPPAGECLASS.getColumnId());
        alertColumnList.add(LogEntryColumn.STEPPAGENAME.getColumnId());
        alertColumnList.add(LogEntryColumn.PRSTACKTRACE.getColumnId());
        alertColumnList.add(LogEntryColumn.PARAMETERPAGEDATA.getColumnId());
        alertColumnList.add(LogEntryColumn.MESSAGE.getColumnId());

        return alertColumnList;
    }

    private static ArrayList<String> getAlertColumnListV6() {

        ArrayList<String> alertColumnList = new ArrayList<String>();

        alertColumnList.add(LogEntryColumn.LINE.getColumnId());
        alertColumnList.add(LogEntryColumn.TIMESTAMP.getColumnId());
        alertColumnList.add(LogEntryColumn.VERSION.getColumnId());
        alertColumnList.add(LogEntryColumn.MESSAGEID.getColumnId());
        alertColumnList.add(LogEntryColumn.OBSERVEDKPI.getColumnId());
        alertColumnList.add(LogEntryColumn.THRESHOLDKPI.getColumnId());
        alertColumnList.add(LogEntryColumn.NODEID.getColumnId());
        alertColumnList.add(LogEntryColumn.REQUESTORID.getColumnId());
        alertColumnList.add(LogEntryColumn.USERID.getColumnId());
        alertColumnList.add(LogEntryColumn.WORKPOOL.getColumnId());
        alertColumnList.add(LogEntryColumn.RULEAPPNAMEANDVERSION.getColumnId());
        alertColumnList.add(LogEntryColumn.ENCODEDRULESET.getColumnId());
        alertColumnList.add(LogEntryColumn.PERSONALRULESETYN.getColumnId());
        alertColumnList.add(LogEntryColumn.INTERACTIONNUMBER.getColumnId());
        alertColumnList.add(LogEntryColumn.ALERTNUMBER.getColumnId());
        alertColumnList.add(LogEntryColumn.THREAD.getColumnId());
        alertColumnList.add(LogEntryColumn.PEGATHREAD.getColumnId());
        alertColumnList.add(LogEntryColumn.LOGGER.getColumnId());
        alertColumnList.add(LogEntryColumn.STACK.getColumnId());
        alertColumnList.add(LogEntryColumn.LASTINPUT.getColumnId());
        alertColumnList.add(LogEntryColumn.FIRSTACTIVITY.getColumnId());
        alertColumnList.add(LogEntryColumn.LASTSTEP.getColumnId());
        alertColumnList.add(LogEntryColumn.TRACELIST.getColumnId());
        alertColumnList.add(LogEntryColumn.PALDATA.getColumnId());
        alertColumnList.add(LogEntryColumn.PRIMARYPAGECLASS.getColumnId());
        alertColumnList.add(LogEntryColumn.PRIMARYPAGENAME.getColumnId());
        alertColumnList.add(LogEntryColumn.STEPPAGECLASS.getColumnId());
        alertColumnList.add(LogEntryColumn.STEPPAGENAME.getColumnId());
        alertColumnList.add(LogEntryColumn.PRSTACKTRACE.getColumnId());
        alertColumnList.add(LogEntryColumn.PARAMETERPAGEDATA.getColumnId());
        alertColumnList.add(LogEntryColumn.MESSAGE.getColumnId());

        return alertColumnList;
    }

    private static ArrayList<String> getAlertColumnListV7() {

        ArrayList<String> alertColumnList = new ArrayList<String>();

        alertColumnList.add(LogEntryColumn.LINE.getColumnId());
        alertColumnList.add(LogEntryColumn.TIMESTAMP.getColumnId());
        alertColumnList.add(LogEntryColumn.VERSION.getColumnId());
        alertColumnList.add(LogEntryColumn.MESSAGEID.getColumnId());
        alertColumnList.add(LogEntryColumn.OBSERVEDKPI.getColumnId());
        alertColumnList.add(LogEntryColumn.THRESHOLDKPI.getColumnId());
        alertColumnList.add(LogEntryColumn.NODEID.getColumnId());
        alertColumnList.add(LogEntryColumn.TENANTID.getColumnId());
        alertColumnList.add(LogEntryColumn.TENANTIDHASH.getColumnId());
        alertColumnList.add(LogEntryColumn.REQUESTORID.getColumnId());
        alertColumnList.add(LogEntryColumn.USERID.getColumnId());
        alertColumnList.add(LogEntryColumn.WORKPOOL.getColumnId());
        alertColumnList.add(LogEntryColumn.RULEAPPNAMEANDVERSION.getColumnId());
        alertColumnList.add(LogEntryColumn.ENCODEDRULESET.getColumnId());
        alertColumnList.add(LogEntryColumn.PERSONALRULESETYN.getColumnId());
        alertColumnList.add(LogEntryColumn.INTERACTIONNUMBER.getColumnId());
        alertColumnList.add(LogEntryColumn.ALERTNUMBER.getColumnId());
        alertColumnList.add(LogEntryColumn.THREAD.getColumnId());
        alertColumnList.add(LogEntryColumn.PEGATHREAD.getColumnId());
        alertColumnList.add(LogEntryColumn.LOGGER.getColumnId());
        alertColumnList.add(LogEntryColumn.STACK.getColumnId());
        alertColumnList.add(LogEntryColumn.LASTINPUT.getColumnId());
        alertColumnList.add(LogEntryColumn.FIRSTACTIVITY.getColumnId());
        alertColumnList.add(LogEntryColumn.LASTSTEP.getColumnId());
        alertColumnList.add(LogEntryColumn.TRACELIST.getColumnId());
        alertColumnList.add(LogEntryColumn.PALDATA.getColumnId());
        alertColumnList.add(LogEntryColumn.PRIMARYPAGECLASS.getColumnId());
        alertColumnList.add(LogEntryColumn.PRIMARYPAGENAME.getColumnId());
        alertColumnList.add(LogEntryColumn.STEPPAGECLASS.getColumnId());
        alertColumnList.add(LogEntryColumn.STEPPAGENAME.getColumnId());
        alertColumnList.add(LogEntryColumn.PRSTACKTRACE.getColumnId());
        alertColumnList.add(LogEntryColumn.PARAMETERPAGEDATA.getColumnId());
        alertColumnList.add(LogEntryColumn.MESSAGE.getColumnId());

        return alertColumnList;
    }

    private static ArrayList<String> getAlertColumnListV8() {

        ArrayList<String> alertColumnList = new ArrayList<String>();

        alertColumnList.add(LogEntryColumn.LINE.getColumnId());
        alertColumnList.add(LogEntryColumn.TIMESTAMP.getColumnId());
        alertColumnList.add(LogEntryColumn.VERSION.getColumnId());
        alertColumnList.add(LogEntryColumn.MESSAGEID.getColumnId());
        alertColumnList.add(LogEntryColumn.OBSERVEDKPI.getColumnId());
        alertColumnList.add(LogEntryColumn.THRESHOLDKPI.getColumnId());
        alertColumnList.add(LogEntryColumn.NODEID.getColumnId());
        alertColumnList.add(LogEntryColumn.TENANTID.getColumnId());
        alertColumnList.add(LogEntryColumn.TENANTIDHASH.getColumnId());
        alertColumnList.add(LogEntryColumn.REQUESTORID.getColumnId());
        alertColumnList.add(LogEntryColumn.USERID.getColumnId());
        alertColumnList.add(LogEntryColumn.WORKPOOL.getColumnId());
        alertColumnList.add(LogEntryColumn.RULEAPPNAMEANDVERSION.getColumnId());
        alertColumnList.add(LogEntryColumn.ENCODEDRULESET.getColumnId());
        alertColumnList.add(LogEntryColumn.PERSONALRULESETYN.getColumnId());
        alertColumnList.add(LogEntryColumn.INTERACTIONNUMBER.getColumnId());
        alertColumnList.add(LogEntryColumn.CORRELATIONID.getColumnId());
        alertColumnList.add(LogEntryColumn.ALERTNUMBER.getColumnId());
        alertColumnList.add(LogEntryColumn.THREAD.getColumnId());
        alertColumnList.add(LogEntryColumn.PEGATHREAD.getColumnId());
        alertColumnList.add(LogEntryColumn.LOGGER.getColumnId());
        alertColumnList.add(LogEntryColumn.STACK.getColumnId());
        alertColumnList.add(LogEntryColumn.LASTINPUT.getColumnId());
        alertColumnList.add(LogEntryColumn.FIRSTACTIVITY.getColumnId());
        alertColumnList.add(LogEntryColumn.LASTSTEP.getColumnId());
        alertColumnList.add(LogEntryColumn.CLIENTPAGELOADID.getColumnId());
        alertColumnList.add(LogEntryColumn.ISSTATELESSAPP.getColumnId());
        alertColumnList.add(LogEntryColumn.CLIENTREQUESTID.getColumnId());
        alertColumnList.add(LogEntryColumn.FUTURE3.getColumnId());
        alertColumnList.add(LogEntryColumn.TRACELIST.getColumnId());
        alertColumnList.add(LogEntryColumn.PALDATA.getColumnId());
        alertColumnList.add(LogEntryColumn.PRIMARYPAGECLASS.getColumnId());
        alertColumnList.add(LogEntryColumn.PRIMARYPAGENAME.getColumnId());
        alertColumnList.add(LogEntryColumn.STEPPAGECLASS.getColumnId());
        alertColumnList.add(LogEntryColumn.STEPPAGENAME.getColumnId());
        alertColumnList.add(LogEntryColumn.PRSTACKTRACE.getColumnId());
        alertColumnList.add(LogEntryColumn.PARAMETERPAGEDATA.getColumnId());
        alertColumnList.add(LogEntryColumn.MESSAGE.getColumnId());

        return alertColumnList;
    }

}
