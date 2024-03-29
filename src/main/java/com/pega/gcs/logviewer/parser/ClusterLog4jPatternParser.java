
package com.pega.gcs.logviewer.parser;

import java.nio.charset.Charset;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import com.pega.gcs.logviewer.logfile.Log4jPattern;

public class ClusterLog4jPatternParser extends Log4jPatternParser {

    // 2023-05-11 06:29:30,274 (.DataFlowDiagnosticsFileLogger) INFO -
    private static final String V2_STR_FORMAT = "%s [%s] (%s) %s - ";

    public ClusterLog4jPatternParser(Log4jPattern log4jPattern, Charset charset, Locale locale, ZoneId displayZoneId) {

        super(log4jPattern, charset, locale, displayZoneId);

    }

    @Override
    protected void processCloudKLogMap(String time, StringBuilder logEntryTextSB, Map<String, Object> logMap) {

        // in older version, time is also present in the parent structure.
        // String time = (String) logMap.get("@timestamp");
        String thread = (String) logMap.get("thread_name");
        String logger = (String) logMap.get("logger_name");
        String logLevel = (String) logMap.get("level");
        String message = (String) logMap.get("message");
        @SuppressWarnings("unchecked")
        Map<String, String> exceptionMap = (Map<String, String>) logMap.get("exception");

        // sometimes not all fields are present
        thread = thread != null ? thread : "";
        logger = logger != null ? logger : "";
        logLevel = logLevel != null ? logLevel : "";
        message = message != null ? message : "";

        // (TIMESTAMP);
        // (THREAD);
        // (LOGGER);
        // (LEVEL);
        // (MESSAGE);

        addLogEntryColumnValue(time);
        addLogEntryColumnValue(thread);
        addLogEntryColumnValue(logger);
        addLogEntryColumnValue(logLevel);

        logEntryTextSB.append(String.format(V2_STR_FORMAT, time, thread, logger, logLevel));

        String messageFirstLine = processMessage(message);

        addLogEntryColumnValue(messageFirstLine);

        logEntryTextSB.append(messageFirstLine);

        processException(exceptionMap);

    }

}
