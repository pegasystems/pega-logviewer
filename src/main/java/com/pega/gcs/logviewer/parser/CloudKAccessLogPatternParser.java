
package com.pega.gcs.logviewer.parser;

import java.nio.charset.Charset;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.logfile.Log4jPattern;

public class CloudKAccessLogPatternParser extends CloudKLogPatternParser {

    private static final Log4j2Helper LOG = new Log4j2Helper(CloudKAccessLogPatternParser.class);

    public CloudKAccessLogPatternParser(Log4jPattern log4jPattern, Charset charset, Locale locale,
            ZoneId displayZoneId) {

        super(log4jPattern, charset, locale, displayZoneId);

    }

    @Override
    protected void processCloudKLogMap(String time, StringBuilder logEntryTextSB, Map<String, Object> fieldMap) {

        String logMessage = null;

        Map<String, Object> logObject = (Map<String, Object>) fieldMap.get("log");

        logMessage = logObject.get("log").toString();

        logEntryTextSB.append(String.format(LOG_STR_FORMAT, time, logMessage));

        Pattern linePattern = getLinePattern();

        Matcher lineMatcher = linePattern.matcher(logEntryTextSB.toString());

        if (lineMatcher.matches()) {

            MatchResult result = lineMatcher.toMatchResult();

            int groupCount = result.groupCount();

            for (int i = 1; i <= groupCount; i++) {

                StringBuilder valueSB = new StringBuilder(result.group(i));

                addLogEntryColumnValue(valueSB.toString());
            }

        } else {

            LOG.error("Error parsing log entry " + logEntryTextSB);

        }

    }

}
