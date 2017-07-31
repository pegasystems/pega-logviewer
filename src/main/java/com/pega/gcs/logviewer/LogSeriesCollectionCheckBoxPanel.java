/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.pega.gcs.logviewer.model.LogSeries;

public class LogSeriesCollectionCheckBoxPanel extends JPanel {

	private static final long serialVersionUID = 7164788613377009953L;

	private JCheckBox jCheckBox;

	Map<String, JLabel> logSeriesCountLabelMap;

	// not passing collection because of multiple uses from LogTimeSeries and
	// LogIntervalMarker
	public LogSeriesCollectionCheckBoxPanel(Collection<LogSeries> logSeriesList) {

		super();

		logSeriesCountLabelMap = new HashMap<>();

		setLayout(new GridBagLayout());

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

		JPanel jCheckBoxPanel = getJCheckBoxPanel();
		JPanel legendItemsPanel = getLegendItemsPanel(logSeriesList);

		add(jCheckBoxPanel, gbc1);
		add(legendItemsPanel, gbc2);

	}

	/**
	 * @return the jCheckBox
	 */
	public JCheckBox getjCheckBox() {

		if (jCheckBox == null) {
			jCheckBox = new JCheckBox();
		}

		return jCheckBox;
	}

	private JPanel getJCheckBoxPanel() {

		JPanel jCheckBoxPanel = new JPanel();

		jCheckBoxPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weightx = 0.0D;
		gbc1.weighty = 1.0D;
		gbc1.fill = GridBagConstraints.VERTICAL;
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.insets = new Insets(0, 3, 0, 3);

		JCheckBox jCheckBox = getjCheckBox();

		jCheckBoxPanel.add(jCheckBox, gbc1);

		jCheckBoxPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

		return jCheckBoxPanel;
	}

	private JPanel getLegendItemsPanel(Collection<LogSeries> logSeriesList) {

		JPanel legendItemsPanel = new JPanel();

		legendItemsPanel.setLayout(new GridBagLayout());

		int index = 0;

		boolean defaultShowLogTimeSeries = false;

		for (LogSeries logSeries : logSeriesList) {

			String logSeriesName = logSeries.getName();

			GridBagConstraints gbc1 = new GridBagConstraints();
			gbc1.gridx = 0;
			gbc1.gridy = index;
			gbc1.weightx = 1.0D;
			gbc1.weighty = 1.0D;
			gbc1.fill = GridBagConstraints.BOTH;
			gbc1.anchor = GridBagConstraints.NORTHWEST;
			gbc1.insets = new Insets(1, 1, 1, 1);

			JPanel legendJPanel = getLegendJPanel(logSeries);

			legendItemsPanel.add(legendJPanel, gbc1);

			if (logSeries.isShowCount()) {

				GridBagConstraints gbc2 = new GridBagConstraints();
				gbc2.gridx = 1;
				gbc2.gridy = index;
				gbc2.weightx = 0.0D;
				gbc2.weighty = 1.0D;
				gbc2.fill = GridBagConstraints.BOTH;
				gbc2.anchor = GridBagConstraints.NORTHWEST;
				gbc2.insets = new Insets(1, 1, 1, 8);

				String countStr = String.valueOf(logSeries.getCount());

				JLabel logSeriesCountLabel = new JLabel(countStr);

				Dimension dim = new Dimension(35, Integer.MAX_VALUE);
				logSeriesCountLabel.setPreferredSize(dim);
				logSeriesCountLabel.setMinimumSize(dim);
				logSeriesCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);

				legendItemsPanel.add(logSeriesCountLabel, gbc2);

				logSeriesCountLabelMap.put(logSeriesName, logSeriesCountLabel);

			}

			defaultShowLogTimeSeries = defaultShowLogTimeSeries || logSeries.isDefaultShowLogTimeSeries();

			index++;
		}

		JCheckBox jCheckBox = getjCheckBox();

		jCheckBox.setSelected(defaultShowLogTimeSeries);

		legendItemsPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

		return legendItemsPanel;
	}

	private JPanel getLegendJPanel(LogSeries logSeries) {

		JPanel legendJPanel = new JPanel();

		legendJPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weightx = 1.0D;
		gbc1.weighty = 1.0D;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.insets = new Insets(0, 0, 0, 0);

		JLabel legendLabel = new JLabel(logSeries.getName());

		legendLabel.setOpaque(true);
		legendLabel.setForeground(Color.WHITE);
		legendLabel.setBackground(logSeries.getColor());
		legendLabel.setHorizontalAlignment(SwingConstants.CENTER);

		legendJPanel.add(legendLabel, gbc1);

		legendJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

		return legendJPanel;
	}

	public void updatelogSeriesCounts(Collection<LogSeries> logSeriesList) {

		for (LogSeries logSeries : logSeriesList) {

			if (logSeries.isShowCount()) {

				String logSeriesName = logSeries.getName();

				JLabel logSeriesCountLabel = logSeriesCountLabelMap.get(logSeriesName);

				String countStr = String.valueOf(logSeries.getCount());

				logSeriesCountLabel.setText(countStr);
			}
		}
	}
}
