/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.report.alert;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.statistics.BoxAndWhiskerItem;

import com.pega.gcs.fringecommon.guiutilities.ClickablePathPanel;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.guiutilities.NoteJPanel;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.logviewer.CombinedDomainXYPlotMouseListener;
import com.pega.gcs.logviewer.CustomChartPanel;
import com.pega.gcs.logviewer.LogTable;
import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.LogViewerUtil;
import com.pega.gcs.logviewer.model.AlertBoxAndWhiskerItem;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.AlertLogTimeSeries;
import com.pega.gcs.logviewer.model.LogEntry;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogEntryModel;
import com.pega.gcs.logviewer.model.LogSeries;
import com.pega.gcs.logviewer.model.LogSeriesCollection;
import com.pega.gcs.logviewer.model.LogTimeSeries;
import com.pega.gcs.logviewer.model.alert.AlertMessageList.AlertMessage;
import com.pega.gcs.logviewer.model.alert.AlertMessageListProvider;

public class AlertSummaryJPanel extends JPanel implements ListSelectionListener {

    private static final long serialVersionUID = -8110603275329775468L;

    private static final Log4j2Helper LOG = new Log4j2Helper(AlertSummaryJPanel.class);

    private String alertMessageId;

    private CombinedDomainXYPlot combinedDomainXYPlot;

    private LogTable logTable;

    private NavigationTableController<LogEntryKey> navigationTableController;

    private List<IntervalMarker> manualIntervalMarkerList;

    public AlertSummaryJPanel(LogSeriesCollection logTimeSeriesCollection, LogTable logTable,
            NavigationTableController<LogEntryKey> navigationTableController) {

        super();

        this.alertMessageId = logTimeSeriesCollection.getName();
        this.logTable = logTable;
        this.navigationTableController = navigationTableController;
        this.manualIntervalMarkerList = new ArrayList<IntervalMarker>();

        ListSelectionModel lsm = logTable.getSelectionModel();
        lsm.addListSelectionListener(this);

        setLayout(new GridBagLayout());

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
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(0, 0, 0, 0);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 2;
        gbc3.weightx = 1.0D;
        gbc3.weighty = 1.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(0, 0, 0, 0);

        JPanel generalJPanel = getGeneralJPanel(alertMessageId);
        JPanel boxAndWhiskerStatisticsJPanel = getMainBoxAndWhiskerStatisticsJPanel(logTimeSeriesCollection);

        JSplitPane alertChartAndTableSplitPane = getAlertChartAndTableSplitPane(logTimeSeriesCollection);

        add(generalJPanel, gbc1);
        add(boxAndWhiskerStatisticsJPanel, gbc2);
        add(alertChartAndTableSplitPane, gbc3);
    }

    private JPanel getGeneralJPanel(String alertMessageId) {

        JPanel generalJPanel = new JPanel();

        generalJPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(8, 0, 8, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 1.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(0, 0, 0, 0);

        JLabel titleJLabel = new JLabel(alertMessageId);
        Font labelFont = titleJLabel.getFont();
        Font tabFont = labelFont.deriveFont(Font.BOLD, 11);
        titleJLabel.setFont(tabFont);
        titleJLabel.setHorizontalAlignment(SwingConstants.CENTER);

        AlertMessageListProvider alertMessageListProvider = AlertMessageListProvider.getInstance();
        AlertMessage alertMessage = alertMessageListProvider.getAlertMessage(alertMessageId);

        JPanel alertMessageJPanel = getAlertMessageJPanel(alertMessage);

        generalJPanel.add(titleJLabel, gbc1);
        generalJPanel.add(alertMessageJPanel, gbc2);

        return generalJPanel;
    }

    private JPanel getMainBoxAndWhiskerStatisticsJPanel(LogSeriesCollection logTimeSeriesCollection) {

        JPanel mainMoxAndWhiskerStatisticsJPanel = new JPanel();

        mainMoxAndWhiskerStatisticsJPanel.setLayout(new GridBagLayout());

        LogTableModel logTableModel = (LogTableModel) logTable.getModel();

        Locale locale = logTableModel.getLocale();

        int yindex = 0;

        Collection<LogSeries> logSeriesList = logTimeSeriesCollection.getLogSeriesList();

        for (LogSeries logSeries : logSeriesList) {

            AlertLogTimeSeries alertLogTimeSeries = (AlertLogTimeSeries) logSeries;

            AlertBoxAndWhiskerItem alertBoxAndWhiskerItem = alertLogTimeSeries.getBoxAndWhiskerItem();

            if (alertBoxAndWhiskerItem != null) {

                GridBagConstraints gbc1 = new GridBagConstraints();
                gbc1.gridx = 0;
                gbc1.gridy = yindex;
                gbc1.weightx = 1.0D;
                gbc1.weighty = 1.0D;
                gbc1.fill = GridBagConstraints.BOTH;
                gbc1.anchor = GridBagConstraints.NORTHWEST;
                gbc1.insets = new Insets(0, 0, 0, 0);

                JPanel alertBoxAndWhiskerStatisticsJPanel = new AlertBoxAndWhiskerStatisticsJPanel(
                        alertBoxAndWhiskerItem, locale);

                mainMoxAndWhiskerStatisticsJPanel.add(alertBoxAndWhiskerStatisticsJPanel, gbc1);

                yindex++;

            }
        }

        return mainMoxAndWhiskerStatisticsJPanel;
    }

    private JPanel getNameJPanel(String name, int horizontalAlignment, Insets insets, Dimension preferredSize) {

        JPanel statisticsNameJPanel = new JPanel();

        statisticsNameJPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = insets;

        JLabel nameJLabel = new JLabel(name);

        Font labelFont = nameJLabel.getFont();
        Font tabFont = labelFont.deriveFont(Font.BOLD, 11);

        nameJLabel.setFont(tabFont);

        nameJLabel.setHorizontalAlignment(horizontalAlignment);

        if (preferredSize != null) {
            nameJLabel.setPreferredSize(preferredSize);
        }

        statisticsNameJPanel.add(nameJLabel, gbc1);

        statisticsNameJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return statisticsNameJPanel;
    }

    private JPanel getValueJPanel(String value, int horizontalAlignment, Insets insets, boolean isUrl) {

        JPanel statisticsValueJPanel = new JPanel();

        statisticsValueJPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = insets;

        JComponent valueComponent = null;

        if (isUrl) {

            ClickablePathPanel clickablePathPanel = new ClickablePathPanel();
            clickablePathPanel.setUrl(value);
            valueComponent = clickablePathPanel;

        } else {
            JTextField valueJTextField = new JTextField(value);
            valueJTextField.setEditable(false);
            valueJTextField.setBackground(null);
            valueJTextField.setBorder(null);

            valueJTextField.setHorizontalAlignment(horizontalAlignment);
            valueComponent = valueJTextField;
        }

        statisticsValueJPanel.add(valueComponent, gbc1);

        statisticsValueJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return statisticsValueJPanel;
    }

    private JSplitPane getAlertChartAndTableSplitPane(LogSeriesCollection logTimeSeriesCollection) {

        JPanel chartAndWiskerJPanel = getChartAndWhiskerJPanel(logTimeSeriesCollection);
        JPanel alertMessageReportJPanel = getAlertMessageReportJPanel(logTimeSeriesCollection);

        JSplitPane alertChartAndTableSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartAndWiskerJPanel,
                alertMessageReportJPanel);

        alertChartAndTableSplitPane.setContinuousLayout(true);
        alertChartAndTableSplitPane.setDividerLocation(150);
        alertChartAndTableSplitPane.setResizeWeight(0.5);

        return alertChartAndTableSplitPane;
    }

    private JPanel getChartAndWhiskerJPanel(LogSeriesCollection logSeriesCollection) {

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

        LogTableModel logTableModel = (LogTableModel) logTable.getModel();
        LogEntryModel logEntryModel = logTableModel.getLogEntryModel();

        DateFormat modelDateFormat = logEntryModel.getModelDateFormat();

        CombinedDomainXYPlot combinedDomainXYPlot = getCombinedDomainXYPlot();
        DateAxis domainAxis = logEntryModel.getDomainAxis();

        try {
            combinedDomainXYPlot.setDomainAxis((DateAxis) domainAxis.clone());
        } catch (CloneNotSupportedException e) {
            LOG.error("Error setting domain axis.", e);
        }

        XYPlot lscXYPlot = new XYPlot();
        lscXYPlot.setDomainCrosshairVisible(false);
        lscXYPlot.setDomainCrosshairLockedOnData(false);
        lscXYPlot.setRangeCrosshairVisible(false);
        lscXYPlot.setRangeCrosshairLockedOnData(false);

        combinedDomainXYPlot.add(lscXYPlot);

        long lowerDomainRange = logEntryModel.getLowerDomainRange();
        long upperDomainRange = logEntryModel.getUpperDomainRange();
        Locale locale = logTableModel.getLocale();

        // empty plot to re-adjust time domain
        XYPlot logXYPlot = LogViewerUtil.getLogXYPlot(lowerDomainRange, upperDomainRange, modelDateFormat, locale);

        combinedDomainXYPlot.add(logXYPlot);
        logXYPlot.setWeight(0);

        CategoryPlot categoryPlot = new CategoryPlot();
        categoryPlot.setDomainCrosshairVisible(false);
        categoryPlot.setRangeCrosshairVisible(false);
        categoryPlot.setRangeCrosshairLockedOnData(false);

        LogViewerUtil.updatePlots(lscXYPlot, categoryPlot, logSeriesCollection, modelDateFormat, locale, false);

        JPanel chartPanel = getChartPanel();

        chartAndWiskerJPanel.add(chartPanel, gbc1);

        int xindex = 1;

        Collection<LogSeries> logSeriesList = logSeriesCollection.getLogSeriesList();

        for (LogSeries logSeries : logSeriesList) {

            LogTimeSeries logTimeSeries = (LogTimeSeries) logSeries;

            BoxAndWhiskerItem boxAndWhiskerItem;
            boxAndWhiskerItem = logTimeSeries.getBoxAndWhiskerItem();

            if (boxAndWhiskerItem != null) {

                GridBagConstraints gbc2 = new GridBagConstraints();
                gbc2.gridx = xindex;
                gbc2.gridy = 0;
                gbc2.weightx = 0.0D;
                gbc2.weighty = 1.0D;
                gbc2.fill = GridBagConstraints.BOTH;
                gbc2.anchor = GridBagConstraints.NORTHWEST;
                gbc2.insets = new Insets(10, 0, 10, 0);

                JPanel boxAndWiskerPanel = getBoxAndWiskerPanel(categoryPlot);

                chartAndWiskerJPanel.add(boxAndWiskerPanel, gbc2);

                xindex++;
            }
        }

        chartAndWiskerJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return chartAndWiskerJPanel;
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

        LogTableModel logTableModel = (LogTableModel) logTable.getModel();
        LogEntryModel logEntryModel = logTableModel.getLogEntryModel();

        RecentFile recentFile = logTableModel.getRecentFile();

        String filePath = recentFile.getPath();
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        String name = FileUtilities.getFileBaseName(file);

        StringBuilder titleSB = new StringBuilder();
        titleSB.append(name);
        titleSB.append("-");
        titleSB.append(alertMessageId);

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

        try {
            customChartPanel.setDefaultDirectoryForSaveAs(parentDir);
        } catch (IllegalArgumentException iae) {
            LOG.error("Unable to set chart save-as directory: " + parentDir, iae);
        }

        customChartPanel.addChartMouseListener(
                new CombinedDomainXYPlotMouseListener(customChartPanel, logEntryModel, navigationTableController));

        return customChartPanel;
    }

    private ChartPanel getBoxAndWiskerPanel(CategoryPlot categoryPlot) {

        LogTableModel logTableModel = (LogTableModel) logTable.getModel();
        RecentFile recentFile = logTableModel.getRecentFile();

        String filePath = recentFile.getPath();
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        String name = FileUtilities.getFileBaseName(file);

        StringBuilder titleSB = new StringBuilder();
        titleSB.append(name);
        titleSB.append("-");
        titleSB.append(alertMessageId);
        titleSB.append("-");
        titleSB.append("BoxPlot");

        CategoryAxis categoryAxis = new CategoryAxis("Box Plot");
        categoryAxis.setLowerMargin(0.01);
        categoryAxis.setUpperMargin(0.01);

        categoryPlot.setDomainAxis(categoryAxis);

        JFreeChart chart = new JFreeChart(titleSB.toString(), JFreeChart.DEFAULT_TITLE_FONT, categoryPlot, false);

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
        try {
            customChartPanel.setDefaultDirectoryForSaveAs(parentDir);
        } catch (IllegalArgumentException iae) {
            LOG.error("Unable to set chart save-as directory: " + parentDir, iae);
        }

        // boxAndWiskerPanel.addChartMouseListener(new
        // CombinedDomainCategoryPlotMouseListener(chartPanel, logTable));

        // TODO revert when functionality done
        // boxAndWiskerPanel.setVisible(false);

        return customChartPanel;
    }

    private JPanel getAlertMessageReportJPanel(LogSeriesCollection logTimeSeriesCollection) {

        JPanel alertMessageReportJPanel = new JPanel();

        alertMessageReportJPanel.setLayout(new GridBagLayout());

        String messageId = logTimeSeriesCollection.getName();

        Integer alertId = AlertMessageListProvider.getInstance().getAlertId(messageId);

        if (alertId != null) {

            LogTableModel logTableModel = (LogTableModel) logTable.getModel();

            AlertLogEntryModel alertLogEntryModel = (AlertLogEntryModel) logTableModel.getLogEntryModel();

            Map<Integer, AlertMessageReportModel> alertMessageReportModelMap;
            alertMessageReportModelMap = alertLogEntryModel.getAlertMessageReportModelMap();

            AlertMessageReportModel alertMessageReportModel = alertMessageReportModelMap.get(alertId);

            if (alertMessageReportModel != null) {

                GridBagConstraints gbc1 = new GridBagConstraints();
                gbc1.gridx = 0;
                gbc1.gridy = 0;
                gbc1.weightx = 1.0D;
                gbc1.weighty = 1.0D;
                gbc1.fill = GridBagConstraints.BOTH;
                gbc1.anchor = GridBagConstraints.NORTHWEST;
                gbc1.insets = new Insets(0, 0, 0, 0);

                GridBagConstraints gbc2 = new GridBagConstraints();
                gbc2.gridx = 0;
                gbc2.gridy = 1;
                gbc2.weightx = 1.0D;
                gbc2.weighty = 0.0D;
                gbc2.fill = GridBagConstraints.BOTH;
                gbc2.anchor = GridBagConstraints.NORTHWEST;
                gbc2.insets = new Insets(0, 0, 0, 0);

                String alertModelName = logTableModel.getModelName();

                AlertMessageReportTableMouseListener alertMessageReportTableMouseListener;
                alertMessageReportTableMouseListener = new AlertMessageReportTableMouseListener(logTableModel,
                        navigationTableController, this);

                AlertMessageReportTable alertMessageReportTable = new AlertMessageReportTable(alertModelName,
                        alertMessageReportModel);

                alertMessageReportTable.setAlertMessageReportTableMouseListener(alertMessageReportTableMouseListener);

                JScrollPane alertMessageReportTableScrollPane = new JScrollPane(alertMessageReportTable);

                alertMessageReportJPanel.add(alertMessageReportTableScrollPane, gbc1);

                String noteText = "Double click on a row to see list of alerts for the selected key.";

                NoteJPanel noteJPanel = new NoteJPanel(noteText, 1);
                alertMessageReportJPanel.add(noteJPanel, gbc2);
            }

        }

        alertMessageReportJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return alertMessageReportJPanel;
    }

    private JPanel getAlertMessageJPanel(AlertMessage alertMessage) {

        JPanel alertMessageJPanel = new JPanel();

        alertMessageJPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
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

        JPanel alertGeneralJPanel = getAlertGeneralJPanel(alertMessage);
        JPanel alertDssPanel = getAlertDssPanel(alertMessage);

        alertMessageJPanel.add(alertGeneralJPanel, gbc1);
        alertMessageJPanel.add(alertDssPanel, gbc2);

        return alertMessageJPanel;
    }

    private JPanel getAlertGeneralJPanel(AlertMessage alertMessage) {

        JPanel alertGeneralJPanel = new JPanel();

        alertGeneralJPanel.setLayout(new GridBagLayout());

        Insets insets = new Insets(1, 5, 1, 5);
        Dimension preferredSize = new Dimension(150, 20);

        JPanel titleJPanel = getNameJPanel("Title: ", SwingConstants.LEFT, insets, preferredSize);
        JPanel descJPanel = getNameJPanel("Decsription: ", SwingConstants.LEFT, insets, preferredSize);
        JPanel pegaUrlJPanel = getNameJPanel("Pega Url: ", SwingConstants.LEFT, insets, preferredSize);

        JPanel titleValueJPanel = getValueJPanel(alertMessage.getTitle(), SwingConstants.LEFT, insets, false);
        JPanel descValueJPanel = getValueJPanel(alertMessage.getDescription(), SwingConstants.LEFT, insets, false);
        JPanel pegaUrlValueJPanel = getValueJPanel(alertMessage.getPegaUrl(), SwingConstants.LEFT, insets, true);

        List<JPanel> namePanelList = new ArrayList<>();

        namePanelList.add(titleJPanel);
        namePanelList.add(descJPanel);
        namePanelList.add(pegaUrlJPanel);

        HashMap<JPanel, JPanel> panelMap = new HashMap<>();
        panelMap.put(titleJPanel, titleValueJPanel);
        panelMap.put(descJPanel, descValueJPanel);
        panelMap.put(pegaUrlJPanel, pegaUrlValueJPanel);

        int yindex = 0;

        for (JPanel namePanel : namePanelList) {

            GridBagConstraints gbc1 = new GridBagConstraints();
            gbc1.gridx = 0;
            gbc1.gridy = yindex;
            gbc1.weightx = 0.0D;
            gbc1.weighty = 1.0D;
            gbc1.fill = GridBagConstraints.BOTH;
            gbc1.anchor = GridBagConstraints.NORTHWEST;
            gbc1.insets = new Insets(0, 0, 0, 0);

            GridBagConstraints gbc2 = new GridBagConstraints();
            gbc2.gridx = 1;
            gbc2.gridy = yindex;
            gbc2.weightx = 1.0D;
            gbc2.weighty = 1.0D;
            gbc2.fill = GridBagConstraints.BOTH;
            gbc2.anchor = GridBagConstraints.NORTHWEST;
            gbc2.insets = new Insets(0, 0, 0, 0);

            yindex++;

            JPanel nameJPanel = namePanel;
            JPanel valueJPanel = panelMap.get(nameJPanel);

            alertGeneralJPanel.add(nameJPanel, gbc1);
            alertGeneralJPanel.add(valueJPanel, gbc2);
        }

        return alertGeneralJPanel;
    }

    private JPanel getAlertDssPanel(AlertMessage alertMessage) {

        JPanel alertDssPanel = new JPanel();

        alertDssPanel.setLayout(new GridBagLayout());

        Insets insets = new Insets(5, 5, 5, 5);
        Dimension preferredSize = new Dimension(250, 20);

        JPanel dssEnableConfigJPanel = getNameJPanel("DSS Enable Config", SwingConstants.CENTER, insets, preferredSize);
        JPanel dssConfigJPanel = getNameJPanel("DSS Threshold Config", SwingConstants.CENTER, insets, preferredSize);
        JPanel dssDefaultValueJPanel = getNameJPanel("DSS Default Value", SwingConstants.CENTER, insets, preferredSize);
        JPanel dssValueUnitJPanel = getNameJPanel("DSS Value Unit", SwingConstants.CENTER, insets, preferredSize);

        JPanel dssEnableConfigValueJPanel = getDssValueJPanel(alertMessage.getDssEnableConfig());
        JPanel dssThresholdConfigValueJPanel = getDssValueJPanel(alertMessage.getDssThresholdConfig());
        JPanel dssDefaultValueValueJPanel = getDssValueJPanel(alertMessage.getDssDefaultValue());
        JPanel dssValueUnitValueJPanel = getDssValueJPanel(alertMessage.getDssValueUnit());

        List<JPanel> namePanelList = new ArrayList<>();

        namePanelList.add(dssEnableConfigJPanel);
        namePanelList.add(dssConfigJPanel);
        namePanelList.add(dssDefaultValueJPanel);
        namePanelList.add(dssValueUnitJPanel);

        HashMap<JPanel, JPanel> panelMap = new HashMap<>();
        panelMap.put(dssEnableConfigJPanel, dssEnableConfigValueJPanel);
        panelMap.put(dssConfigJPanel, dssThresholdConfigValueJPanel);
        panelMap.put(dssDefaultValueJPanel, dssDefaultValueValueJPanel);
        panelMap.put(dssValueUnitJPanel, dssValueUnitValueJPanel);

        int xindex = 0;

        for (JPanel namePanel : namePanelList) {

            GridBagConstraints gbc1 = new GridBagConstraints();
            gbc1.gridx = xindex;
            gbc1.gridy = 0;
            gbc1.weightx = 1.0D;
            gbc1.weighty = 1.0D;
            gbc1.fill = GridBagConstraints.BOTH;
            gbc1.anchor = GridBagConstraints.NORTHWEST;
            gbc1.insets = new Insets(0, 0, 0, 0);

            GridBagConstraints gbc2 = new GridBagConstraints();
            gbc2.gridx = xindex;
            gbc2.gridy = 1;
            gbc2.weightx = 1.0D;
            gbc2.weighty = 1.0D;
            gbc2.fill = GridBagConstraints.BOTH;
            gbc2.anchor = GridBagConstraints.NORTHWEST;
            gbc2.insets = new Insets(0, 0, 0, 0);

            JPanel nameJPanel = namePanel;
            JPanel valueJPanel = panelMap.get(nameJPanel);

            alertDssPanel.add(nameJPanel, gbc1);
            alertDssPanel.add(valueJPanel, gbc2);

            xindex++;
        }

        return alertDssPanel;
    }

    private JPanel getDssValueJPanel(String value) {

        JPanel dssValueJPanel = new JPanel();

        dssValueJPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 0.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(5, 5, 5, 5);

        int rows = 1;

        if (value != null) {

            int fromIndex = 0;

            while ((fromIndex = value.indexOf("\\n", fromIndex)) != -1) {
                fromIndex++;
                rows++;
            }

            value = value.replaceAll("\\\\n", System.getProperty("line.separator"));
        }

        JTextArea valueJTextArea = new JTextArea(value);
        valueJTextArea.setEditable(false);
        valueJTextArea.setBackground(null);
        valueJTextArea.setBorder(null);
        valueJTextArea.setRows(rows);

        valueJTextArea.setAlignmentX(CENTER_ALIGNMENT);
        valueJTextArea.setAlignmentY(CENTER_ALIGNMENT);

        valueJTextArea.setFont(this.getFont());

        dssValueJPanel.add(valueJTextArea, gbc1);

        dssValueJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return dssValueJPanel;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {

        if (!listSelectionEvent.getValueIsAdjusting()) {

            LogTableModel ltm = (LogTableModel) logTable.getModel();

            int[] selectedRows = logTable.getSelectedRows();

            CombinedDomainXYPlot combinedDomainXYPlot = getCombinedDomainXYPlot();

            for (XYPlot subPlot : (List<XYPlot>) combinedDomainXYPlot.getSubplots()) {

                for (IntervalMarker im : manualIntervalMarkerList) {
                    subPlot.removeDomainMarker(im);
                }
            }

            manualIntervalMarkerList.clear();

            for (int row : selectedRows) {

                try {

                    LogEntry logEntry = (LogEntry) ltm.getValueAt(row, 0);

                    long logEntryTime = logEntry.getKey().getTimestamp();

                    if (logEntryTime != -1) {

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
    public void removeNotify() {

        super.removeNotify();

        ListSelectionModel lsm = logTable.getSelectionModel();
        lsm.removeListSelectionListener(this);

    }
}
