
package com.pega.gcs.logviewer.ddsmetrics.model;

import com.pega.gcs.logviewer.model.LogEntryKey;

public class DdsMetricHistogram extends DdsMetric {

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

    public DdsMetricHistogram(LogEntryKey logEntryKey, long count, double min, double max, double mean, double stddev,
            double median, double p75, double p95, double p98, double p99, double p999) {

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

    @Override
    public DdsMetricType getType() {
        return DdsMetricType.HISTOGRAM;
    }

}
