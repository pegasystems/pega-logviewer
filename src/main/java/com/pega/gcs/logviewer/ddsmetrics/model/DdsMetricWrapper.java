
package com.pega.gcs.logviewer.ddsmetrics.model;

public class DdsMetricWrapper {

    private String tablename;

    private String operation;

    private String metric;

    private DdsMetric ddsMetric;

    public DdsMetricWrapper(String tablename, String operation, String metric, DdsMetric ddsMetric) {
        super();
        this.tablename = tablename;
        this.operation = operation;
        this.metric = metric;
        this.ddsMetric = ddsMetric;
    }

    public String getTablename() {
        return tablename;
    }

    public String getOperation() {
        return operation;
    }

    public String getMetric() {
        return metric;
    }

    public DdsMetric getDdsMetric() {
        return ddsMetric;
    }

}
