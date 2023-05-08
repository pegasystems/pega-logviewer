
package com.pega.gcs.logviewer.ddsmetrics.model;

import com.pega.gcs.logviewer.model.LogEntryKey;

public class DdsMetricGauge extends DdsMetric {

    private double value;

    public DdsMetricGauge(LogEntryKey logEntryKey, double value) {
        super(logEntryKey);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public DdsMetricType getType() {
        return DdsMetricType.GAUGE;
    }
}
