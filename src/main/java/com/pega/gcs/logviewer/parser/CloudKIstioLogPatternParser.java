
package com.pega.gcs.logviewer.parser;

import java.nio.charset.Charset;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pega.gcs.logviewer.logfile.Log4jPattern;
import com.pega.gcs.logviewer.model.LogEntryModel;

public class CloudKIstioLogPatternParser extends CloudKLogPatternParser {

    private Pattern istioInfoPattern;

    public CloudKIstioLogPatternParser(Log4jPattern log4jPattern, Charset charset, Locale locale,
            ZoneId displayZoneId) {

        super(log4jPattern, charset, locale, displayZoneId);

        String istioInfoRegex = "(\\S+-\\S+-\\S+T\\S+:\\S+:\\S+\\.\\S+)(.*?)";

        istioInfoPattern = Pattern.compile(istioInfoRegex);

    }

    @Override
    protected void processCloudKLogMap(String time, StringBuilder logEntryTextSB, Map<String, Object> fieldMap) {

        String logMessage = null;

        Object logObject = fieldMap.get("log");
        logMessage = logObject.toString();

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

            LogEntryModel logEntryModel = getLogEntryModel();
            int size = logEntryModel.getLogEntryColumnList().size();

            Matcher istioInfoMatcher = istioInfoPattern.matcher(logMessage);

            String startTime = null;
            String remainingStr = null;

            if (istioInfoMatcher.matches()) {
                startTime = istioInfoMatcher.group(1);
                remainingStr = istioInfoMatcher.group(2);
                remainingStr = remainingStr.replaceAll("\\t", " ");
            }

            addLogEntryColumnValue(time);

            for (int i = 1; i < size; i++) {

                switch (i) {
                case 1:
                    if (startTime != null) {
                        addLogEntryColumnValue(startTime); // START_TIME
                    } else {
                        addLogEntryColumnValue(""); // START_TIME
                    }
                    break;

                case 2:
                    if (remainingStr != null) {
                        addLogEntryColumnValue(remainingStr); // REQPATH
                    } else {
                        addLogEntryColumnValue(logMessage); // REQPATH
                    }

                    break;

                default:
                    addLogEntryColumnValue("");
                    break;
                }
            }
        }
    }

}
