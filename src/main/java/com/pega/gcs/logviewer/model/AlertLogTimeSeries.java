
package com.pega.gcs.logviewer.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.time.TimeSeriesDataItem;

public class AlertLogTimeSeries extends LogTimeSeries {

    private long thresholdKPI;

    private String kpiUnit;

    private List<Double> valueList;

    private boolean rebuildAlertBoxAndWhiskerItem;

    private AlertBoxAndWhiskerItem alertBoxAndWhiskerItem;

    public AlertLogTimeSeries(String timeSeriesName, Color color, ValueMarker thresholdMarker, boolean showCount,
            boolean defaultShowLogTimeSeries, long thresholdKPI, String kpiUnit) {

        super(timeSeriesName, color, thresholdMarker, showCount, defaultShowLogTimeSeries);

        this.thresholdKPI = thresholdKPI;
        this.kpiUnit = kpiUnit;

        this.valueList = new ArrayList<>();
        this.rebuildAlertBoxAndWhiskerItem = false;
    }

    @Override
    public void addTimeSeriesDataItem(TimeSeriesDataItem timeSeriesDataItem) {
        super.addTimeSeriesDataItem(timeSeriesDataItem);

        Double value = (Double) timeSeriesDataItem.getValue();

        valueList.add(value);

        rebuildAlertBoxAndWhiskerItem = true;
    }

    @Override
    public AlertBoxAndWhiskerItem getBoxAndWhiskerItem() {

        if ((alertBoxAndWhiskerItem == null) || rebuildAlertBoxAndWhiskerItem) {

            Collections.sort(valueList);

            alertBoxAndWhiskerItem = AlertBoxAndWhiskerCalculator.calculateStatistics(valueList, thresholdKPI, kpiUnit);

            rebuildAlertBoxAndWhiskerItem = false;
        }

        return alertBoxAndWhiskerItem;
    }
}
