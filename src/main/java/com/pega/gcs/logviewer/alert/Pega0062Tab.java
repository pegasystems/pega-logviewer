
package com.pega.gcs.logviewer.alert;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.logviewer.LogViewerUtil;

public class Pega0062Tab extends JPanel {

    private static final long serialVersionUID = 3374989678760572519L;

    private static DecimalFormat INT_FORMAT = new DecimalFormat("###,###,###");

    private static DecimalFormat DOUBLE_FORMAT = new DecimalFormat("###,###,###.###");

    private StyleSheet styleSheet;

    private DateTimeFormatter displayDateTimeFormatter;

    private ZoneId displayZoneId;

    public Pega0062Tab(ArrayList<Map<String, Object>> stageMetricsList, DateTimeFormatter displayDateTimeFormatter,
            ZoneId displayZoneId) {

        super();

        this.displayDateTimeFormatter = displayDateTimeFormatter;
        this.displayZoneId = displayZoneId;

        this.styleSheet = FileUtilities.getStyleSheet(this.getClass(), "styles.css");

        String dataFlowKey = null;

        List<StageRecord> stageRecordList = new ArrayList<>();

        if (stageMetricsList != null) {

            for (Map<String, Object> stageMetric : stageMetricsList) {

                if (dataFlowKey == null) {
                    dataFlowKey = getDataflowKey(stageMetric);
                }

                StageRecord stageRecord = getStageRecord(stageMetric);

                stageRecordList.add(stageRecord);

            }
        }

        setLayout(new GridBagLayout());

        int xindex = 0;
        int yindex = 0;

        if (dataFlowKey != null) {

            GridBagConstraints gbc1 = new GridBagConstraints();
            gbc1.gridx = xindex;
            gbc1.gridy = yindex;
            gbc1.weightx = 1.0D;
            gbc1.weighty = 1.0D;
            gbc1.fill = GridBagConstraints.BOTH;
            gbc1.anchor = GridBagConstraints.NORTHWEST;
            gbc1.insets = new Insets(0, 0, 0, 0);

            JPanel dataflowKeypanel = getDataflowKeyComponent(dataFlowKey);

            add(dataflowKeypanel, gbc1);
        }

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = xindex;
        gbc2.gridy = yindex++;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 1.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(0, 0, 0, 0);

        JPanel stageMetricPanel = getStageMetricComponent(stageRecordList);

        add(stageMetricPanel, gbc2);

        for (StageRecord stageRecord : stageRecordList) {

            if (stageRecord.hasStopwatches()) {

                String stageId = stageRecord.getStageId();
                String stageName = stageRecord.getStageName();

                List<StopwatchRecord> stopwatchesList = stageRecord.getStopwatchesList();

                JComponent stopWatchMetricComponent = getStopWatchMetricPanel(stageId, stageName, stopwatchesList);

                GridBagConstraints gbc3 = new GridBagConstraints();
                gbc3.gridx = xindex++;
                gbc3.gridy = yindex;
                gbc3.weightx = 1.0D;
                gbc3.weighty = 1.0D;
                gbc3.fill = GridBagConstraints.BOTH;
                gbc3.anchor = GridBagConstraints.NORTHWEST;
                gbc3.insets = new Insets(0, 0, 0, 0);

                add(stopWatchMetricComponent, gbc3);
            }
        }

    }

    private DateTimeFormatter getDisplayDateTimeFormatter() {
        return displayDateTimeFormatter;
    }

    private ZoneId getDisplayZoneId() {
        return displayZoneId;
    }

    private StyleSheet getStyleSheet() {
        return styleSheet;
    }

    private String getDataflowKey(Map<String, Object> stageMetric) {

        @SuppressWarnings("unchecked")
        Map<String, Object> dataFlowKeys = (Map<String, Object>) stageMetric.get("dataFlowKeys");

        String className = (String) dataFlowKeys.get("className");
        String name = (String) dataFlowKeys.get("name");

        StringBuilder dataflowkeySB = new StringBuilder();

        dataflowkeySB.append(className);
        dataflowkeySB.append(".");
        dataflowkeySB.append(name);

        return dataflowkeySB.toString();
    }

    private StageRecord getStageRecord(Map<String, Object> stageMetric) {

        StageRecord stageRecord = null;

        String stageId = (String) stageMetric.get("stageId");
        String stageName = (String) stageMetric.get("stageName");
        Object movingAverageObject = stageMetric.get("movingAverage");

        double movingAverage = -1d;

        if (movingAverageObject instanceof Map) {
            movingAverage = (double) ((Map<String, Object>) stageMetric.get("movingAverage")).get("value");
        } else if (movingAverageObject instanceof Double) {
            movingAverage = (double) stageMetric.get("movingAverage");
        }
        int recordsIn = (int) stageMetric.get("recordsIn");
        int recordsOut = (int) stageMetric.get("recordsOut");
        int errorsCount = (int) stageMetric.get("errorsCount");
        long lastRecordTimestamp = Long.parseLong(stageMetric.get("lastRecordTimestamp").toString());
        long executionTime = Long.parseLong(stageMetric.get("executionTime").toString());
        double avgTimePerRecord = (double) stageMetric.get("avgTimePerRecord");

        List<StopwatchRecord> stopwatchesList = null;

        @SuppressWarnings("unchecked")
        Map<String, Object> internalMetrics = (Map<String, Object>) stageMetric.get("internalMetrics");

        if (internalMetrics != null) {

            @SuppressWarnings("unchecked")
            Map<String, Object> stopwatches = (Map<String, Object>) internalMetrics.get("stopwatches");

            if (stopwatches != null) {

                stopwatchesList = new ArrayList<>();

                for (String stopwatch : stopwatches.keySet()) {

                    @SuppressWarnings("unchecked")
                    Map<String, Object> stopwatchEntryMap = (Map<String, Object>) stopwatches.get(stopwatch);

                    if (stopwatchEntryMap != null) {

                        long elapsedNanos = Long.parseLong(stopwatchEntryMap.get("elapsedNanos").toString());
                        long timesCalled = Long.parseLong(stopwatchEntryMap.get("timesCalled").toString());

                        StopwatchRecord stopwatchRecord = new StopwatchRecord(stopwatch, elapsedNanos, timesCalled);

                        stopwatchesList.add(stopwatchRecord);
                    }

                }
            }
        }

        stageRecord = new StageRecord(stageId, stageName, movingAverage, recordsIn, recordsOut, errorsCount,
                lastRecordTimestamp, executionTime, avgTimePerRecord, stopwatchesList);

        return stageRecord;

    }

    private JPanel getDataflowKeyComponent(String dataflowKey) {

        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        StyleSheet htmlStyleSheet = htmlEditorKit.getStyleSheet();

        StyleSheet styleSheet = getStyleSheet();
        htmlStyleSheet.addStyleSheet(styleSheet);

        JEditorPane stageMetricEditorPane = new JEditorPane();
        stageMetricEditorPane.setEditable(false);
        stageMetricEditorPane.setContentType("text/html");
        stageMetricEditorPane.setEditorKitForContentType("text/html", htmlEditorKit);

        StringBuilder htmlStringBuilder = new StringBuilder();

        htmlStringBuilder.append("<H3 align='center'>");
        htmlStringBuilder.append("Dataflow Key: ");
        htmlStringBuilder.append(dataflowKey);
        htmlStringBuilder.append("</H3>");
        htmlStringBuilder.append("</DIV></BODY></HTML>");

        stageMetricEditorPane.setText(htmlStringBuilder.toString());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(2, 2, 2, 2);

        JPanel stageMetricPanel = new JPanel();

        stageMetricPanel.setLayout(new GridBagLayout());

        stageMetricPanel.add(stageMetricEditorPane, gbc1);
        stageMetricPanel.setBackground(Color.WHITE);
        stageMetricPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        return stageMetricPanel;
    }

    private JPanel getStageMetricComponent(List<StageRecord> stageRecordList) {

        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        StyleSheet htmlStyleSheet = htmlEditorKit.getStyleSheet();

        StyleSheet styleSheet = getStyleSheet();
        htmlStyleSheet.addStyleSheet(styleSheet);

        JEditorPane stageMetricEditorPane = new JEditorPane();
        stageMetricEditorPane.setEditable(false);
        stageMetricEditorPane.setContentType("text/html");
        stageMetricEditorPane.setEditorKitForContentType("text/html", htmlEditorKit);

        StringBuilder htmlStringBuilder = new StringBuilder();

        htmlStringBuilder.append("<H3 align='center'>" + "STAGE METRICS" + "</H3>");

        StringBuilder tablehtmlSB = new StringBuilder();

        tablehtmlSB.append("<table border=\"1\" width=\"100%\">");

        String headerHtml = getStageMetricHeaderRow();

        tablehtmlSB.append(headerHtml);

        Collections.sort(stageRecordList);

        for (StageRecord stageRecord : stageRecordList) {

            String stageTableRow = stageRecord.getTableRow();

            tablehtmlSB.append(stageTableRow);
        }

        tablehtmlSB.append("</table>");

        htmlStringBuilder.append(tablehtmlSB);

        htmlStringBuilder.append("</DIV></BODY></HTML>");

        stageMetricEditorPane.setText(htmlStringBuilder.toString());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(2, 2, 2, 2);

        JPanel stageMetricPanel = new JPanel();

        stageMetricPanel.setLayout(new GridBagLayout());

        stageMetricPanel.add(stageMetricEditorPane, gbc1);
        stageMetricPanel.setBackground(Color.WHITE);
        stageMetricPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        return stageMetricPanel;
    }

    private JPanel getStopWatchMetricPanel(String stageId, String stageName, List<StopwatchRecord> stopwatchesList) {

        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        StyleSheet htmlStyleSheet = htmlEditorKit.getStyleSheet();

        StyleSheet styleSheet = getStyleSheet();
        htmlStyleSheet.addStyleSheet(styleSheet);

        JEditorPane stageMetricEditorPane = new JEditorPane();
        stageMetricEditorPane.setEditable(false);
        stageMetricEditorPane.setContentType("text/html");
        stageMetricEditorPane.setEditorKitForContentType("text/html", htmlEditorKit);

        StringBuilder htmlStringBuilder = new StringBuilder();

        htmlStringBuilder.append("<H3 align='center'> STOP WATCHES </H3>");

        StringBuilder tablehtmlSB = new StringBuilder();

        tablehtmlSB.append("<table border=\"1\" width=\"100%\">");

        String headerHtml = getStopwatchesHeaderRow(stageId, stageName);

        tablehtmlSB.append(headerHtml);

        Collections.sort(stopwatchesList);

        for (StopwatchRecord stopwatchRecord : stopwatchesList) {

            String stopwatchTableRow = stopwatchRecord.getTableRow();

            tablehtmlSB.append(stopwatchTableRow);
        }

        tablehtmlSB.append("</table>");

        htmlStringBuilder.append(tablehtmlSB);

        htmlStringBuilder.append("</DIV></BODY></HTML>");

        stageMetricEditorPane.setText(htmlStringBuilder.toString());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(2, 2, 2, 2);

        JPanel stageMetricPanel = new JPanel();

        stageMetricPanel.setLayout(new GridBagLayout());

        stageMetricPanel.add(stageMetricEditorPane, gbc1);
        stageMetricPanel.setBackground(Color.WHITE);
        stageMetricPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        return stageMetricPanel;
    }

    private String getStageMetricHeaderRow() {
        StringBuilder stageHeaderHtmlSB = new StringBuilder();

        stageHeaderHtmlSB.append("<tr>");

        stageHeaderHtmlSB.append("<th class=\"tableHeaderCenter\">");
        stageHeaderHtmlSB.append("Stage Id");
        stageHeaderHtmlSB.append("</th>");

        stageHeaderHtmlSB.append("<th class=\"tableHeaderCenter\">");
        stageHeaderHtmlSB.append("Stage Name");
        stageHeaderHtmlSB.append("</th>");

        stageHeaderHtmlSB.append("<th class=\"tableHeaderCenter\">");
        stageHeaderHtmlSB.append("Moving Average");
        stageHeaderHtmlSB.append("</th>");

        stageHeaderHtmlSB.append("<th class=\"tableHeaderCenter\">");
        stageHeaderHtmlSB.append("Records In");
        stageHeaderHtmlSB.append("</th>");

        stageHeaderHtmlSB.append("<th class=\"tableHeaderCenter\">");
        stageHeaderHtmlSB.append("Records Out");
        stageHeaderHtmlSB.append("</th>");

        stageHeaderHtmlSB.append("<th class=\"tableHeaderCenter\">");
        stageHeaderHtmlSB.append("Errors Count");
        stageHeaderHtmlSB.append("</th>");

        stageHeaderHtmlSB.append("<th class=\"tableHeaderCenter\">");
        stageHeaderHtmlSB.append("Last Record Timestamp");
        stageHeaderHtmlSB.append("</th>");

        stageHeaderHtmlSB.append("<th class=\"tableHeaderCenter\">");
        stageHeaderHtmlSB.append("Execution Time (nanos) &#x2193;");
        stageHeaderHtmlSB.append("</th>");

        stageHeaderHtmlSB.append("<th class=\"tableHeaderCenter\">");
        stageHeaderHtmlSB.append("Avg Time Per Record");
        stageHeaderHtmlSB.append("</th>");

        stageHeaderHtmlSB.append("</tr>");

        return stageHeaderHtmlSB.toString();
    }

    private String getStopwatchesHeaderRow(String stageId, String stageName) {

        StringBuilder stopwatchesSB = new StringBuilder();

        stopwatchesSB.append("<tr>");
        stopwatchesSB.append("<th class=\"tableHeaderCenter\" colspan=\"3\">");
        stopwatchesSB.append(stageId);
        stopwatchesSB.append(" - ");
        stopwatchesSB.append(stageName);
        stopwatchesSB.append("</th>");
        stopwatchesSB.append("</tr>");

        stopwatchesSB.append("<tr>");

        stopwatchesSB.append("<th class=\"tableHeaderCenter\">");
        stopwatchesSB.append("Name");
        stopwatchesSB.append("</th>");

        stopwatchesSB.append("<th class=\"tableHeaderCenter\">");
        stopwatchesSB.append("Elapsed Nanos &#x2193;");
        stopwatchesSB.append("</th>");

        stopwatchesSB.append("<th class=\"tableHeaderCenter\">");
        stopwatchesSB.append("Times Called");
        stopwatchesSB.append("</th>");

        stopwatchesSB.append("</tr>");

        return stopwatchesSB.toString();
    }

    private class StageRecord implements Comparable<StageRecord> {

        private String stageId;

        private String stageName;

        private double movingAverage;

        private int recordsIn;

        private int recordsOut;

        private int errorsCount;

        private long lastRecordTimestamp;

        private long executionTime;

        private double avgTimePerRecord;

        private List<StopwatchRecord> stopwatchesList;

        private StageRecord(String stageId, String stageName, double movingAverage, int recordsIn, int recordsOut,
                int errorsCount, long lastRecordTimestamp, long executionTime, double avgTimePerRecord,
                List<StopwatchRecord> stopwatchesList) {
            super();
            this.stageId = stageId;
            this.stageName = stageName;
            this.movingAverage = movingAverage;
            this.recordsIn = recordsIn;
            this.recordsOut = recordsOut;
            this.errorsCount = errorsCount;
            this.lastRecordTimestamp = lastRecordTimestamp;
            this.executionTime = executionTime;
            this.avgTimePerRecord = avgTimePerRecord;
            this.stopwatchesList = stopwatchesList;
        }

        private String getStageId() {
            return stageId;
        }

        private String getStageName() {
            return stageName;
        }

        private boolean hasStopwatches() {
            return (stopwatchesList != null) ? true : false;
        }

        private List<StopwatchRecord> getStopwatchesList() {
            return stopwatchesList;
        }

        private String getTableRow() {

            StringBuilder stageRecordHtmlSB = new StringBuilder();

            stageRecordHtmlSB.append("<tr>");

            stageRecordHtmlSB.append("<td class=\"valueColumnLeftIndent\">");
            stageRecordHtmlSB.append(stageId);
            stageRecordHtmlSB.append("</td>");

            stageRecordHtmlSB.append("<td class=\"valueColumnLeftIndent\">");
            stageRecordHtmlSB.append(stageName);
            stageRecordHtmlSB.append("</td>");

            stageRecordHtmlSB.append("<td class=\"valueColumnRightIndent\">");
            stageRecordHtmlSB.append(getFormattedNumberData(movingAverage));
            stageRecordHtmlSB.append("</td>");

            stageRecordHtmlSB.append("<td class=\"valueColumnRightIndent\">");
            stageRecordHtmlSB.append(getFormattedNumberData(recordsIn));
            stageRecordHtmlSB.append("</td>");

            stageRecordHtmlSB.append("<td class=\"valueColumnRightIndent\">");
            stageRecordHtmlSB.append(getFormattedNumberData(recordsOut));
            stageRecordHtmlSB.append("</td>");

            stageRecordHtmlSB.append("<td class=\"valueColumnRightIndent\">");
            stageRecordHtmlSB.append(getFormattedNumberData(errorsCount));
            stageRecordHtmlSB.append("</td>");

            stageRecordHtmlSB.append("<td class=\"valueColumnCenter\">");
            ZoneId displayZoneId = getDisplayZoneId();
            DateTimeFormatter displayDateTimeFormatter = getDisplayDateTimeFormatter();
            stageRecordHtmlSB.append(
                    LogViewerUtil.getFormattedTimeStr(lastRecordTimestamp, displayDateTimeFormatter, displayZoneId));
            stageRecordHtmlSB.append("</td>");

            stageRecordHtmlSB.append("<td class=\"valueColumnRightIndent\">");
            stageRecordHtmlSB.append(getFormattedNumberData(executionTime));
            stageRecordHtmlSB.append("</td>");

            stageRecordHtmlSB.append("<td class=\"valueColumnRightIndent\">");
            stageRecordHtmlSB.append(getFormattedNumberData(avgTimePerRecord));
            stageRecordHtmlSB.append("</td>");

            stageRecordHtmlSB.append("</tr>");

            return stageRecordHtmlSB.toString();
        }

        @Override
        public int compareTo(StageRecord other) {

            // sort decending
            return Long.compare(other.executionTime, executionTime);
        }
    }

    private class StopwatchRecord implements Comparable<StopwatchRecord> {

        private String name;

        private long elapsedNanos;

        private long timesCalled;

        private StopwatchRecord(String name, long elapsedNanos, long timesCalled) {
            super();
            this.name = name;
            this.elapsedNanos = elapsedNanos;
            this.timesCalled = timesCalled;
        }

        private String getTableRow() {

            StringBuilder stageRecordHtmlSB = new StringBuilder();

            stageRecordHtmlSB.append("<tr>");

            stageRecordHtmlSB.append("<td class=\"valueColumnLeftIndent\">");
            stageRecordHtmlSB.append(name);
            stageRecordHtmlSB.append("</td>");

            stageRecordHtmlSB.append("<td class=\"valueColumnRightIndent\">");
            stageRecordHtmlSB.append(getFormattedNumberData(elapsedNanos));
            stageRecordHtmlSB.append("</td>");

            stageRecordHtmlSB.append("<td class=\"valueColumnRightIndent\">");
            stageRecordHtmlSB.append(getFormattedNumberData(timesCalled));
            stageRecordHtmlSB.append("</td>");

            stageRecordHtmlSB.append("</tr>");

            return stageRecordHtmlSB.toString();
        }

        @Override
        public int compareTo(StopwatchRecord other) {

            // sort decending
            return Long.compare(other.elapsedNanos, elapsedNanos);
        }

    }

    private String getFormattedNumberData(Number number) {

        StringBuilder dataSB = new StringBuilder();

        if (number instanceof Double) {
            dataSB.append(DOUBLE_FORMAT.format(number));
        } else if ((number instanceof Integer) || (number instanceof Long)) {
            dataSB.append(INT_FORMAT.format(number));
        } else {
            dataSB.append(number.toString());
        }

        return dataSB.toString();

    }

}
