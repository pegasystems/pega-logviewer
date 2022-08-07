/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;

import com.pega.gcs.fringecommon.guiutilities.BaseFrame;
import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.fringecommon.guiutilities.GoToLineDialog;
import com.pega.gcs.fringecommon.guiutilities.MemoryStatusBarJPanel;
import com.pega.gcs.fringecommon.guiutilities.Message;
import com.pega.gcs.fringecommon.guiutilities.Message.MessageType;
import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.RecentFileContainer;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkContainer;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkModel;
import com.pega.gcs.fringecommon.guiutilities.markerbar.MarkerBar;
import com.pega.gcs.fringecommon.guiutilities.search.SearchData;
import com.pega.gcs.fringecommon.guiutilities.search.SearchMarkerModel;
import com.pega.gcs.fringecommon.guiutilities.search.SearchPanel;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.GeneralUtilities;
import com.pega.gcs.logviewer.logfile.AbstractLogPattern;
import com.pega.gcs.logviewer.logfile.AbstractLogPattern.LogType;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogEntryModel;
import com.pega.gcs.logviewer.model.LogViewerSetting;
import com.pega.gcs.logviewer.report.AlertSystemReportDialog;
import com.pega.gcs.logviewer.report.Log4jSystemReportDialog;
import com.pega.gcs.logviewer.report.SystemReportDialog;
import com.pega.gcs.logviewer.report.alertpal.AlertPALReportDialog;

public class LogDataMainPanel extends JPanel {

    private static final long serialVersionUID = -836183144753785975L;

    private static final Log4j2Helper LOG = new Log4j2Helper(LogDataMainPanel.class);

    private static final String SHOW_CHART = "Show Chart";

    private static final String HIDE_CHART = "Hide Chart";

    private LogViewerSetting logViewerSetting;

    private LogTableModel logTableModel;

    private LogTable logTable;

    private LogTableMouseListener logTableMouseListener;

    private LogFileLoadTask logFileLoadTask;

    private SearchPanel<LogEntryKey> searchPanel;

    private JButton chartButton;

    private JButton gotoLineButton;

    private JButton overviewButton;

    private JButton reloadButton;

    private JButton palOverviewButton;

    private JButton logExportButton;

    private JTextField charsetLabel;

    private JTextField timezoneLabel;

    private JTextField localeLabel;

    private JTextField fileSizeLabel;

    private JTextField statusBar;

    private JProgressBar progressBar;

    private JLabel progressText;

    private JButton stopTailLogFileButton;

    private NavigationTableController<LogEntryKey> navigationTableController;

    private int dividerLocation;

    private JSplitPane chartAndTableSplitPane;

    private ChartAndLegendPanel chartAndLegendPanel;

    private SystemReportDialog systemReportDialog;

    private AlertPALReportDialog alertPALReportDialog;

    private Rectangle oldBounds;

    public LogDataMainPanel(File selectedFile, RecentFileContainer recentFileContainer,
            LogViewerSetting logViewerSetting) {

        super();

        this.logViewerSetting = logViewerSetting;
        this.logFileLoadTask = null;

        String charset = logViewerSetting.getCharset();
        Locale locale = logViewerSetting.getLocale();

        Map<String, Object> defaultAttribsIfNew = new HashMap<>();
        defaultAttribsIfNew.put(RecentFile.KEY_LOCALE, locale);

        RecentFile recentFile = recentFileContainer.getRecentFile(selectedFile, charset, defaultAttribsIfNew);

        SearchData<LogEntryKey> searchData = new SearchData<>(null);

        this.logTableModel = new LogTableModel(recentFile, searchData);

        // not moving bookmark loading to end of file load, In tail mode completeLoad doesnt get called.

        BookmarkContainer<LogEntryKey> bookmarkContainer;
        bookmarkContainer = (BookmarkContainer<LogEntryKey>) recentFile.getAttribute(RecentFile.KEY_BOOKMARK);

        if (bookmarkContainer == null) {

            bookmarkContainer = new BookmarkContainer<LogEntryKey>();

            recentFile.setAttribute(RecentFile.KEY_BOOKMARK, bookmarkContainer);
        }

        BookmarkModel<LogEntryKey> bookmarkModel = new BookmarkModel<LogEntryKey>(bookmarkContainer, logTableModel);

        logTableModel.setBookmarkModel(bookmarkModel);

        navigationTableController = new NavigationTableController<LogEntryKey>(logTableModel);

        logTableModel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {

                String propertyName = evt.getPropertyName();

                if ("message".equals(propertyName)) {

                    JTextField statusBar = getStatusBar();
                    Message message = (Message) evt.getNewValue();
                    GUIUtilities.setMessage(statusBar, message);

                } else if ("logEntryModel".equals(propertyName)) {
                    // 'logEntryModel' fired by LogTableModel as the type
                    // of log file is known after parsing the file
                    LogTable logTable = getLogTable();
                    logTable.setColumnModel(logTableModel.getTableColumnModel());
                }

            }
        });

        logTableMouseListener = new LogTableMouseListener(this);

        logTableModel.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent tableModelEvent) {
                updateDisplayPanel();
            }
        });

        oldBounds = new Rectangle(1915, 941);

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent componentEvent) {

                Rectangle newBounds = componentEvent.getComponent().getBounds();

                if (!oldBounds.equals(newBounds)) {
                    try {
                        performComponentResized(oldBounds, newBounds);
                    } finally {
                        oldBounds = newBounds;
                    }
                }
            }
        });

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
        gbc2.weighty = 1.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(0, 0, 0, 0);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 2;
        gbc3.weightx = 1.0D;
        gbc3.weighty = 0.0D;
        gbc3.fill = GridBagConstraints.HORIZONTAL;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(0, 0, 0, 0);

        JPanel utilityCompositePanel = getUtilityCompositePanel();
        JSplitPane chartAndTableSplitPane = getChartAndTableSplitPane();
        JPanel bottomJPanel = getBottomJPanel();

        add(utilityCompositePanel, gbc1);
        add(chartAndTableSplitPane, gbc2);
        add(bottomJPanel, gbc3);

        dividerLocation = chartAndTableSplitPane.getDividerLocation();

        loadFile(this, logTableModel, logViewerSetting, false);

    }

    @Override
    public void removeNotify() {
        super.removeNotify();

        if ((logFileLoadTask != null) && ((!logFileLoadTask.isCancelled()) || (!logFileLoadTask.isDone()))) {
            LOG.info("Tab removed. Cancelling LogFileLoadTask");
            logFileLoadTask.cancel(true);
        }

        clearJDialogList();
    }

    private LogViewerSetting getLogViewerSetting() {
        return logViewerSetting;
    }

    private NavigationTableController<LogEntryKey> getNavigationTableController() {
        return navigationTableController;
    }

    private int getDividerLocation() {
        return dividerLocation;
    }

    private void setDividerLocation(int dividerLocation) {
        this.dividerLocation = dividerLocation;
    }

    private SystemReportDialog getSystemReportDialog() {

        if (systemReportDialog == null) {

            LogTable logTable = getLogTable();
            LogTableModel logTableModel = (LogTableModel) logTable.getModel();
            AbstractLogPattern abstractLogPattern = logTableModel.getLogPattern();

            LogType logType = abstractLogPattern.getLogType();

            NavigationTableController<LogEntryKey> navigationTableController = getNavigationTableController();

            if (logType == LogType.PEGA_ALERT) {
                systemReportDialog = new AlertSystemReportDialog(logTableModel, navigationTableController, logTable,
                        BaseFrame.getAppIcon(), LogDataMainPanel.this);
            } else {

                systemReportDialog = new Log4jSystemReportDialog(logTableModel, navigationTableController,
                        BaseFrame.getAppIcon(), LogDataMainPanel.this);
            }

            systemReportDialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosed(WindowEvent windowEvent) {
                    setSystemReportDialog(null);
                }

            });

            systemReportDialog.setVisible(true);
        }

        return systemReportDialog;
    }

    private void setSystemReportDialog(SystemReportDialog systemReportDialog) {
        this.systemReportDialog = systemReportDialog;
    }

    private AlertPALReportDialog getAlertPALReportDialog() {
        return alertPALReportDialog;
    }

    private void setAlertPALReportDialog(AlertPALReportDialog alertPALReportDialog) {
        this.alertPALReportDialog = alertPALReportDialog;
    }

    private LogTable getLogTable() {

        if (logTable == null) {
            logTable = new LogTable(logTableModel);
            logTable.setFillsViewportHeight(true);

            navigationTableController.addCustomJTable(logTable);
            logTable.addMouseListener(logTableMouseListener);
        }

        return logTable;
    }

    private SearchPanel<LogEntryKey> getSearchPanel() {

        if (searchPanel == null) {

            searchPanel = new SearchPanel<LogEntryKey>(navigationTableController, logTableModel.getSearchModel());
        }

        return searchPanel;
    }

    private JTextField getStatusBar() {

        if (statusBar == null) {
            statusBar = new JTextField();
            statusBar.setEditable(false);
            statusBar.setBackground(null);
            statusBar.setBorder(null);
            statusBar.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        }

        return statusBar;
    }

    private JSplitPane getChartAndTableSplitPane() {

        if (chartAndTableSplitPane == null) {

            ChartAndLegendPanel chartAndLegendPanel = getChartAndLegendPanel();
            JPanel logTablePanel = getLogTablePanel();

            chartAndTableSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartAndLegendPanel, logTablePanel);

            chartAndTableSplitPane.setContinuousLayout(true);
        }

        return chartAndTableSplitPane;
    }

    private JPanel getBottomJPanel() {

        JPanel bottomJPanel = new JPanel();

        LayoutManager layout = new BoxLayout(bottomJPanel, BoxLayout.LINE_AXIS);
        bottomJPanel.setLayout(layout);

        JLabel statusLabel = new JLabel("Status:");

        JTextField statusBar = getStatusBar();
        JProgressBar progressBar = getProgressBar();
        JButton stopTailLogFileJButton = getStopTailLogFileButton();
        JLabel progressText = getProgressText();

        MemoryStatusBarJPanel memoryStatusBarJPanel = new MemoryStatusBarJPanel();

        bottomJPanel.add(Box.createRigidArea(new Dimension(5, 20)));
        bottomJPanel.add(statusLabel);
        bottomJPanel.add(Box.createRigidArea(new Dimension(5, 20)));
        bottomJPanel.add(statusBar);
        bottomJPanel.add(Box.createRigidArea(new Dimension(5, 20)));
        bottomJPanel.add(progressBar);
        bottomJPanel.add(Box.createRigidArea(new Dimension(10, 20)));
        bottomJPanel.add(stopTailLogFileJButton);
        bottomJPanel.add(Box.createRigidArea(new Dimension(10, 20)));
        bottomJPanel.add(progressText);
        bottomJPanel.add(Box.createRigidArea(new Dimension(5, 20)));
        bottomJPanel.add(memoryStatusBarJPanel);

        bottomJPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        return bottomJPanel;
    }

    private ChartAndLegendPanel getChartAndLegendPanel() {

        if (chartAndLegendPanel == null) {

            SearchPanel<LogEntryKey> searchPanel = getSearchPanel();

            LogTable logTable = getLogTable();

            chartAndLegendPanel = new ChartAndLegendPanel(logTable, searchPanel, navigationTableController);

            chartAndLegendPanel.setVisible(false);

            logTableModel.addTableModelListener(chartAndLegendPanel);
        }

        return chartAndLegendPanel;
    }

    private JPanel getLogTablePanel() {

        JPanel logTablePanel = new JPanel();
        logTablePanel.setLayout(new BorderLayout());

        LogTable logTable = getLogTable();

        JScrollPane logTableScrollpane = new JScrollPane(logTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel markerBarPanel = getMarkerBarPanel();

        logTablePanel.add(logTableScrollpane, BorderLayout.CENTER);
        logTablePanel.add(markerBarPanel, BorderLayout.EAST);

        return logTablePanel;
    }

    private JPanel getMarkerBarPanel() {

        JPanel markerBarPanel = new JPanel();
        markerBarPanel.setLayout(new BorderLayout());

        Dimension topDimension = new Dimension(16, 30);

        JLabel topSpacer = new JLabel();
        topSpacer.setPreferredSize(topDimension);

        Dimension bottomDimension = new Dimension(16, 18);

        JLabel bottomSpacer = new JLabel();
        bottomSpacer.setPreferredSize(bottomDimension);

        MarkerBar<LogEntryKey> markerBar = getMarkerBar();

        markerBarPanel.add(topSpacer, BorderLayout.NORTH);
        markerBarPanel.add(markerBar, BorderLayout.CENTER);
        markerBarPanel.add(bottomSpacer, BorderLayout.SOUTH);

        return markerBarPanel;
    }

    private MarkerBar<LogEntryKey> getMarkerBar() {

        SearchMarkerModel<LogEntryKey> searchMarkerModel = new SearchMarkerModel<>(logTableModel);

        MarkerBar<LogEntryKey> markerBar = new MarkerBar<>(navigationTableController, searchMarkerModel);

        BookmarkModel<LogEntryKey> bookmarkModel;
        bookmarkModel = logTableModel.getBookmarkModel();

        markerBar.addMarkerModel(bookmarkModel);

        return markerBar;

    }

    private JPanel getUtilityCompositePanel() {

        JPanel utilityCompositePanel = new JPanel();
        LayoutManager layout = new BoxLayout(utilityCompositePanel, BoxLayout.X_AXIS);
        utilityCompositePanel.setLayout(layout);

        JPanel searchPanel = getSearchPanel();
        JPanel utilityPanel = getUtilityPanel();
        JPanel infoPanel = getInfoPanel();

        utilityCompositePanel.add(searchPanel);
        utilityCompositePanel.add(utilityPanel);
        utilityCompositePanel.add(infoPanel);

        return utilityCompositePanel;
    }

    private JPanel getUtilityPanel() {
        JPanel utilityPanel = new JPanel();

        LayoutManager layout = new BoxLayout(utilityPanel, BoxLayout.X_AXIS);
        utilityPanel.setLayout(layout);

        JButton chartButton = getChartButton();
        JButton gotoLineButton = getGotoLineButton();
        JButton overviewButton = getOverviewButton();
        JButton reloadButton = getReloadButton();
        JButton palOverviewButton = getPalOverviewButton();
        JButton logXMLExportButton = getLogExportButton();

        Dimension spacer = new Dimension(10, 40);

        utilityPanel.add(Box.createHorizontalGlue());
        utilityPanel.add(Box.createRigidArea(spacer));
        utilityPanel.add(chartButton);
        utilityPanel.add(Box.createRigidArea(spacer));
        utilityPanel.add(gotoLineButton);
        utilityPanel.add(Box.createRigidArea(spacer));
        utilityPanel.add(overviewButton);
        utilityPanel.add(Box.createRigidArea(spacer));
        utilityPanel.add(reloadButton);
        utilityPanel.add(Box.createRigidArea(spacer));
        utilityPanel.add(palOverviewButton);
        utilityPanel.add(Box.createRigidArea(spacer));
        utilityPanel.add(logXMLExportButton);
        utilityPanel.add(Box.createRigidArea(spacer));
        utilityPanel.add(Box.createHorizontalGlue());

        utilityPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return utilityPanel;
    }

    private JPanel getInfoPanel() {

        JPanel infoPanel = new JPanel();

        LayoutManager layout = new BoxLayout(infoPanel, BoxLayout.X_AXIS);
        infoPanel.setLayout(layout);

        JPanel charsetPanel = getCharsetPanel();
        JPanel localePanel = getLocalePanel();
        JPanel timezonePanel = getTimezonePanel();
        JPanel fileSizePanel = getFileSizePanel();

        infoPanel.add(charsetPanel);
        infoPanel.add(localePanel);
        infoPanel.add(timezonePanel);
        infoPanel.add(fileSizePanel);

        return infoPanel;
    }

    private JButton getChartButton() {

        if (chartButton == null) {
            chartButton = new JButton(SHOW_CHART);

            Dimension size = new Dimension(100, 26);
            chartButton.setPreferredSize(size);

            chartButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    JSplitPane chartAndTableSplitPane = getChartAndTableSplitPane();
                    ChartAndLegendPanel chartAndLegendPanel = getChartAndLegendPanel();
                    JButton chartJButton = getChartButton();

                    if (actionEvent.getActionCommand().equals(SHOW_CHART)) {
                        chartJButton.setText(HIDE_CHART);
                        chartAndLegendPanel.setVisible(true);

                        chartAndTableSplitPane.setDividerLocation(getDividerLocation());

                    } else {
                        chartJButton.setText(SHOW_CHART);

                        setDividerLocation(chartAndTableSplitPane.getDividerLocation());
                        chartAndLegendPanel.setVisible(false);

                    }

                }
            });
        }

        return chartButton;
    }

    private JButton getGotoLineButton() {

        if (gotoLineButton == null) {

            gotoLineButton = new JButton("Go to line");

            Dimension size = new Dimension(90, 26);
            gotoLineButton.setPreferredSize(size);

            gotoLineButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    LogTable logTable = getLogTable();
                    LogTableModel logTableModel = (LogTableModel) logTable.getModel();

                    LogEntryModel logEntryModel = logTableModel.getLogEntryModel();

                    List<LogEntryKey> logEntryKeyList = logEntryModel.getLogEntryKeyList();

                    // Map<LogEntryKey, LogEntry> logEntryMap = logEntryModel.getLogEntryMap();
                    //
                    // List<Integer> logEntryIndexList = new ArrayList<>(logEntryMap.keySet());
                    //
                    // Collections.sort(logEntryIndexList);

                    int startIndex = logEntryKeyList.get(0).getLineNo();
                    int endIndex = logEntryKeyList.get(logEntryKeyList.size() - 1).getLineNo();

                    GoToLineDialog lgtld = new GoToLineDialog(startIndex, endIndex, BaseFrame.getAppIcon(),
                            LogDataMainPanel.this);
                    lgtld.setVisible(true);

                    Integer selectedIndex = lgtld.getSelectedInteger();

                    if (selectedIndex != null) {

                        LogEntryKey logEntryKey = logEntryModel.getClosestLogEntryKeyForLineNo(selectedIndex);

                        getNavigationTableController().scrollToKey(logEntryKey);
                    }
                }
            });
        }

        return gotoLineButton;
    }

    private JButton getOverviewButton() {

        if (overviewButton == null) {
            overviewButton = new JButton("Overview");

            Dimension size = new Dimension(90, 26);
            overviewButton.setPreferredSize(size);

            overviewButton.setEnabled(true);

            overviewButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    SystemReportDialog systemReportDialog = getSystemReportDialog();

                    systemReportDialog.toFront();

                }
            });
        }

        return overviewButton;

    }

    private JButton getReloadButton() {

        if (reloadButton == null) {
            reloadButton = new JButton("Reload file");

            Dimension size = new Dimension(100, 26);
            reloadButton.setPreferredSize(size);

            reloadButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    LogTable logTable = getLogTable();
                    LogTableModel logTableModel = (LogTableModel) logTable.getModel();

                    String origCharsetName = logTableModel.getCharset().name();
                    Locale origLocale = logTableModel.getLocale();
                    TimeZone origTimezone = logTableModel.getLogTimeZone();

                    ChartTablePanelSettingDialog chartTablePanelSettingDialog;

                    chartTablePanelSettingDialog = new ChartTablePanelSettingDialog(origCharsetName, origLocale,
                            origTimezone, BaseFrame.getAppIcon(), LogDataMainPanel.this);

                    boolean settingUpdated = chartTablePanelSettingDialog.isSettingUpdated();

                    if (settingUpdated) {

                        String selectedCharsetName = chartTablePanelSettingDialog.getSelectedCharsetName();
                        Locale locale = chartTablePanelSettingDialog.getSelectedLocale();
                        TimeZone timezone = chartTablePanelSettingDialog.getSelectedTimeZone();

                        logTableModel.updateRecentFile(selectedCharsetName, locale, timezone);

                        if (origCharsetName.equals(selectedCharsetName)) {

                            LogTableModel ltm = (LogTableModel) logTable.getModel();

                            if (!origTimezone.equals(timezone)) {
                                LogEntryModel lem = ltm.getLogEntryModel();
                                lem.setDisplayDateFormatTimeZone(timezone);
                            }

                            ltm.fireTableDataChanged();

                            populateDisplayPanel();

                        } else {
                            // clear and reset the model.
                            logTableModel.resetModel();

                            // charset changed, read/parse the file again
                            loadFile(LogDataMainPanel.this, logTableModel, getLogViewerSetting(), false);
                        }
                    }

                }
            });
        }

        return reloadButton;
    }

    private JButton getPalOverviewButton() {

        if (palOverviewButton == null) {

            palOverviewButton = new JButton("PAL Report");

            Dimension size = new Dimension(100, 26);
            palOverviewButton.setPreferredSize(size);
            palOverviewButton.setEnabled(false);

            palOverviewButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    AlertPALReportDialog alertPALReportDialog = getAlertPALReportDialog();

                    if (alertPALReportDialog == null) {
                        // additional check to make sure this dialog get called
                        // only for alerts
                        LogTable logTable = getLogTable();
                        LogTableModel logTableModel = (LogTableModel) logTable.getModel();
                        AbstractLogPattern abstractLogPattern = logTableModel.getLogPattern();

                        LogType logType = abstractLogPattern.getLogType();

                        if (logType == LogType.PEGA_ALERT) {

                            alertPALReportDialog = new AlertPALReportDialog(logTableModel,
                                    getNavigationTableController(), BaseFrame.getAppIcon(), LogDataMainPanel.this);

                            alertPALReportDialog.setVisible(true);

                            alertPALReportDialog.addWindowListener(new WindowAdapter() {

                                @Override
                                public void windowClosed(WindowEvent windowEvent) {
                                    AlertPALReportDialog alertPALReportDialog = getAlertPALReportDialog();
                                    alertPALReportDialog.destroyPanel();
                                    setAlertPALReportDialog(null);
                                }

                            });

                            setAlertPALReportDialog(alertPALReportDialog);
                        }

                    } else {
                        alertPALReportDialog.toFront();
                    }

                }
            });
        }

        return palOverviewButton;
    }

    private JButton getLogExportButton() {

        if (logExportButton == null) {
            logExportButton = new JButton("Export Messages");

            Dimension size = new Dimension(120, 26);
            logExportButton.setPreferredSize(size);

            logExportButton.setActionCommand("ExportMessage");

            logExportButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    LogTable logTable = getLogTable();
                    LogTableModel logTableModel = (LogTableModel) logTable.getModel();

                    String actionCommand = actionEvent.getActionCommand();

                    if ("ExportMessage".equals(actionCommand)) {

                        LogMessagesExportDialog lmed = new LogMessagesExportDialog(logTableModel,
                                BaseFrame.getAppIcon(), LogDataMainPanel.this);
                        lmed.setVisible(true);

                    } else {

                        LogXMLExportDialog lxmled = new LogXMLExportDialog(logTableModel, BaseFrame.getAppIcon(),
                                LogDataMainPanel.this);
                        lxmled.setVisible(true);

                    }
                }
            });
        }

        return logExportButton;
    }

    private JTextField getCharsetLabel() {

        if (charsetLabel == null) {
            charsetLabel = GUIUtilities.getNonEditableTextField();
        }

        return charsetLabel;
    }

    private JTextField getTimezoneLabel() {

        if (timezoneLabel == null) {
            timezoneLabel = GUIUtilities.getNonEditableTextField();
        }

        return timezoneLabel;
    }

    private JTextField getLocaleLabel() {

        if (localeLabel == null) {
            localeLabel = GUIUtilities.getNonEditableTextField();
        }

        return localeLabel;
    }

    private JTextField getFileSizeLabel() {

        if (fileSizeLabel == null) {
            fileSizeLabel = GUIUtilities.getNonEditableTextField();
        }

        return fileSizeLabel;
    }

    private JPanel getCharsetPanel() {

        JTextField charsetLabel = getCharsetLabel();
        JPanel charsetPanel = getMetadataPanel(charsetLabel);

        return charsetPanel;
    }

    private JPanel getTimezonePanel() {

        JTextField timezoneLabel = getTimezoneLabel();
        JPanel timezonePanel = getMetadataPanel(timezoneLabel);

        return timezonePanel;
    }

    private JPanel getLocalePanel() {

        JTextField localeLabel = getLocaleLabel();
        JPanel localePanel = getMetadataPanel(localeLabel);

        return localePanel;
    }

    private JPanel getFileSizePanel() {

        JTextField fileSizeLabel = getFileSizeLabel();
        JPanel fileSizePanel = getMetadataPanel(fileSizeLabel);

        return fileSizePanel;
    }

    private JPanel getMetadataPanel(Component metadataLabel) {

        JPanel metadataPanel = new JPanel();

        LayoutManager layout = new BoxLayout(metadataPanel, BoxLayout.X_AXIS);
        metadataPanel.setLayout(layout);

        Dimension dim = new Dimension(10, 40);

        metadataPanel.add(Box.createHorizontalGlue());
        metadataPanel.add(Box.createRigidArea(dim));
        metadataPanel.add(metadataLabel);
        metadataPanel.add(Box.createRigidArea(dim));
        metadataPanel.add(Box.createHorizontalGlue());

        metadataPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return metadataPanel;
    }

    protected JProgressBar getProgressBar() {

        if (progressBar == null) {
            progressBar = new JProgressBar();

            progressBar.setStringPainted(true);
        }

        return progressBar;
    }

    protected JLabel getProgressText() {

        if (progressText == null) {
            progressText = new JLabel();

            Dimension size = new Dimension(200, 20);
            progressText.setPreferredSize(size);
            progressText.setMaximumSize(size);
        }

        return progressText;
    }

    protected JButton getStopTailLogFileButton() {

        if (stopTailLogFileButton == null) {
            stopTailLogFileButton = new JButton("Stop tailing");

            LogViewerSetting logViewerSetting = getLogViewerSetting();

            if (logViewerSetting.isTailLogFile()) {
                stopTailLogFileButton.setEnabled(true);
            } else {
                stopTailLogFileButton.setEnabled(false);
            }

            stopTailLogFileButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    if ((logFileLoadTask != null)
                            && ((!logFileLoadTask.isCancelled()) || (!logFileLoadTask.isDone()))) {

                        stopTailLogFileButton.setEnabled(false);

                        logFileLoadTask.stopTailing();

                        LOG.info("Stop tailing LogFileLoadTask.");
                    }
                }
            });
        }

        return stopTailLogFileButton;
    }

    protected void updateDisplayPanel() {

        LOG.info("updateDisplayPanel");

        populateDisplayPanel();

        LogTable logTable = getLogTable();
        LogTableModel logTableModel = (LogTableModel) logTable.getModel();

        AbstractLogPattern abstractLogPattern = logTableModel.getLogPattern();

        JButton palOverviewJButton = getPalOverviewButton();
        JButton logExportButton = getLogExportButton();

        palOverviewJButton.setEnabled(false);
        logExportButton.setEnabled(false);

        if (abstractLogPattern != null) {

            LogType logType = abstractLogPattern.getLogType();

            if (logType == LogType.PEGA_ALERT) {

                palOverviewJButton.setEnabled(true);

                logExportButton.setText("Export XML");
                logExportButton.setToolTipText("Export events as xml");
                logExportButton.setActionCommand("ExportXml");
                logExportButton.setEnabled(true);

            } else {

                logExportButton.setText("Export Messages");
                logExportButton.setToolTipText("Export Message Column");
                logExportButton.setActionCommand("ExportMessage");
                logExportButton.setEnabled(true);
            }
        }
    }

    protected void populateDisplayPanel() {

        LOG.info("populateDisplayPanel");

        JTextField charsetLabel = getCharsetLabel();
        JTextField timezoneLabel = getTimezoneLabel();
        JTextField localeLabel = getLocaleLabel();
        JTextField fileSizeLabel = getFileSizeLabel();

        LogTable logTable = getLogTable();
        LogTableModel logTableModel = (LogTableModel) logTable.getModel();

        Charset charset = logTableModel.getCharset();
        Locale locale = logTableModel.getLocale();
        TimeZone timeZone = logTableModel.getLogTimeZone();
        Long fileSize = logTableModel.getFileSize();

        String timezoneStr = null;

        if (timeZone != null) {
            timezoneStr = timeZone.getDisplayName(timeZone.useDaylightTime(), TimeZone.SHORT);
        }

        String fileSizeStr = null;

        if (fileSize != null) {
            fileSizeStr = GeneralUtilities.humanReadableSize(fileSize.longValue(), false);
        }

        charsetLabel.setText(charset.name());
        localeLabel.setText(locale.toString());
        timezoneLabel.setText(timezoneStr);
        fileSizeLabel.setText(fileSizeStr);

    }

    protected void loadFile(final JComponent parent, final LogTableModel logTableModel,
            LogViewerSetting logViewerSetting, final boolean waitMode) {

        if ((logFileLoadTask != null) && ((!logFileLoadTask.isCancelled()) || (!logFileLoadTask.isDone()))) {
            logFileLoadTask.cancel(true);
            LOG.info("cancelling previous LogFileLoadTask.");
        }

        RecentFile recentFile = logTableModel.getRecentFile();

        if (recentFile != null) {

            // final String logFilePath = (String)
            // recentFile.getAttribute(RecentFile.KEY_FILE);

            UIManager.put("ModalProgressMonitor.progressText", "Loading log file");

            // the initial note to be kept longer so that bigger dialog gets created and progress texts fits in.
            final ModalProgressMonitor mProgressMonitor = new ModalProgressMonitor(parent, "",
                    "Starting to load log file ...                                                   ", 0, 100);
            mProgressMonitor.setMillisToDecideToPopup(0);
            mProgressMonitor.setMillisToPopup(0);

            JProgressBar progressBar = getProgressBar();
            JLabel progressText = getProgressText();

            logFileLoadTask = new LogFileLoadTask(parent, logTableModel, logViewerSetting, mProgressMonitor,
                    progressBar, progressText) {

                @Override
                protected void done() {

                    if (!waitMode) {
                        completeLoad(this, mProgressMonitor, parent, logTableModel);
                    }
                }
            };

            logFileLoadTask.execute();

            if (waitMode) {
                completeLoad(logFileLoadTask, mProgressMonitor, parent, logTableModel);
            }
        } else {
            logTableModel.setMessage(new Message(MessageType.ERROR, "No file selected for model"));
        }
    }

    // because of continuous read, complete load may never occur unless there is
    // some error. hence all the post load actions needs to be triggered using
    // table model from within LogFileLoadTask
    protected void completeLoad(LogFileLoadTask tflt, ModalProgressMonitor modalProgressMonitor, JComponent parent,
            LogTableModel logTableModel) {

        String filePath = logTableModel.getFilePath();

        try {

            tflt.get();

            System.gc();

            ChartAndLegendPanel chartAndLegendPanel = getChartAndLegendPanel();
            chartAndLegendPanel.setChartMouseListener(logTableModel.getLogEntryModel());

            int processedCount = tflt.getProcessedCount();

            // not moving bookmark loading to end of file load, In tail mode completeLoad doesnt get called.

            // RecentFile recentFile = logTableModel.getRecentFile();
            //
            // BookmarkContainer<Integer> bookmarkContainer;
            // bookmarkContainer = (BookmarkContainer<Integer>) recentFile.getAttribute(RecentFile.KEY_BOOKMARK);
            //
            // if (bookmarkContainer == null) {
            //
            // bookmarkContainer = new BookmarkContainer<Integer>();
            //
            // recentFile.setAttribute(RecentFile.KEY_BOOKMARK, bookmarkContainer);
            // }
            //
            // BookmarkModel<Integer> bookmarkModel = new BookmarkModel<Integer>(bookmarkContainer, logTableModel);
            //
            // logTableModel.setBookmarkModel(bookmarkModel);

            logTableModel.fireTableDataChanged();

            LOG.info("LogFileLoadTask - Done: " + filePath + " processedCount:" + processedCount);

        } catch (CancellationException ce) {

            LOG.error("LogFileLoadTask - Cancelled " + filePath);

            MessageType messageType = MessageType.ERROR;
            Message modelmessage = new Message(messageType, filePath + " - file loading cancelled.");
            logTableModel.setMessage(modelmessage);

        } catch (ExecutionException ee) {

            LOG.error("Execution Error in LogFileLoadTask", ee);

            String message = null;

            if (ee.getCause() instanceof OutOfMemoryError) {

                message = "Out Of Memory Error has occured while loading " + filePath
                        + ".\nPlease increase the JVM's max heap size (-Xmx) and try again.";

                JOptionPane.showMessageDialog(parent, message, "Out Of Memory Error", JOptionPane.ERROR_MESSAGE);
            } else {
                message = ee.getCause().getMessage() + " has occured while loading " + filePath + ".";

                JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
            }

            MessageType messageType = MessageType.ERROR;
            Message modelmessage = new Message(messageType, message);
            logTableModel.setMessage(modelmessage);

        } catch (Exception e) {
            LOG.error("Error loading file: " + filePath, e);
            MessageType messageType = MessageType.ERROR;

            StringBuilder messageB = new StringBuilder();
            messageB.append("Error loading file: ");
            messageB.append(filePath);

            Message message = new Message(messageType, messageB.toString());
            logTableModel.setMessage(message);

        } finally {

            modalProgressMonitor.close();

            System.gc();
        }
    }

    private void clearJDialogList() {

        // in case of error in file load.
        if (logTableMouseListener != null) {
            logTableMouseListener.clearJDialogList();
        }

        if (systemReportDialog != null) {
            systemReportDialog.dispose();
            systemReportDialog = null;
        }
    }

    private void performComponentResized(Rectangle oldBounds, Rectangle newBounds) {

        LogTable logTable = getLogTable();

        TableColumnModel tableColumnModel = logTable.getColumnModel();

        GUIUtilities.applyTableColumnResize(tableColumnModel, oldBounds, newBounds);
    }
}
