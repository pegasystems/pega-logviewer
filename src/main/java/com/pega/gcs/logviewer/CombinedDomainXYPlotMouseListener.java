/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;

import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogEntryModel;

public class CombinedDomainXYPlotMouseListener implements ChartMouseListener {

    private static final Log4j2Helper LOG = new Log4j2Helper(CombinedDomainXYPlotMouseListener.class);

    private ChartPanel chartPanel;

    private LogEntryModel logEntryModel;

    private NavigationTableController<LogEntryKey> navigationTableController;

    public CombinedDomainXYPlotMouseListener(ChartPanel chartPanel, LogEntryModel logEntryModel,
            NavigationTableController<LogEntryKey> navigationTableController) {

        super();

        this.chartPanel = chartPanel;
        this.logEntryModel = logEntryModel;
        this.navigationTableController = navigationTableController;
    }

    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        try {

            int mouseX = event.getTrigger().getX();
            int mouseY = event.getTrigger().getY();

            Point mousePoint = new Point(mouseX, mouseY);

            XYPlot xyPlot = (XYPlot) event.getChart().getPlot();
            ChartRenderingInfo chartInfo = chartPanel.getChartRenderingInfo();

            Point2D java2DPoint = chartPanel.translateScreenToJava2D(mousePoint);

            PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();

            Rectangle2D dataArea = plotInfo.getDataArea();

            ValueAxis domainAxis = xyPlot.getDomainAxis();

            long logEntryTimestamp = (long) domainAxis.java2DToValue(java2DPoint.getX(), dataArea,
                    xyPlot.getDomainAxisEdge());

            LogEntryKey logEntryKey = logEntryModel.getClosestLogEntryKeyForTime(logEntryTimestamp);

            if (logEntryKey != null) {
                navigationTableController.scrollToKey(logEntryKey);
            }

        } catch (Exception e) {
            LOG.error("Error handling mouse click", e);
        }
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent event) {

    }

}
