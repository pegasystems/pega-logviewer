
package com.pega.gcs.logviewer.parser;

import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.LogViewerUtil;
import com.pega.gcs.logviewer.logfile.Log4jPattern;
import com.pega.gcs.logviewer.model.Log4jLogEntry;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogEntryModel;

public class CloudKLogPatternParser extends Log4jPatternParser {

    private static final Log4j2Helper LOG = new Log4j2Helper(CloudKLogPatternParser.class);

    protected static final String LOG_STR_FORMAT = "%s %s";

    private int responseCodeIndex;

    public CloudKLogPatternParser(Log4jPattern log4jPattern, Charset charset, Locale locale, ZoneId displayZoneId) {

        super(log4jPattern, charset, locale, displayZoneId);

        responseCodeIndex = -1;

        LogEntryModel logEntryModel = getLogEntryModel();

        String cloudKTimestampPattern = "yyyy-MM-dd'T'HH:mm:ss[.SSSSSSSSS][.SSSSSSSS][.SSSSSSS][.SSSSSS][.SSSSS][.SSSS][.SSS]X";
        DateTimeFormatter modelDateTimeFormatter = DateTimeFormatter.ofPattern(cloudKTimestampPattern);

        ZoneId modelZoneId = ZoneOffset.UTC;

        setModelZoneId(modelZoneId);
        setModelDateTimeFormatter(modelDateTimeFormatter);

        logEntryModel.setModelZoneId(modelZoneId);
        logEntryModel.setModelDateTimeFormatter(modelDateTimeFormatter);

    }

    protected int getResponseCodeIndex() {

        if (responseCodeIndex == -1) {
            LogEntryModel logEntryModel = getLogEntryModel();
            responseCodeIndex = logEntryModel.getLogEntryColumnIndex(LogEntryColumn.RESPONSE_CODE);
        }

        return responseCodeIndex;
    }

    @Override
    public void parse(String line) {

        Map<String, Object> fieldMap = getCloudKFieldMap(line);

        if (fieldMap != null) {

            String time = (String) fieldMap.get("time");

            StringBuilder logEntryTextSB = new StringBuilder();

            processCloudKLogMap(time, logEntryTextSB, fieldMap);

            setLogEntryText(logEntryTextSB.toString());

            buildLogEntry();
        }
    }

    @Override
    protected void buildLogEntry() {

        LogEntryModel logEntryModel = getLogEntryModel();
        AtomicInteger logEntryIndex = getLogEntryIndex();

        ArrayList<String> logEntryColumnValueList = getLogEntryColumnValueList();
        String logEntryText = getLogEntryText();
        List<String> additionalLines = getAdditionalLines();

        if ((logEntryColumnValueList.size() == 0) && (additionalLines.size() > 0)) {

            LOG.info("found " + additionalLines.size() + " additional lines at the begining.");

            logEntryIndex.addAndGet(additionalLines.size());

            additionalLines.clear();

        } else if (logEntryColumnValueList.size() > 0) {

            logEntryIndex.incrementAndGet();

            addLogEntryColumnValue(0, String.valueOf(logEntryIndex));

            int timestampIndex = getTimestampIndex();
            int responseCodeIndex = getResponseCodeIndex();

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

            byte logLevelId = -1;

            try {
                String responseCodeStr = logEntryColumnValueList.get(responseCodeIndex);

                logLevelId = getLogLevelId(Integer.parseInt(responseCodeStr));
            } catch (Exception e) {
                LOG.debug("Error extracting level", e);
            }

            Log4jLogEntry log4jLogEntry = new Log4jLogEntry(logEntryKey, logEntryColumnValueList, logEntryText, false,
                    logLevelId);

            logEntryModel.addLogEntry(log4jLogEntry, logEntryColumnValueList, getCharset(), getLocale());

            // update the processed counter
            incrementAndGetProcessedCount();

            clearBuildLogEntryData();
        }
    }

    protected byte getLogLevelId(int responseCode) {

        byte levelId = 0;

        if (responseCode == 200) {
            levelId = 3;
        } else if (responseCode > 200 && responseCode < 300) {
            levelId = 4;
        } else if (responseCode >= 300 && responseCode < 400) {
            levelId = 1;
        } else if (responseCode >= 400 && responseCode < 500) {
            levelId = 2;
        } else if (responseCode >= 500) {
            levelId = 6;
        } /*
           * else { levelId = 1; }
           */

        return levelId;

    }
}
