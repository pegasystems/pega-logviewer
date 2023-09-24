
package com.pega.gcs.logviewer.parser;

import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.LogViewerUtil;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.LifeCycleEventMessageParser;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.message.LifeCycleEventMessage;
import com.pega.gcs.logviewer.logfile.Log4jPattern;
import com.pega.gcs.logviewer.model.Log4jLogEntry;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogEntryModel;

public class DataflowLog4jPatternParser extends Log4jPatternParser {

    private static final Log4j2Helper LOG = new Log4j2Helper(DataflowLog4jPatternParser.class);

    // 2023-05-11 06:29:30,274 (.DataFlowDiagnosticsFileLogger) INFO -
    private static final String V2_STR_FORMAT = "%s [%s] [%s] (%s) %s %s - %s";

    // additional DF specific columns
    private List<LogEntryColumn> dataflowLogEventColumnList;

    private LifeCycleEventMessageParser lifeCycleEventMessageParser;

    public DataflowLog4jPatternParser(Log4jPattern log4jPattern, Charset charset, Locale locale, ZoneId displayZoneId) {

        super(log4jPattern, charset, locale, displayZoneId);

        // log4j related columns
        LogEntryModel logEntryModel = getLogEntryModel();
        List<LogEntryColumn> logEntryColumnList = logEntryModel.getLogEntryColumnList();

        dataflowLogEventColumnList = LogEntryColumn.getDataflowLogEventColumnList();

        List<LogEntryColumn> dfLogEntryColumnList = new ArrayList<>();

        dfLogEntryColumnList.addAll(logEntryColumnList);
        dfLogEntryColumnList.addAll(dataflowLogEventColumnList);

        // reset model columns with LFE columns
        logEntryModel.updateLogEntryColumnList(dfLogEntryColumnList);

        lifeCycleEventMessageParser = new LifeCycleEventMessageParser();

    }

    private List<LogEntryColumn> getDataflowLogEventColumnList() {
        return dataflowLogEventColumnList;
    }

    private LifeCycleEventMessageParser getLifeCycleEventMessageParser() {
        return lifeCycleEventMessageParser;
    }

    @Override
    protected void processCloudKLogMap(String time, StringBuilder logEntryTextSB, Map<String, Object> fieldMap) {

        // in older version, time is also present in the parent structure.
        // String time = (String) logMap.get("@timestamp");
        String thread = (String) fieldMap.get("thread_name");
        String pegaThread = (String) fieldMap.get("pegathread");
        String logger = (String) fieldMap.get("logger_name");
        String logLevel = (String) fieldMap.get("level");
        String correlationId = (String) fieldMap.get("CorrelationId");
        String message = (String) fieldMap.get("message");

        // (TIMESTAMP);
        // (THREAD);
        // (PEGATHREAD);
        // (LOGGER);
        // (LEVEL);
        // (CORRELATIONID);
        // (MESSAGE);

        addLogEntryColumnValue(time);
        addLogEntryColumnValue(thread);
        addLogEntryColumnValue(pegaThread);
        addLogEntryColumnValue(logger);
        addLogEntryColumnValue(logLevel);
        addLogEntryColumnValue(correlationId);
        addLogEntryColumnValue(message);

        logEntryTextSB.append(
                String.format(V2_STR_FORMAT, time, thread, pegaThread, logger, logLevel, correlationId, message));
    }

    @Override
    protected void buildLogEntry() {

        LogEntryModel logEntryModel = getLogEntryModel();
        AtomicInteger logEntryIndex = getLogEntryIndex();

        ArrayList<String> logEntryColumnValueList = getLogEntryColumnValueList();
        String logEntryText = getLogEntryText();

        if (logEntryColumnValueList.size() > 0) {

            logEntryIndex.incrementAndGet();

            addLogEntryColumnValue(0, String.valueOf(logEntryIndex));

            int levelIndex = getLevelIndex();
            int timestampIndex = getTimestampIndex();
            int messageIndex = getMessageIndex();

            String timestampStr = logEntryColumnValueList.get(timestampIndex);

            DateTimeFormatter modelDateTimeFormatter = logEntryModel.getModelDateTimeFormatter();
            ZoneId modelZoneId = logEntryModel.getModelZoneId();

            long logEntryTime = -1;
            try {

                logEntryTime = LogViewerUtil.getTimeMillis(timestampStr, modelDateTimeFormatter, modelZoneId);

            } catch (Exception e) {
                LOG.error("Error parsing line: [" + logEntryIndex + "] logentry: [" + logEntryText + "]", e);
            }

            LogEntryKey logEntryKey = new LogEntryKey(logEntryIndex.intValue(), logEntryTime);

            String level = logEntryColumnValueList.get(levelIndex);
            String message = logEntryColumnValueList.get(messageIndex);

            byte logLevelId = getLogLevelId(level.trim());

            LifeCycleEventMessageParser lifeCycleEventMessageParser;
            lifeCycleEventMessageParser = getLifeCycleEventMessageParser();

            LifeCycleEventMessage lifeCycleEventMessage;
            lifeCycleEventMessage = lifeCycleEventMessageParser.getLifeCycleEventMessage(message);

            if (lifeCycleEventMessage == null) {
                LOG.error("LFE Parser error: " + message);
            }

            // append lfe column values
            List<LogEntryColumn> dataflowLogEventColumnList = getDataflowLogEventColumnList();

            for (LogEntryColumn lfeCol : dataflowLogEventColumnList) {
                String columnValue = null;

                if (lifeCycleEventMessage != null) {
                    columnValue = lifeCycleEventMessage.getColumnValueForLifeCycleEventColumn(lfeCol,
                            modelDateTimeFormatter, modelZoneId);
                }

                logEntryColumnValueList.add(columnValue);
            }

            Log4jLogEntry log4jLogEntry = new Log4jLogEntry(logEntryKey, logEntryColumnValueList, logEntryText, false,
                    logLevelId);

            logEntryModel.addLogEntry(log4jLogEntry, logEntryColumnValueList, getCharset(), getLocale());

            // update the processed counter
            incrementAndGetProcessedCount();

            clearBuildLogEntryData();
        }
    }

}
