
package com.pega.gcs.logviewer.parser;

import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.LogViewerUtil;
import com.pega.gcs.logviewer.ddsmetrics.DdsMetricMessageParser;
import com.pega.gcs.logviewer.ddsmetrics.model.DdsMetricChartModel;
import com.pega.gcs.logviewer.ddsmetrics.model.DdsMetricWrapper;
import com.pega.gcs.logviewer.logfile.Log4jPattern;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogEntryModel;

public class DdsMetricLog4jPatternParser extends Log4jPatternParser {

    private static final Log4j2Helper LOG = new Log4j2Helper(DdsMetricLog4jPatternParser.class);

    private DdsMetricChartModel ddsMetricChartModel;

    private DdsMetricMessageParser ddsMetricMessageParser;

    public DdsMetricLog4jPatternParser(Log4jPattern log4jPattern, Charset charset, Locale locale,
            ZoneId displayZoneId) {

        super(log4jPattern, charset, locale, displayZoneId);

        DateTimeFormatter modelDateTimeFormatter = getModelDateTimeFormatter();
        ZoneId modelZoneId = getModelZoneId();

        ddsMetricChartModel = new DdsMetricChartModel(modelDateTimeFormatter, modelZoneId, displayZoneId);

        ddsMetricMessageParser = new DdsMetricMessageParser();

    }

    @Override
    public LogEntryModel getLogEntryModel() {
        return ddsMetricChartModel;
    }

    private DdsMetricMessageParser getDdsMetricMessageParser() {
        return ddsMetricMessageParser;
    }

    @Override
    protected void buildLogEntry() {

        DdsMetricChartModel ddsMetricChartModel = (DdsMetricChartModel) getLogEntryModel();
        AtomicInteger logEntryIndex = getLogEntryIndex();
        ArrayList<String> logEntryColumnValueList = getLogEntryColumnValueList();
        String logEntryText = getLogEntryText();

        if (logEntryColumnValueList.size() > 0) {

            logEntryIndex.incrementAndGet();

            int timestampIndex = getTimestampIndex();
            int messageIndex = getMessageIndex();

            String timestampStr = logEntryColumnValueList.get(timestampIndex);
            String message = logEntryColumnValueList.get(messageIndex);

            DateTimeFormatter modelDateTimeFormatter = ddsMetricChartModel.getModelDateTimeFormatter();
            ZoneId modelZoneId = ddsMetricChartModel.getModelZoneId();

            long logEntryTime = -1;
            try {

                logEntryTime = LogViewerUtil.getTimeMillis(timestampStr, modelDateTimeFormatter, modelZoneId);

            } catch (Exception e) {
                LOG.error("Error parsing line: [" + logEntryIndex + "] logentry: [" + logEntryText + "]", e);
            }

            LogEntryKey logEntryKey = new LogEntryKey(logEntryIndex.intValue(), logEntryTime);

            DdsMetricMessageParser ddsMetricMessageParser = getDdsMetricMessageParser();

            DdsMetricWrapper ddsMetricWrapper = ddsMetricMessageParser.getDdsMetricWrapper(logEntryKey, message);

            if (ddsMetricWrapper != null) {
                ddsMetricChartModel.addMetric(ddsMetricWrapper);
            } else {
                LOG.error("DDS Metric Parser error: " + message);
            }

            // update the processed counter
            incrementAndGetProcessedCount();

            clearBuildLogEntryData();
        }
    }

}
