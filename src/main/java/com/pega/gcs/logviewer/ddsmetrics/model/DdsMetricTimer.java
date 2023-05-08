
package com.pega.gcs.logviewer.ddsmetrics.model;

import com.pega.gcs.logviewer.model.LogEntryKey;

public class DdsMetricTimer extends DdsMetric {

    private long count;

    private double min;

    private double max;

    private double mean;

    private double stddev;

    private double median;

    private double p75;

    private double p95;

    private double p98;

    private double p99;

    private double p999;

    private double meanRate;

    private double m1;

    private double m5;

    private double m15;

    private String rateUnit;

    private String durationUnit;

    public DdsMetricTimer(LogEntryKey logEntryKey, long count, double min, double max, double mean, double stddev,
            double median, double p75, double p95, double p98, double p99, double p999, double meanRate, double m1,
            double m5, double m15, String rateUnit, String durationUnit) {

        super(logEntryKey);

        this.count = count;
        this.min = min;
        this.max = max;
        this.mean = mean;
        this.stddev = stddev;
        this.median = median;
        this.p75 = p75;
        this.p95 = p95;
        this.p98 = p98;
        this.p99 = p99;
        this.p999 = p999;
        this.meanRate = meanRate;
        this.m1 = m1;
        this.m5 = m5;
        this.m15 = m15;
        this.rateUnit = rateUnit;
        this.durationUnit = durationUnit;
    }

    public long getCount() {
        return count;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getMean() {
        return mean;
    }

    public double getStddev() {
        return stddev;
    }

    public double getMedian() {
        return median;
    }

    public double getP75() {
        return p75;
    }

    public double getP95() {
        return p95;
    }

    public double getP98() {
        return p98;
    }

    public double getP99() {
        return p99;
    }

    public double getP999() {
        return p999;
    }

    public double getMeanRate() {
        return meanRate;
    }

    public double getM1() {
        return m1;
    }

    public double getM5() {
        return m5;
    }

    public double getM15() {
        return m15;
    }

    public String getRateUnit() {
        return rateUnit;
    }

    public String getDurationUnit() {
        return durationUnit;
    }

    @Override
    public DdsMetricType getType() {
        return DdsMetricType.TIMER;
    }
}
