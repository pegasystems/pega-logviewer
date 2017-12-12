/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.report.alert;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.guiutilities.NoteJPanel;
import com.pega.gcs.logviewer.AlertLogEntryPanel;
import com.pega.gcs.logviewer.model.AlertBoxAndWhiskerItem;
import com.pega.gcs.logviewer.model.AlertLogEntry;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntry;

public class AlertMessageReportEntryFrame extends JFrame {

	private static final long serialVersionUID = -4017902477655712304L;

	private AlertMessageReportEntry alertMessageReportEntry;

	private AlertMessageReportModel alertMessageReportModel;

	private NavigationTableController<Integer> navigationTableController;

	private JComboBox<BoxAndWhiskerStatisticsRange> boxPlotRangeJCombobox;

	private JList<AlertReportListEntry> alertLogEntryKeyJList;

	private JPanel alertLogEntryDisplayPanel;

	public AlertMessageReportEntryFrame(String alertModelName, AlertMessageReportEntry alertMessageReportEntry,
			AlertMessageReportModel alertMessageReportModel,
			NavigationTableController<Integer> navigationTableController, ImageIcon appIcon, Component parent) {

		this.alertMessageReportEntry = alertMessageReportEntry;
		this.alertMessageReportModel = alertMessageReportModel;
		this.navigationTableController = navigationTableController;

		String alertMessageId = alertMessageReportModel.getAlertMessageID();

		StringBuffer titleSB = new StringBuffer();

		titleSB.append(alertMessageId);
		titleSB.append(" - ");
		titleSB.append(alertMessageReportEntry.getAlertMessageReportEntryKey());

		int titleSBLen = titleSB.length();

		if (titleSBLen > 100) {
			titleSB.delete(100, titleSBLen);
			titleSB.append("...");
		}

		titleSB.append(" - ");
		titleSB.append(alertModelName);

		setTitle(titleSB.toString());

		setIconImage(appIcon.getImage());

		setPreferredSize(new Dimension(1200, 800));

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		setContentPane(getMainJPanel());

		pack();

		setLocationRelativeTo(parent);

		// visible should be the last step
		setVisible(true);
	}

	protected NavigationTableController<Integer> getNavigationTableController() {
		return navigationTableController;
	}

	private JComboBox<BoxAndWhiskerStatisticsRange> getBoxPlotRangeJCombobox() {

		if (boxPlotRangeJCombobox == null) {

			BoxAndWhiskerStatisticsRange[] values = BoxAndWhiskerStatisticsRange.values();

			boxPlotRangeJCombobox = new JComboBox<>(values);

			boxPlotRangeJCombobox.setMaximumRowCount(values.length);

			boxPlotRangeJCombobox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					BoxAndWhiskerStatisticsRange boxAndWhiskerStatisticsRange;
					boxAndWhiskerStatisticsRange = (BoxAndWhiskerStatisticsRange) boxPlotRangeJCombobox
							.getSelectedItem();

					updateAlertLogEntryKeyJList(boxAndWhiskerStatisticsRange);

				}
			});

			DefaultListCellRenderer dlcr = new DefaultListCellRenderer() {

				private static final long serialVersionUID = -1431720875270818508L;

				@Override
				public Component getListCellRendererComponent(JList<?> list, Object value, int index,
						boolean isSelected, boolean cellHasFocus) {

					super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

					setBorder(new EmptyBorder(1, 5, 1, 1));

					return this;
				}

			};

			boxPlotRangeJCombobox.setRenderer(dlcr);

		}

		return boxPlotRangeJCombobox;
	}

	private JList<AlertReportListEntry> getAlertLogEntryKeyJList() {

		if (alertLogEntryKeyJList == null) {

			List<Integer> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

			DefaultListModel<AlertReportListEntry> dlm = new DefaultListModel<AlertReportListEntry>();

			int counter = 1;

			for (Integer alertLogEntryKey : alertLogEntryKeyList) {

				AlertReportListEntry alertReportListEntry = getAlertReportListEntry(counter, alertLogEntryKey);

				dlm.addElement(alertReportListEntry);

				counter++;
			}

			alertLogEntryKeyJList = new JList<AlertReportListEntry>(dlm);
			alertLogEntryKeyJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			ListSelectionListener lsl = new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {

					JList<AlertReportListEntry> alertLogEntryKeyJList = getAlertLogEntryKeyJList();

					int selectedIndex = alertLogEntryKeyJList.getSelectedIndex();

					if ((!e.getValueIsAdjusting()) && (selectedIndex != -1)) {

						AlertReportListEntry alertReportListEntry = alertLogEntryKeyJList.getSelectedValue();

						AlertLogEntryModel alertLogEntryModel = alertMessageReportModel.getAlertLogEntryModel();
						Integer logEntryIndex = alertReportListEntry.getAlertLogEntryKey();

						Map<Integer, LogEntry> logEntryMap = alertLogEntryModel.getLogEntryMap();
						AlertLogEntry alertLogEntry = (AlertLogEntry) logEntryMap.get(logEntryIndex);

						JPanel alertLogEntryPanel = new AlertLogEntryPanel(alertLogEntry, alertLogEntryModel);

						JPanel alertLogEntryDisplayPanel = getAlertLogEntryDisplayPanel();

						alertLogEntryDisplayPanel.removeAll();

						alertLogEntryDisplayPanel.add(alertLogEntryPanel, BorderLayout.CENTER);

						alertLogEntryDisplayPanel.revalidate();
					}
				}
			};

			alertLogEntryKeyJList.addListSelectionListener(lsl);

			alertLogEntryKeyJList.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {

					if (e.getClickCount() == 2) {

						JList<AlertReportListEntry> alertLogEntryKeyJList = getAlertLogEntryKeyJList();

						int clickedIndex = alertLogEntryKeyJList.locationToIndex(e.getPoint());

						List<Integer> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

						Integer logEntryIndex = alertLogEntryKeyList.get(clickedIndex);

						NavigationTableController<Integer> navigationTableController;
						navigationTableController = getNavigationTableController();
						navigationTableController.scrollToKey(logEntryIndex);

					} else {
						super.mouseClicked(e);
					}
				}

			});

			DefaultListCellRenderer dlcr = new DefaultListCellRenderer() {

				private static final long serialVersionUID = -1431720875270818508L;

				@Override
				public Component getListCellRendererComponent(JList<?> list, Object value, int index,
						boolean isSelected, boolean cellHasFocus) {

					super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

					setBorder(new EmptyBorder(1, 10, 1, 1));

					return this;
				}

			};

			alertLogEntryKeyJList.setCellRenderer(dlcr);

			alertLogEntryKeyJList.setFixedCellHeight(20);
		}

		return alertLogEntryKeyJList;
	}

	private JPanel getAlertLogEntryDisplayPanel() {

		if (alertLogEntryDisplayPanel == null) {

			alertLogEntryDisplayPanel = new JPanel();

			alertLogEntryDisplayPanel.setLayout(new BorderLayout());
		}

		return alertLogEntryDisplayPanel;
	}

	protected JComponent getMainJPanel() {

		JPanel mainJPanel = new JPanel();
		mainJPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weightx = 1.0D;
		gbc1.weighty = 0.1D;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.insets = new Insets(0, 0, 0, 0);

		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 0;
		gbc2.gridy = 1;
		gbc2.weightx = 1.0D;
		gbc2.weighty = 0.9D;
		gbc2.fill = GridBagConstraints.BOTH;
		gbc2.anchor = GridBagConstraints.NORTHWEST;
		gbc2.insets = new Insets(0, 0, 0, 0);

		JPanel headerJPanel = getHeaderJPanel();

		JSplitPane dataJSplitPane = getDataJSplitPane();

		mainJPanel.add(headerJPanel, gbc1);
		mainJPanel.add(dataJSplitPane, gbc2);

		return mainJPanel;
	}

	private JPanel getHeaderJPanel() {

		JPanel headerJPanel = new JPanel();
		headerJPanel.setLayout(new GridBagLayout());

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

		GridBagConstraints gbc3 = new GridBagConstraints();
		gbc3.gridx = 0;
		gbc3.gridy = 2;
		gbc3.weightx = 1.0D;
		gbc3.weighty = 0.5D;
		gbc3.fill = GridBagConstraints.BOTH;
		gbc3.anchor = GridBagConstraints.NORTHWEST;
		gbc3.insets = new Insets(0, 0, 0, 0);

		GridBagConstraints gbc4 = new GridBagConstraints();
		gbc4.gridx = 0;
		gbc4.gridy = 3;
		gbc4.weightx = 1.0D;
		gbc4.weighty = 0.0D;
		gbc4.fill = GridBagConstraints.BOTH;
		gbc4.anchor = GridBagConstraints.NORTHWEST;
		gbc4.insets = new Insets(0, 0, 0, 0);

		String alertMessageId = alertMessageReportModel.getAlertMessageID();

		Dimension preferredSize = new Dimension(Integer.MAX_VALUE, 30);

		JLabel titleJLabel = new JLabel(alertMessageId);
		Font labelFont = titleJLabel.getFont();
		Font tabFont = labelFont.deriveFont(Font.BOLD, 11);
		titleJLabel.setFont(tabFont);
		titleJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel titleJPanel = getMessageLabelJPanel(titleJLabel);
		titleJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		titleJPanel.setPreferredSize(preferredSize);

		JPanel alertMessageReportKeyJPanel = getAlertMessageReportKeyJPanel();

		AlertBoxAndWhiskerItem alertBoxAndWhiskerItem = alertMessageReportEntry.getAlertBoxAndWhiskerItem();
		NumberFormat numberFormat = alertMessageReportModel.getAlertLogEntryModel().getNumberFormat();

		JPanel alertBoxAndWhiskerStatisticsJPanel = new AlertBoxAndWhiskerStatisticsJPanel(alertBoxAndWhiskerItem,
				numberFormat);

		String noteText = "Select an entry to see the alert details. Double click on an entry to select the alert in main table.";
		JPanel noteJPanel = new NoteJPanel(noteText, 1);
		noteJPanel.setPreferredSize(preferredSize);

		headerJPanel.add(titleJPanel, gbc1);
		headerJPanel.add(alertMessageReportKeyJPanel, gbc2);
		headerJPanel.add(alertBoxAndWhiskerStatisticsJPanel, gbc3);
		headerJPanel.add(noteJPanel, gbc4);

		return headerJPanel;
	}

	private JPanel getAlertMessageReportKeyJPanel() {

		JPanel alertMessageReportKeyJPanel = new JPanel();
		alertMessageReportKeyJPanel.setLayout(new GridBagLayout());

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

		AlertBoxAndWhiskerReportColumn keyAMRC = alertMessageReportModel.getKeyAlertMessageReportColumn();
		String keyLabelName = (keyAMRC != null) ? keyAMRC.getDisplayName() : "<NULL>";

		String alertMessageReportEntryKey = alertMessageReportEntry.getAlertMessageReportEntryKey();

		JLabel keyJLabel = new JLabel(keyLabelName);
		Font labelFont = keyJLabel.getFont();
		Font tabFont = labelFont.deriveFont(Font.BOLD, 11);
		keyJLabel.setFont(tabFont);
		keyJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel messageLabelJPanel = getMessageLabelJPanel(keyJLabel);
		messageLabelJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

		JTextArea alertMessageReportEntryKeyJTextArea = new JTextArea(alertMessageReportEntryKey);
		alertMessageReportEntryKeyJTextArea.setEditable(false);
		alertMessageReportEntryKeyJTextArea.setBackground(null);
		alertMessageReportEntryKeyJTextArea.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		alertMessageReportEntryKeyJTextArea.setLineWrap(true);
		alertMessageReportEntryKeyJTextArea.setWrapStyleWord(true);
		alertMessageReportEntryKeyJTextArea.setFont(this.getFont());

		JScrollPane alertLogEntryKeyJScrollPane = new JScrollPane(alertMessageReportEntryKeyJTextArea,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		alertMessageReportKeyJPanel.add(messageLabelJPanel, gbc1);
		alertMessageReportKeyJPanel.add(alertLogEntryKeyJScrollPane, gbc2);

		return alertMessageReportKeyJPanel;

	}

	private JPanel getMessageLabelJPanel(JLabel label) {

		JPanel messageLabelJPanel = new JPanel();
		messageLabelJPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weightx = 1.0D;
		gbc1.weighty = 1.0D;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.WEST;
		gbc1.insets = new Insets(5, 5, 5, 5);

		messageLabelJPanel.add(label, gbc1);
		messageLabelJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

		return messageLabelJPanel;

	}

	private JSplitPane getDataJSplitPane() {

		JPanel alertLogEntryDataPanel = getAlertLogEntryDataPanel();
		JPanel alertLogEntryDisplayPanel = getAlertLogEntryDisplayPanel();

		JSplitPane dataJSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, alertLogEntryDataPanel,
				alertLogEntryDisplayPanel);

		dataJSplitPane.setContinuousLayout(true);
		dataJSplitPane.setDividerLocation(300);

		return dataJSplitPane;
	}

	private JPanel getAlertLogEntryDataPanel() {

		JPanel alertLogEntryDataPanel = new JPanel();
		alertLogEntryDataPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weightx = 1.0D;
		gbc1.weighty = 0.0D;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.WEST;
		gbc1.insets = new Insets(0, 0, 0, 0);

		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 0;
		gbc2.gridy = 1;
		gbc2.weightx = 1.0D;
		gbc2.weighty = 1.0D;
		gbc2.fill = GridBagConstraints.BOTH;
		gbc2.anchor = GridBagConstraints.WEST;
		gbc2.insets = new Insets(0, 0, 0, 0);

		JPanel boxPlotRangeJPanel = getBoxPlotRangeJPanel();
		JList<AlertReportListEntry> alertLogEntryKeyJList = getAlertLogEntryKeyJList();

		JScrollPane alertLogEntryKeyJScrollPane = new JScrollPane(alertLogEntryKeyJList,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		alertLogEntryDataPanel.add(boxPlotRangeJPanel, gbc1);
		alertLogEntryDataPanel.add(alertLogEntryKeyJScrollPane, gbc2);

		alertLogEntryDataPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

		return alertLogEntryDataPanel;

	}

	private JPanel getBoxPlotRangeJPanel() {

		JPanel boxPlotRangeJPanel = new JPanel();

		boxPlotRangeJPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weightx = 0.0D;
		gbc1.weighty = 1.0D;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.WEST;
		gbc1.insets = new Insets(5, 5, 5, 5);

		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 1;
		gbc2.gridy = 0;
		gbc2.weightx = 1.0D;
		gbc2.weighty = 1.0D;
		gbc2.fill = GridBagConstraints.HORIZONTAL;
		gbc2.anchor = GridBagConstraints.WEST;
		gbc2.insets = new Insets(5, 2, 5, 5);

		JLabel selectRangeJLabel = new JLabel("Select range: ");
		JComboBox<BoxAndWhiskerStatisticsRange> boxPlotRangeJCombobox = getBoxPlotRangeJCombobox();

		boxPlotRangeJPanel.add(selectRangeJLabel, gbc1);
		boxPlotRangeJPanel.add(boxPlotRangeJCombobox, gbc2);

		return boxPlotRangeJPanel;

	}

	private void updateAlertLogEntryKeyJList(BoxAndWhiskerStatisticsRange boxAndWhiskerStatisticsRange) {

		JList<AlertReportListEntry> alertLogEntryKeyJList = getAlertLogEntryKeyJList();

		DefaultListModel<AlertReportListEntry> defaultListModel;
		defaultListModel = (DefaultListModel<AlertReportListEntry>) alertLogEntryKeyJList.getModel();
		defaultListModel.clear();

		switch (boxAndWhiskerStatisticsRange) {

		case ALL:
			updateALL(defaultListModel);
			break;

		case MIN:
			updateMIN(defaultListModel);
			break;

		case MIN_Q1:
			updateMIN_Q1(defaultListModel);
			break;

		case Q1_MEDIAN:
			updateQ1_MEDIAN(defaultListModel);
			break;

		case MEDIAN_Q3:
			updateMEDIAN_Q3(defaultListModel);
			break;

		case Q3_MAX:
			updateQ3_MAX(defaultListModel);
			break;

		case IQR:
			updateIQR(defaultListModel);
			break;

		case MAX:
			updateMAX(defaultListModel);
			break;

		case OUTLIERS:
			updateOUTLIERS(defaultListModel);
			break;

		default:
			break;

		}

		JPanel alertLogEntryDisplayPanel = getAlertLogEntryDisplayPanel();

		alertLogEntryDisplayPanel.removeAll();
		alertLogEntryDisplayPanel.revalidate();
	}

	private AlertReportListEntry getAlertReportListEntry(int counter, Integer alertLogEntryKey) {

		AlertLogEntryModel alertLogEntryModel = alertMessageReportModel.getAlertLogEntryModel();

		DateFormat displayDateFormat = alertLogEntryModel.getDisplayDateFormat();

		AlertLogEntry alertLogEntry = (AlertLogEntry) alertLogEntryModel.getLogEntry(alertLogEntryKey);

		Date logEntryDate = alertLogEntry.getLogEntryDate();
		String timeText = displayDateFormat.format(logEntryDate);

		StringBuffer elementSB = new StringBuffer();
		elementSB.append(counter);
		elementSB.append(". Line No [");
		elementSB.append(alertLogEntryKey);
		elementSB.append("] Time [");
		elementSB.append(timeText);
		elementSB.append("]");

		AlertReportListEntry alertReportListEntry = new AlertReportListEntry(alertLogEntryKey, elementSB.toString());

		return alertReportListEntry;
	}

	private void updateALL(DefaultListModel<AlertReportListEntry> defaultListModel) {

		List<Integer> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

		int counter = 1;

		for (Integer alertLogEntryKey : alertLogEntryKeyList) {

			AlertReportListEntry alertReportListEntry = getAlertReportListEntry(counter, alertLogEntryKey);

			defaultListModel.addElement(alertReportListEntry);

			counter++;
		}

	}

	private void updateMIN(DefaultListModel<AlertReportListEntry> defaultListModel) {

		double min = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getMinOutlier().doubleValue();

		List<Integer> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

		int counter = 1;

		for (Integer alertLogEntryKey : alertLogEntryKeyList) {

			double value = alertMessageReportEntry.getAlertMessageReportEntryKeyValue(alertLogEntryKey);

			if (value == min) {

				AlertReportListEntry alertReportListEntry = getAlertReportListEntry(counter, alertLogEntryKey);

				defaultListModel.addElement(alertReportListEntry);

				counter++;
			}
		}

	}

	private void updateMIN_Q1(DefaultListModel<AlertReportListEntry> defaultListModel) {

		double min = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getMinOutlier().doubleValue();
		double q1 = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getQ1().doubleValue();

		List<Integer> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

		int counter = 1;

		for (Integer alertLogEntryKey : alertLogEntryKeyList) {

			double value = alertMessageReportEntry.getAlertMessageReportEntryKeyValue(alertLogEntryKey);

			if ((value >= min) && (value <= q1)) {

				AlertReportListEntry alertReportListEntry = getAlertReportListEntry(counter, alertLogEntryKey);

				defaultListModel.addElement(alertReportListEntry);

				counter++;
			}
		}

	}

	private void updateQ1_MEDIAN(DefaultListModel<AlertReportListEntry> defaultListModel) {

		double q1 = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getQ1().doubleValue();
		double median = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getMedian().doubleValue();

		List<Integer> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

		int counter = 1;

		for (Integer alertLogEntryKey : alertLogEntryKeyList) {

			double value = alertMessageReportEntry.getAlertMessageReportEntryKeyValue(alertLogEntryKey);

			if ((value >= q1) && (value <= median)) {

				AlertReportListEntry alertReportListEntry = getAlertReportListEntry(counter, alertLogEntryKey);

				defaultListModel.addElement(alertReportListEntry);

				counter++;
			}
		}

	}

	private void updateMEDIAN_Q3(DefaultListModel<AlertReportListEntry> defaultListModel) {

		double median = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getMedian().doubleValue();
		double q3 = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getQ3().doubleValue();

		List<Integer> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

		int counter = 1;

		for (Integer alertLogEntryKey : alertLogEntryKeyList) {

			double value = alertMessageReportEntry.getAlertMessageReportEntryKeyValue(alertLogEntryKey);

			if ((value >= median) && (value <= q3)) {

				AlertReportListEntry alertReportListEntry = getAlertReportListEntry(counter, alertLogEntryKey);

				defaultListModel.addElement(alertReportListEntry);

				counter++;
			}
		}

	}

	private void updateQ3_MAX(DefaultListModel<AlertReportListEntry> defaultListModel) {

		double q3 = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getQ3().doubleValue();
		double max = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getMaxOutlier().doubleValue();

		List<Integer> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

		int counter = 1;

		for (Integer alertLogEntryKey : alertLogEntryKeyList) {

			double value = alertMessageReportEntry.getAlertMessageReportEntryKeyValue(alertLogEntryKey);

			if ((value >= q3) && (value <= max)) {

				AlertReportListEntry alertReportListEntry = getAlertReportListEntry(counter, alertLogEntryKey);

				defaultListModel.addElement(alertReportListEntry);

				counter++;
			}
		}

	}

	private void updateIQR(DefaultListModel<AlertReportListEntry> defaultListModel) {

		double q1 = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getQ1().doubleValue();
		double q3 = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getQ3().doubleValue();

		List<Integer> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

		int counter = 1;

		for (Integer alertLogEntryKey : alertLogEntryKeyList) {

			double value = alertMessageReportEntry.getAlertMessageReportEntryKeyValue(alertLogEntryKey);

			if ((value >= q1) && (value <= q3)) {

				AlertReportListEntry alertReportListEntry = getAlertReportListEntry(counter, alertLogEntryKey);

				defaultListModel.addElement(alertReportListEntry);

				counter++;
			}
		}

	}

	private void updateMAX(DefaultListModel<AlertReportListEntry> defaultListModel) {

		double max = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getMaxOutlier().doubleValue();

		List<Integer> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

		int counter = 1;

		for (Integer alertLogEntryKey : alertLogEntryKeyList) {

			double value = alertMessageReportEntry.getAlertMessageReportEntryKeyValue(alertLogEntryKey);

			if (value == max) {

				AlertReportListEntry alertReportListEntry = getAlertReportListEntry(counter, alertLogEntryKey);

				defaultListModel.addElement(alertReportListEntry);

				counter++;
			}
		}

	}

	private void updateOUTLIERS(DefaultListModel<AlertReportListEntry> defaultListModel) {

		@SuppressWarnings("unchecked")
		List<Double> outliersList = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getOutliers();

		List<Integer> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

		int counter = 1;

		for (Integer alertLogEntryKey : alertLogEntryKeyList) {

			double value = alertMessageReportEntry.getAlertMessageReportEntryKeyValue(alertLogEntryKey);

			if (outliersList.contains(value)) {

				AlertReportListEntry alertReportListEntry = getAlertReportListEntry(counter, alertLogEntryKey);

				defaultListModel.addElement(alertReportListEntry);

				counter++;
			}
		}

	}

	private class AlertReportListEntry {

		private Integer alertLogEntryKey;

		private String displaytext;

		public AlertReportListEntry(Integer alertLogEntryKey, String displaytext) {
			super();
			this.alertLogEntryKey = alertLogEntryKey;
			this.displaytext = displaytext;
		}

		public Integer getAlertLogEntryKey() {
			return alertLogEntryKey;
		}

		public String getDisplaytext() {
			return displaytext;
		}

		@Override
		public String toString() {
			return getDisplaytext();
		}

	}

	private enum BoxAndWhiskerStatisticsRange {

		// @formatter:off
		ALL ("All"), 
		MIN ("Min"),
		MIN_Q1("Min to Q1"),
		Q1_MEDIAN("Q1 to Median"),
		MEDIAN_Q3("Median to Q3"),
		Q3_MAX("Q3 to Max"),
		IQR("IQR(Q1 to Q3)"),
		MAX ("Max"),
		OUTLIERS("Outliers");
		// @formatter:on

		private String displaytext;

		private BoxAndWhiskerStatisticsRange(String displaytext) {
			this.displaytext = displaytext;
		}

		public String getDisplaytext() {
			return displaytext;
		}

		@Override
		public String toString() {
			return getDisplaytext();
		}
	}
}
