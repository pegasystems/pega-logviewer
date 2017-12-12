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

public class CombinedDomainXYPlotMouseListener implements ChartMouseListener {

	private static final Log4j2Helper LOG = new Log4j2Helper(CombinedDomainXYPlotMouseListener.class);

	private ChartPanel chartPanel;

	private LogTableModel logTableModel;

	private NavigationTableController<Integer> navigationTableController;

	public CombinedDomainXYPlotMouseListener(ChartPanel chartPanel, LogTableModel logTableModel,
			NavigationTableController<Integer> navigationTableController) {

		super();

		this.chartPanel = chartPanel;
		this.logTableModel = logTableModel;
		this.navigationTableController = navigationTableController;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jfree.chart.ChartMouseListener#chartMouseClicked(org.jfree.chart.
	 * ChartMouseEvent)
	 */
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

			Integer logEntryIndex = logTableModel.getClosestLogEntryIndex(logEntryTimestamp);

			if (logEntryIndex != null) {
				navigationTableController.scrollToKey(logEntryIndex);
			}
			// }

		} catch (Exception e) {
			LOG.error("Error handling mouse click", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jfree.chart.ChartMouseListener#chartMouseMoved(org.jfree.chart.
	 * ChartMouseEvent)
	 */
	@Override
	public void chartMouseMoved(ChartMouseEvent event) {

	}

}
