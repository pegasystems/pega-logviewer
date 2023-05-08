
package com.pega.gcs.logviewer.ddsmetrics.model;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;

public class DdsMetricTableInfo {

    // keyspacename.columnfamilyname
    private String tablename;

    private Map<String, Set<DdsMetricGauge>> guageOperationMetricMap;

    private Map<String, Set<DdsMetricHistogram>> histogramOperationMetricMap;

    private Map<String, Set<DdsMetricMeter>> meterOperationMetricMap;

    private Map<String, Set<DdsMetricTimer>> timerOperationMetricMap;

    public DdsMetricTableInfo(String tablename) {

        super();

        this.tablename = tablename;

        this.guageOperationMetricMap = new TreeMap<>();
        this.histogramOperationMetricMap = new TreeMap<>();
        this.meterOperationMetricMap = new TreeMap<>();
        this.timerOperationMetricMap = new TreeMap<>();

    }

    public String getTablename() {
        return tablename;
    }

    private Map<String, Set<DdsMetricGauge>> getGuageOperationMetricMap() {
        return guageOperationMetricMap;
    }

    private Map<String, Set<DdsMetricHistogram>> getHistogramOperationMetricMap() {
        return histogramOperationMetricMap;
    }

    private Map<String, Set<DdsMetricMeter>> getMeterOperationMetricMap() {
        return meterOperationMetricMap;
    }

    private Map<String, Set<DdsMetricTimer>> getTimerOperationMetricMap() {
        return timerOperationMetricMap;
    }

    public Map<String, TimeSeriesCollection> getTimeSeriesCollectionMap() {

        Map<String, TimeSeriesCollection> timeSeriesCollectionMap = new TreeMap<>();

        // GAUGE
        processGuageOperationMetricMap(timeSeriesCollectionMap);

        // HISTOGRAM
        processHistogramOperationMetricMap(timeSeriesCollectionMap);

        // METER
        processMeterOperationMetricMap(timeSeriesCollectionMap);

        // TIMER
        processTimerOperationMetricMap(timeSeriesCollectionMap);

        return timeSeriesCollectionMap;
    }

    private String getOperationMetricKey(DdsMetricType ddsMetricType, String operation, String metric) {

        StringBuilder keySB = new StringBuilder();

        keySB.append(ddsMetricType);
        keySB.append("-");
        keySB.append(operation);
        keySB.append("-");
        keySB.append(metric);

        return keySB.toString();
    }

    private void processGuageOperationMetricMap(Map<String, TimeSeriesCollection> timeSeriesCollectionMap) {

        Map<String, Set<DdsMetricGauge>> guageOperationMetricMap = getGuageOperationMetricMap();

        for (Entry<String, Set<DdsMetricGauge>> operationMetricEntry : guageOperationMetricMap.entrySet()) {

            String operationMetricKey = operationMetricEntry.getKey();

            Set<DdsMetricGauge> guageMetricSet = operationMetricEntry.getValue();

            TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();

            timeSeriesCollectionMap.put(operationMetricKey, timeSeriesCollection);

            TimeSeries valueTimeSeries = new TimeSeries("value");

            timeSeriesCollection.addSeries(valueTimeSeries);

            for (DdsMetricGauge ddsMetricGauge : guageMetricSet) {

                long logEntryTime = ddsMetricGauge.getLogEntryKey().getTimestamp();

                double value = ddsMetricGauge.getValue();

                RegularTimePeriod regularTimePeriod = new Millisecond(new Date(logEntryTime));

                TimeSeriesDataItem timeSeriesDataItem = new TimeSeriesDataItem(regularTimePeriod, value);

                valueTimeSeries.add(timeSeriesDataItem, false);
            }

        }

    }

    private void processHistogramOperationMetricMap(Map<String, TimeSeriesCollection> timeSeriesCollectionMap) {

        Map<String, Set<DdsMetricHistogram>> histogramOperationMetricMap = getHistogramOperationMetricMap();

        for (Entry<String, Set<DdsMetricHistogram>> operationMetricEntry : histogramOperationMetricMap.entrySet()) {

            String operationMetricKey = operationMetricEntry.getKey();

            Set<DdsMetricHistogram> histogramMetricSet = operationMetricEntry.getValue();

            TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();

            timeSeriesCollectionMap.put(operationMetricKey, timeSeriesCollection);

            TimeSeries countTimeSeries = new TimeSeries("count");
            TimeSeries minTimeSeries = new TimeSeries("min");
            TimeSeries maxTimeSeries = new TimeSeries("max");
            TimeSeries meanTimeSeries = new TimeSeries("mean");
            TimeSeries stddevTimeSeries = new TimeSeries("stddev");
            TimeSeries medianTimeSeries = new TimeSeries("median");
            TimeSeries p75TimeSeries = new TimeSeries("p75");
            TimeSeries p95TimeSeries = new TimeSeries("p95");
            TimeSeries p98TimeSeries = new TimeSeries("p98");
            TimeSeries p99TimeSeries = new TimeSeries("p99");
            TimeSeries p999TimeSeries = new TimeSeries("p999");

            timeSeriesCollection.addSeries(countTimeSeries);
            timeSeriesCollection.addSeries(minTimeSeries);
            timeSeriesCollection.addSeries(maxTimeSeries);
            timeSeriesCollection.addSeries(meanTimeSeries);
            timeSeriesCollection.addSeries(stddevTimeSeries);
            timeSeriesCollection.addSeries(medianTimeSeries);
            timeSeriesCollection.addSeries(p75TimeSeries);
            timeSeriesCollection.addSeries(p95TimeSeries);
            timeSeriesCollection.addSeries(p98TimeSeries);
            timeSeriesCollection.addSeries(p99TimeSeries);
            timeSeriesCollection.addSeries(p999TimeSeries);

            for (DdsMetricHistogram ddsMetricHistogram : histogramMetricSet) {

                long logEntryTime = ddsMetricHistogram.getLogEntryKey().getTimestamp();

                long count = ddsMetricHistogram.getCount();
                double min = ddsMetricHistogram.getMin();
                double max = ddsMetricHistogram.getMax();
                double mean = ddsMetricHistogram.getMean();
                double stddev = ddsMetricHistogram.getStddev();
                double median = ddsMetricHistogram.getMedian();
                double p75 = ddsMetricHistogram.getP75();
                double p95 = ddsMetricHistogram.getP95();
                double p98 = ddsMetricHistogram.getP98();
                double p99 = ddsMetricHistogram.getP99();
                double p999 = ddsMetricHistogram.getP999();

                RegularTimePeriod regularTimePeriod = new Millisecond(new Date(logEntryTime));

                TimeSeriesDataItem countDataItem = new TimeSeriesDataItem(regularTimePeriod, count);
                TimeSeriesDataItem minDataItem = new TimeSeriesDataItem(regularTimePeriod, min);
                TimeSeriesDataItem maxDataItem = new TimeSeriesDataItem(regularTimePeriod, max);
                TimeSeriesDataItem meanDataItem = new TimeSeriesDataItem(regularTimePeriod, mean);
                TimeSeriesDataItem stddevDataItem = new TimeSeriesDataItem(regularTimePeriod, stddev);
                TimeSeriesDataItem medianDataItem = new TimeSeriesDataItem(regularTimePeriod, median);
                TimeSeriesDataItem p75DataItem = new TimeSeriesDataItem(regularTimePeriod, p75);
                TimeSeriesDataItem p95DataItem = new TimeSeriesDataItem(regularTimePeriod, p95);
                TimeSeriesDataItem p98DataItem = new TimeSeriesDataItem(regularTimePeriod, p98);
                TimeSeriesDataItem p99DataItem = new TimeSeriesDataItem(regularTimePeriod, p99);
                TimeSeriesDataItem p999DataItem = new TimeSeriesDataItem(regularTimePeriod, p999);

                countTimeSeries.add(countDataItem, false);
                minTimeSeries.add(minDataItem, false);
                maxTimeSeries.add(maxDataItem, false);
                meanTimeSeries.add(meanDataItem, false);
                stddevTimeSeries.add(stddevDataItem, false);
                medianTimeSeries.add(medianDataItem, false);
                p75TimeSeries.add(p75DataItem, false);
                p95TimeSeries.add(p95DataItem, false);
                p98TimeSeries.add(p98DataItem, false);
                p99TimeSeries.add(p99DataItem, false);
                p999TimeSeries.add(p999DataItem, false);

            }

        }

    }

    private void processMeterOperationMetricMap(Map<String, TimeSeriesCollection> timeSeriesCollectionMap) {

        Map<String, Set<DdsMetricMeter>> meterOperationMetricMap = getMeterOperationMetricMap();

        for (Entry<String, Set<DdsMetricMeter>> operationMetricEntry : meterOperationMetricMap.entrySet()) {

            String operationMetricKey = operationMetricEntry.getKey();

            Set<DdsMetricMeter> meterMetricSet = operationMetricEntry.getValue();

            TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();

            timeSeriesCollectionMap.put(operationMetricKey, timeSeriesCollection);

            TimeSeries countTimeSeries = new TimeSeries("count");
            TimeSeries meanRateTimeSeries = new TimeSeries("mean_rate");
            TimeSeries m1TimeSeries = new TimeSeries("m1");
            TimeSeries m5TimeSeries = new TimeSeries("m5");
            TimeSeries m15TimeSeries = new TimeSeries("m15");

            timeSeriesCollection.addSeries(countTimeSeries);
            timeSeriesCollection.addSeries(meanRateTimeSeries);
            timeSeriesCollection.addSeries(m1TimeSeries);
            timeSeriesCollection.addSeries(m5TimeSeries);
            timeSeriesCollection.addSeries(m15TimeSeries);

            for (DdsMetricMeter ddsMetricMeter : meterMetricSet) {

                long logEntryTime = ddsMetricMeter.getLogEntryKey().getTimestamp();

                long count = ddsMetricMeter.getCount();
                double meanRate = ddsMetricMeter.getMeanRate();
                double m1 = ddsMetricMeter.getM1();
                double m5 = ddsMetricMeter.getM5();
                double m15 = ddsMetricMeter.getM15();

                RegularTimePeriod regularTimePeriod = new Millisecond(new Date(logEntryTime));

                TimeSeriesDataItem countDataItem = new TimeSeriesDataItem(regularTimePeriod, count);
                TimeSeriesDataItem meanRateDataItem = new TimeSeriesDataItem(regularTimePeriod, meanRate);
                TimeSeriesDataItem m1DataItem = new TimeSeriesDataItem(regularTimePeriod, m1);
                TimeSeriesDataItem m5DataItem = new TimeSeriesDataItem(regularTimePeriod, m5);
                TimeSeriesDataItem m15DataItem = new TimeSeriesDataItem(regularTimePeriod, m15);

                countTimeSeries.add(countDataItem, false);
                meanRateTimeSeries.add(meanRateDataItem, false);
                m1TimeSeries.add(m1DataItem, false);
                m5TimeSeries.add(m5DataItem, false);
                m15TimeSeries.add(m15DataItem, false);

            }

        }

    }

    private void processTimerOperationMetricMap(Map<String, TimeSeriesCollection> timeSeriesCollectionMap) {

        Map<String, Set<DdsMetricTimer>> timerOperationMetricMap = getTimerOperationMetricMap();

        for (Entry<String, Set<DdsMetricTimer>> operationMetricEntry : timerOperationMetricMap.entrySet()) {

            String operationMetricKey = operationMetricEntry.getKey();

            Set<DdsMetricTimer> timerMetricSet = operationMetricEntry.getValue();

            TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();

            timeSeriesCollectionMap.put(operationMetricKey, timeSeriesCollection);

            TimeSeries countTimeSeries = new TimeSeries("count");
            TimeSeries minTimeSeries = new TimeSeries("min");
            TimeSeries maxTimeSeries = new TimeSeries("max");
            TimeSeries meanTimeSeries = new TimeSeries("mean");
            TimeSeries stddevTimeSeries = new TimeSeries("stddev");
            TimeSeries medianTimeSeries = new TimeSeries("median");
            TimeSeries p75TimeSeries = new TimeSeries("p75");
            TimeSeries p95TimeSeries = new TimeSeries("p95");
            TimeSeries p98TimeSeries = new TimeSeries("p98");
            TimeSeries p99TimeSeries = new TimeSeries("p99");
            TimeSeries p999TimeSeries = new TimeSeries("p999");
            TimeSeries meanRateTimeSeries = new TimeSeries("mean_rate");
            TimeSeries m1TimeSeries = new TimeSeries("m1");
            TimeSeries m5TimeSeries = new TimeSeries("m5");
            TimeSeries m15TimeSeries = new TimeSeries("m15");

            timeSeriesCollection.addSeries(countTimeSeries);
            timeSeriesCollection.addSeries(minTimeSeries);
            timeSeriesCollection.addSeries(maxTimeSeries);
            timeSeriesCollection.addSeries(meanTimeSeries);
            timeSeriesCollection.addSeries(stddevTimeSeries);
            timeSeriesCollection.addSeries(medianTimeSeries);
            timeSeriesCollection.addSeries(p75TimeSeries);
            timeSeriesCollection.addSeries(p95TimeSeries);
            timeSeriesCollection.addSeries(p98TimeSeries);
            timeSeriesCollection.addSeries(p99TimeSeries);
            timeSeriesCollection.addSeries(p999TimeSeries);
            timeSeriesCollection.addSeries(meanRateTimeSeries);
            timeSeriesCollection.addSeries(m1TimeSeries);
            timeSeriesCollection.addSeries(m5TimeSeries);
            timeSeriesCollection.addSeries(m15TimeSeries);

            for (DdsMetricTimer ddsMetricTimer : timerMetricSet) {

                long logEntryTime = ddsMetricTimer.getLogEntryKey().getTimestamp();

                long count = ddsMetricTimer.getCount();
                double min = ddsMetricTimer.getMin();
                double max = ddsMetricTimer.getMax();
                double mean = ddsMetricTimer.getMean();
                double stddev = ddsMetricTimer.getStddev();
                double median = ddsMetricTimer.getMedian();
                double p75 = ddsMetricTimer.getP75();
                double p95 = ddsMetricTimer.getP75();
                double p98 = ddsMetricTimer.getP98();
                double p99 = ddsMetricTimer.getP99();
                double p999 = ddsMetricTimer.getP999();
                double meanRate = ddsMetricTimer.getMeanRate();
                double m1 = ddsMetricTimer.getM1();
                double m5 = ddsMetricTimer.getM5();
                double m15 = ddsMetricTimer.getM15();

                RegularTimePeriod regularTimePeriod = new Millisecond(new Date(logEntryTime));

                TimeSeriesDataItem countDataItem = new TimeSeriesDataItem(regularTimePeriod, count);
                TimeSeriesDataItem minDataItem = new TimeSeriesDataItem(regularTimePeriod, min);
                TimeSeriesDataItem maxDataItem = new TimeSeriesDataItem(regularTimePeriod, max);
                TimeSeriesDataItem meanDataItem = new TimeSeriesDataItem(regularTimePeriod, mean);
                TimeSeriesDataItem stddevDataItem = new TimeSeriesDataItem(regularTimePeriod, stddev);
                TimeSeriesDataItem medianDataItem = new TimeSeriesDataItem(regularTimePeriod, median);
                TimeSeriesDataItem p75DataItem = new TimeSeriesDataItem(regularTimePeriod, p75);
                TimeSeriesDataItem p95DataItem = new TimeSeriesDataItem(regularTimePeriod, p95);
                TimeSeriesDataItem p98DataItem = new TimeSeriesDataItem(regularTimePeriod, p98);
                TimeSeriesDataItem p99DataItem = new TimeSeriesDataItem(regularTimePeriod, p99);
                TimeSeriesDataItem p999DataItem = new TimeSeriesDataItem(regularTimePeriod, p999);
                TimeSeriesDataItem meanRateDataItem = new TimeSeriesDataItem(regularTimePeriod, meanRate);
                TimeSeriesDataItem m1DataItem = new TimeSeriesDataItem(regularTimePeriod, m1);
                TimeSeriesDataItem m5DataItem = new TimeSeriesDataItem(regularTimePeriod, m5);
                TimeSeriesDataItem m15DataItem = new TimeSeriesDataItem(regularTimePeriod, m15);

                countTimeSeries.add(countDataItem, false);
                minTimeSeries.add(minDataItem, false);
                maxTimeSeries.add(maxDataItem, false);
                meanTimeSeries.add(meanDataItem, false);
                stddevTimeSeries.add(stddevDataItem, false);
                medianTimeSeries.add(medianDataItem, false);
                p75TimeSeries.add(p75DataItem, false);
                p95TimeSeries.add(p95DataItem, false);
                p98TimeSeries.add(p98DataItem, false);
                p99TimeSeries.add(p99DataItem, false);
                p999TimeSeries.add(p999DataItem, false);
                meanRateTimeSeries.add(meanRateDataItem, false);
                m1TimeSeries.add(m1DataItem, false);
                m5TimeSeries.add(m5DataItem, false);
                m15TimeSeries.add(m15DataItem, false);

            }

        }

    }

    public void addDdsMetric(String operation, String metric, DdsMetric ddsMetric) {

        DdsMetricType ddsMetricType = ddsMetric.getType();

        String operationMetricKey = getOperationMetricKey(ddsMetricType, operation, metric);

        switch (ddsMetricType) {

        case GAUGE:

            Map<String, Set<DdsMetricGauge>> guageOperationMetricMap = getGuageOperationMetricMap();

            Set<DdsMetricGauge> guageMetricSet = guageOperationMetricMap.get(operationMetricKey);

            if (guageMetricSet == null) {
                guageMetricSet = new TreeSet<>();
                guageOperationMetricMap.put(operationMetricKey, guageMetricSet);
            }

            guageMetricSet.add((DdsMetricGauge) ddsMetric);

            break;

        case HISTOGRAM:

            Map<String, Set<DdsMetricHistogram>> histogramOperationMetricMap = getHistogramOperationMetricMap();

            Set<DdsMetricHistogram> histogramMetricSet = histogramOperationMetricMap.get(operationMetricKey);

            if (histogramMetricSet == null) {
                histogramMetricSet = new TreeSet<>();
                histogramOperationMetricMap.put(operationMetricKey, histogramMetricSet);
            }

            histogramMetricSet.add((DdsMetricHistogram) ddsMetric);

            break;

        case METER:

            Map<String, Set<DdsMetricMeter>> meterOperationMetricMap = getMeterOperationMetricMap();

            Set<DdsMetricMeter> meterMetricSet = meterOperationMetricMap.get(operationMetricKey);

            if (meterMetricSet == null) {
                meterMetricSet = new TreeSet<>();
                meterOperationMetricMap.put(operationMetricKey, meterMetricSet);
            }

            meterMetricSet.add((DdsMetricMeter) ddsMetric);

            break;

        case TIMER:

            Map<String, Set<DdsMetricTimer>> timerOperationMetricMap = getTimerOperationMetricMap();

            Set<DdsMetricTimer> timerMetricSet = timerOperationMetricMap.get(operationMetricKey);

            if (timerMetricSet == null) {
                timerMetricSet = new TreeSet<>();
                timerOperationMetricMap.put(operationMetricKey, timerMetricSet);
            }

            timerMetricSet.add((DdsMetricTimer) ddsMetric);

            break;

        default:
            break;

        }
    }

}
