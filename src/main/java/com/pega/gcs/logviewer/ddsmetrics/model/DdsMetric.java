
package com.pega.gcs.logviewer.ddsmetrics.model;

import com.pega.gcs.logviewer.model.LogEntryKey;

public abstract class DdsMetric implements Comparable<DdsMetric> {

    private LogEntryKey logEntryKey;

    public abstract DdsMetricType getType();

    public DdsMetric(LogEntryKey logEntryKey) {

        super();

        this.logEntryKey = logEntryKey;

    }

    public LogEntryKey getLogEntryKey() {
        return logEntryKey;
    }

    @Override
    public int compareTo(DdsMetric other) {
        long thisTimestamp = getLogEntryKey().getTimestamp();
        long otherTimestamp = other.getLogEntryKey().getTimestamp();

        return Long.compare(thisTimestamp, otherTimestamp);
    }

}
