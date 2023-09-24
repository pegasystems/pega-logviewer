
package com.pega.gcs.logviewer.ddsmetrics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.charset.Charset;
import java.time.ZoneId;
import java.util.HashMap;
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
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.pega.gcs.fringecommon.guiutilities.BaseFrame;
import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.fringecommon.guiutilities.MemoryStatusBarJPanel;
import com.pega.gcs.fringecommon.guiutilities.Message;
import com.pega.gcs.fringecommon.guiutilities.Message.MessageType;
import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.RecentFileContainer;
import com.pega.gcs.fringecommon.guiutilities.search.SearchData;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.GeneralUtilities;
import com.pega.gcs.logviewer.ChartTablePanelSettingDialog;
import com.pega.gcs.logviewer.LogFileLoadTask;
import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogViewerSetting;

public class DdsMetricMainPanel extends JPanel {

    private static final long serialVersionUID = -3817114289952313556L;

    private static final Log4j2Helper LOG = new Log4j2Helper(DdsMetricMainPanel.class);

    private LogViewerSetting logViewerSetting;

    private LogFileLoadTask logFileLoadTask;

    private LogTableModel logTableModel;

    private JButton overviewButton;

    private JButton reloadButton;

    private JTextField charsetLabel;

    private JTextField timezoneLabel;

    private JTextField localeLabel;

    private JTextField fileSizeLabel;

    private JTabbedPane metricsTabbedPane;

    private JTextField statusBar;

    private JProgressBar progressBar;

    private JLabel progressText;

    private JButton stopTailLogFileButton;

    public DdsMetricMainPanel(File selectedFile, RecentFileContainer recentFileContainer,
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

        logTableModel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {

                String propertyName = evt.getPropertyName();

                if ("message".equals(propertyName)) {

                    JTextField statusBar = getStatusBar();
                    Message message = (Message) evt.getNewValue();
                    GUIUtilities.setMessage(statusBar, message);

                }

            }
        });

        logTableModel.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent tableModelEvent) {

                populateDisplayPanel();

                buildTabs();
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
        JTabbedPane metricsTabbedPane = getMetricsTabbedPane();
        JPanel bottomJPanel = getBottomJPanel();

        add(utilityCompositePanel, gbc1);
        add(metricsTabbedPane, gbc2);
        add(bottomJPanel, gbc3);

        // TODO - NOT FULLY IMPLEMENTED
        // loadFile(this, logViewerSetting, false);

    }

    @Override
    public void removeNotify() {

        super.removeNotify();

        if ((logFileLoadTask != null) && ((!logFileLoadTask.isCancelled()) || (!logFileLoadTask.isDone()))) {

            LOG.info("Tab removed. Cancelling LogFileLoadTask");

            logFileLoadTask.cancel(true);
        }

    }

    private LogViewerSetting getLogViewerSetting() {
        return logViewerSetting;
    }

    private LogTableModel getLogTableModel() {
        return logTableModel;
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
                    // TODO
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

                    LogTableModel logTableModel = getLogTableModel();

                    String origCharsetName = logTableModel.getCharset().name();
                    Locale origLocale = logTableModel.getLocale();
                    ZoneId origZoneId = logTableModel.getZoneId();

                    ChartTablePanelSettingDialog chartTablePanelSettingDialog;

                    chartTablePanelSettingDialog = new ChartTablePanelSettingDialog(origCharsetName, origLocale,
                            origZoneId, BaseFrame.getAppIcon(), DdsMetricMainPanel.this);

                    boolean settingUpdated = chartTablePanelSettingDialog.isSettingUpdated();

                    if (settingUpdated) {

                        String selectedCharsetName = chartTablePanelSettingDialog.getSelectedCharsetName();
                        Locale locale = chartTablePanelSettingDialog.getSelectedLocale();
                        ZoneId zoneId = chartTablePanelSettingDialog.getSelectedZoneId();

                        logTableModel.updateRecentFile(selectedCharsetName, locale, zoneId);

                        if (origCharsetName.equals(selectedCharsetName)) {

                            // LogTableModel ltm = (LogTableModel) logTable.getModel();
                            //
                            // if (!origTimezone.equals(timezone)) {
                            // LogEntryModel lem = ltm.getLogEntryModel();
                            // lem.setDisplayDateFormatTimeZone(timezone);
                            // }
                            //
                            // ltm.fireTableDataChanged();

                            populateDisplayPanel();

                        } else {
                            // clear and reset the model.
                            logTableModel.resetModel();

                            // charset changed, read/parse the file again
                            loadFile(DdsMetricMainPanel.this, getLogViewerSetting(), false);
                        }
                    }

                }
            });
        }

        return reloadButton;
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

    private JTabbedPane getMetricsTabbedPane() {

        if (metricsTabbedPane == null) {
            metricsTabbedPane = new JTabbedPane();
        }

        return metricsTabbedPane;
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

    private JProgressBar getProgressBar() {

        if (progressBar == null) {
            progressBar = new JProgressBar();

            progressBar.setStringPainted(true);
        }

        return progressBar;
    }

    private JLabel getProgressText() {

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

    private JPanel getUtilityCompositePanel() {

        JPanel utilityCompositePanel = new JPanel();
        LayoutManager layout = new BoxLayout(utilityCompositePanel, BoxLayout.X_AXIS);
        utilityCompositePanel.setLayout(layout);

        JPanel utilityPanel = getUtilityPanel();
        JPanel infoPanel = getInfoPanel();

        utilityCompositePanel.add(utilityPanel);
        utilityCompositePanel.add(infoPanel);

        return utilityCompositePanel;
    }

    private JPanel getUtilityPanel() {
        JPanel utilityPanel = new JPanel();

        LayoutManager layout = new BoxLayout(utilityPanel, BoxLayout.X_AXIS);
        utilityPanel.setLayout(layout);

        JButton overviewButton = getOverviewButton();
        JButton reloadButton = getReloadButton();

        Dimension spacer = new Dimension(10, 40);

        utilityPanel.add(Box.createHorizontalGlue());
        utilityPanel.add(Box.createRigidArea(spacer));
        utilityPanel.add(overviewButton);
        utilityPanel.add(Box.createRigidArea(spacer));
        utilityPanel.add(reloadButton);
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

    private void populateDisplayPanel() {

        LOG.info("populateDisplayPanel");

        JTextField charsetLabel = getCharsetLabel();
        JTextField timezoneLabel = getTimezoneLabel();
        JTextField localeLabel = getLocaleLabel();
        JTextField fileSizeLabel = getFileSizeLabel();

        LogTableModel logTableModel = getLogTableModel();

        Charset charset = logTableModel.getCharset();
        Locale locale = logTableModel.getLocale();
        ZoneId zoneId = logTableModel.getZoneId();
        Long fileSize = logTableModel.getFileSize();

        String zoneIdStr = null;

        if (zoneId != null) {
            zoneIdStr = zoneId.getId();
        }

        String fileSizeStr = null;

        if (fileSize != null) {
            fileSizeStr = GeneralUtilities.humanReadableSize(fileSize.longValue(), false);
        }

        charsetLabel.setText(charset.name());
        localeLabel.setText(locale.toString());
        timezoneLabel.setText(zoneIdStr);
        fileSizeLabel.setText(fileSizeStr);

    }

    // TODO - NOT FULLY IMPLEMENTED
    private void buildTabs() {

    }
    // private void buildTabs() {
    //
    // try {
    //
    // LogTableModel logTableModel = getLogTableModel();
    // DdsMetricChartModel ddsMetricChartModel = (DdsMetricChartModel) logTableModel.getLogEntryModel();
    //
    // JTabbedPane metricsTabbedPane = getMetricsTabbedPane();
    // metricsTabbedPane.removeAll();
    //
    // Dimension labelDim = new Dimension(100, 26);
    //
    // DateFormat displayDateFormat = ddsMetricChartModel.getDisplayDateFormat();
    // DateAxis domainAxis = ddsMetricChartModel.getDomainAxis();
    // long lowerDomainRange = ddsMetricChartModel.getLowerDomainRange();
    // long upperDomainRange = ddsMetricChartModel.getUpperDomainRange();
    // Map<String, DdsMetricTableInfo> tableinfoMap = ddsMetricChartModel.getTableinfoMap();
    //
    // for (Map.Entry<String, DdsMetricTableInfo> entry : tableinfoMap.entrySet()) {
    //
    // String name = entry.getKey();
    // DdsMetricTableInfo ddsMetricTableInfo = entry.getValue();
    //
    // DdsMetricTableInfoChartPanel ddsMetricTableInfoChartPanel;
    // ddsMetricTableInfoChartPanel = new DdsMetricTableInfoChartPanel(displayDateFormat, domainAxis,
    // lowerDomainRange, upperDomainRange, ddsMetricTableInfo, logTableModel);
    //
    // logTableModel.addTableModelListener(ddsMetricTableInfoChartPanel);
    //
    // GUIUtilities.addTab(metricsTabbedPane, ddsMetricTableInfoChartPanel, name, labelDim);
    //
    // }
    // } catch (Exception e) {
    // LOG.error("Error building metric tabs.", e);
    // } finally {
    // validate();
    // repaint();
    // }
    //
    // }

    private void loadFile(JComponent parent, LogViewerSetting logViewerSetting, final boolean waitMode) {

        if ((logFileLoadTask != null) && ((!logFileLoadTask.isCancelled()) || (!logFileLoadTask.isDone()))) {
            logFileLoadTask.cancel(true);
            LOG.info("cancelling previous LogFileLoadTask.");
        }

        LogTableModel logTableModel = getLogTableModel();

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

            int processedCount = tflt.getProcessedCount();

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

}
