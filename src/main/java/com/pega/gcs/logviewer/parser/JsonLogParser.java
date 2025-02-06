/*******************************************************************************
 * Copyright (c) 2023 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.parser;

import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.LogViewerUtil;
import com.pega.gcs.logviewer.model.JsonLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;

public class JsonLogParser extends LogParser {

    private static final Log4j2Helper LOG = new Log4j2Helper(JsonLogParser.class);

    private JsonLogEntryModel jsonLogEntryModel;

    private Map<String, LogEntryColumn> fieldKeyColumnMap;

    public JsonLogParser(Charset charset, Locale locale, ZoneId displayZoneId) {

        super(null, charset, locale, displayZoneId);

        DateTimeFormatter modelDateTimeFormatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
                .optionalStart().appendFraction(ChronoField.MICRO_OF_SECOND, 1, 6, true).optionalEnd()
                .appendLiteral(" UTC").toFormatter();

        ZoneId modelZoneId = ZoneOffset.UTC;

        jsonLogEntryModel = new JsonLogEntryModel(modelDateTimeFormatter, modelZoneId, displayZoneId);

        setModelZoneId(modelZoneId);
        setModelDateTimeFormatter(modelDateTimeFormatter);

        fieldKeyColumnMap = new HashMap<>();
    }

    @Override
    protected void parseV1(String line) {
        parseV3(line);
    }

    @Override
    protected void parseV2(String line) {
        parseV3(line);
    }

    @Override
    protected void parseV3(String line) {

        Map<String, String> fieldMap = getJsonFieldMap(line);

        if (fieldMap != null) {

            setupLogEntryColumnList(fieldMap);

            buildLogEntry(fieldMap, line);
        } else {
            LOG.info("discarding line: " + line);
        }
    }

    private void setupLogEntryColumnList(Map<String, String> fieldMap) {

        JsonLogEntryModel jsonLogEntryModel = getLogEntryModel();
        List<LogEntryColumn> jsonLogEntryColumnList = jsonLogEntryModel.getLogEntryColumnList();

        if ((fieldMap != null) && ((jsonLogEntryColumnList == null) || (jsonLogEntryColumnList.isEmpty()))) {

            int prefColumnWidth = 200;
            int horizontalAlignment = SwingConstants.CENTER;
            boolean visibleColumn = true;
            boolean filterable = true;

            fieldKeyColumnMap.clear();

            for (String fieldkey : fieldMap.keySet()) {

                String upperFieldKey = fieldkey.toUpperCase();
                LogEntryColumn logEntryColumn = LogEntryColumn.getTableColumnById(upperFieldKey);

                if (logEntryColumn == null) {
                    logEntryColumn = new LogEntryColumn(fieldkey, fieldkey, prefColumnWidth, horizontalAlignment,
                            visibleColumn, filterable);
                }

                fieldKeyColumnMap.put(fieldkey, logEntryColumn);

            }

            List<LogEntryColumn> logEntryColumnList = new ArrayList<>();

            // add Line and timestamp first
            logEntryColumnList.add(LogEntryColumn.LINE);
            logEntryColumnList.add(LogEntryColumn.TIMESTAMP);

            for (LogEntryColumn logEntryColumn : fieldKeyColumnMap.values()) {

                if ((!logEntryColumnList.contains(logEntryColumn))
                        && (!LogEntryColumn.MESSAGE.equals(logEntryColumn))) {
                    logEntryColumnList.add(logEntryColumn);
                }
            }

            // add message column to last
            logEntryColumnList.add(LogEntryColumn.MESSAGE);

            LOG.info("logEntryColumnList: " + logEntryColumnList);

            jsonLogEntryModel.updateLogEntryColumnList(logEntryColumnList);

        }
    }

    @Override
    public void parseFinalInternal() {

    }

    @Override
    public JsonLogEntryModel getLogEntryModel() {
        return jsonLogEntryModel;
    }

    private void buildLogEntry(Map<String, String> fieldMap, String line) {

        if (fieldMap != null) {

            JsonLogEntryModel jsonLogEntryModel = getLogEntryModel();
            AtomicInteger logEntryIndex = getLogEntryIndex();

            try {

                String timestampStr = null;
                Map<LogEntryColumn, String> logEntryColumnValueMap = new HashMap<>();

                for (String fieldkey : fieldMap.keySet()) {
                    LogEntryColumn logEntryColumn = fieldKeyColumnMap.get(fieldkey);

                    String columnValue = fieldMap.get(fieldkey);
                    logEntryColumnValueMap.put(logEntryColumn, columnValue);

                    if (logEntryColumn.equals(LogEntryColumn.TIMESTAMP)) {
                        timestampStr = columnValue;
                    }
                }

                int lineNo = logEntryIndex.incrementAndGet();

                logEntryColumnValueMap.put(LogEntryColumn.LINE, String.valueOf(lineNo));

                long logEntryTime = -1;
                try {
                    DateTimeFormatter modelDateTimeFormatter = jsonLogEntryModel.getModelDateTimeFormatter();
                    ZoneId modelZoneId = jsonLogEntryModel.getModelZoneId();

                    logEntryTime = LogViewerUtil.getTimeMillis(timestampStr, modelDateTimeFormatter, modelZoneId);

                } catch (Exception e) {
                    LOG.error("Error parsing line: [" + logEntryIndex + "] logentry: [" + line + "]", e);
                }

                LogEntryKey logEntryKey = new LogEntryKey(lineNo, logEntryTime);

                jsonLogEntryModel.addLogEntry(logEntryKey, logEntryColumnValueMap, line, getCharset(), getLocale());

                // update the processed counter
                incrementAndGetProcessedCount();

            } catch (Exception e) {
                LOG.error("Error parsing Log text: \n" + fieldMap);
                LOG.error("Error parsing Log index: " + logEntryIndex, e);

            }
        }
    }

}
