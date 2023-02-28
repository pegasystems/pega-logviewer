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
import java.io.File;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
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

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.statistics.BoxAndWhiskerItem;

import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.logviewer.AlertLogEntryPanel;
import com.pega.gcs.logviewer.CombinedDomainXYPlotMouseListener;
import com.pega.gcs.logviewer.CustomChartPanel;
import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.LogViewerUtil;
import com.pega.gcs.logviewer.model.AlertBoxAndWhiskerItem;
import com.pega.gcs.logviewer.model.AlertLogEntry;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.AlertLogTimeSeries;
import com.pega.gcs.logviewer.model.LogEntry;
import com.pega.gcs.logviewer.model.LogEntryKey;

public class AlertMessageReportEntryFrame extends JFrame {

    private static final long serialVersionUID = -4017902477655712304L;

    private static final Log4j2Helper LOG = new Log4j2Helper(AlertMessageReportEntryFrame.class);

    private int snoIndex;

    private AlertMessageReportEntry alertMessageReportEntry;

    private AlertMessageReportModel alertMessageReportModel;

    private LogTableModel logTableModel;

    private NavigationTableController<LogEntryKey> navigationTableController;

    private JComboBox<BoxAndWhiskerStatisticsRange> boxPlotRangeJCombobox;

    private JList<AlertReportListEntry> alertLogEntryKeyJList;

    private JPanel alertLogEntryDisplayPanel;

    private CombinedDomainXYPlot combinedDomainXYPlot;

    public AlertMessageReportEntryFrame(int snoIndex, AlertMessageReportEntry alertMessageReportEntry,
            AlertMessageReportModel alertMessageReportModel, LogTableModel logTableModel,
            NavigationTableController<LogEntryKey> navigationTableController, ImageIcon appIcon, Component parent) {

        this.snoIndex = snoIndex;
        this.alertMessageReportEntry = alertMessageReportEntry;
        this.alertMessageReportModel = alertMessageReportModel;
        this.logTableModel = logTableModel;
        this.navigationTableController = navigationTableController;

        String alertMessageId = alertMessageReportModel.getAlertMessageID();

        StringBuilder titleSB = new StringBuilder();

        titleSB.append(alertMessageId);
        titleSB.append(" - ");
        titleSB.append(alertMessageReportEntry.getAlertMessageReportEntryKey());

        int titleSBLen = titleSB.length();

        if (titleSBLen > 100) {
            titleSB.delete(100, titleSBLen);
            titleSB.append("...");
        }

        String alertModelName = logTableModel.getModelName();

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

    private NavigationTableController<LogEntryKey> getNavigationTableController() {
        return navigationTableController;
    }

    private JComboBox<BoxAndWhiskerStatisticsRange> getBoxPlotRangeJCombobox() {

        if (boxPlotRangeJCombobox == null) {

            BoxAndWhiskerStatisticsRange[] values = BoxAndWhiskerStatisticsRange.values();

            boxPlotRangeJCombobox = new JComboBox<>(values);

            boxPlotRangeJCombobox.setMaximumRowCount(values.length);

            boxPlotRangeJCombobox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

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

            List<LogEntryKey> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

            DefaultListModel<AlertReportListEntry> dlm = new DefaultListModel<AlertReportListEntry>();

            int counter = 1;

            for (LogEntryKey alertLogEntryKey : alertLogEntryKeyList) {

                AlertReportListEntry alertReportListEntry = getAlertReportListEntry(counter, alertLogEntryKey);

                dlm.addElement(alertReportListEntry);

                counter++;
            }

            alertLogEntryKeyJList = new JList<AlertReportListEntry>(dlm);
            alertLogEntryKeyJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            ListSelectionListener lsl = new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent listSelectionEvent) {

                    JList<AlertReportListEntry> alertLogEntryKeyJList = getAlertLogEntryKeyJList();

                    int selectedIndex = alertLogEntryKeyJList.getSelectedIndex();

                    if ((!listSelectionEvent.getValueIsAdjusting()) && (selectedIndex != -1)) {

                        AlertReportListEntry alertReportListEntry = alertLogEntryKeyJList.getSelectedValue();

                        Charset charset = logTableModel.getCharset();
                        AlertLogEntryModel alertLogEntryModel = (AlertLogEntryModel) logTableModel.getLogEntryModel();

                        LogEntryKey logEntryKey = alertReportListEntry.getAlertLogEntryKey();

                        Map<LogEntryKey, LogEntry> logEntryMap = alertLogEntryModel.getLogEntryMap();
                        AlertLogEntry alertLogEntry = (AlertLogEntry) logEntryMap.get(logEntryKey);

                        JPanel alertLogEntryPanel = new AlertLogEntryPanel(alertLogEntry, alertLogEntryModel, charset);

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
                public void mouseClicked(MouseEvent mouseEvent) {

                    if (mouseEvent.getClickCount() == 2) {

                        JList<AlertReportListEntry> alertLogEntryKeyJList = getAlertLogEntryKeyJList();

                        int clickedIndex = alertLogEntryKeyJList.locationToIndex(mouseEvent.getPoint());

                        List<LogEntryKey> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

                        LogEntryKey logEntryKey = alertLogEntryKeyList.get(clickedIndex);

                        NavigationTableController<LogEntryKey> navigationTableController;
                        navigationTableController = getNavigationTableController();
                        navigationTableController.scrollToKey(logEntryKey);

                    } else {
                        super.mouseClicked(mouseEvent);
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

    private ChartPanel getChartPanel() {

        RecentFile recentFile = logTableModel.getRecentFile();
        String filePath = recentFile.getPath();
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        String name = FileUtilities.getFileBaseName(file);
        String alertMessageId = alertMessageReportModel.getAlertMessageID();

        StringBuilder titleSB = new StringBuilder();
        titleSB.append(name);
        titleSB.append("-");
        titleSB.append(alertMessageId);
        titleSB.append("-");
        titleSB.append(snoIndex);

        AlertLogEntryModel alertLogEntryModel = (AlertLogEntryModel) logTableModel.getLogEntryModel();

        CombinedDomainXYPlot combinedDomainXYPlot = getCombinedDomainXYPlot();

        // title will be used to fabricate filename
        JFreeChart chart = new JFreeChart(titleSB.toString(), JFreeChart.DEFAULT_TITLE_FONT, combinedDomainXYPlot,
                false);

        TextTitle textTitle = chart.getTitle();
        textTitle.setVisible(false);

        CustomChartPanel customChartPanel = new CustomChartPanel(chart);

        customChartPanel.setMinimumDrawWidth(0);
        customChartPanel.setMinimumDrawHeight(0);
        customChartPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
        customChartPanel.setMaximumDrawHeight(Integer.MAX_VALUE);
        customChartPanel.setMouseWheelEnabled(true);
        customChartPanel.setRangeZoomable(false);
        customChartPanel.setDefaultDirectoryForSaveAs(parentDir);

        customChartPanel.addChartMouseListener(
                new CombinedDomainXYPlotMouseListener(customChartPanel, alertLogEntryModel, navigationTableController));

        return customChartPanel;
    }

    private JComponent getMainJPanel() {

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

        JSplitPane alertChartAndTableSplitPane = getAlertChartAndTableSplitPane();

        mainJPanel.add(headerJPanel, gbc1);
        mainJPanel.add(alertChartAndTableSplitPane, gbc2);

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

        // GridBagConstraints gbc4 = new GridBagConstraints();
        // gbc4.gridx = 0;
        // gbc4.gridy = 3;
        // gbc4.weightx = 1.0D;
        // gbc4.weighty = 0.0D;
        // gbc4.fill = GridBagConstraints.BOTH;
        // gbc4.anchor = GridBagConstraints.NORTHWEST;
        // gbc4.insets = new Insets(0, 0, 0, 0);

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
        Locale locale = logTableModel.getLocale();

        JPanel alertBoxAndWhiskerStatisticsJPanel = new AlertBoxAndWhiskerStatisticsJPanel(alertBoxAndWhiskerItem,
                locale);

        // String noteText = "Select an entry to see the alert details. Double click on an entry to select the alert in main table.";
        // JPanel noteJPanel = new NoteJPanel(noteText, 1);
        // noteJPanel.setPreferredSize(preferredSize);

        headerJPanel.add(titleJPanel, gbc1);
        headerJPanel.add(alertMessageReportKeyJPanel, gbc2);
        headerJPanel.add(alertBoxAndWhiskerStatisticsJPanel, gbc3);
        // headerJPanel.add(noteJPanel, gbc4);

        return headerJPanel;
    }

    private JSplitPane getAlertChartAndTableSplitPane() {

        JPanel chartAndWiskerJPanel = getChartAndWhiskerJPanel();
        JSplitPane dataJSplitPane = getDataJSplitPane();

        JSplitPane alertChartAndTableSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartAndWiskerJPanel,
                dataJSplitPane);

        alertChartAndTableSplitPane.setContinuousLayout(true);
        alertChartAndTableSplitPane.setDividerLocation(150);
        alertChartAndTableSplitPane.setResizeWeight(0.5);

        return alertChartAndTableSplitPane;
    }

    private JPanel getChartAndWhiskerJPanel() {

        JPanel chartAndWiskerJPanel = new JPanel();

        chartAndWiskerJPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(10, 0, 10, 0);

        Locale locale = logTableModel.getLocale();

        AlertLogEntryModel alertLogEntryModel = (AlertLogEntryModel) logTableModel.getLogEntryModel();

        DateFormat modelDateFormat = alertLogEntryModel.getModelDateFormat();

        CombinedDomainXYPlot combinedDomainXYPlot = getCombinedDomainXYPlot();
        DateAxis domainAxis = alertLogEntryModel.getDomainAxis();

        try {
            combinedDomainXYPlot.setDomainAxis((DateAxis) domainAxis.clone());
        } catch (CloneNotSupportedException e) {
            LOG.error("Error setting domain axis.", e);
        }

        XYPlot lsXYPlot = new XYPlot();
        lsXYPlot.setDomainCrosshairVisible(false);
        lsXYPlot.setDomainCrosshairLockedOnData(false);
        lsXYPlot.setRangeCrosshairVisible(false);
        lsXYPlot.setRangeCrosshairLockedOnData(false);

        combinedDomainXYPlot.add(lsXYPlot);

        long lowerDomainRange = alertLogEntryModel.getLowerDomainRange();
        long upperDomainRange = alertLogEntryModel.getUpperDomainRange();

        // empty plot to re-adjust time domain
        XYPlot logXYPlot = LogViewerUtil.getLogXYPlot(lowerDomainRange, upperDomainRange, modelDateFormat, locale);

        combinedDomainXYPlot.add(logXYPlot);
        logXYPlot.setWeight(0);

        CategoryPlot categoryPlot = new CategoryPlot();
        categoryPlot.setDomainCrosshairVisible(false);
        categoryPlot.setRangeCrosshairVisible(false);
        categoryPlot.setRangeCrosshairLockedOnData(false);

        AlertLogTimeSeries alertLogTimeSeries = alertMessageReportEntry.getAlertLogTimeSeries();

        // keep the same range axis for all AlertMessageReportEntry items
        boolean autoRange = false;
        double minRangeValue = alertMessageReportModel.getMinRangeValue();
        double maxRangeValue = alertMessageReportModel.getMaxRangeValue();

        LogViewerUtil.updatePlots(lsXYPlot, categoryPlot, alertLogTimeSeries, modelDateFormat, locale, false, autoRange,
                minRangeValue, maxRangeValue);

        JPanel chartPanel = getChartPanel();

        chartAndWiskerJPanel.add(chartPanel, gbc1);

        BoxAndWhiskerItem boxAndWhiskerItem = alertLogTimeSeries.getBoxAndWhiskerItem();

        if (boxAndWhiskerItem != null) {

            GridBagConstraints gbc2 = new GridBagConstraints();
            gbc2.gridx = 1;
            gbc2.gridy = 0;
            gbc2.weightx = 0.0D;
            gbc2.weighty = 1.0D;
            gbc2.fill = GridBagConstraints.BOTH;
            gbc2.anchor = GridBagConstraints.NORTHWEST;
            gbc2.insets = new Insets(10, 0, 10, 0);

            JPanel boxAndWiskerPanel = getBoxAndWiskerPanel(categoryPlot);

            chartAndWiskerJPanel.add(boxAndWiskerPanel, gbc2);

        }

        chartAndWiskerJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return chartAndWiskerJPanel;
    }

    private ChartPanel getBoxAndWiskerPanel(CategoryPlot categoryPlot) {

        RecentFile recentFile = logTableModel.getRecentFile();
        String filePath = recentFile.getPath();
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        String name = FileUtilities.getFileBaseName(file);
        String alertMessageId = alertMessageReportModel.getAlertMessageID();

        StringBuilder titleSB = new StringBuilder();
        titleSB.append(name);
        titleSB.append("-");
        titleSB.append(alertMessageId);
        titleSB.append("-");
        titleSB.append(snoIndex);
        titleSB.append("-");
        titleSB.append("BoxPlot");

        CategoryAxis categoryAxis = new CategoryAxis("Box Plot");
        categoryAxis.setLowerMargin(0.01);
        categoryAxis.setUpperMargin(0.01);

        categoryPlot.setDomainAxis(categoryAxis);

        JFreeChart chart = new JFreeChart(titleSB.toString(), JFreeChart.DEFAULT_TITLE_FONT, categoryPlot, false);

        // customise the title position and font
        TextTitle textTitle = chart.getTitle();
        textTitle.setVisible(false);

        CustomChartPanel customChartPanel = new CustomChartPanel(chart);

        Dimension preferredSize = new Dimension(150, Integer.MAX_VALUE);

        customChartPanel.setPreferredSize(preferredSize);

        customChartPanel.setMinimumDrawWidth(0);
        customChartPanel.setMinimumDrawHeight(0);
        customChartPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
        customChartPanel.setMaximumDrawHeight(Integer.MAX_VALUE);
        customChartPanel.setMouseWheelEnabled(true);
        customChartPanel.setRangeZoomable(false);
        customChartPanel.setDefaultDirectoryForSaveAs(parentDir);

        return customChartPanel;
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

        AlertBoxAndWhiskerReportColumn keyReportColumn = alertMessageReportModel.getKeyAlertMessageReportColumn();
        String keyLabelName = (keyReportColumn != null) ? keyReportColumn.getDisplayName() : "<NULL>";

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
            updateQ1Min(defaultListModel);
            break;

        case Q1_MEDIAN:
            updateQ1Median(defaultListModel);
            break;

        case MEDIAN_Q3:
            updateQ3Median(defaultListModel);
            break;

        case Q3_MAX:
            updateQ3Max(defaultListModel);
            break;

        case IQR:
            updateIQR(defaultListModel);
            break;

        case MAX:
            updateMAX(defaultListModel);
            break;

        case OUTLIERS:
            updateOutliers(defaultListModel);
            break;

        default:
            break;

        }

        JPanel alertLogEntryDisplayPanel = getAlertLogEntryDisplayPanel();

        alertLogEntryDisplayPanel.removeAll();
        alertLogEntryDisplayPanel.revalidate();
    }

    private AlertReportListEntry getAlertReportListEntry(int counter, LogEntryKey alertLogEntryKey) {

        AlertLogEntryModel alertLogEntryModel = alertMessageReportModel.getAlertLogEntryModel();

        String timeText = alertLogEntryModel.getLogEntryTimeDisplayString(alertLogEntryKey);

        StringBuilder elementSB = new StringBuilder();
        elementSB.append(counter);
        elementSB.append(". Line No [");
        elementSB.append(alertLogEntryKey.getLineNo());
        elementSB.append("] Time [");
        elementSB.append(timeText);
        elementSB.append("]");

        AlertReportListEntry alertReportListEntry = new AlertReportListEntry(alertLogEntryKey, elementSB.toString());

        return alertReportListEntry;
    }

    private void updateALL(DefaultListModel<AlertReportListEntry> defaultListModel) {

        List<LogEntryKey> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

        int counter = 1;

        for (LogEntryKey alertLogEntryKey : alertLogEntryKeyList) {

            AlertReportListEntry alertReportListEntry = getAlertReportListEntry(counter, alertLogEntryKey);

            defaultListModel.addElement(alertReportListEntry);

            counter++;
        }

    }

    private void updateMIN(DefaultListModel<AlertReportListEntry> defaultListModel) {

        double min = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getMinOutlier().doubleValue();

        List<LogEntryKey> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

        int counter = 1;

        for (LogEntryKey alertLogEntryKey : alertLogEntryKeyList) {

            double value = alertMessageReportEntry.getAlertMessageReportEntryKeyValue(alertLogEntryKey);

            if (value == min) {

                AlertReportListEntry alertReportListEntry = getAlertReportListEntry(counter, alertLogEntryKey);

                defaultListModel.addElement(alertReportListEntry);

                counter++;
            }
        }

    }

    private void updateQ1Min(DefaultListModel<AlertReportListEntry> defaultListModel) {

        double min = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getMinOutlier().doubleValue();
        double q1 = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getQ1().doubleValue();

        List<LogEntryKey> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

        int counter = 1;

        for (LogEntryKey alertLogEntryKey : alertLogEntryKeyList) {

            double value = alertMessageReportEntry.getAlertMessageReportEntryKeyValue(alertLogEntryKey);

            if ((value >= min) && (value <= q1)) {

                AlertReportListEntry alertReportListEntry = getAlertReportListEntry(counter, alertLogEntryKey);

                defaultListModel.addElement(alertReportListEntry);

                counter++;
            }
        }

    }

    private void updateQ1Median(DefaultListModel<AlertReportListEntry> defaultListModel) {

        double q1 = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getQ1().doubleValue();
        double median = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getMedian().doubleValue();

        List<LogEntryKey> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

        int counter = 1;

        for (LogEntryKey alertLogEntryKey : alertLogEntryKeyList) {

            double value = alertMessageReportEntry.getAlertMessageReportEntryKeyValue(alertLogEntryKey);

            if ((value >= q1) && (value <= median)) {

                AlertReportListEntry alertReportListEntry = getAlertReportListEntry(counter, alertLogEntryKey);

                defaultListModel.addElement(alertReportListEntry);

                counter++;
            }
        }

    }

    private void updateQ3Median(DefaultListModel<AlertReportListEntry> defaultListModel) {

        double median = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getMedian().doubleValue();
        double q3 = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getQ3().doubleValue();

        List<LogEntryKey> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

        int counter = 1;

        for (LogEntryKey alertLogEntryKey : alertLogEntryKeyList) {

            double value = alertMessageReportEntry.getAlertMessageReportEntryKeyValue(alertLogEntryKey);

            if ((value >= median) && (value <= q3)) {

                AlertReportListEntry alertReportListEntry = getAlertReportListEntry(counter, alertLogEntryKey);

                defaultListModel.addElement(alertReportListEntry);

                counter++;
            }
        }

    }

    private void updateQ3Max(DefaultListModel<AlertReportListEntry> defaultListModel) {

        double q3 = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getQ3().doubleValue();
        double max = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getMaxOutlier().doubleValue();

        List<LogEntryKey> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

        int counter = 1;

        for (LogEntryKey alertLogEntryKey : alertLogEntryKeyList) {

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

        List<LogEntryKey> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

        int counter = 1;

        for (LogEntryKey alertLogEntryKey : alertLogEntryKeyList) {

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

        List<LogEntryKey> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

        int counter = 1;

        for (LogEntryKey alertLogEntryKey : alertLogEntryKeyList) {

            double value = alertMessageReportEntry.getAlertMessageReportEntryKeyValue(alertLogEntryKey);

            if (value == max) {

                AlertReportListEntry alertReportListEntry = getAlertReportListEntry(counter, alertLogEntryKey);

                defaultListModel.addElement(alertReportListEntry);

                counter++;
            }
        }

    }

    private void updateOutliers(DefaultListModel<AlertReportListEntry> defaultListModel) {

        @SuppressWarnings("unchecked")
        List<Number> outliersList = alertMessageReportEntry.getAlertBoxAndWhiskerItem().getOutliers();

        List<LogEntryKey> alertLogEntryKeyList = alertMessageReportEntry.getAlertLogEntryKeyList();

        int counter = 1;

        for (LogEntryKey alertLogEntryKey : alertLogEntryKeyList) {

            double value = alertMessageReportEntry.getAlertMessageReportEntryKeyValue(alertLogEntryKey);

            if (outliersList.contains(value)) {

                AlertReportListEntry alertReportListEntry = getAlertReportListEntry(counter, alertLogEntryKey);

                defaultListModel.addElement(alertReportListEntry);

                counter++;
            }
        }

    }

    private class AlertReportListEntry {

        private LogEntryKey alertLogEntryKey;

        private String displaytext;

        public AlertReportListEntry(LogEntryKey alertLogEntryKey, String displaytext) {
            super();
            this.alertLogEntryKey = alertLogEntryKey;
            this.displaytext = displaytext;
        }

        public LogEntryKey getAlertLogEntryKey() {
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
        // CHECKSTYLE:OFF
        ALL       ("All"),
        MIN       ("Min"),
        MIN_Q1    ("Min to Q1"),
        Q1_MEDIAN ("Q1 to Median"),
        MEDIAN_Q3 ("Median to Q3"),
        Q3_MAX    ("Q3 to Max"),
        IQR       ("IQR(Q1 to Q3)"),
        MAX       ("Max"),
        OUTLIERS  ("Outliers");
        // CHECKSTYLE:ON
        // @formatter:on

        private final String displaytext;

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
