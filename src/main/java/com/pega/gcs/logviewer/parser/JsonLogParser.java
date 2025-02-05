/*******************************************************************************
 * Copyright (c) 2023 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.parser;

import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.LogViewerUtil;
import com.pega.gcs.logviewer.model.AlertLogEntry;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.JsonLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.alert.AlertMessageListProvider;

public class JsonLogParser extends LogParser {

    private static final Log4j2Helper LOG = new Log4j2Helper(JsonLogParser.class);

    private JsonLogEntryModel jsonLogEntryModel;

    public JsonLogParser(Charset charset, Locale locale, ZoneId displayZoneId) {

        super(null, charset, locale, displayZoneId);

        DateTimeFormatter modelDateTimeFormatter = getModelDateTimeFormatter();
        ZoneId modelZoneId = getModelZoneId();

        jsonLogEntryModel = new JsonLogEntryModel(modelDateTimeFormatter, modelZoneId, displayZoneId);
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

        Map<String, Object> fieldMap = getCloudKFieldMap(line);

        if (fieldMap != null) {

            setupLogEntryColumnList(line);

            @SuppressWarnings("unchecked")
            Map<String, String> logMap = (Map<String, String>) fieldMap.get("log");

            String message = (String) logMap.get("message");
            // TODO - implement
            buildLogEntry(logMap);
        }
    }

    private void setupLogEntryColumnList(String line) {

        JsonLogEntryModel jsonLogEntryModel = getLogEntryModel();
        List<LogEntryColumn> jsonLogEntryColumnList = jsonLogEntryModel.getLogEntryColumnList();

        if ((line != null) && (!line.isEmpty())
                && ((jsonLogEntryColumnList == null) || (jsonLogEntryColumnList.isEmpty()))) {

            Map<String, Object> fieldMap = getCloudKFieldMap(line);

            if (fieldMap != null) {

                int prefColumnWidth = 200;
                int horizontalAlignment = SwingConstants.CENTER;
                boolean visibleColumn = true;
                boolean filterable = true;

                Map<String, LogEntryColumn> logEntryColumnMap = new HashMap<>();

                for (String fieldkey : fieldMap.keySet()) {

                    LogEntryColumn logEntryColumn = LogEntryColumn.getTableColumnById(fieldkey);

                    if (logEntryColumn == null) {
                        logEntryColumn = new LogEntryColumn(fieldkey, fieldkey, prefColumnWidth, horizontalAlignment,
                                visibleColumn, filterable);
                    }

                    logEntryColumnMap.put(fieldkey, logEntryColumn);

                }

                List<LogEntryColumn> logEntryColumnList = new ArrayList<>();

                // add timestamp first
                LogEntryColumn logEntryColumn = logEntryColumnMap.get(LogEntryColumn.TIMESTAMP.getColumnId());
                logEntryColumnList.add(logEntryColumn);
                logEntryColumnMap.remove(logEntryColumn.getColumnId());

                logEntryColumnList.addAll(logEntryColumnMap.values());

                LOG.info("logEntryColumnList: " + logEntryColumnList);

                jsonLogEntryModel.updateLogEntryColumnList(logEntryColumnList);

            } else {
                LOG.info("discarding line: " + line);
            }
        }

    }

    @Override
    public void parseFinalInternal() {

    }

    @Override
    public JsonLogEntryModel getLogEntryModel() {
        return jsonLogEntryModel;
    }

    private AlertLogEntry buildLogEntry(Map<String, String> fieldMap) {

        AlertLogEntry alertLogEntry = null;

        if (fieldMap != null) {

            JsonLogEntryModel jsonLogEntryModel = getLogEntryModel();
            AtomicInteger logEntryIndex = getLogEntryIndex();

            try {

                logEntryIndex.incrementAndGet();

                String jsonString = getJsonString(fieldMap);

                jsonLogEntryModel.addLogEntry(logEntryIndex.get(), fieldMap, jsonString);

                // update the processed counter
                incrementAndGetProcessedCount();

            } catch (Exception e) {
                LOG.error("Error parsing Log text: \n" + fieldMap);
                LOG.error("Error parsing Log index: " + logEntryIndex, e);

            }
        }

        return alertLogEntry;
    }

}
