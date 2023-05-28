/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.parser;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.core.pattern.FormattingInfo;
import org.apache.logging.log4j.core.pattern.LiteralPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.core.util.OptionConverter;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.DateTimeUtilities;
import com.pega.gcs.fringecommon.utilities.TimeZoneUtil;
import com.pega.gcs.logviewer.logfile.Log4jPattern;
import com.pega.gcs.logviewer.logfile.LogPatternFactory;
import com.pega.gcs.logviewer.model.HazelcastMemberInfo;
import com.pega.gcs.logviewer.model.HazelcastMembership;
import com.pega.gcs.logviewer.model.HazelcastMembership.HzMembershipEvent;
import com.pega.gcs.logviewer.model.Log4jLogEntry;
import com.pega.gcs.logviewer.model.Log4jLogEntryModel;
import com.pega.gcs.logviewer.model.Log4jLogExceptionEntry;
import com.pega.gcs.logviewer.model.Log4jLogHzMembershipEntry;
import com.pega.gcs.logviewer.model.Log4jLogRequestorLockEntry;
import com.pega.gcs.logviewer.model.Log4jLogSystemStartEntry;
import com.pega.gcs.logviewer.model.Log4jLogThreadDumpEntry;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogEntryModel;
import com.pega.gcs.logviewer.model.SystemStart;

public class Log4jPatternParser extends LogParser {

    private static final Log4j2Helper LOG = new Log4j2Helper(Log4jPatternParser.class);

    private static final String NOSPACE_GROUP = "(\\s*?\\S*?\\s*?)";
    private static final String DEFAULT_GROUP = "(.*?)";
    private static final String GREEDY_GROUP = "(.*)";
    private static final String LIMITING_GROUP = "(.{n,m})"; // (\\s*?.{5,5})
    private static final String LOG_LEVEL_GROUP = "([a-zA-Z\\s]{n,m})"; // (\\s*?.{5,5})
    @SuppressWarnings("unused")
    private static final String MULTIPLE_SPACES_REGEXP = "[ ]+";
    private static final String OPTIONAL_GROUP = "(.*?)?";

    private static Pattern MDC_PATTERN = Pattern.compile("MDC\\{(.*?)\\}");
    private static Pattern MAP_PATTERN = Pattern.compile("MAP\\{(.*?)\\}");

    private static final long THREADDUMP_TIMESTAMP_INTERVAL = 5 * 60 * 1000;

    private static final int MATCHER_STR_LEN = 500;

    private static final String V2_STR_FORMAT = "%s [%s] [%s] [%s] (%s) %s %s %s %s - %s";

    private String regExp;

    private Pattern linePattern;
    private Pattern requestorLockPattern;
    private Pattern exceptionPattern;
    private Pattern discardedMessagesPattern;
    private Pattern systemDatepattern;
    private Pattern hzMemberspattern;

    private ArrayList<String> logEntryColumnValueList;
    private String logEntryText;
    private ArrayList<String> additionalLines;

    private int lineCount;

    private StringBuilder parseLine;
    private int parseLineCount;

    private Log4jLogEntryModel log4jLogEntryModel;

    private int levelIndex;
    private int timestampIndex;
    private int loggerIndex;
    private int messageIndex;

    private TimeZone modelTimezone;
    private TimeZone displayTimezone;

    private AtomicInteger systemStartCounter;
    private SystemStart systemStart;

    private AtomicInteger threadDumpEntryCounter;
    private Log4jLogThreadDumpEntry log4jLogThreadDumpEntry;

    private AtomicInteger hzMembershipCounter;
    private HazelcastMembership hazelcastMembership;
    private Boolean clusterMembershipManagerV84;

    public Log4jPatternParser(Log4jPattern log4jPattern, Charset charset, Locale locale, TimeZone displayTimezone) {

        super(log4jPattern, charset, locale);

        this.displayTimezone = displayTimezone;

        parseLine = null;
        parseLineCount = 1;

        levelIndex = -1;
        timestampIndex = -1;
        loggerIndex = -1;
        messageIndex = -1;

        modelTimezone = null;

        // System Start
        systemStartCounter = new AtomicInteger(0);
        systemStart = null;

        // Thread Dump
        threadDumpEntryCounter = new AtomicInteger(0);
        log4jLogThreadDumpEntry = null;

        // Hazelcast Membership
        hzMembershipCounter = new AtomicInteger(0);
        hazelcastMembership = null;
        clusterMembershipManagerV84 = null;

        logEntryColumnValueList = new ArrayList<>();
        logEntryText = null;
        additionalLines = new ArrayList<>();

        String systemDateRegex = "System date:(?:.*?)\\d{2}\\:\\d{2}\\:\\d{2}(.*?)\\d{4}\\s+Total memory\\:(?:.*?)";
        systemDatepattern = Pattern.compile(systemDateRegex);

        // @formatter:off
        // CHECKSTYLE:OFF
        String threadRegEx = "(.*)Unable to synchronize on requestor (.+?) within (\\d{1,19}) seconds: \\(thisThread = (.+?)\\) \\(originally locked by = (.+?)\\) \\(finally locked by = (.+?)\\)(.*)";
        // CHECKSTYLE:ON
        // @formatter:on

        requestorLockPattern = Pattern.compile(threadRegEx);
        // exceptionPattern = Pattern.compile("([\\w\\.]*Exception|Error)");
        exceptionPattern = Pattern.compile("([\\w\\.]+Exception)|([\\w\\.]+Error)");

        discardedMessagesPattern = Pattern.compile("Discarded (.+?) messages due to full event buffer(.*)");

        hzMemberspattern = Pattern.compile("Members \\[(.+?)\\] \\{");

        List<LogEntryColumn> logEntryColumnList = getLogEntryColumnsFromLog4jPattern();

        log4jLogEntryModel = new Log4jLogEntryModel(getDateFormat(), displayTimezone);

        log4jLogEntryModel.updateLogEntryColumnList(logEntryColumnList);

    }

    protected int getLevelIndex() {

        if (levelIndex == -1) {
            LogEntryModel logEntryModel = getLogEntryModel();
            levelIndex = logEntryModel.getLogEntryColumnIndex(LogEntryColumn.LEVEL);
        }

        return levelIndex;
    }

    protected int getTimestampIndex() {

        if (timestampIndex == -1) {
            LogEntryModel logEntryModel = getLogEntryModel();
            timestampIndex = logEntryModel.getLogEntryColumnIndex(LogEntryColumn.TIMESTAMP);
        }

        return timestampIndex;
    }

    private int getLoggerIndex() {

        if (loggerIndex == -1) {
            LogEntryModel logEntryModel = getLogEntryModel();
            loggerIndex = logEntryModel.getLogEntryColumnIndex(LogEntryColumn.LOGGER);
        }

        return loggerIndex;
    }

    protected int getMessageIndex() {

        if (messageIndex == -1) {
            LogEntryModel logEntryModel = getLogEntryModel();
            messageIndex = logEntryModel.getLogEntryColumnIndex(LogEntryColumn.MESSAGE);
        }

        return messageIndex;
    }

    @Override
    public String toString() {
        return "Log4jPatternParser [regExp=" + regExp + ", log4jPattern=" + getLogPattern() + "]";
    }

    private List<LogEntryColumn> getLogEntryColumnsFromLog4jPattern() {

        ArrayList<LogEntryColumn> logEntryColumnList = new ArrayList<>();

        lineCount = 0;
        regExp = "";

        Log4jPattern log4jPattern = (Log4jPattern) getLogPattern();

        String logPatternStr = log4jPattern.getPatternString();

        String pattern = OptionConverter.convertSpecialChars(logPatternStr);

        LOG.info("getLogEntryColumnsFromLog4jPattern - logPatternStr: " + logPatternStr);

        PatternParser patternParser = LogPatternFactory.getInstance().getPatternParser();

        List<PatternFormatter> patternFormatterList = patternParser.parse(pattern);

        logEntryColumnList.add(LogEntryColumn.LINE);

        for (PatternFormatter patternFormatter : patternFormatterList) {

            String format = null;

            FormattingInfo formattingInfo = patternFormatter.getFormattingInfo();
            int minLength = formattingInfo.getMinLength();
            int maxLength = formattingInfo.getMaxLength();

            PatternConverter patternConverter = patternFormatter.getConverter();
            String patternConverterName = patternConverter.getName();

            String mapPropertyName = null;
            String mdcPropertyName = null;

            if (patternConverterName.startsWith("MAP{")) {
                mapPropertyName = getMAPName(patternConverterName);
                patternConverterName = "MAP";

            } else if (patternConverterName.startsWith("MDC{")) {
                mdcPropertyName = getMDCName(patternConverterName);
                patternConverterName = "MDC";
            }

            switch (patternConverterName) {

            // ClassNamePatternConverter
            case "Class Name":

                format = DEFAULT_GROUP;

                regExp = regExp + format;
                logEntryColumnList.add(LogEntryColumn.CLASS);

                break;

            // DatePatternConverter
            case "Date":

                format = getTimeStampFormat(logPatternStr);
                format = format.replaceAll(Pattern.quote("+"), "[+]");
                format = format.replaceAll(("[" + DateTimeUtilities.VALID_DATEFORMAT_CHARS + "]+"), "\\\\S+");
                format = format.replaceAll(Pattern.quote("."), "\\\\.");
                format = "(" + format + ")";

                regExp = regExp + format;
                logEntryColumnList.add(LogEntryColumn.TIMESTAMP);

                break;

            // FileLocationPatternConverter
            case "File Location":

                format = DEFAULT_GROUP;

                regExp = regExp + format;
                logEntryColumnList.add(LogEntryColumn.FILE);

                break;

            // FullLocationPatternConverter
            case "Full Location":

                format = DEFAULT_GROUP;

                regExp = regExp + format;
                logEntryColumnList.add(LogEntryColumn.LOCATIONINFO);

                break;

            // LevelPatternConverter
            case "Level":

                if (minLength > 0) {
                    format = LOG_LEVEL_GROUP;
                    // the Regex parsing is very slow if the max value is open
                    // ended like {5,} hence setting both min and max to min
                    // value because i assume that standard log levels are
                    // not going to be greater than 5 chars.
                    format = format.replace("n", String.valueOf(minLength));

                    if (maxLength != Integer.MAX_VALUE) {
                        format = format.replace("m", String.valueOf(maxLength));
                    } else {
                        format = format.replace("m", String.valueOf(minLength));
                    }
                } else {
                    format = NOSPACE_GROUP;
                }

                regExp = regExp + format;
                logEntryColumnList.add(LogEntryColumn.LEVEL);

                break;

            // LineLocationPatternConverter
            case "Line":

                format = DEFAULT_GROUP;

                regExp = regExp + format;
                logEntryColumnList.add(LogEntryColumn.LINE);

                break;

            // LineSeparatorPatternConverter
            case "Line Sep":

                lineCount++;

                break;

            // LiteralPatternConverter
            case "Literal":

                format = ((LiteralPatternConverter) patternConverter).getLiteral();

                if (Pattern.quote("${CW_LOG} ").equals(Pattern.quote(format))) {
                    format = ".*?[ ]+";
                } else {
                    format = escapeRegexChars(format);
                }

                regExp = regExp + format;

                break;

            // LoggerPatternConverter
            case "Logger":

                format = NOSPACE_GROUP;

                regExp = regExp + format;
                logEntryColumnList.add(LogEntryColumn.LOGGER);

                break;

            // MapPatternConverter
            case "MAP":

                format = DEFAULT_GROUP;

                regExp = regExp + format;

                LogEntryColumn mapLogEntryColumn = LogEntryColumn.getTableColumnById(mapPropertyName.toUpperCase());

                logEntryColumnList.add(mapLogEntryColumn);

                break;

            // MdcPatternConverter
            case "MDC":

                if (minLength > 0) {
                    format = LIMITING_GROUP;
                    format = format.replace("n", String.valueOf(minLength));

                    if ((maxLength > 0) && (maxLength < Integer.MAX_VALUE)) {
                        format = format.replace("m", String.valueOf(maxLength));
                    } else {
                        format = format.replace("m", "");
                    }
                } else {
                    format = OPTIONAL_GROUP;
                }

                regExp = regExp + format;

                LogEntryColumn mdcLogEntryColumn = LogEntryColumn.getTableColumnById(mdcPropertyName.toUpperCase());

                logEntryColumnList.add(mdcLogEntryColumn);

                break;

            // MessagePatternConverter
            case "Message":

                format = GREEDY_GROUP;

                regExp = regExp + format;
                logEntryColumnList.add(LogEntryColumn.MESSAGE);

                break;

            // MethodLocationPatternConverter
            case "Method":

                format = DEFAULT_GROUP;

                regExp = regExp + format;
                logEntryColumnList.add(LogEntryColumn.METHOD);

                break;

            // NdcPatternConverter
            case "NDC":

                format = DEFAULT_GROUP;

                regExp = regExp + format;
                logEntryColumnList.add(LogEntryColumn.NDC);

                break;

            // RelativeTimePatternConverter
            case "Time":

                format = DEFAULT_GROUP;

                regExp = regExp + format;
                logEntryColumnList.add(LogEntryColumn.RELATIVETIME);

                break;

            // SequenceNumberPatternConverter
            case "Sequence Number":

                format = DEFAULT_GROUP;

                regExp = regExp + format;
                logEntryColumnList.add(LogEntryColumn.LOG4JID);

                break;

            // SimpleLiteralPatternConverter
            case "SimpleLiteral":

                StringBuilder literalSB = new StringBuilder();

                patternConverter.format(null, literalSB);

                format = literalSB.toString();

                format = escapeRegexChars(format);

                regExp = regExp + format;

                break;

            // ThreadNamePatternConverter
            case "Thread":

                format = DEFAULT_GROUP;

                regExp = regExp + format;
                logEntryColumnList.add(LogEntryColumn.THREAD);

                break;

            default:
                LOG.error("Unknown patternConverterName: " + patternConverterName);
                break;

            }

        }

        // LOG.info("loggerIndex: " + levelIndex);
        LOG.info("generateRegExp - lineCount: " + lineCount);
        LOG.info("generateRegExp - regExp: " + regExp);
        LOG.info("generateRegExp - logEntryColumnList: " + logEntryColumnList);

        linePattern = Pattern.compile(regExp);

        return logEntryColumnList;
    }

    private String getMDCName(String patternConverterName) {

        String propertyName = patternConverterName;

        Matcher matcher = MDC_PATTERN.matcher(patternConverterName);

        boolean found = matcher.find();

        if (found) {
            propertyName = matcher.group(1).trim();
        }

        return propertyName;
    }

    private String getMAPName(String patternConverterName) {

        String propertyName = patternConverterName;

        Matcher matcher = MAP_PATTERN.matcher(patternConverterName);

        boolean found = matcher.find();

        if (found) {
            propertyName = matcher.group(1).trim();
        }

        return propertyName;
    }

    protected void addLogEntryColumnValue(String value) {
        logEntryColumnValueList.add(value);
    }

    protected void addLogEntryColumnValue(int index, String value) {
        logEntryColumnValueList.add(index, value);
    }

    protected void addAdditionalLine(String line) {
        additionalLines.add(line);
    }

    @Override
    protected void parseV1(String line) {

        line = getLineFromCloudK(line);

        if (parseLine == null) {
            parseLine = new StringBuilder();
        }

        if (parseLineCount < lineCount) {
            // accumulate
            // parseLine = parseLine + System.getProperty("line.separator") + line;
            parseLine.append(System.getProperty("line.separator"));
            parseLine.append(line);
            parseLineCount++;
            return;

        } else {
            if (parseLineCount > 1) {
                // parseLine = parseLine + System.getProperty("line.separator") + line;
                parseLine.append(System.getProperty("line.separator"));
                parseLine.append(line);

            } else {
                // parseLine = line;
                parseLine.append(line);
            }
        }

        // performance change- pick only first 1000 chars for pattern matching, append
        // the remaining to last group

        int parseLineLen = parseLine.length();
        String matcherStr = null;
        String balanceStr = null;

        if (parseLineLen > MATCHER_STR_LEN) {
            matcherStr = parseLine.substring(0, MATCHER_STR_LEN);
            balanceStr = parseLine.substring(MATCHER_STR_LEN);
        } else {
            matcherStr = parseLine.toString();
        }

        Matcher lineMatcher = linePattern.matcher(matcherStr);
        // capture the current one and build the previous one
        if (lineMatcher.matches()) {

            buildLogEntry();

            logEntryText = parseLine.toString();

            MatchResult result = lineMatcher.toMatchResult();

            int groupCount = result.groupCount();

            for (int i = 1; i <= groupCount; i++) {

                StringBuilder valueSB = new StringBuilder(result.group(i));

                // if the original string was split, append
                // the remaining to last group
                if ((balanceStr != null) && (i == groupCount)) {
                    valueSB.append(balanceStr);
                }

                addLogEntryColumnValue(valueSB.toString());
            }

        } else {
            addAdditionalLine(parseLine.toString());
        }

        parseLine = null;
    }

    @Override
    protected void parseV2(String line) {

        StringBuilder logEntryTextSB = new StringBuilder();

        Map<String, Object> fieldMap = getCloudKFieldMap(line);

        if (fieldMap != null) {

            processCloudKLogMap(logEntryTextSB, fieldMap);

            logEntryText = logEntryTextSB.toString();

            buildLogEntry();
        }
    }

    @Override
    protected void parseV3(String line) {

        StringBuilder logEntryTextSB = new StringBuilder();

        Map<String, Object> fieldMap = getCloudKFieldMap(line);

        if (fieldMap != null) {

            Map<String, Object> logMap = (Map<String, Object>) fieldMap.get("log");

            processCloudKLogMap(logEntryTextSB, logMap);

            logEntryText = logEntryTextSB.toString();

            buildLogEntry();
        }
    }

    // construct the last event when the are no more log lines
    @Override
    public void parseFinalInternal() {

        buildLogEntry();
    }

    @Override
    public LogEntryModel getLogEntryModel() {
        return log4jLogEntryModel;
    }

    protected ArrayList<String> getLogEntryColumnValueList() {
        return logEntryColumnValueList;
    }

    protected String getLogEntryText() {
        return logEntryText;
    }

    protected void clearBuildLogEntryData() {
        logEntryText = null;
        logEntryColumnValueList.clear();
        additionalLines.clear();
    }

    protected void processException(StringBuilder logEntryTextSB, Map<String, String> exceptionMap) {

        if (exceptionMap != null) {

            String stacktrace = (String) exceptionMap.get("stacktrace");

            if (stacktrace != null) {

                String[] stacktraceLines = stacktrace.split("\\n");

                for (String stacktraceLine : stacktraceLines) {

                    logEntryTextSB.append(System.lineSeparator());
                    logEntryTextSB.append(stacktraceLine);
                    addAdditionalLine(stacktraceLine);
                }
            }
        }

    }

    protected void processCloudKLogMap(StringBuilder logEntryTextSB, Map<String, Object> logMap) {

        String time = (String) logMap.get("@timestamp");
        String thread = (String) logMap.get("thread_name");
        String pegaThread = (String) logMap.get("pegathread");
        String app = (String) logMap.get("app");
        String logger = (String) logMap.get("logger_name");
        String logLevel = (String) logMap.get("level");
        String stack = (String) logMap.get("stack");
        String requestorId = (String) logMap.get("RequestorId");
        String userid = (String) logMap.get("userid");
        String message = (String) logMap.get("message");
        @SuppressWarnings("unchecked")
        Map<String, String> exceptionMap = (Map<String, String>) logMap.get("exception");

        // sometimes not all fields are present
        thread = thread != null ? thread : "";
        pegaThread = pegaThread != null ? pegaThread : "";
        app = app != null ? app : "";
        logger = logger != null ? logger : "";
        logLevel = logLevel != null ? logLevel : "";
        stack = stack != null ? stack : "";
        requestorId = requestorId != null ? requestorId : "";
        userid = userid != null ? userid : "";
        message = message != null ? message : "";

        // (TIMESTAMP);
        // (THREAD);
        // (PEGATHREAD);
        // (APP);
        // (LOGGER);
        // (LEVEL);
        // (STACK);
        // (REQUESTORID);
        // (USERID);
        // (MESSAGE);

        addLogEntryColumnValue(time);
        addLogEntryColumnValue(thread);
        addLogEntryColumnValue(pegaThread);
        addLogEntryColumnValue(app);
        addLogEntryColumnValue(logger);
        addLogEntryColumnValue(logLevel);
        addLogEntryColumnValue(stack);
        addLogEntryColumnValue(requestorId);
        addLogEntryColumnValue(userid);
        addLogEntryColumnValue(message);

        logEntryTextSB.append(String.format(V2_STR_FORMAT, time, thread, pegaThread, app, logger, logLevel, stack,
                requestorId, userid, message));

        processException(logEntryTextSB, exceptionMap);
    }

    protected void buildLogEntry() {

        Log4jLogEntryModel log4jLogEntryModel = (Log4jLogEntryModel) getLogEntryModel();
        AtomicInteger logEntryIndex = getLogEntryIndex();
        ArrayList<String> logEntryColumnValueList = getLogEntryColumnValueList();
        String logEntryText = getLogEntryText();

        if ((logEntryColumnValueList.size() == 0) && (additionalLines.size() > 0)) {

            LOG.info("found " + additionalLines.size() + " additional lines at the begining.");

            logEntryIndex.addAndGet(additionalLines.size());

            additionalLines.clear();

        } else if (logEntryColumnValueList.size() > 0) {

            logEntryIndex.incrementAndGet();

            addLogEntryColumnValue(0, String.valueOf(logEntryIndex));

            boolean sysdateEntry = false;
            boolean systemStartEntry = false;
            boolean threadDumpEntry = false;
            boolean requestorLockEntry = false;
            boolean exceptionsEntry = false;
            boolean hzMembershipEntry = false;
            boolean clusterMembershipEntry = false;

            int levelIndex = getLevelIndex();
            int timestampIndex = getTimestampIndex();
            int loggerIndex = getLoggerIndex();
            int messageIndex = getMessageIndex();

            String message = logEntryColumnValueList.get(messageIndex);

            String logger = logEntryColumnValueList.get(loggerIndex).toUpperCase();

            if ((logger.endsWith("INTERNAL.ASYNC.AGENT")) || (logger.endsWith("ENGINE.CONTEXT.AGENT"))
                    || (logger.endsWith("AGENTCONFIGURATION"))) {

                if (message.startsWith("System date:")) {

                    sysdateEntry = true;

                    if (modelTimezone == null) {

                        Matcher systemDateMatcher = systemDatepattern.matcher(message);

                        if (systemDateMatcher.matches()) {

                            String timezoneID = systemDateMatcher.group(1).trim();
                            LOG.info("Identified System Date timezone Id: " + timezoneID);

                            modelTimezone = TimeZoneUtil.getTimeZoneFromAbbreviatedString(timezoneID);

                            if (modelTimezone == null) {

                                TimeZone currTimezone = log4jLogEntryModel.getModelDateFormat().getTimeZone();

                                LOG.error("Unable to detect timezone, using " + currTimezone,
                                        new Exception("Unable to parse timezoneID: " + timezoneID));

                                // modelTimezone = TimeZoneUtil.getTimeZoneFromAbbreviatedString("GMT");
                                modelTimezone = TimeZone.getDefault();

                            } else {
                                LOG.info("Setting modelTimezone: " + modelTimezone);
                            }

                            log4jLogEntryModel.setModelDateFormatTimeZone(modelTimezone);

                            if (displayTimezone == null) {
                                log4jLogEntryModel.setDisplayDateFormatTimeZone(modelTimezone);
                                LOG.info("Setting displayTimezone: " + displayTimezone);
                            }
                        } else {
                            LOG.info("attempt to extract system date modelTimezone failed");
                        }
                    }
                }
            } else if (logger.endsWith("ERVLET.WEBAPPLIFECYCLELISTENER")) {

                if ((systemStart == null) && (message.startsWith("System Start Date"))) {

                    systemStartEntry = true;

                } else if ((systemStart != null) && (message.startsWith("System Start Date"))) {
                    // may end up here when jvm is killed and restarted.
                    LOG.info("Found a killed jvm and restart");

                    systemStart.setAbruptStop(true);

                    systemStartEntry = true;
                    systemStart = null;

                } else if (systemStart != null) {

                    if ((message.startsWith("Web Tier initialization is complete."))
                            || (message.startsWith("Exception during startup processing"))
                            || (message.startsWith("Thin Web Tier initialization is complete."))) {

                        // this systemStart is already added to the list.
                        systemStartEntry = false;
                        systemStart = null;
                    }
                }
            } else if (logger.endsWith(".ENVIRONMENTDIAGNOSTICS")) {

                if (message.startsWith("--- Thread Dump Starts ---")) {

                    threadDumpEntry = true;
                }

            } else if (logger.endsWith("TIL.HAZELCASTMEMBERSHIPMANAGER")) {

                if (hazelcastMembership == null) {

                    hzMembershipEntry = true;
                }

            } else if (logger.endsWith(".UTIL.CLUSTERMEMBERSHIPMANAGER")) {

                clusterMembershipEntry = true;
                // the members info is also printed on node start. currently not handling that scenario

                // Topology print format changed in 8.4
                if (clusterMembershipManagerV84 == null) {

                    if ("".equals(message)) {
                        clusterMembershipManagerV84 = Boolean.TRUE;
                    } else {
                        clusterMembershipManagerV84 = Boolean.FALSE;
                    }
                }

                if (!clusterMembershipManagerV84) {

                    if ((hazelcastMembership != null) && (message.trim().equals("}"))) {
                        // members block ended
                        hazelcastMembership.postProcess();

                        hazelcastMembership = null;

                    } else {
                        processHazelcastMembership(hazelcastMembership, message);
                    }
                }
            }

            String timestampStr = logEntryColumnValueList.get(timestampIndex);

            DateFormat modelDateFormat = log4jLogEntryModel.getModelDateFormat();

            long logEntryTime = -1;
            try {

                Date logEntryDate = modelDateFormat.parse(timestampStr);
                logEntryTime = logEntryDate.getTime();

            } catch (ParseException e) {
                LOG.error("Error parsing line: [" + logEntryIndex + "] logentry: [" + logEntryText + "]", e);
            }

            LogEntryKey logEntryKey = new LogEntryKey(logEntryIndex.intValue(), logEntryTime);

            String level = logEntryColumnValueList.get(levelIndex);
            byte logLevelId = getLogLevelId(level.trim());

            // check only first 3 lines of the additional line set.
            int exceptionLineCounter = 0;

            // construct log entry text
            StringBuilder fullLogEntryTextSB = new StringBuilder();
            fullLogEntryTextSB.append(logEntryText);

            if (additionalLines.size() > 0) {

                for (String line : additionalLines) {

                    fullLogEntryTextSB.append(System.getProperty("line.separator"));
                    fullLogEntryTextSB.append(line);

                    Map<String, List<LogEntryKey>> exceptionClassLogEntryIndexMap = log4jLogEntryModel
                            .getExceptionClassLogEntryIndexMap();

                    // in case of error log check if there is an exception in
                    // the lines
                    if ((logLevelId > 5) && (exceptionLineCounter < 3) && (!exceptionsEntry)) {

                        Matcher matcher = exceptionPattern.matcher(line);

                        if (matcher.find()) {

                            String exception = matcher.group(0);

                            List<LogEntryKey> errorLogEntryIndexList;
                            errorLogEntryIndexList = exceptionClassLogEntryIndexMap.get(exception);

                            if (errorLogEntryIndexList == null) {
                                errorLogEntryIndexList = new ArrayList<LogEntryKey>();
                                exceptionClassLogEntryIndexMap.put(exception, errorLogEntryIndexList);
                            }

                            errorLogEntryIndexList.add(logEntryKey);
                            exceptionsEntry = true;

                        }
                        // if the exception is not found in the first 3
                        // additional
                        // lines, then search in the main text.
                    }

                    exceptionLineCounter++;

                    // Topology print format changed in 8.4
                    if (clusterMembershipEntry && (clusterMembershipManagerV84 != null)
                            && (clusterMembershipManagerV84)) {

                        if ((hazelcastMembership != null) && (line.trim().equals("}"))) {

                            // members block ended
                            hazelcastMembership.postProcess();

                            hazelcastMembership = null;

                        } else {
                            processHazelcastMembership(hazelcastMembership, line);
                        }
                    }
                }
            }

            // if this is just an error entry, capture it in an empty group
            if ((logLevelId > 5) && (!exceptionsEntry)) {

                Map<String, List<LogEntryKey>> errorLogEntryIndexMap = log4jLogEntryModel
                        .getExceptionClassLogEntryIndexMap();

                Matcher matcher = exceptionPattern.matcher(message);

                if (matcher.find()) {

                    String exception = matcher.group(0);

                    List<LogEntryKey> errorLogEntryIndexList;
                    errorLogEntryIndexList = errorLogEntryIndexMap.get(exception);

                    if (errorLogEntryIndexList == null) {
                        errorLogEntryIndexList = new ArrayList<>();
                        errorLogEntryIndexMap.put(exception, errorLogEntryIndexList);
                    }

                    errorLogEntryIndexList.add(logEntryKey);
                } else {
                    List<LogEntryKey> errorLogEntryIndexList;
                    errorLogEntryIndexList = errorLogEntryIndexMap.get("");

                    if (errorLogEntryIndexList == null) {
                        errorLogEntryIndexList = new ArrayList<>();
                        errorLogEntryIndexMap.put("", errorLogEntryIndexList);
                    }

                    errorLogEntryIndexList.add(logEntryKey);
                }

                exceptionsEntry = true;

            }

            String fullLogEntryText = fullLogEntryTextSB.toString();

            Log4jLogEntry log4jLogEntry;

            // if a thread dump is found earlier then look for 'Unable to
            // synchronize' messages. also need to check that whether this
            // message is within 5 minutes (using default setting) of the
            // occurrence of thread dump

            if ((!threadDumpEntry) && (log4jLogThreadDumpEntry != null) && (logEntryTime != -1)) {

                // check if we are within 5 minutes threshold
                long threadDumpEntryLogEntryTime = log4jLogThreadDumpEntry.getKey().getTimestamp();

                threadDumpEntryLogEntryTime = threadDumpEntryLogEntryTime + THREADDUMP_TIMESTAMP_INTERVAL;

                if ((threadDumpEntryLogEntryTime > logEntryTime)) {

                    Matcher discardedMessagesPatternMatcher = discardedMessagesPattern.matcher(message);
                    boolean discardedLogMessage = discardedMessagesPatternMatcher.matches();

                    int rleIndex = message.indexOf("com.pega.pegarules.pub.context.RequestorLockException");

                    if ((!discardedLogMessage) && (rleIndex != -1) && (additionalLines.size() > 0)) {

                        // also check the message "Unable to synchronize on
                        // requestor"
                        String syncMessage = additionalLines.get(0);

                        int smIndex = syncMessage.indexOf("Unable to synchronize on requestor");

                        if (smIndex != -1) {
                            requestorLockEntry = true;
                        }
                    }
                } else {
                    // we have passed the 5 minutes threshold. thread dump
                    // entry is not required now.
                    log4jLogThreadDumpEntry = null;
                }
            }

            if (threadDumpEntry) {

                int index = threadDumpEntryCounter.incrementAndGet();

                log4jLogThreadDumpEntry = new Log4jLogThreadDumpEntry(index, logEntryKey, logEntryColumnValueList,
                        fullLogEntryText, sysdateEntry, logLevelId);

                log4jLogEntry = log4jLogThreadDumpEntry;

                List<LogEntryKey> threadDumpLogEntryIndexList;
                threadDumpLogEntryIndexList = log4jLogEntryModel.getThreadDumpLogEntryKeyList();
                threadDumpLogEntryIndexList.add(logEntryKey);

            } else if (requestorLockEntry) {

                Log4jLogRequestorLockEntry log4jLogRequestorLockEntry;

                log4jLogRequestorLockEntry = new Log4jLogRequestorLockEntry(logEntryKey, logEntryColumnValueList,
                        fullLogEntryText, sysdateEntry, logLevelId);

                String requestorLockStr = additionalLines.get(0);

                parseRequestorLockString(requestorLockStr, log4jLogRequestorLockEntry);

                List<Log4jLogRequestorLockEntry> log4jLogRequestorLockEntryList;

                log4jLogRequestorLockEntryList = log4jLogThreadDumpEntry.getLog4jLogRequestorLockEntryList();

                log4jLogRequestorLockEntryList.add(log4jLogRequestorLockEntry);

                log4jLogEntry = log4jLogRequestorLockEntry;

            } else if (systemStartEntry) {
                log4jLogEntry = new Log4jLogSystemStartEntry(logEntryKey, logEntryColumnValueList, fullLogEntryText,
                        sysdateEntry, logLevelId);
            } else if (exceptionsEntry) {
                log4jLogEntry = new Log4jLogExceptionEntry(logEntryKey, logEntryColumnValueList, fullLogEntryText,
                        sysdateEntry, logLevelId);
            } else if (hzMembershipEntry) {
                log4jLogEntry = new Log4jLogHzMembershipEntry(logEntryKey, logEntryColumnValueList, fullLogEntryText,
                        sysdateEntry, logLevelId);
            } else {
                log4jLogEntry = new Log4jLogEntry(logEntryKey, logEntryColumnValueList, fullLogEntryText, sysdateEntry,
                        logLevelId);
            }

            if (systemStartEntry) {

                int index = systemStartCounter.incrementAndGet();
                systemStart = new SystemStart(index, logEntryKey);

                List<SystemStart> systemStartList;
                systemStartList = log4jLogEntryModel.getSystemStartList();

                systemStartList.add(systemStart);

            } else if (systemStart != null) {

                systemStart.addLogEntryKey(logEntryKey);
            }

            if (hzMembershipEntry) {

                HzMembershipEvent hzMembershipEvent = null;
                HazelcastMemberInfo hazelcastMemberInfo;

                int index = hzMembershipCounter.incrementAndGet();

                if (message.startsWith("New member has joined the cluster")) {
                    hzMembershipEvent = HzMembershipEvent.MEMBER_ADDED;
                } else {
                    hzMembershipEvent = HzMembershipEvent.MEMBER_LEFT;
                }

                LogHzMemberInfoParser logHzMemberInfoParser = LogHzMemberInfoParser.getInstance();
                hazelcastMemberInfo = logHzMemberInfoParser.getHazelcastMemberInfo(message);

                hazelcastMembership = new HazelcastMembership(index, logEntryKey, hzMembershipEvent,
                        hazelcastMemberInfo);

                List<HazelcastMembership> hazelcastMembershipList;
                hazelcastMembershipList = log4jLogEntryModel.getHazelcastMembershipList();

                hazelcastMembershipList.add(hazelcastMembership);
            }

            logEntryIndex.addAndGet(additionalLines.size());

            log4jLogEntryModel.addLogEntry(log4jLogEntry, logEntryColumnValueList, getCharset(), getLocale());

            // update the processed counter
            incrementAndGetProcessedCount();

            clearBuildLogEntryData();
        }

    }

    private void processHazelcastMembership(HazelcastMembership hazelcastMembership, String message) {

        if (hazelcastMembership != null) {

            Matcher hzMembersMatcher = hzMemberspattern.matcher(message);

            if (hzMembersMatcher.matches()) {
                String memberCountStr = hzMembersMatcher.group(1);

                try {

                    int memberCount = Integer.parseInt(memberCountStr);

                    hazelcastMembership.setMemberCount(memberCount);

                } catch (NumberFormatException nfe) {
                    LOG.error("Unable to parse Hz Member count: " + message);
                }

            } else {
                LogHzMemberInfoParser logHzMemberInfoParser = LogHzMemberInfoParser.getInstance();

                HazelcastMemberInfo hzMemberInfo = logHzMemberInfoParser.getHazelcastMemberInfo(message);

                if (hzMemberInfo != null) {
                    hazelcastMembership.addHzMemberInfo(hzMemberInfo);
                } else {
                    LOG.error("Unable to parse Hz Member Info: " + message);
                }
            }
        }
    }

    protected byte getLogLevelId(String level) {

        byte levelId = 0;

        if (level.equalsIgnoreCase("TRACE")) {
            levelId = 1;
        } else if (level.equalsIgnoreCase("DEBUG")) {
            levelId = 2;
        } else if (level.equalsIgnoreCase("INFO")) {
            levelId = 3;
        } else if (level.equalsIgnoreCase("WARN")) {
            levelId = 4;
        } else if (level.equalsIgnoreCase("ALERT")) {
            levelId = 5;
        } else if (level.equalsIgnoreCase("ERROR")) {
            levelId = 6;
        } else if (level.equalsIgnoreCase("FATAL")) {
            levelId = 7;
        }

        return levelId;

    }

    private void parseRequestorLockString(String requestorLockStr,
            Log4jLogRequestorLockEntry log4jLogRequestorLockEntry) {

        Matcher lineMatcher = requestorLockPattern.matcher(requestorLockStr);

        if (lineMatcher.matches()) {

            int count = lineMatcher.groupCount();

            if (count >= 6) {

                String requestorId;
                Integer timeInterval = null;
                String thisThreadName;
                String originalLockThreadName;
                String finallyLockThreadName;

                requestorId = lineMatcher.group(2);
                String timeIntervalStr = lineMatcher.group(3);

                try {
                    timeInterval = Integer.parseInt(timeIntervalStr);
                } catch (Exception e) {
                    LOG.error("Error parsing time interval: " + timeIntervalStr, e);
                }

                thisThreadName = lineMatcher.group(4);
                originalLockThreadName = lineMatcher.group(5);
                finallyLockThreadName = lineMatcher.group(6);

                log4jLogRequestorLockEntry.setRequestorId(requestorId);
                log4jLogRequestorLockEntry.setTimeInterval(timeInterval);
                log4jLogRequestorLockEntry.setThisThreadName(thisThreadName);
                log4jLogRequestorLockEntry.setOriginalLockThreadName(originalLockThreadName);
                log4jLogRequestorLockEntry.setFinallyLockThreadName(finallyLockThreadName);
            }

        }
    }

    public static void main(String[] args) {

        List<String> logLineList = new ArrayList<>();

        // @formatter:off
        // CHECKSTYLE:OFF
        logLineList.add("2017-05-04 22:39:49,345 [orkmanager_PRPC : 12] [  STANDARD] [     PegaRULES:07.10] (al.authorization.Authorization) DEBUG File.RWAOperatorListener|process|A3E07D4B2E67913EABBDC14E695AA7EC6|1493930054694000|RWAOperatorsPackage/RBG-Int-RWAOperators-/ProcessRWAOperators batchprocess@rabobank.com - pxReqServletName = /xvVsgsdbXM6xHWMgWNTRXVYCwM--oh4KDYhMzQmFsNBszM62iOq0-w%5B%5B*/!STANDARD");
        logLineList.add("2017-05-04 22:39:49,350 [orkmanager_PRPC : 12] [  STANDARD] [     PegaRULES:07.10] (.ManagedApplicationContextImpl) INFO  File.RWAOperatorListener|process|A3E07D4B2E67913EABBDC14E695AA7EC6|1493930054694000|RWAOperatorsPackage/RBG-Int-RWAOperators-/ProcessRWAOperators batchprocess@rabobank.com - Invalid configuration: application context defined by access Group(RBGRaboCollectionsImpl:Administrators) and Application:Version(RBGRaboCollectionsCurrent:01.01.01) defines one or more ruleset that override ruleset(s) in ancestor PegaRULES:[Pega-ProcessCommander:07-10, Pega-DeploymentDefaults:07-10, Pega-DecisionArchitect:07-10, Pega-LP-Mobile:07-10, Pega-LP-ProcessAndRules:07-10, Pega-LP-Integration:07-10, Pega-LP-Reports:07-10, Pega-LP-SystemSettings:07-10, Pega-LP-UserInterface:07-10, Pega-LP-OrgAndSecurity:07-10, Pega-LP-DataModel:07-10, Pega-LP-Application:07-10, Pega-LP:07-10, Pega-UpdateManager:07-10, Pega-SecurityVA:07-10, Pega-Feedback:07-10, Pega-AutoTest:07-10, Pega-AppDefinition:07-10, Pega-ImportExport:07-10, Pega-LocalizationTools:07-10, Pega-RuleRefactoring:07-10, Pega-ProcessArchitect:07-10, Pega-Portlet:07-10, Pega-Content:07-10, Pega-BigData:07-10, Pega-NLP:07-10, Pega-DecisionEngine:07-10, Pega-IntegrationArchitect:07-10, Pega-SystemArchitect:07-10, Pega-Desktop:07-10, Pega-EndUserUI:07-10, Pega-Social:07-10, Pega-API:07-10, Pega-EventProcessing:07-10, Pega-Reporting:07-10, Pega-UIDesign:07-10, Pega-Gadgets:07-10, Pega-UIEngine:07-10, Pega-ProcessEngine:07-10, Pega-SearchEngine:07-10, Pega-IntegrationEngine:07-10, Pega-RulesEngine:07-10, Pega-Engine:07-10, Pega-ProCom:07-10, Pega-IntSvcs:07-10, Pega-WB:07-10, Pega-RULES:07-10]\"; RuleSetList=[PrivateSale_nl:01-19, ServiceCustomer_nl:03-19, WorkInstructions_nl:01-16, RaboCollectionsFW_nl:03-21, RBGBusinessViews_nl:03-18, RBGSendLetter_Refactor_nl:03-02, RBGInt_nl:03-21, RBG_nl:03-21, Pega-ProcessCommander_nl:07-10, Pega-LP-ProcessAndRules_nl:07-10, Pega-LP-SystemSettings_nl:07-10, Pega-LP-UserInterface_nl:07-10, Pega-LP-OrgAndSecurity_nl:07-10, Pega-LP-DataModel_nl:07-10, Pega-LP-Application_nl:07-10, Pega-LP_nl:07-10, Pega-UpdateManager_nl:07-10, Pega-Feedback_nl:07-10, Pega-AutoTest_nl:07-10, Pega-AppDefinition_nl:07-10, Pega-ImportExport_nl:07-10, Pega-LocalizationTools_nl:07-10, Pega-RuleRefactoring_nl:07-10, Pega-ProcessArchitect_nl:07-10, Pega-IntegrationArchitect_nl:07-10, Pega-SystemArchitect_nl:07-10, Pega-Desktop_nl:07-10, Pega-EndUserUI_nl:07-10, Pega-Reporting_nl:07-10, Pega-UIDesign_nl:07-10, Pega-Gadgets_nl:07-10, Pega-ProcessEngine_nl:07-10, Pega-SearchEngine_nl:07-10, Pega-IntegrationEngine_nl:07-10, Pega-RulesEngine_nl:07-10, Pega-Engine_nl:07-10, Pega-ProCom_nl:07-10, Pega-IntSvcs_nl:07-10, Pega-WB_nl:07-10, Pega-RULES_nl:07-10, RBGRaboCollectionsCurrent:03-21, RBGRaboImpl:03-21, RBGMigrationScripts:03-02, Reminder:01-18, PrivateSale:01-19, Hospital:01-18, FallBackDossier:01-17, PaymentArrangement:01-19, ServiceCustomer:03-19, WorkInstructions:01-16, RaboCollectionsFW:03-21, RBGBusinessViews:03-18, RBGBusServPopulateCommObject:03-01, RBGBusServPreviewLetters:03-01, RBGSendLetter_Refactor:03-02, RBGIntPrintNet:03-19, SAMportal:03-18, BulkTransfer:03-18, RBGBusinessServices:03-21, RBGBusServGetFacilities:03-21, RBGBusServArchiveDocumentEKD:03-01, RBGBusServGetRelations:03-01, RBGBusServGetRelationsV2:03-01, RBGBusServGetDocumentEKD:03-01, RBGBusServGetEmployeeDetails:03-01, RBGBusServGetProductArrangementList:03-01, RBGBusServGetCollateralObject:03-16, RBGBusServGetCollateralArrangement:03-18, RBGBusServGetRelation:03-01, RBGBusServSendNotification:03-01, RBGBusServSendDefaultSignal:03-21, RBGBusServGetProductArrangement:03-17, RBGInt:03-21, RBGIntPrintNetFW:03-01, RBGRelationArrangementViewCRMI20Int:03-01, RBGArchiveDocumentInt:03-01, RBGGetRelationDocumentInt:03-02, RBGGetRelationCRMI19Int:03-02, RBGSearchCustomerCRMI18Int:03-01, RBGIntCPS69RaadplegenInstelling:03-18, RBGIntCPS31RaadplegenRegister:03-01, RBGIntCPS63SelecterenVerpanding:03-01, RBGIntCPS66SetBBInd:03-01, RBGIntCPS64SelecterenBorgtocht:03-01, RBGIntCPS62SelecterenHypotheek:03-01, RBGIntCPSArrears:03-18, RBGIntCPS32GetLoanDetails:03-02, RBGIntGloba:03-01, RBGGetDossierForce7Int:03-01, RBGGetFinancialDossierSAP6Int:03-02, RBGIntSAPArrears:03-02, RBGIntSAPLetters:03-01, RBGRWAOperators:03-20, RBGRWACaseBasedSecurity:03-02, RBGRWASSOInt:03-01, RBGGetSavingDepositDetailsAZS21Int:03-01, RBGIntCRMi59GetIDFromCrabNumber:03-01, RBGIntCRMi61GetArrangement:03-01, RBGIntCRMi67UpdateServiceRequest:03-01, RBGIntCRMi29CreateServiceRequest:03-01, RBGIntEKD28GetRelationDocument:03-01, RBGIntEKD42ArchiveDocument:03-01, RBGIntGetFacilities:03-21, RBGIntBatchStatus:03-02, RBGIntArrearsBatch:03-01, RBGIntCache:03-02, RBGIntBatch:03-01, RBGIntRWAFW:03-01, RBGIntGlobaFW:01-01, RBGIntEKDFW:03-01, RBGIntCRMiFW:03-01, RBGIntCPSFW:03-02, RBGIntFLFFW:03-21, RBGIntBatchFW:03-02, RBGIntAZSFW:01-01, RBGIntFW:03-03, InnovationCobol:03-01, RBGIntDSPFW:03-21, RBGIntDSP:03-21, RBGRabo:03-21, RBG:03-21, RBGArchivingUniqueId:03-19, RBGUtilities:03-17, DynamicClassReferencing:03-01, CommonFunctionLibrary:03-02, RBGDataModel:03-21, UI-Kit-7:03-01, SamSenses:01-18, PegaCPMFS:07-14, PegaCPMFSInt:07-14, PegaFSSCM:07-14, PegaNPVCalc:07-14, PegaLoanPmtCalc:07-14, Pega-DecisionArchitect:07-10, Pega-DecisionEngine:07-10, CPM:07-14, CPM-Social:07-14, CPM-Reports:07-14, PegaAppCA:07-14, PegaFW-Social:07-14, PegaKM:07-14, KMReports:07-14, PegaFW-NewsFeed:07-14, Pega-LP_CPM:07-14, PegaApp:07-14, PegaFW-Chat:07-14, Pega-Chat:07-14, Pega-CTI:07-13, Pega-ChannelServices:07-13, Pega-UITheme:07-14, PegaFSRequirements:07-15, PegaFS:07-15, PegaFSInt:07-15, PegaAccounting:07-15, PegaAccounting-Classes:07-15, PegaEQ:07-15, PegaEQInt:07-15, Pega-IAC:07-10, PegaRequirements:07-15, CMISPlus:07-15, PegaFSUI:07-15, PegaFWUI:07-13, PegaSCM:07-14, PegaSCMInt:07-14, Pega-ProcessCommander:07-10, Pega-DeploymentDefaults:07-10, Pega-LP-Mobile:07-10, Pega-LP-ProcessAndRules:07-10, Pega-LP-Integration:07-10, Pega-LP-Reports:07-10, Pega-LP-SystemSettings:07-10, Pega-LP-UserInterface:07-10, Pega-LP-OrgAndSecurity:07-10, Pega-LP-DataModel:07-10, Pega-LP-Application:07-10, Pega-LP:07-10, Pega-UpdateManager:07-10, Pega-SecurityVA:07-10, Pega-Feedback:07-10, Pega-AutoTest:07-10, Pega-AppDefinition:07-10, Pega-ImportExport:07-10, Pega-LocalizationTools:07-10, Pega-RuleRefactoring:07-10, Pega-ProcessArchitect:07-10, Pega-Portlet:07-10, Pega-Content:07-10, Pega-BigData:07-10, Pega-NLP:07-10, Pega-IntegrationArchitect:07-10, Pega-SystemArchitect:07-10, Pega-Desktop:07-10, Pega-EndUserUI:07-10, Pega-Social:07-10, Pega-API:07-10, Pega-EventProcessing:07-10, Pega-Reporting:07-10, Pega-UIDesign:07-10, Pega-Gadgets:07-10, Pega-UIEngine:07-10, Pega-ProcessEngine:07-10, Pega-SearchEngine:07-10, Pega-IntegrationEngine:07-10, Pega-RulesEngine:07-10, Pega-Engine:07-10, Pega-ProCom:07-10, Pega-IntSvcs:07-10, Pega-WB:07-10, Pega-RULES:07-10]");
        logLineList.add("2017-04-26 10:58:07,489 [    EMAIL-Thread-266] [  STANDARD] [         MWDSS:03.01] (yMails.MW_FW_UAPFW_Work.Action) INFO  EMAIL.DSSCSRListener.Listener|from(haripriyas@incessanttechnologies.com)|sub(New triage)|Email|DSSEmailDefault|MW-SD-DSS-WORK-Application|ProcessDSSEnquiryMails|A361D4FF08DDD162D9C290282C9629AC3 DSSBatchProcess -  WorkObject hit the devconTime:20170426T105807.489 GMT");
        logLineList.add("2017-05-04 21:56:11,103 [workmanager_PRPC : 2] [  STANDARD] [     PegaRULES:07.10] (.ManagedApplicationContextImpl) INFO    - Invalid configuration: application context defined by access Group(RBGRaboCollectionsImpl:Administrators) and Application:Version(RBGRaboCollectionsCurrent:01.01.01) defines one or more ruleset that override ruleset(s) in ancestor PegaRULES:[Pega-ProcessCommander:07-10, Pega-DeploymentDefaults:07-10, Pega-DecisionArchitect:07-10, Pega-LP-Mobile:07-10, Pega-LP-ProcessAndRules:07-10, Pega-LP-Integration:07-10, Pega-LP-Reports:07-10, Pega-LP-SystemSettings:07-10, Pega-LP-UserInterface:07-10, Pega-LP-OrgAndSecurity:07-10, Pega-LP-DataModel:07-10, Pega-LP-Application:07-10, Pega-LP:07-10, Pega-UpdateManager:07-10, Pega-SecurityVA:07-10, Pega-Feedback:07-10, Pega-AutoTest:07-10, Pega-AppDefinition:07-10, Pega-ImportExport:07-10, Pega-LocalizationTools:07-10, Pega-RuleRefactoring:07-10, Pega-ProcessArchitect:07-10, Pega-Portlet:07-10, Pega-Content:07-10, Pega-BigData:07-10, Pega-NLP:07-10, Pega-DecisionEngine:07-10, Pega-IntegrationArchitect:07-10, Pega-SystemArchitect:07-10, Pega-Desktop:07-10, Pega-EndUserUI:07-10, Pega-Social:07-10, Pega-API:07-10, Pega-EventProcessing:07-10, Pega-Reporting:07-10, Pega-UIDesign:07-10, Pega-Gadgets:07-10, Pega-UIEngine:07-10, Pega-ProcessEngine:07-10, Pega-SearchEngine:07-10, Pega-IntegrationEngine:07-10, Pega-RulesEngine:07-10, Pega-Engine:07-10, Pega-ProCom:07-10, Pega-IntSvcs:07-10, Pega-WB:07-10, Pega-RULES:07-10]\"; RuleSetList=[PrivateSale_nl:01-19, ServiceCustomer_nl:03-19, WorkInstructions_nl:01-16, RaboCollectionsFW_nl:03-21, RBGBusinessViews_nl:03-18, RBGSendLetter_Refactor_nl:03-02, RBGInt_nl:03-21, RBG_nl:03-21, Pega-ProcessCommander_nl:07-10, Pega-LP-ProcessAndRules_nl:07-10, Pega-LP-SystemSettings_nl:07-10, Pega-LP-UserInterface_nl:07-10, Pega-LP-OrgAndSecurity_nl:07-10, Pega-LP-DataModel_nl:07-10, Pega-LP-Application_nl:07-10, Pega-LP_nl:07-10, Pega-UpdateManager_nl:07-10, Pega-Feedback_nl:07-10, Pega-AutoTest_nl:07-10, Pega-AppDefinition_nl:07-10, Pega-ImportExport_nl:07-10, Pega-LocalizationTools_nl:07-10, Pega-RuleRefactoring_nl:07-10, Pega-ProcessArchitect_nl:07-10, Pega-IntegrationArchitect_nl:07-10, Pega-SystemArchitect_nl:07-10, Pega-Desktop_nl:07-10, Pega-EndUserUI_nl:07-10, Pega-Reporting_nl:07-10, Pega-UIDesign_nl:07-10, Pega-Gadgets_nl:07-10, Pega-ProcessEngine_nl:07-10, Pega-SearchEngine_nl:07-10, Pega-IntegrationEngine_nl:07-10, Pega-RulesEngine_nl:07-10, Pega-Engine_nl:07-10, Pega-ProCom_nl:07-10, Pega-IntSvcs_nl:07-10, Pega-WB_nl:07-10, Pega-RULES_nl:07-10, Hospital_Branch_Jan-Jaap:, RBGRaboCollectionsCurrent:03-21, RBGRaboImpl:03-21, RBGMigrationScripts:03-02, Reminder:01-18, PrivateSale:01-19, Hospital:01-18, FallBackDossier:01-17, PaymentArrangement:01-19, ServiceCustomer:03-19, WorkInstructions:01-16, RaboCollectionsFW:03-21, RBGBusinessViews:03-18, RBGBusServPopulateCommObject:03-01, RBGBusServPreviewLetters:03-01, RBGSendLetter_Refactor:03-02, RBGIntPrintNet:03-19, SAMportal:03-18, BulkTransfer:03-18, RBGBusinessServices:03-21, RBGBusServGetFacilities:03-21, RBGBusServArchiveDocumentEKD:03-01, RBGBusServGetRelations:03-01, RBGBusServGetRelationsV2:03-01, RBGBusServGetDocumentEKD:03-01, RBGBusServGetEmployeeDetails:03-01, RBGBusServGetProductArrangementList:03-01, RBGBusServGetCollateralObject:03-16, RBGBusServGetCollateralArrangement:03-18, RBGBusServGetRelation:03-01, RBGBusServSendNotification:03-01, RBGBusServSendDefaultSignal:03-21, RBGBusServGetProductArrangement:03-17, RBGInt:03-21, RBGIntPrintNetFW:03-01, RBGRelationArrangementViewCRMI20Int:03-01, RBGArchiveDocumentInt:03-01, RBGGetRelationDocumentInt:03-02, RBGGetRelationCRMI19Int:03-02, RBGSearchCustomerCRMI18Int:03-01, RBGIntCPS69RaadplegenInstelling:03-18, RBGIntCPS31RaadplegenRegister:03-01, RBGIntCPS63SelecterenVerpanding:03-01, RBGIntCPS66SetBBInd:03-01, RBGIntCPS64SelecterenBorgtocht:03-01, RBGIntCPS62SelecterenHypotheek:03-01, RBGIntCPSArrears:03-18, RBGIntCPS32GetLoanDetails:03-02, RBGIntGloba:03-01, RBGGetDossierForce7Int:03-01, RBGGetFinancialDossierSAP6Int:03-02, RBGIntSAPArrears:03-02, RBGIntSAPLetters:03-01, RBGRWAOperators:03-20, RBGRWACaseBasedSecurity:03-02, RBGRWASSOInt:03-01, RBGGetSavingDepositDetailsAZS21Int:03-01, RBGIntCRMi59GetIDFromCrabNumber:03-01, RBGIntCRMi61GetArrangement:03-01, RBGIntCRMi67UpdateServiceRequest:03-01, RBGIntCRMi29CreateServiceRequest:03-01, RBGIntEKD28GetRelationDocument:03-01, RBGIntEKD42ArchiveDocument:03-01, RBGIntGetFacilities:03-21, RBGIntBatchStatus:03-02, RBGIntArrearsBatch:03-01, RBGIntCache:03-02, RBGIntBatch:03-01, RBGIntRWAFW:03-01, RBGIntGlobaFW:01-01, RBGIntEKDFW:03-01, RBGIntCRMiFW:03-01, RBGIntCPSFW:03-02, RBGIntFLFFW:03-21, RBGIntBatchFW:03-02, RBGIntAZSFW:01-01, RBGIntFW:03-03, InnovationCobol:03-01, RBGIntDSPFW:03-21, RBGIntDSP:03-21, RBGRabo:03-21, RBG:03-21, RBGArchivingUniqueId:03-19, RBGUtilities:03-17, DynamicClassReferencing:03-01, CommonFunctionLibrary:03-02, RBGDataModel:03-21, UI-Kit-7:03-01, SamSenses:01-18, PegaCPMFS:07-14, PegaCPMFSInt:07-14, PegaFSSCM:07-14, PegaNPVCalc:07-14, PegaLoanPmtCalc:07-14, CPM:07-14, CPM-Social:07-14, CPM-Reports:07-14, PegaAppCA:07-14, PegaFW-Social:07-14, PegaKM:07-14, KMReports:07-14, PegaFW-NewsFeed:07-14, PegaApp:07-14, PegaFW-Chat:07-14, PegaFSRequirements:07-15, PegaFS:07-15, PegaFSInt:07-15, PegaAccounting:07-15, PegaAccounting-Classes:07-15, PegaEQ:07-15, PegaEQInt:07-15, PegaRequirements:07-15, CMISPlus:07-15, PegaFSUI:07-15, PegaFWUI:07-13, PegaSCM:07-14, PegaSCMInt:07-14, Pega-DecisionArchitect:07-10, Pega-DecisionEngine:07-10, Pega-LP_CPM:07-14, Pega-Chat:07-14, Pega-CTI:07-13, Pega-ChannelServices:07-13, Pega-UITheme:07-14, Pega-IAC:07-10, Pega-ProcessCommander:07-10, Pega-DeploymentDefaults:07-10, Pega-LP-Mobile:07-10, Pega-LP-ProcessAndRules:07-10, Pega-LP-Integration:07-10, Pega-LP-Reports:07-10, Pega-LP-SystemSettings:07-10, Pega-LP-UserInterface:07-10, Pega-LP-OrgAndSecurity:07-10, Pega-LP-DataModel:07-10, Pega-LP-Application:07-10, Pega-LP:07-10, Pega-UpdateManager:07-10, Pega-SecurityVA:07-10, Pega-Feedback:07-10, Pega-AutoTest:07-10, Pega-AppDefinition:07-10, Pega-ImportExport:07-10, Pega-LocalizationTools:07-10, Pega-RuleRefactoring:07-10, Pega-ProcessArchitect:07-10, Pega-Portlet:07-10, Pega-Content:07-10, Pega-BigData:07-10, Pega-NLP:07-10, Pega-IntegrationArchitect:07-10, Pega-SystemArchitect:07-10, Pega-Desktop:07-10, Pega-EndUserUI:07-10, Pega-Social:07-10, Pega-API:07-10, Pega-EventProcessing:07-10, Pega-Reporting:07-10, Pega-UIDesign:07-10, Pega-Gadgets:07-10, Pega-UIEngine:07-10, Pega-ProcessEngine:07-10, Pega-SearchEngine:07-10, Pega-IntegrationEngine:07-10, Pega-RulesEngine:07-10, Pega-Engine:07-10, Pega-ProCom:07-10, Pega-IntSvcs:07-10, Pega-WB:07-10, Pega-RULES:07-10]");
        // String regex = "(\\S+-\\S+-\\S+
        // \\S+:\\S+:\\S+,\\S+)\\s\\[(.*?)\\]\\s\\[(.*?)\\]\\s\\[(.*?)\\]\\s\\((\\s*?\\S*?\\s*?)\\)\\s(.{5,5})\\s(.*?)\\s(.*?)\\s\\-\\s(.*)";
        String regex = "(\\S+-\\S+-\\S+ \\S+:\\S+:\\S+,\\S+)\\s\\[(.*?)\\]\\s\\[(.*)\\]\\s\\[(.*)\\]\\s\\((\\s*?\\S*?\\s*?)\\)\\s(.{5,5})\\s(.*)\\s(.*)\\s\\-\\s(.*)";
        // String regex = "(\\S+-\\S+-\\S+
        // \\S+:\\S+:\\S+,\\S+)\\s\\[(.*?)\\]\\s\\[(.*?)\\]\\s\\[(.*?)\\]\\s\\((\\s*?\\S*?\\s*?)\\)\\s(\\s*?\\S*?\\s*?)\\s(.*?)\\s(.*?)\\s\\-\\s(.*)";
        // CHECKSTYLE:ON
        // @formatter:on

        Pattern pattern = Pattern.compile(regex);

        for (String logLine : logLineList) {
            Matcher matcher = pattern.matcher(logLine);
            if (matcher.matches()) {
                System.out.println("Match");
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    System.out.println("Group " + i + ". " + matcher.group(i));
                }
            } else {
                System.out.println("No Match");
            }
        }
    }
}
