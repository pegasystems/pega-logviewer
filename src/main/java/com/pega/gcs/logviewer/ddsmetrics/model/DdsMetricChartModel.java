
package com.pega.gcs.logviewer.ddsmetrics.model;

import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.pega.gcs.logviewer.model.LogEntry;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogEntryModel;
import com.pega.gcs.logviewer.model.LogIntervalMarker;
import com.pega.gcs.logviewer.model.LogSeriesCollection;

public class DdsMetricChartModel extends LogEntryModel {

    private Map<String, DdsMetricTableInfo> tableinfoMap;

    public DdsMetricChartModel(DateTimeFormatter modelDateTimeFormatter, ZoneId modelZoneId, ZoneId displayZoneId) {

        super(modelDateTimeFormatter, modelZoneId, displayZoneId);

        tableinfoMap = new TreeMap<>();

    }

    @Override
    public void rebuildLogTimeSeriesCollectionSet(Locale locale) throws Exception {

    }

    @Override
    public Set<LogSeriesCollection> getLogTimeSeriesCollectionSet(boolean filtered, Locale locale) throws Exception {
        return null;
    }

    @Override
    public Set<LogIntervalMarker> getLogIntervalMarkerSet() {
        return null;
    }

    @Override
    public LogEntryColumn[] getReportTableColumns() {
        return null;
    }

    @Override
    public String getTypeName() {
        return "DDSMetric";
    }

    @Override
    protected void postProcess(LogEntry logEntry, ArrayList<String> logEntryValueList, Charset charset, Locale locale) {

    }

    public Map<String, DdsMetricTableInfo> getTableinfoMap() {
        return Collections.unmodifiableMap(tableinfoMap);
    }

    public void addMetric(DdsMetricWrapper ddsMetricWrapper) {

        String tablename = ddsMetricWrapper.getTablename();
        String operation = ddsMetricWrapper.getOperation();
        String metric = ddsMetricWrapper.getMetric();
        DdsMetric ddsMetric = ddsMetricWrapper.getDdsMetric();

        DdsMetricTableInfo ddsMetricTableInfo = tableinfoMap.get(tablename);

        if (ddsMetricTableInfo == null) {
            ddsMetricTableInfo = new DdsMetricTableInfo(tablename);
            tableinfoMap.put(tablename, ddsMetricTableInfo);
        }

        ddsMetricTableInfo.addDdsMetric(operation, metric, ddsMetric);

        LogEntryKey logEntryKey = ddsMetric.getLogEntryKey();

        long logEntryTime = logEntryKey.getTimestamp();

        long lowerDomainRange = getLowerDomainRange();
        long upperDomainRange = getUpperDomainRange();

        if (lowerDomainRange == -1) {
            lowerDomainRange = logEntryTime - 1;
        } else {
            lowerDomainRange = Math.min(lowerDomainRange, logEntryTime);
        }

        if (upperDomainRange == -1) {
            upperDomainRange = logEntryTime;
        } else {
            upperDomainRange = Math.max(upperDomainRange, logEntryTime);
        }

        setLowerDomainRange(lowerDomainRange);
        setUpperDomainRange(upperDomainRange);
    }
}
