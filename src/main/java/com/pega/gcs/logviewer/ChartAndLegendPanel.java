/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.HorizontalAlignment;

import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.guiutilities.search.SearchPanel;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.LogEntry;
import com.pega.gcs.logviewer.model.LogEntryModel;
import com.pega.gcs.logviewer.model.LogIntervalMarker;
import com.pega.gcs.logviewer.model.LogSeries;
import com.pega.gcs.logviewer.model.LogSeriesCollection;

public class ChartAndLegendPanel extends JPanel implements ListSelectionListener, TableModelListener {

	private static final long serialVersionUID = 459528916499269469L;

	private static final Log4j2Helper LOG = new Log4j2Helper(ChartAndLegendPanel.class);

	private String filename;

	private LogTable logTable;

	private SearchPanel<Integer> searchPanel;

	private CombinedDomainXYPlot combinedDomainXYPlot;

	private HashMap<String, XYPlot> xyPlotMap;

	private HashMap<String, CategoryPlot> categoryPlotMap;

	// private HashMap<String, LogIntervalMarker> logIntervalMarkerMap;

	private HashMap<String, LogSeriesCollectionCheckBoxPanel> logSeriesCollectionCheckBoxPanelMap;

	private CombinedDomainCategoryPlot combinedBoxAndWiskerCategoryPlot;

	private ChartPanel chartPanel;

	private ChartPanel boxAndWiskerPanel;

	private JPanel legendPanel;

	private boolean dataUpdated;

	private List<IntervalMarker> manualIntervalMarkerList;

	// prevent the last plot legend to uncheck by keeping track.
	// private List<LogSeriesCollectionCheckBoxPanel> plotLegendPanelList;

	public ChartAndLegendPanel(String filename, LogTable logTable, SearchPanel<Integer> searchPanel) {

		super();

		this.filename = filename;
		this.logTable = logTable;
		this.searchPanel = searchPanel;

		this.xyPlotMap = new HashMap<>();
		this.categoryPlotMap = new HashMap<>();
		// this.logIntervalMarkerMap = new HashMap<>();
		this.logSeriesCollectionCheckBoxPanelMap = new HashMap<>();

		this.dataUpdated = false;
		this.manualIntervalMarkerList = new LinkedList<IntervalMarker>();

		ListSelectionModel lsm = logTable.getSelectionModel();
		lsm.addListSelectionListener(this);

		setLayout(new GridBagLayout());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weightx = 1.0D;
		gbc1.weighty = 1.0D;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.insets = new Insets(0, 0, 0, 0);

		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 1;
		gbc2.gridy = 0;
		gbc2.weightx = 0.0D;
		gbc2.weighty = 1.0D;
		gbc2.fill = GridBagConstraints.BOTH;
		gbc2.anchor = GridBagConstraints.NORTHWEST;
		gbc2.insets = new Insets(0, 0, 0, 0);

		GridBagConstraints gbc3 = new GridBagConstraints();
		gbc3.gridx = 2;
		gbc3.gridy = 0;
		gbc3.weightx = 0.0D;
		gbc3.weighty = 1.0D;
		gbc3.fill = GridBagConstraints.BOTH;
		gbc3.anchor = GridBagConstraints.NORTHWEST;
		gbc3.insets = new Insets(0, 0, 0, 0);

		JPanel chartPanel = getChartPanel();
		JPanel boxAndWiskerPanel = getBoxAndWiskerPanel();
		JPanel legendCompositeJPanel = getLegendCompositeJPanel();

		add(chartPanel, gbc1);
		add(boxAndWiskerPanel, gbc2);
		add(legendCompositeJPanel, gbc3);
	}

	/**
	 * @return the combinedDomainXYPlot
	 */
	private CombinedDomainXYPlot getCombinedDomainXYPlot() {

		if (combinedDomainXYPlot == null) {
			DateAxis domainAxis = new DateAxis("Time (-NA-)");
			domainAxis.setLowerMargin(0.02);
			domainAxis.setUpperMargin(0.02);

			Font labelFont = new Font("Arial", Font.PLAIN, 10);
			domainAxis.setLabelFont(labelFont);

			combinedDomainXYPlot = new CombinedDomainXYPlot(domainAxis);
			combinedDomainXYPlot.setGap(5.0);
			combinedDomainXYPlot.setOrientation(PlotOrientation.VERTICAL);
		}

		return combinedDomainXYPlot;
	}

	/**
	 * @return the combinedBoxAndWiskerCategoryPlot
	 */
	private CombinedDomainCategoryPlot getCombinedBoxAndWiskerCategoryPlot() {

		if (combinedBoxAndWiskerCategoryPlot == null) {

			CategoryAxis categoryAxis = new CategoryAxis("Box Plot");
			categoryAxis.setLowerMargin(0.01);
			categoryAxis.setUpperMargin(0.01);
			categoryAxis.setCategoryLabelPositionOffset(14);

			combinedBoxAndWiskerCategoryPlot = new CombinedDomainCategoryPlot(categoryAxis);
			combinedBoxAndWiskerCategoryPlot.setGap(5.0);
			combinedBoxAndWiskerCategoryPlot.setOrientation(PlotOrientation.VERTICAL);
		}

		return combinedBoxAndWiskerCategoryPlot;
	}

	private ChartPanel getChartPanel() {

		if (chartPanel == null) {

			CombinedDomainXYPlot combinedDomainXYPlot = getCombinedDomainXYPlot();

			JFreeChart jFreeChart = new JFreeChart(filename, JFreeChart.DEFAULT_TITLE_FONT, combinedDomainXYPlot,
					false);

			ChartUtilities.applyCurrentTheme(jFreeChart);

			// customise the title position and font
			TextTitle t = jFreeChart.getTitle();
			t.setHorizontalAlignment(HorizontalAlignment.CENTER);
			t.setPaint(Color.DARK_GRAY);
			t.setFont(new Font("Arial", Font.BOLD, 12));
			t.setPadding(10, 10, 5, 10);

			chartPanel = new ChartPanel(jFreeChart);

			chartPanel.setMinimumDrawWidth(0);
			chartPanel.setMinimumDrawHeight(0);
			chartPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
			chartPanel.setMaximumDrawHeight(Integer.MAX_VALUE);
			chartPanel.setMouseWheelEnabled(true);

			LogTableModel logTableModel = (LogTableModel) logTable.getModel();

			NavigationTableController<Integer> navigationTableController;
			navigationTableController = new NavigationTableController<Integer>(logTableModel);
			navigationTableController.addCustomJTable(logTable);

			chartPanel.addChartMouseListener(
					new CombinedDomainXYPlotMouseListener(chartPanel, logTableModel, navigationTableController));

			chartPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		}

		return chartPanel;
	}

	/**
	 * @return the boxAndWiskerPanel
	 */
	private ChartPanel getBoxAndWiskerPanel() {

		if (boxAndWiskerPanel == null) {

			CombinedDomainCategoryPlot combinedBoxAndWiskerCategoryPlot = getCombinedBoxAndWiskerCategoryPlot();

			JFreeChart jFreeChart = new JFreeChart("  ", JFreeChart.DEFAULT_TITLE_FONT,
					combinedBoxAndWiskerCategoryPlot, false);

			ChartUtilities.applyCurrentTheme(jFreeChart);

			TextTitle t = jFreeChart.getTitle();
			t.setHorizontalAlignment(HorizontalAlignment.CENTER);
			t.setPaint(Color.DARK_GRAY);
			t.setFont(new Font("Arial", Font.BOLD, 12));
			t.setPadding(10, 10, 5, 10);

			boxAndWiskerPanel = new ChartPanel(jFreeChart);

			Dimension preferredSize = new Dimension(140, 200);

			boxAndWiskerPanel.setMinimumSize(preferredSize);
			boxAndWiskerPanel.setPreferredSize(preferredSize);

			boxAndWiskerPanel.setMinimumDrawWidth(0);
			boxAndWiskerPanel.setMinimumDrawHeight(0);
			boxAndWiskerPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
			boxAndWiskerPanel.setMaximumDrawHeight(Integer.MAX_VALUE);
			boxAndWiskerPanel.setMouseWheelEnabled(true);

			boxAndWiskerPanel.addChartMouseListener(new CombinedDomainCategoryPlotMouseListener(chartPanel, logTable));

			boxAndWiskerPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

			// TODO revert when functionality done
			boxAndWiskerPanel.setVisible(true);

		}

		return boxAndWiskerPanel;
	}

	/**
	 * @return the legendPanel
	 */
	private JPanel getLegendPanel() {

		if (legendPanel == null) {
			legendPanel = new JPanel();

			legendPanel.setLayout(new GridBagLayout());

			Dimension preferredSize = new Dimension(160, 200);

			legendPanel.setMinimumSize(preferredSize);
			legendPanel.setPreferredSize(preferredSize);

		}

		return legendPanel;
	}

	private JPanel getLegendCompositeJPanel() {

		JPanel legendCompositeJPanel = new JPanel();

		legendCompositeJPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weightx = 1.0D;
		gbc1.weighty = 0.0D;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.insets = new Insets(0, 0, 0, 0);

		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 0;
		gbc2.gridy = 1;
		gbc2.weightx = 1.0D;
		gbc2.weighty = 1.0D;
		gbc2.fill = GridBagConstraints.BOTH;
		gbc2.anchor = GridBagConstraints.NORTHWEST;
		gbc2.insets = new Insets(0, 0, 0, 0);

		JPanel legendHeaderPanel = getLegendHeaderPanel();
		JPanel legendPanel = getLegendPanel();

		legendCompositeJPanel.add(legendHeaderPanel, gbc1);
		legendCompositeJPanel.add(legendPanel, gbc2);

		return legendCompositeJPanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);

		if (aFlag && dataUpdated) {
			LogTableModel ltm = (LogTableModel) logTable.getModel();
			refreshChart(ltm);

		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void valueChanged(ListSelectionEvent e) {

		if (!e.getValueIsAdjusting()) {

			searchPanel.updateSearchNavIndexDetails();

			LogTableModel ltm = (LogTableModel) logTable.getModel();

			int[] selectedRows = logTable.getSelectedRows();

			CombinedDomainXYPlot combinedDomainXYPlot = getCombinedDomainXYPlot();

			for (IntervalMarker im : manualIntervalMarkerList) {

				for (XYPlot subPlot : (List<XYPlot>) combinedDomainXYPlot.getSubplots()) {

					subPlot.removeDomainMarker(im);
				}
			}

			manualIntervalMarkerList.clear();

			for (int row : selectedRows) {

				try {

					LogEntry logEntry = (LogEntry) ltm.getValueAt(row, 0);

					Date logEntryDate = logEntry.getLogEntryDate();

					if (logEntryDate != null) {
						long logEntryTime = logEntryDate.getTime();
						Color color = Color.BLACK;

						IntervalMarker im;
						// im = new IntervalMarker(logEntryTime, logEntryTime);
						im = new IntervalMarker(logEntryTime, logEntryTime, color, new BasicStroke(0.6f), color,
								new BasicStroke(0.6f), 0.8f);

						for (XYPlot subPlot : (List<XYPlot>) combinedDomainXYPlot.getSubplots()) {

							subPlot.addDomainMarker(im);
						}

						manualIntervalMarkerList.add(im);
					}
				} catch (Exception e1) {
					LOG.error("Error adding interval marker.", e1);
				}
			}

		}

	}

	@Override
	public void tableChanged(TableModelEvent e) {

		if (e.getType() == TableModelEvent.UPDATE) {
			LogTableModel logTableModel = (LogTableModel) e.getSource();

			if (isVisible()) {
				refreshChart(logTableModel);
			} else {
				dataUpdated = true;
			}
		}
	}

	protected void processLogSeriesCollectionCheckBoxPanelMap() {

		int counter = 0;
		LogSeriesCollectionCheckBoxPanel lastLogSeriesCollectionCheckBoxPanel = null;

		for (String logSeriesCollectionName : logSeriesCollectionCheckBoxPanelMap.keySet()) {

			LogSeriesCollectionCheckBoxPanel logSeriesCollectionCheckBoxPanel = logSeriesCollectionCheckBoxPanelMap
					.get(logSeriesCollectionName);

			JCheckBox checkbox = logSeriesCollectionCheckBoxPanel.getjCheckBox();

			if (checkbox.isSelected()) {
				counter++;
				lastLogSeriesCollectionCheckBoxPanel = logSeriesCollectionCheckBoxPanel;
			}
		}

		if ((counter == 1) && (lastLogSeriesCollectionCheckBoxPanel != null)) {

			JCheckBox checkbox = lastLogSeriesCollectionCheckBoxPanel.getjCheckBox();
			checkbox.setEnabled(false);

		} else {

			for (String logSeriesCollectionName : logSeriesCollectionCheckBoxPanelMap.keySet()) {

				LogSeriesCollectionCheckBoxPanel logSeriesCollectionCheckBoxPanel = logSeriesCollectionCheckBoxPanelMap
						.get(logSeriesCollectionName);

				JCheckBox checkbox = logSeriesCollectionCheckBoxPanel.getjCheckBox();

				checkbox.setEnabled(true);
			}
		}

	}

	private JPanel getLegendHeaderPanel() {

		JPanel legendHeaderPanel = new JPanel();

		legendHeaderPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weightx = 0.0D;
		gbc1.weighty = 1.0D;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.insets = new Insets(0, 0, 0, 0);

		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 1;
		gbc2.gridy = 0;
		gbc2.weightx = 1.0D;
		gbc2.weighty = 1.0D;
		gbc2.fill = GridBagConstraints.BOTH;
		gbc2.anchor = GridBagConstraints.NORTHWEST;
		gbc2.insets = new Insets(0, 0, 0, 0);

		GridBagConstraints gbc3 = new GridBagConstraints();
		gbc3.gridx = 2;
		gbc3.gridy = 0;
		gbc3.weightx = 0.0D;
		gbc3.weighty = 1.0D;
		gbc3.fill = GridBagConstraints.BOTH;
		gbc3.anchor = GridBagConstraints.NORTHWEST;
		gbc3.insets = new Insets(0, 0, 0, 0);

		JPanel legendHeaderCheckBoxJPanel = getLegendHeaderCheckBoxJPanel();
		JPanel legendHeaderLegendJPanel = getLegendHeaderLegendJPanel();
		JPanel legendHeaderCountJPanel = getLegendHeaderCountJPanel();

		legendHeaderPanel.add(legendHeaderCheckBoxJPanel, gbc1);
		legendHeaderPanel.add(legendHeaderLegendJPanel, gbc2);
		legendHeaderPanel.add(legendHeaderCountJPanel, gbc3);

		Dimension preferredSize = new Dimension(160, 30);

		legendHeaderPanel.setMinimumSize(preferredSize);
		legendHeaderPanel.setPreferredSize(preferredSize);

		return legendHeaderPanel;
	}

	private JPanel getLegendHeaderCheckBoxJPanel() {

		JPanel legendHeaderCheckBoxJPanel = new JPanel();

		legendHeaderCheckBoxJPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weightx = 1.0D;
		gbc1.weighty = 1.0D;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.insets = new Insets(3, 3, 3, 3);

		JCheckBox legendHeaderCheckBox = new JCheckBox();
		legendHeaderCheckBox.setSelected(true);

		legendHeaderCheckBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				boolean selected = false;

				if (e.getStateChange() == ItemEvent.DESELECTED) {
					selected = false;
				} else {
					selected = true;
				}

				for (String logSeriesCollectionName : logSeriesCollectionCheckBoxPanelMap.keySet()) {

					LogSeriesCollectionCheckBoxPanel logSeriesCollectionCheckBoxPanel = logSeriesCollectionCheckBoxPanelMap
							.get(logSeriesCollectionName);

					JCheckBox checkbox = logSeriesCollectionCheckBoxPanel.getjCheckBox();
					checkbox.setSelected(selected);
				}

			}
		});
		legendHeaderCheckBoxJPanel.add(legendHeaderCheckBox, gbc1);

		legendHeaderCheckBoxJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

		return legendHeaderCheckBoxJPanel;
	}

	private JPanel getLegendHeaderLegendJPanel() {

		JPanel legendHeaderLegendJPanel = new JPanel();

		legendHeaderLegendJPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weightx = 1.0D;
		gbc1.weighty = 1.0D;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.insets = new Insets(0, 3, 0, 3);

		JLabel legendJLabel = new JLabel("Legend");
		legendJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		legendHeaderLegendJPanel.add(legendJLabel, gbc1);

		legendHeaderLegendJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

		return legendHeaderLegendJPanel;
	}

	private JPanel getLegendHeaderCountJPanel() {

		JPanel legendHeaderCountJPanel = new JPanel();

		legendHeaderCountJPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weightx = 0.0D;
		gbc1.weighty = 1.0D;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.insets = new Insets(0, 5, 0, 5);

		JLabel countJLabel = new JLabel("Count");

		Dimension dim = new Dimension(35, Integer.MAX_VALUE);
		countJLabel.setPreferredSize(dim);
		countJLabel.setMinimumSize(dim);
		countJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		legendHeaderCountJPanel.add(countJLabel, gbc1);

		legendHeaderCountJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

		return legendHeaderCountJPanel;
	}

	@SuppressWarnings("unchecked")
	private void refreshChart(LogTableModel logTableModel) {

		try {

			LOG.info("Refresh chart - start");

			LogEntryModel logEntryModel = logTableModel.getLogEntryModel();

			DateFormat modelDateFormat = logEntryModel.getModelDateFormat();
			NumberFormat numberFormat = logEntryModel.getNumberFormat();

			CombinedDomainXYPlot combinedDomainXYPlot = getCombinedDomainXYPlot();
			CombinedDomainCategoryPlot combinedBoxAndWiskerCategoryPlot = getCombinedBoxAndWiskerCategoryPlot();

			ValueAxis domainAxis = logEntryModel.getDomainAxis();

			combinedDomainXYPlot.setDomainAxis(domainAxis);

			List<String> logSeriesCollectionNameArray = new ArrayList<>();

			Set<LogSeriesCollection> logTimeSeriesCollectionSet = logEntryModel.getLogTimeSeriesCollectionSet(true);

			Iterator<LogSeriesCollection> logSeriesCollectionIt = logTimeSeriesCollectionSet.iterator();

			while (logSeriesCollectionIt.hasNext()) {

				LogSeriesCollection logSeriesCollection = logSeriesCollectionIt.next();

				String logSeriesCollectionName = logSeriesCollection.getName();
				List<LogSeries> logSeriesList = logSeriesCollection.getLogSeriesList();

				logSeriesCollectionNameArray.add(logSeriesCollectionName);

				XYPlot xyPlot = xyPlotMap.get(logSeriesCollectionName);
				CategoryPlot categoryPlot = categoryPlotMap.get(logSeriesCollectionName);
				LogSeriesCollectionCheckBoxPanel lsccbp = logSeriesCollectionCheckBoxPanelMap
						.get(logSeriesCollectionName);

				if (xyPlot == null) {

					final XYPlot lscXYPlot = new XYPlot();
					lscXYPlot.setDomainCrosshairVisible(false);
					lscXYPlot.setDomainCrosshairLockedOnData(false);
					lscXYPlot.setRangeCrosshairVisible(false);
					lscXYPlot.setRangeCrosshairLockedOnData(false);

					final CategoryPlot lscCategoryPlot = new CategoryPlot();
					lscCategoryPlot.setDomainCrosshairVisible(false);
					lscCategoryPlot.setRangeCrosshairVisible(false);
					lscCategoryPlot.setRangeCrosshairLockedOnData(false);

					// add to main plot
					combinedDomainXYPlot.add(lscXYPlot);
					combinedBoxAndWiskerCategoryPlot.add(lscCategoryPlot);

					xyPlotMap.put(logSeriesCollectionName, lscXYPlot);
					categoryPlotMap.put(logSeriesCollectionName, lscCategoryPlot);

					lsccbp = new LogSeriesCollectionCheckBoxPanel(logSeriesList);

					JCheckBox checkbox = lsccbp.getjCheckBox();

					checkbox.addItemListener(new ItemListener() {

						@Override
						public void itemStateChanged(ItemEvent e) {

							if (e.getStateChange() == ItemEvent.DESELECTED) {
								lscXYPlot.setWeight(0);
								lscCategoryPlot.setWeight(0);
							} else {
								lscXYPlot.setWeight(1);
								lscCategoryPlot.setWeight(1);
							}

							processLogSeriesCollectionCheckBoxPanelMap();
						}
					});

					logSeriesCollectionCheckBoxPanelMap.put(logSeriesCollectionName, lsccbp);

					int yIndex = logSeriesCollectionCheckBoxPanelMap.size() - 1;

					GridBagConstraints gbc1 = new GridBagConstraints();
					gbc1.gridx = 0;
					gbc1.gridy = yIndex;
					gbc1.weightx = 1.0D;
					gbc1.weighty = 1.0D;
					gbc1.fill = GridBagConstraints.BOTH;
					gbc1.anchor = GridBagConstraints.NORTHWEST;
					gbc1.insets = new Insets(0, 0, 0, 0);

					legendPanel.add(lsccbp, gbc1);

					xyPlot = lscXYPlot;
					categoryPlot = lscCategoryPlot;
				}

				// check if plot needs to be visible based on whether it was
				// unchecked before
				if (lsccbp.getjCheckBox().isSelected()) {
					xyPlot.setWeight(1);
					categoryPlot.setWeight(1);
				} else {
					xyPlot.setWeight(0);
					categoryPlot.setWeight(0);
				}

				LogViewerUtil.updatePlots(xyPlot, categoryPlot, logSeriesCollection, modelDateFormat, numberFormat,
						false);

				lsccbp.updatelogSeriesCounts(logSeriesList);

			}

			// add the dummy plot (with logfile time start and end time already set) to
			// setup the actual log time range. this is due to limitation in jfreechart
			// where making mouse movement to reset the chart zoom will set autorange to
			// true. hence forcing the graph to show only the time range limited to the data
			// set.

			XYPlot logXYPlot = xyPlotMap.get("LogXYPlot");

			if (logXYPlot == null) {

				logXYPlot = LogViewerUtil.getLogXYPlot(logEntryModel);

				combinedDomainXYPlot.add(logXYPlot);
				xyPlotMap.put("LogXYPlot", logXYPlot);

				logXYPlot.setWeight(0);
			}

			// setup Permanent markers

			// clear permanent markers as plot as constant now.
			for (XYPlot subPlot : (List<XYPlot>) combinedDomainXYPlot.getSubplots()) {
				subPlot.clearDomainMarkers();
			}

			Set<LogIntervalMarker> logIntervalMarkerSet = logEntryModel.getLogIntervalMarkerSet();

			if (logIntervalMarkerSet != null) {
				Iterator<LogIntervalMarker> logIntervalMarkerIt = logIntervalMarkerSet.iterator();

				while (logIntervalMarkerIt.hasNext()) {

					final LogIntervalMarker logIntervalMarker = logIntervalMarkerIt.next();

					String logIntervalMarkerName = logIntervalMarker.getName();

					LogSeriesCollectionCheckBoxPanel lsccbp = logSeriesCollectionCheckBoxPanelMap
							.get(logIntervalMarkerName);

					if ((lsccbp == null) || ((lsccbp != null) && (lsccbp.getjCheckBox().isSelected()))) {

						List<ValueMarker> valueMarkerList = logIntervalMarker.getValueMarkerList();

						for (ValueMarker valueMarker : valueMarkerList) {

							for (XYPlot subPlot : (List<XYPlot>) combinedDomainXYPlot.getSubplots()) {
								subPlot.addDomainMarker(valueMarker);
							}
						}
					}

					if (lsccbp == null) {

						List<LogSeries> logIntervalMarkerList = new ArrayList<>();
						logIntervalMarkerList.add(logIntervalMarker);

						lsccbp = new LogSeriesCollectionCheckBoxPanel(logIntervalMarkerList);

						JCheckBox checkbox = lsccbp.getjCheckBox();

						checkbox.addItemListener(new ItemListener() {

							@Override
							public void itemStateChanged(ItemEvent e) {

								if (e.getStateChange() == ItemEvent.DESELECTED) {

									List<ValueMarker> valueMarkerList = logIntervalMarker.getValueMarkerList();

									for (ValueMarker valueMarker : valueMarkerList) {

										for (XYPlot subPlot : (List<XYPlot>) combinedDomainXYPlot.getSubplots()) {
											subPlot.removeDomainMarker(valueMarker);
										}
									}

								} else {

									List<ValueMarker> valueMarkerList = logIntervalMarker.getValueMarkerList();

									for (ValueMarker valueMarker : valueMarkerList) {

										for (XYPlot subPlot : (List<XYPlot>) combinedDomainXYPlot.getSubplots()) {
											subPlot.addDomainMarker(valueMarker);
										}
									}
								}
							}
						});

						logSeriesCollectionCheckBoxPanelMap.put(logIntervalMarkerName, lsccbp);

						int yIndex = logSeriesCollectionCheckBoxPanelMap.size() - 1;

						GridBagConstraints gbc1 = new GridBagConstraints();
						gbc1.gridx = 0;
						gbc1.gridy = yIndex;
						gbc1.weightx = 1.0D;
						gbc1.weighty = 1.0D;
						gbc1.fill = GridBagConstraints.BOTH;
						gbc1.anchor = GridBagConstraints.NORTHWEST;
						gbc1.insets = new Insets(0, 0, 0, 0);

						legendPanel.add(lsccbp, gbc1);
					}
				}
			}

			// disable the plots that are not in the latest LogSeriesCollection
			for (String logSeriesCollectionName : xyPlotMap.keySet()) {

				if (!logSeriesCollectionNameArray.contains(logSeriesCollectionName)) {

					XYPlot xyPlot = xyPlotMap.get(logSeriesCollectionName);
					CategoryPlot categoryPlot = categoryPlotMap.get(logSeriesCollectionName);

					if (xyPlot != null) {
						xyPlot.setWeight(0);
					}

					if (categoryPlot != null) {
						categoryPlot.setWeight(0);
					}
				}
			}

		} catch (Exception e) {
			LOG.info("Error refreshing chart", e);
		} finally {
			dataUpdated = false;
			revalidate();

			LOG.info("Refresh chart - end");
		}
	}
}
