
package com.pega.gcs.logviewer.ddsmetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.ddsmetrics.model.DdsMetricGauge;
import com.pega.gcs.logviewer.ddsmetrics.model.DdsMetricHistogram;
import com.pega.gcs.logviewer.ddsmetrics.model.DdsMetricMeter;
import com.pega.gcs.logviewer.ddsmetrics.model.DdsMetricTimer;
import com.pega.gcs.logviewer.ddsmetrics.model.DdsMetricType;
import com.pega.gcs.logviewer.ddsmetrics.model.DdsMetricWrapper;
import com.pega.gcs.logviewer.model.LogEntryKey;

public class DdsMetricMessageParser {

    private static final Log4j2Helper LOG = new Log4j2Helper(DdsMetricMessageParser.class);

    private static final String KEY_TYPE = "type";
    private static final String KEY_NAME = "name";
    private static final String KEY_OPERATION = "operation";
    private static final String KEY_METRIC = "metric";

    private Pattern dataPattern;

    public DdsMetricMessageParser() {

        String dataRegex = "([^=,]+)=([^\0]+?)(?=,[^,]+=|$)";
        dataPattern = Pattern.compile(dataRegex);

    }

    public DdsMetricWrapper getDdsMetricWrapper(LogEntryKey logEntryKey, String message) {

        DdsMetricWrapper ddsMetricWrapper = null;

        Map<String, String> dataMap = getDataMap(message);

        String type = dataMap.get(KEY_TYPE);
        String nameMessageStr = dataMap.get(KEY_NAME);

        Map<String, String> nameDataMap = getNameDataMap(nameMessageStr);

        String name = nameDataMap.get(KEY_NAME);
        String operation = nameDataMap.get(KEY_OPERATION);
        String metric = nameDataMap.get(KEY_METRIC);

        if ((type != null) && (!"".equals(type)) && (name != null) && (!"".equals(name)) && (operation != null)
                && (!"".equals(operation)) && (metric != null) && (!"".equals(metric))) {

            try {

                DdsMetricType ddsMetricType = DdsMetricType.valueOf(type);

                switch (ddsMetricType) {

                case GAUGE:
                    ddsMetricWrapper = getDdsMetricGaugeWrapper(logEntryKey, name, operation, metric, dataMap);
                    break;

                case HISTOGRAM:
                    ddsMetricWrapper = getDdsMetricHistogramWrapper(logEntryKey, name, operation, metric, dataMap);
                    break;

                case METER:
                    ddsMetricWrapper = getDdsMetricMeterWrapper(logEntryKey, name, operation, metric, dataMap);
                    break;

                case TIMER:
                    ddsMetricWrapper = getDdsMetricTimerWrapper(logEntryKey, name, operation, metric, dataMap);
                    break;

                default:
                    break;
                }
            } catch (Exception e) {
                LOG.error("Unknown type from message: " + message, e);
            }

        } else {
            LOG.error("Could not parse DdsMetric from message: " + message);
        }

        return ddsMetricWrapper;
    }

    private Map<String, String> getDataMap(String message) {

        Map<String, String> dataMap = new HashMap<>();

        Matcher dataMatcher = dataPattern.matcher(message);

        while (dataMatcher.find()) {

            String key = dataMatcher.group(1).trim();
            String value = dataMatcher.group(2);

            dataMap.put(key, value);
        }

        return dataMap;

    }

    private Map<String, String> getNameDataMap(String nameMessageStr) {

        Map<String, String> nameDataMap = new HashMap<>();

        // not able to find a proper regex to match both, doing crude approach
        // legacy-host-latency.c730d201-81b6-437f-9766-6da5bd8f0e03
        // data-customerstates_44587054998dbf1bac53ae4432e95759-browse-duration

        int dotIndex = nameMessageStr.indexOf(".");
        String nameStr = (dotIndex == -1) ? nameMessageStr : nameMessageStr.substring(0, dotIndex);

        String[] nameDataArray = nameStr.split("-");

        int nameDataArrayLen = nameDataArray.length;

        switch (nameDataArrayLen) {

        case 3:

            nameDataMap.put(KEY_NAME, nameDataArray[0]);
            nameDataMap.put(KEY_OPERATION, nameDataArray[1]);
            nameDataMap.put(KEY_METRIC, nameDataArray[2]);

            break;

        case 4:

            nameDataMap.put(KEY_NAME, nameDataArray[0] + "." + nameDataArray[1]);
            nameDataMap.put(KEY_OPERATION, nameDataArray[2]);
            nameDataMap.put(KEY_METRIC, nameDataArray[3]);

            break;

        default:
            LOG.error("Error parsing name data: " + nameMessageStr);
        }

        return nameDataMap;

    }

    private DdsMetricWrapper getDdsMetricGaugeWrapper(LogEntryKey logEntryKey, String name, String operation,
            String metric, Map<String, String> dataMap) {

        String valueStr = dataMap.get("value");
        double value = Double.parseDouble(valueStr);

        DdsMetricGauge ddsMetricGauge = new DdsMetricGauge(logEntryKey, value);

        DdsMetricWrapper ddsMetricWrapper = new DdsMetricWrapper(name, operation, metric, ddsMetricGauge);

        return ddsMetricWrapper;
    }

    private DdsMetricWrapper getDdsMetricHistogramWrapper(LogEntryKey logEntryKey, String name, String operation,
            String metric, Map<String, String> dataMap) {

        String countStr = dataMap.get("count");
        long count = Long.parseLong(countStr);

        String minStr = dataMap.get("min");
        double min = Double.parseDouble(minStr);

        String maxStr = dataMap.get("max");
        double max = Double.parseDouble(maxStr);

        String meanStr = dataMap.get("mean");
        double mean = Double.parseDouble(meanStr);

        String stddevStr = dataMap.get("stddev");
        double stddev = Double.parseDouble(stddevStr);

        String medianStr = dataMap.get("median");
        double median = Double.parseDouble(medianStr);

        String p75Str = dataMap.get("p75");
        double p75 = Double.parseDouble(p75Str);

        String p95Str = dataMap.get("p95");
        double p95 = Double.parseDouble(p95Str);

        String p98Str = dataMap.get("p98");
        double p98 = Double.parseDouble(p98Str);

        String p99Str = dataMap.get("p99");
        double p99 = Double.parseDouble(p99Str);

        String p999Str = dataMap.get("p999");
        double p999 = Double.parseDouble(p999Str);

        DdsMetricHistogram ddsMetricHistogram = new DdsMetricHistogram(logEntryKey, count, min, max, mean, stddev,
                median, p75, p95, p98, p99, p999);

        DdsMetricWrapper ddsMetricWrapper = new DdsMetricWrapper(name, operation, metric, ddsMetricHistogram);

        return ddsMetricWrapper;
    }

    private DdsMetricWrapper getDdsMetricMeterWrapper(LogEntryKey logEntryKey, String name, String operation,
            String metric, Map<String, String> dataMap) {

        String countStr = dataMap.get("count");
        long count = Long.parseLong(countStr);

        String meanRateStr = dataMap.get("mean_rate");
        double meanRate = Double.parseDouble(meanRateStr);

        String m1Str = dataMap.get("m1");
        double m1 = Double.parseDouble(m1Str);

        String m5Str = dataMap.get("m5");
        double m5 = Double.parseDouble(m5Str);

        String m15Str = dataMap.get("m15");
        double m15 = Double.parseDouble(m15Str);

        String rateUnit = dataMap.get("rate_unit");

        DdsMetricMeter ddsMetricMeter = new DdsMetricMeter(logEntryKey, count, meanRate, m1, m5, m15, rateUnit);

        DdsMetricWrapper ddsMetricWrapper = new DdsMetricWrapper(name, operation, metric, ddsMetricMeter);

        return ddsMetricWrapper;
    }

    private DdsMetricWrapper getDdsMetricTimerWrapper(LogEntryKey logEntryKey, String name, String operation,
            String metric, Map<String, String> dataMap) {

        String countStr = dataMap.get("count");
        long count = Long.parseLong(countStr);

        String minStr = dataMap.get("min");
        double min = Double.parseDouble(minStr);

        String maxStr = dataMap.get("max");
        double max = Double.parseDouble(maxStr);

        String meanStr = dataMap.get("mean");
        double mean = Double.parseDouble(meanStr);

        String stddevStr = dataMap.get("stddev");
        double stddev = Double.parseDouble(stddevStr);

        String medianStr = dataMap.get("median");
        double median = Double.parseDouble(medianStr);

        String p75Str = dataMap.get("p75");
        double p75 = Double.parseDouble(p75Str);

        String p95Str = dataMap.get("p95");
        double p95 = Double.parseDouble(p95Str);

        String p98Str = dataMap.get("p98");
        double p98 = Double.parseDouble(p98Str);

        String p99Str = dataMap.get("p99");
        double p99 = Double.parseDouble(p99Str);

        String p999Str = dataMap.get("p999");
        double p999 = Double.parseDouble(p999Str);

        String meanRateStr = dataMap.get("mean_rate");
        double meanRate = Double.parseDouble(meanRateStr);

        String m1Str = dataMap.get("m1");
        double m1 = Double.parseDouble(m1Str);

        String m5Str = dataMap.get("m5");
        double m5 = Double.parseDouble(m5Str);

        String m15Str = dataMap.get("m15");
        double m15 = Double.parseDouble(m15Str);

        String rateUnit = dataMap.get("rate_unit");

        String durationUnit = dataMap.get("duration_unit");

        DdsMetricTimer ddsMetricTimer = new DdsMetricTimer(logEntryKey, count, min, max, mean, stddev, median, p75, p95,
                p98, p99, p999, meanRate, m1, m5, m15, rateUnit, durationUnit);

        DdsMetricWrapper ddsMetricWrapper = new DdsMetricWrapper(name, operation, metric, ddsMetricTimer);

        return ddsMetricWrapper;
    }

    public static void main(String[] args) {

        DdsMetricMessageParser ddsMetricMessageParser = new DdsMetricMessageParser();

        String testStr1 = "type=GAUGE, name=data-actioninsights2_9f19ab49e34a551f6db210f757a5fd98-truncate-lastExecutionTime, value=0";

        String testStr2 = "type=HISTOGRAM, name=legacy-adm-requestSize, count=3434, min=300, max=411, mean=329.80624349583326, stddev=18.97"
                + "8914586504363, median=317.0, p75=352.0, p95=358.0, p98=373.0, p99=373.0, p999=373.0";

        String testStr3 = "type=METER, name=data-cdhdetailedinte_3b38dd60dd967db7fdb0ec78fea09329-upsert-requests, count=17489, mean_rate=0"
                + ".06772150827145709, m1=0.8201700235584181, m5=0.7942054283119905, m15=0.5238230915780855, rate_unit=events/second";

        String testStr4 = "type=TIMER, name=data-customerstates_44587054998dbf1bac53ae4432e95759-browse-duration, count=168900, min=0.52876"
                + "89999999999, max=457.70992099999995, mean=1.1475443128619744, stddev=4.989296889092131, median=0.9539099999999999, p75=1"
                + ".120257, p95=1.515599, p98=2.2430529999999997, p99=3.6931369999999997, p999=17.106351999999998, mean_rate=0.645799782531"
                + "4519, m1=2.6032215131813645, m5=1.0570076021661452, m15=0.4133912900950714, rate_unit=events/second, duration_unit=milli"
                + "seconds";

        String testStr5 = "type=HISTOGRAM, name=legacy-adm-requestSize, count=3366, min=300, max=480, mean=339.33840548963815, stddev=22.42"
                + "3400843883893, median=352.0, p75=358.0, p95=365.0, p98=368.0, p99=370.0, p999=411.0";

        List<String> testList = new ArrayList<>();
        testList.add(testStr1);
        testList.add(testStr2);
        testList.add(testStr3);
        testList.add(testStr4);
        testList.add(testStr5);

        for (String testStr : testList) {
            ddsMetricMessageParser.getDdsMetricWrapper(null, testStr);
        }
    }
}
