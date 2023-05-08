
package com.pega.gcs.logviewer.ddsmetrics.model;

import com.pega.gcs.logviewer.model.LogEntryKey;

public class DdsMetricMeter extends DdsMetric {

    private long count;

    private double meanRate;

    private double m1;

    private double m5;

    private double m15;

    private String rateUnit;

    public DdsMetricMeter(LogEntryKey logEntryKey, long count, double meanRate, double m1, double m5, double m15,
            String rateUnit) {

        super(logEntryKey);

        this.count = count;
        this.meanRate = meanRate;
        this.m1 = m1;
        this.m5 = m5;
        this.m15 = m15;
        this.rateUnit = rateUnit;
    }

    public long getCount() {
        return count;
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

    @Override
    public DdsMetricType getType() {
        return DdsMetricType.METER;
    }
}
