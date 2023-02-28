/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.parser;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.DateTimeUtilities;
import com.pega.gcs.logviewer.logfile.AbstractLogPattern;
import com.pega.gcs.logviewer.logfile.AbstractLogPattern.LogType;
import com.pega.gcs.logviewer.logfile.AlertLogPattern;
import com.pega.gcs.logviewer.logfile.Log4jPattern;
import com.pega.gcs.logviewer.logfile.Log4jPatternManager;
import com.pega.gcs.logviewer.logfile.LogPatternFactory;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogEntryModel;
import com.pega.gcs.logviewer.parser.cloudkjson.CloudKMessage;

public abstract class LogParser {

    private static final Log4j2Helper LOG = new Log4j2Helper(LogParser.class);

    private AbstractLogPattern abstractLogPattern;

    private Charset charset;

    private Locale locale;

    private DateFormat dateFormat;

    public abstract void parse(String line);

    public abstract void parseFinalInternal();

    public abstract LogEntryModel getLogEntryModel();

    private AtomicInteger processedCount;

    private boolean resetProcessedCount;

    private Pattern cloudkPattern;

    private Boolean isCloudK;

    private ObjectMapper objectMapper;

    public LogParser(AbstractLogPattern abstractLogPattern, Charset charset, Locale locale) {
        this.dateFormat = null;
        this.abstractLogPattern = abstractLogPattern;
        this.charset = charset;
        this.locale = locale;
        this.processedCount = new AtomicInteger(0);
        this.resetProcessedCount = false;

        String cloudkRegex = ".*?\\s(.*)";
        cloudkPattern = Pattern.compile(cloudkRegex);

        this.objectMapper = new ObjectMapper();

    }

    public AbstractLogPattern getLogPattern() {
        return abstractLogPattern;
    }

    protected Charset getCharset() {
        return charset;
    }

    protected Locale getLocale() {
        return locale;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    protected void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void parseFinal() {

        parseFinalInternal();

        resetProcessedCount();

        // sort entries with timestamp.
        LogEntryModel logEntryModel = getLogEntryModel();
        List<LogEntryKey> logEntryKeyList = logEntryModel.getLogEntryKeyList();

        Collections.sort(logEntryKeyList);

        // moved from LogEntryModel.addLogEntry()
        HashMap<LogEntryKey, Integer> keyIndexMap = logEntryModel.getKeyIndexMap();

        if (keyIndexMap != null) {

            keyIndexMap.clear();

            for (int index = 0; index < logEntryKeyList.size(); index++) {

                LogEntryKey key = logEntryKeyList.get(index);

                keyIndexMap.put(key, index);
            }
        }
    }

    protected String quoteTimeStampChars(String input) {
        // put single quotes around text that isn't a supported dateformat char
        StringBuilder result = new StringBuilder();
        // ok to default to false because we also check for index zero below
        boolean lastCharIsDateFormat = false;

        for (int i = 0; i < input.length(); i++) {

            String thisVal = input.substring(i, i + 1);
            boolean thisCharIsDateFormat = DateTimeUtilities.VALID_DATEFORMAT_CHARS.contains(thisVal);
            // we have encountered a non-dateformat char
            if (!thisCharIsDateFormat && (i == 0 || lastCharIsDateFormat)) {
                result.append("'");
            }
            // we have encountered a dateformat char after previously
            // encountering a non-dateformat char
            if (thisCharIsDateFormat && i > 0 && !lastCharIsDateFormat) {
                result.append("'");
            }
            lastCharIsDateFormat = thisCharIsDateFormat;
            result.append(thisVal);
        }
        // append an end single-quote if we ended with non-dateformat char
        if (!lastCharIsDateFormat) {
            result.append("'");
        }
        return result.toString();
    }

    protected String getTimeStampFormat(String log4jPattern) {

        String timeStampFormat = null;
        String timeZoneStr = null;

        int index = log4jPattern.indexOf("%d");

        if (index != -1) {

            index = log4jPattern.indexOf("%d{");

            // use ISO8601 format
            if (index == -1) {
                timeStampFormat = DateTimeUtilities.DATEFORMAT_ISO8601;
            } else {
                // identify format
                int formatIndex = log4jPattern.substring(index).indexOf("}");

                timeStampFormat = log4jPattern.substring(index + "%d{".length(), index + formatIndex);

                if (timeStampFormat.equals("ABSOLUTE")) {
                    timeStampFormat = DateTimeUtilities.DATEFORMAT_ABSOLUTE;
                } else if ("".equals(timeStampFormat) || timeStampFormat.equals("ISO8601")) {
                    timeStampFormat = DateTimeUtilities.DATEFORMAT_ISO8601;
                } else if (timeStampFormat.equals("DATE")) {
                    timeStampFormat = DateTimeUtilities.DATEFORMAT_DATE;
                }

                // identify time zone-Optional
                // if the adjoining character is a curly brace
                int tzIndex = log4jPattern.substring(index + formatIndex + 1).indexOf("{");

                if (tzIndex == 0) {
                    tzIndex = index + formatIndex + 1;
                    timeZoneStr = log4jPattern.substring(tzIndex + "{".length(), log4jPattern.indexOf("}", tzIndex));
                    timeStampFormat = timeStampFormat + " z";
                }
            }

            String quoteTimeStamp = quoteTimeStampChars(timeStampFormat);

            LOG.info("Using Timestamp format: " + quoteTimeStamp);

            DateFormat dateFormat = new SimpleDateFormat(quoteTimeStamp);

            if (timeZoneStr != null) {

                TimeZone tz = TimeZone.getTimeZone(timeZoneStr);
                dateFormat.setTimeZone(tz);
            }

            setDateFormat(dateFormat);

        }

        return timeStampFormat;
    }

    protected String escapeRegexChars(String input) {
        String escapedStr = input;

        escapedStr = escapedStr.replaceAll("\\\\", "\\\\\\");
        escapedStr = escapedStr.replaceAll(Pattern.quote("*"), "\\\\*");
        escapedStr = escapedStr.replaceAll(Pattern.quote("]"), "\\\\]");
        escapedStr = escapedStr.replaceAll(Pattern.quote("["), "\\\\[");
        escapedStr = escapedStr.replaceAll(Pattern.quote("^"), "\\\\^");
        // escapedStr = escapedStr.replaceAll(Pattern.quote("$"), "\\\\$");
        escapedStr = escapedStr.replaceAll(Pattern.quote("."), "\\\\.");
        escapedStr = escapedStr.replaceAll(Pattern.quote("|"), "\\\\|");
        escapedStr = escapedStr.replaceAll(Pattern.quote("?"), "\\\\?");
        escapedStr = escapedStr.replaceAll(Pattern.quote("+"), "\\\\+");
        escapedStr = escapedStr.replaceAll(Pattern.quote("("), "\\\\(");
        escapedStr = escapedStr.replaceAll(Pattern.quote(")"), "\\\\)");
        escapedStr = escapedStr.replaceAll(Pattern.quote("-"), "\\\\-");
        escapedStr = escapedStr.replaceAll(Pattern.quote("{"), "\\\\{");
        escapedStr = escapedStr.replaceAll(Pattern.quote("}"), "\\\\}");
        escapedStr = escapedStr.replaceAll(Pattern.quote("#"), "\\\\#");
        escapedStr = escapedStr.replaceAll(Pattern.quote(" "), "\\\\s");

        return escapedStr;
    }

    public static LogParser getLogParser(String filename, List<String> readLineList, Charset charset, Locale locale,
            TimeZone displayTimezone) {

        LogParser logParser = null;

        Log4jPatternManager log4jPatternManager = Log4jPatternManager.getInstance();

        // check if an Alert log file
        if (filename.toUpperCase().contains("ALERT")) {

            AlertLogPattern alertLogPattern = LogPatternFactory.getInstance().getAlertLogPattern();

            AlertLogParser alertLogParser = new AlertLogParser(alertLogPattern, charset, locale);

            int rowCount = tryLogParser(alertLogParser, readLineList);

            if (rowCount > 0) {
                // success
                LOG.info("creating AlertLogParser for " + filename);
                logParser = alertLogParser;
            }

        } else if ((filename.toUpperCase().contains("CLUSTER")) || (filename.toUpperCase().contains("HAZELCAST"))
                || (filename.toUpperCase().contains("BIX"))) {

            Set<Log4jPattern> log4jPatternSet = log4jPatternManager.getDefaultClusterLog4jPatternSet();

            logParser = getLog4jParser(readLineList, log4jPatternSet, charset, locale, displayTimezone);

        } else if (filename.toUpperCase().contains("DATAFLOW")) {

            Set<Log4jPattern> log4jPatternSet = log4jPatternManager.getDefaultDataflowLog4jPatternSet();

            logParser = getLog4jParser(readLineList, log4jPatternSet, charset, locale, displayTimezone);

        } else if (filename.toUpperCase().contains("DDSMETRICS")) {

            Set<Log4jPattern> log4jPatternSet = log4jPatternManager.getDefaultDdsMetricLog4jPatternSet();

            logParser = getLog4jParser(readLineList, log4jPatternSet, charset, locale, displayTimezone);
        }

        // check if a PegaRules log file
        if (logParser == null) {

            Set<Log4jPattern> pegaRulesLog4jPatternSet = log4jPatternManager.getDefaultRulesLog4jPatternSet();

            logParser = getLog4jParser(readLineList, pegaRulesLog4jPatternSet, charset, locale, displayTimezone);
        }

        // TODO other log file types

        return logParser;
    }

    public static LogParser getLogParserFromPattern(AbstractLogPattern abstractLogPattern, Charset charset,
            Locale locale, TimeZone displayTimezone) {

        LogParser logParser = null;

        LogType logType = abstractLogPattern.getLogType();

        switch (logType) {

        case PEGA_ALERT:
            logParser = new AlertLogParser((AlertLogPattern) abstractLogPattern, charset, locale);
            break;
        case PEGA_RULES:
        case PEGA_CLUSTER:
            logParser = new Log4jPatternParser((Log4jPattern) abstractLogPattern, charset, locale, displayTimezone);
            break;
        case PEGA_DATAFLOW:
            logParser = new DataflowLog4jPatternParser((Log4jPattern) abstractLogPattern, charset, locale,
                    displayTimezone);
            break;
        case PEGA_DDSMETRIC:
            logParser = new DdsMetricLog4jPatternParser((Log4jPattern) abstractLogPattern, charset, locale,
                    displayTimezone);
            break;
        default:
            break;
        }

        return logParser;
    }

    // currently assuming all pattern as of type pegarules. change when
    // implementing other types like WAS, WLS
    // with 8.6 changes, multiple patterns are now matching , hence selecting a pattern that returns the max nos of rows.
    public static LogParser getLog4jParser(List<String> readLineList, Set<Log4jPattern> log4jPatternSet,
            Charset charset, Locale locale, TimeZone displayTimezone) {

        LogParser logParser = null;

        for (Log4jPattern log4jPattern : log4jPatternSet) {

            LogParser currentLogParser = getLogParserFromPattern(log4jPattern, charset, locale, displayTimezone);

            int rowCount = tryLogParser(currentLogParser, readLineList);

            int logParserRowCount = (logParser != null) ? logParser.getProcessedCount() : 0;

            if (rowCount > logParserRowCount) {
                logParser = currentLogParser;
            }

        }

        if (logParser != null) {
            // success
            int logParserRowCount = logParser.getProcessedCount();

            LOG.info("Creating Log4jPatternParser: *rowCount: " + logParserRowCount + " Parser: " + logParser);
        }

        return logParser;
    }

    private static int tryLogParser(LogParser logParser, List<String> readLineList) {

        LOG.info("Trying LogParser " + logParser);

        for (String readLine : readLineList) {
            logParser.parse(readLine);
        }

        logParser.parseFinal();

        int rowCount = logParser.getProcessedCount();

        LOG.info("Trying LogParser " + logParser + " rowCount:" + rowCount);

        return rowCount;

    }

    protected int incrementAndGetProcessedCount() {

        if (resetProcessedCount) {
            processedCount.set(0);
            resetProcessedCount = false;
        }

        return processedCount.incrementAndGet();
    }

    protected void resetProcessedCount() {
        resetProcessedCount = true;
    }

    public int getProcessedCount() {
        return processedCount.get();
    }

    private String extractJson(String line) {

        String json = null;

        Matcher cloudkPatternMatcher = cloudkPattern.matcher(line);

        if (cloudkPatternMatcher.find()) {
            json = cloudkPatternMatcher.group(1);
        }

        return json;
    }

    private Boolean isValidJson(String line) {

        Boolean validJson = null;

        String json = extractJson(line);

        if ((json != null) && (!"".equals(json.trim()))) {
            try {
                objectMapper.readTree(json);
                validJson = Boolean.TRUE;
            } catch (JacksonException e) {
                validJson = Boolean.FALSE;
            }
        }

        return validJson;
    }

    protected String getLineFromCloudK(String line) {

        String lineFromCloudK = null;

        if (isCloudK == null) {
            // check and set if valid json
            Boolean validJson = isValidJson(line);

            isCloudK = validJson != null ? validJson : null;
        }

        if ((isCloudK != null) && (isCloudK)) {
            // extract message from json
            try {

                String json = extractJson(line);

                CloudKMessage cloudKMessage = objectMapper.readValue(json, CloudKMessage.class);

                lineFromCloudK = cloudKMessage.getMessage();

            } catch (Exception e) {
                LOG.error("Error parsing CloudK Json");

                lineFromCloudK = line;
            }

        } else {
            lineFromCloudK = line;
        }

        return lineFromCloudK;
    }
}
