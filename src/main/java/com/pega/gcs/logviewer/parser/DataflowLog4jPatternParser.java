
package com.pega.gcs.logviewer.parser;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.LifeCycleEventMessageParser;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.message.LifeCycleEventMessage;
import com.pega.gcs.logviewer.logfile.Log4jPattern;
import com.pega.gcs.logviewer.model.Log4jLogEntry;
import com.pega.gcs.logviewer.model.Log4jLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;

public class DataflowLog4jPatternParser extends Log4jPatternParser {

    private static final Log4j2Helper LOG = new Log4j2Helper(DataflowLog4jPatternParser.class);

    private List<LogEntryColumn> dataflowLogEventColumnList;

    private LifeCycleEventMessageParser lifeCycleEventMessageParser;

    public DataflowLog4jPatternParser(Log4jPattern log4jPattern, Charset charset, Locale locale,
            TimeZone displayTimezone) {

        super(log4jPattern, charset, locale, displayTimezone);

        dataflowLogEventColumnList = LogEntryColumn.getDataflowLogEventColumnList();

        // Add Dataflow LFE Columns
        for (LogEntryColumn lfeCol : dataflowLogEventColumnList) {
            addColumn(lfeCol);
        }

        // reset model columns with LFE columns
        List<LogEntryColumn> logEntryColumnList = getLogEntryColumnList();
        Log4jLogEntryModel log4jLogEntryModel = getLogEntryModel();

        log4jLogEntryModel.updateLogEntryColumnList(logEntryColumnList);

        lifeCycleEventMessageParser = new LifeCycleEventMessageParser();

    }

    /**
     * @return the dataflowLogEventColumnList
     */
    private List<LogEntryColumn> getDataflowLogEventColumnList() {
        return dataflowLogEventColumnList;
    }

    private LifeCycleEventMessageParser getLifeCycleEventMessageParser() {
        return lifeCycleEventMessageParser;
    }

    @Override
    protected void buildLogEntry() {

        Log4jLogEntryModel log4jLogEntryModel = getLogEntryModel();
        AtomicInteger logEntryIndex = getLogEntryIndex();
        ArrayList<String> logEntryColumnValueList = getLogEntryColumnValueList();
        String logEntryText = getLogEntryText();

        if (logEntryColumnValueList.size() > 0) {

            logEntryIndex.incrementAndGet();

            int levelIndex = getLevelIndex();
            int timestampIndex = getTimestampIndex();
            int messageIndex = getMessageIndex();

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

            String message = logEntryColumnValueList.get(messageIndex);

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
                    columnValue = lifeCycleEventMessage.getColumnValueForLifeCycleEventColumn(lfeCol);
                }

                logEntryColumnValueList.add(columnValue);
            }

            // add all last otherwise messes up the indexes
            logEntryColumnValueList.add(0, String.valueOf(logEntryIndex));

            Log4jLogEntry log4jLogEntry = new Log4jLogEntry(logEntryKey, logEntryColumnValueList, logEntryText, false,
                    logLevelId);

            log4jLogEntryModel.addLogEntry(log4jLogEntry, logEntryColumnValueList, getCharset(), getLocale());

            // update the processed counter
            incrementAndGetProcessedCount();

            clearBuildLogEntryData();
        }
    }

}
