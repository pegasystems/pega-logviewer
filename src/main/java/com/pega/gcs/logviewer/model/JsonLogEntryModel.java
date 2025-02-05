
package com.pega.gcs.logviewer.model;

import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.LogViewerUtil;

public class JsonLogEntryModel extends LogEntryModel {

    private static final Log4j2Helper LOG = new Log4j2Helper(JsonLogEntryModel.class);

    public JsonLogEntryModel(DateTimeFormatter modelDateTimeFormatter, ZoneId modelZoneId, ZoneId displayZoneId) {
        super(modelDateTimeFormatter, modelZoneId, displayZoneId);
    }

    @Override
    public void rebuildLogTimeSeriesCollectionSet(Locale locale) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public Set<LogSeriesCollection> getLogTimeSeriesCollectionSet(boolean filtered, Locale locale) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<LogIntervalMarker> getLogIntervalMarkerSet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LogEntryColumn[] getReportTableColumns() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTypeName() {
        return "JSON";
    }

    @Override
    protected void postProcess(LogEntry logEntry, ArrayList<String> logEntryValueList, Charset charset, Locale locale) {
        // TODO Auto-generated method stub

    }

    public void addLogEntry(int logEntryIndex, Map<String, String> fieldMap, String logEntryText) {

        List<LogEntryColumn> logEntryColumnList = getLogEntryColumnList();
        DateTimeFormatter modelDateTimeFormatter = getModelDateTimeFormatter();
        ZoneId modelZoneId = getModelZoneId();

        ArrayList<String> columnValueList = new ArrayList<String>();

        columnValueList.add(String.valueOf(logEntryIndex));

        String timestampStr = null;

        for (LogEntryColumn logEntryColumn : logEntryColumnList) {

            String columnId = logEntryColumn.getColumnId();
            String columnValue = fieldMap.get(columnId);
            columnValueList.add(columnValue);

            if (logEntryColumn.equals(LogEntryColumn.TIMESTAMP)) {
                timestampStr = columnValue;
            }
        }

        try {

            long logEntryTime = LogViewerUtil.getTimeMillis(timestampStr, modelDateTimeFormatter, modelZoneId);

            LogEntryKey logEntryKey = new LogEntryKey(logEntryIndex, logEntryTime);

            JsonLogEntry jsonLogEntry = new JsonLogEntry(logEntryKey, columnValueList, logEntryText);

        } catch (Exception e) {
            LOG.error(e);
        }
    }

}
