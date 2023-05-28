
package com.pega.gcs.logviewer.parser;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.pega.gcs.logviewer.logfile.Log4jPattern;

public class ClusterLog4jPatternParser extends Log4jPatternParser {

    // 2023-05-11 06:29:30,274 (.DataFlowDiagnosticsFileLogger) INFO -
    private static final String V2_STR_FORMAT = "%s [%s] (%s) %s - %s";

    public ClusterLog4jPatternParser(Log4jPattern log4jPattern, Charset charset, Locale locale,
            TimeZone displayTimezone) {

        super(log4jPattern, charset, locale, displayTimezone);

    }

    @Override
    protected void processCloudKLogMap(StringBuilder logEntryTextSB, Map<String, Object> logMap) {

        String time = (String) logMap.get("@timestamp");
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
        addLogEntryColumnValue(message);

        logEntryTextSB.append(String.format(V2_STR_FORMAT, time, thread, logger, logLevel, message));

        processException(logEntryTextSB, exceptionMap);

    }

}
