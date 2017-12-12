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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.PlotRenderingInfo;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class CombinedDomainCategoryPlotMouseListener implements ChartMouseListener {

	private static final Log4j2Helper LOG = new Log4j2Helper(CombinedDomainCategoryPlotMouseListener.class);

	private ChartPanel boxAndWiskerPanel;

	private LogTable logTable;

	public CombinedDomainCategoryPlotMouseListener(ChartPanel boxAndWiskerPanel, LogTable logTable) {
		super();
		this.boxAndWiskerPanel = boxAndWiskerPanel;
		this.logTable = logTable;
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

			CombinedDomainCategoryPlot combinedDomainCategoryPlot = (CombinedDomainCategoryPlot) event.getChart()
					.getPlot();
			ChartRenderingInfo chartInfo = boxAndWiskerPanel.getChartRenderingInfo();

			Point2D java2DPoint = boxAndWiskerPanel.translateScreenToJava2D(mousePoint);

			PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();

			int subplotIndex = plotInfo.getSubplotIndex(java2DPoint);

			if (subplotIndex >= 0) {

				Rectangle2D dataArea = plotInfo.getDataArea();

				CategoryPlot categoryPlot = (CategoryPlot) combinedDomainCategoryPlot.getSubplots().get(subplotIndex);

				ValueAxis rangeAxis = categoryPlot.getRangeAxis();

				long logEntryTimestamp = (long) rangeAxis.java2DToValue(java2DPoint.getY(), dataArea,
						categoryPlot.getRangeAxisEdge());

				LOG.info("subplotIndex: " + subplotIndex + " logEntryTimestamp: " + logEntryTimestamp);

				// int rowNumber = ltm
				// .getClosestLogEntryRowIndex(logEntryTimestamp);
				//
				// if (rowNumber >= 0) {
				// logTable.setRowSelectionInterval(rowNumber, rowNumber);
				// logTable.scrollRowToVisible(rowNumber);
				// }
			}

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
