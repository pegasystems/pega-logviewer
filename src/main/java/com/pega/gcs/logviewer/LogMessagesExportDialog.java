
package com.pega.gcs.logviewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CancellationException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.esotericsoftware.kryo.Kryo;
import com.pega.gcs.fringecommon.guiutilities.CheckBoxMenuItemPopupEntry;
import com.pega.gcs.fringecommon.guiutilities.ClickableFilePathPanel;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogEntryModel;

public class LogMessagesExportDialog extends JDialog {

    private static final long serialVersionUID = -149436166086557248L;

    private static final Log4j2Helper LOG = new Log4j2Helper(LogMessagesExportDialog.class);

    private LogTableModel logTableModel;

    private LogMessagesLoggerSelectPanel logMessagesLoggerSelectPanel;

    private JComboBox<ParseOption> parseOptionComboBox;

    private JTextField delimiterTextField;

    private JButton logExportButton;

    private JButton logExportCancelButton;

    private JProgressBar logExportProgressBar;

    private JFileChooser fileChooser;

    private ClickableFilePathPanel clickableFilePathPanel;

    private JLabel logExportLabel;

    private LogMessagesExportTask logMessagesExportTask;

    private File logFile;

    public enum ParseOption {
        DELIMITER, JSON, XML
    }

    public LogMessagesExportDialog(LogTableModel logTableModel, ImageIcon appIcon, Component parent) {

        super();

        this.logTableModel = logTableModel;

        this.logMessagesExportTask = null;

        String filePath = logTableModel.getFilePath();

        logFile = new File(filePath);

        // check if the default output file is available
        String fileName = getDefaultMessagesFileName();
        File currentDirectory = logFile.getParentFile();

        File proposedFile = new File(currentDirectory, fileName);

        if (proposedFile.exists() && proposedFile.isFile()) {
            populateGeneratedExportFilePath(proposedFile);
        }

        setPreferredSize(new Dimension(900, 500));

        setIconImage(appIcon.getImage());

        setTitle("Export Messages - " + logTableModel.getModelName());

        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        setContentPane(getMainPanel());

        pack();

        setLocationRelativeTo(parent);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent windowEvent) {
                super.windowClosed(windowEvent);

                cancelTask();
            }
        });

    }

    private LogTableModel getLogTableModel() {
        return logTableModel;
    }

    private LogMessagesLoggerSelectPanel getLogMessagesLoggerSelectPanel() {

        if (logMessagesLoggerSelectPanel == null) {

            List<CheckBoxMenuItemPopupEntry<LogEntryKey>> loggerColumnEntryList = new ArrayList<>();

            LogTableModel logTableModel = getLogTableModel();

            LogEntryModel logEntryModel = logTableModel.getLogEntryModel();
            Map<LogEntryColumn, Integer> logEntryColumnIndexMap = logEntryModel.getLogEntryColumnIndexMap();

            Integer loggerColumnIndex = logEntryColumnIndexMap.get(LogEntryColumn.LOGGER);

            if (loggerColumnIndex != null) {

                Set<CheckBoxMenuItemPopupEntry<LogEntryKey>> loggerColumnEntrySet;
                loggerColumnEntrySet = logTableModel.getColumnFilterEntrySet(loggerColumnIndex);

                // making a copy
                Kryo kryo = new Kryo();
                kryo.setRegistrationRequired(false);
                kryo.register(TreeSet.class);
                kryo.register(CheckBoxMenuItemPopupEntry.class);

                Set<CheckBoxMenuItemPopupEntry<LogEntryKey>> loggerColumnEntrySetCopy;
                loggerColumnEntrySetCopy = kryo.copy(loggerColumnEntrySet);

                loggerColumnEntryList.addAll(loggerColumnEntrySetCopy);
            }

            logMessagesLoggerSelectPanel = new LogMessagesLoggerSelectPanel(loggerColumnEntryList);

        }

        return logMessagesLoggerSelectPanel;

    }

    private JComboBox<ParseOption> getParseOptionComboBox() {

        if (parseOptionComboBox == null) {

            parseOptionComboBox = new JComboBox<>(ParseOption.values());

            // Dimension size = new Dimension(140, 26);
            // parseOptionComboBox.setPreferredSize(size);

            parseOptionComboBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    ParseOption parseOption = (ParseOption) parseOptionComboBox.getSelectedItem();

                    JTextField delimiterTextField = getDelimiterTextField();

                    switch (parseOption) {
                    case DELIMITER:
                        delimiterTextField.setEnabled(true);
                        break;
                    case JSON:
                        delimiterTextField.setEnabled(false);
                        break;
                    default:
                        delimiterTextField.setEnabled(false);
                        break;

                    }

                    // updateAlertLogEntryKeyJList(boxAndWhiskerStatisticsRange);

                }
            });
        }

        return parseOptionComboBox;
    }

    private JTextField getDelimiterTextField() {

        if (delimiterTextField == null) {
            delimiterTextField = new JTextField(",");
        }

        return delimiterTextField;
    }

    private JButton getLogExportButton() {

        if (logExportButton == null) {
            logExportButton = new JButton("Export Messages for Logger");

            logExportButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    if (isValidSelections()) {
                        performExport();
                    }
                }
            });
        }

        return logExportButton;
    }

    private JButton getLogExportCancelButton() {

        if (logExportCancelButton == null) {
            logExportCancelButton = new JButton("Cancel");

            logExportCancelButton.setEnabled(false);

            logExportCancelButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    cancelTask();
                }
            });
        }

        return logExportCancelButton;
    }

    private JProgressBar getLogExportProgressBar() {

        if (logExportProgressBar == null) {

            logExportProgressBar = new JProgressBar(0, 100);
            logExportProgressBar.setValue(0);
            logExportProgressBar.setStringPainted(true);
        }

        return logExportProgressBar;
    }

    private JFileChooser getFileChooser() {

        if (fileChooser == null) {

            String fileName = getDefaultMessagesFileName();

            File currentDirectory = logFile.getParentFile();

            File proposedFile = new File(currentDirectory, fileName);

            fileChooser = new JFileChooser(currentDirectory);

            fileChooser.setDialogTitle("Save TSV(.tsv) File");
            fileChooser.setSelectedFile(proposedFile);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            FileNameExtensionFilter filter = new FileNameExtensionFilter("TSV Format", "tsv");

            fileChooser.setFileFilter(filter);
        }

        return fileChooser;
    }

    private ClickableFilePathPanel getClickableFilePathPanel() {

        if (clickableFilePathPanel == null) {
            clickableFilePathPanel = new ClickableFilePathPanel(true);
        }

        return clickableFilePathPanel;
    }

    private JLabel getLogExportLabel() {

        if (logExportLabel == null) {
            logExportLabel = new JLabel(" ");
        }

        return logExportLabel;
    }

    private JPanel getMainPanel() {

        JPanel mainPanel = new JPanel();

        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(10, 5, 10, 5);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(5, 5, 5, 5);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 2;
        gbc3.weightx = 1.0D;
        gbc3.weighty = 0.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(5, 5, 5, 5);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 0;
        gbc4.gridy = 3;
        gbc4.weightx = 1.0D;
        gbc4.weighty = 0.0D;
        gbc4.fill = GridBagConstraints.BOTH;
        gbc4.anchor = GridBagConstraints.NORTHWEST;
        gbc4.insets = new Insets(5, 5, 5, 5);

        GridBagConstraints gbc5 = new GridBagConstraints();
        gbc5.gridx = 0;
        gbc5.gridy = 4;
        gbc5.weightx = 1.0D;
        gbc5.weighty = 0.0D;
        gbc5.fill = GridBagConstraints.BOTH;
        gbc5.anchor = GridBagConstraints.NORTHWEST;
        gbc5.insets = new Insets(10, 5, 10, 5);

        JPanel selectorPanel = getSelectorPanel();
        JPanel buttonsPanel = getButtonsPanel();
        JProgressBar logExportJProgressBar = getLogExportProgressBar();
        ClickableFilePathPanel clickableFilePathPanel = getClickableFilePathPanel();
        JLabel logExportJLabel = getLogExportLabel();

        mainPanel.add(selectorPanel, gbc1);
        mainPanel.add(buttonsPanel, gbc2);
        mainPanel.add(logExportJProgressBar, gbc3);
        mainPanel.add(clickableFilePathPanel, gbc4);
        mainPanel.add(logExportJLabel, gbc5);

        return mainPanel;
    }

    private JPanel getSelectorPanel() {

        JPanel selectorPanel = new JPanel();

        selectorPanel.setLayout(new GridBagLayout());

        // ROW 1
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.CENTER;
        gbc1.insets = new Insets(0, 0, 0, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.CENTER;
        gbc2.insets = new Insets(0, 0, 0, 0);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 2;
        gbc3.gridy = 0;
        gbc3.weightx = 0.1D;
        gbc3.weighty = 0.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.CENTER;
        gbc3.insets = new Insets(0, 0, 0, 0);

        // ROW 2
        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 0;
        gbc4.gridy = 1;
        gbc4.weightx = 1.0D;
        gbc4.weighty = 1.0D;
        gbc4.fill = GridBagConstraints.BOTH;
        gbc4.anchor = GridBagConstraints.NORTHWEST;
        gbc4.insets = new Insets(0, 0, 0, 0);

        GridBagConstraints gbc5 = new GridBagConstraints();
        gbc5.gridx = 1;
        gbc5.gridy = 1;
        gbc5.weightx = 1.0D;
        gbc5.weighty = 1.0D;
        gbc5.fill = GridBagConstraints.BOTH;
        gbc5.anchor = GridBagConstraints.NORTHWEST;
        gbc5.insets = new Insets(0, 0, 0, 0);

        GridBagConstraints gbc6 = new GridBagConstraints();
        gbc6.gridx = 2;
        gbc6.gridy = 1;
        gbc6.weightx = 0.1D;
        gbc6.weighty = 1.0D;
        gbc6.fill = GridBagConstraints.BOTH;
        gbc6.anchor = GridBagConstraints.NORTHWEST;
        gbc6.insets = new Insets(0, 0, 0, 0);

        String labelText;

        labelText = "Select Log Columns";
        JPanel columnSelectLabelPanel = getHeaderLabelPanel(labelText);

        labelText = "Select Loggers";
        JPanel loggerSelectLabelPanel = getHeaderLabelPanel(labelText);

        labelText = "Message Column Parse Option";
        JPanel parseOptionLabelPanel = getHeaderLabelPanel(labelText);

        JPanel logColumnSelectPanel = getLogColumnSelectPanel();
        JPanel logMessagesLoggerSelectPanel = getLogMessagesLoggerSelectPanel();
        JPanel messageParsePanel = getMessageParsePanel();

        // ROW 1
        selectorPanel.add(columnSelectLabelPanel, gbc1);
        selectorPanel.add(loggerSelectLabelPanel, gbc2);
        selectorPanel.add(parseOptionLabelPanel, gbc3);

        // ROW 2
        selectorPanel.add(logColumnSelectPanel, gbc4);
        selectorPanel.add(logMessagesLoggerSelectPanel, gbc5);
        selectorPanel.add(messageParsePanel, gbc6);

        return selectorPanel;
    }

    private JPanel getLogColumnSelectPanel() {
        JPanel logColumnSelectPanel = new JPanel();

        return logColumnSelectPanel;
    }

    private JPanel getMessageParsePanel() {

        JPanel messageParsePanel = new JPanel();

        messageParsePanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(5, 5, 5, 2);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(5, 2, 5, 5);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 1;
        gbc3.weightx = 1.0D;
        gbc3.weighty = 0.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(5, 5, 5, 2);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 1;
        gbc4.gridy = 1;
        gbc4.weightx = 1.0D;
        gbc4.weighty = 0.0D;
        gbc4.fill = GridBagConstraints.BOTH;
        gbc4.anchor = GridBagConstraints.NORTHWEST;
        gbc4.insets = new Insets(5, 2, 5, 5);

        JLabel parseOptionLabel = new JLabel("Parse Option");
        JComboBox<ParseOption> parseOptionComboBox = getParseOptionComboBox();
        JLabel delimiterLabel = new JLabel("Delimiter");
        JTextField delimiterTextField = getDelimiterTextField();

        messageParsePanel.add(parseOptionLabel, gbc1);
        messageParsePanel.add(parseOptionComboBox, gbc2);
        messageParsePanel.add(delimiterLabel, gbc3);
        messageParsePanel.add(delimiterTextField, gbc4);

        messageParsePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        return messageParsePanel;
    }

    private JPanel getButtonsPanel() {

        JPanel buttonsPanel = new JPanel();

        LayoutManager layout = new BoxLayout(buttonsPanel, BoxLayout.X_AXIS);
        buttonsPanel.setLayout(layout);

        JButton logExportButton = getLogExportButton();
        JButton logExporCancelJButton = getLogExportCancelButton();

        Dimension dim = new Dimension(40, 40);
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(Box.createRigidArea(dim));
        buttonsPanel.add(logExportButton);
        buttonsPanel.add(Box.createRigidArea(dim));
        buttonsPanel.add(logExporCancelJButton);
        buttonsPanel.add(Box.createRigidArea(dim));
        buttonsPanel.add(Box.createHorizontalGlue());

        return buttonsPanel;
    }

    protected void performExport() {

        JFileChooser fileChooser = getFileChooser();

        int returnValue = fileChooser.showSaveDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {

            File exportFile = fileChooser.getSelectedFile();

            returnValue = JOptionPane.YES_OPTION;

            if (exportFile.exists()) {

                returnValue = JOptionPane.showConfirmDialog(this,
                        "Replace existing file '" + exportFile.getAbsolutePath() + "' ?", "File Exists",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            }

            if (returnValue == JOptionPane.YES_OPTION) {

                populateGeneratedExportFilePath(null);

                JButton logExportJButton = getLogExportButton();
                logExportJButton.setEnabled(false);

                JButton logExportCancelJButton = getLogExportCancelButton();
                logExportCancelJButton.setEnabled(true);

                JProgressBar logExportJProgressBar = getLogExportProgressBar();
                logExportJProgressBar.setEnabled(true);
                logExportJProgressBar.setVisible(true);

                LogMessagesLoggerSelectPanel logMessagesLoggerSelectPanel = getLogMessagesLoggerSelectPanel();

                boolean isEmptyLoggerColumnEntryList = logMessagesLoggerSelectPanel.isEmptyLoggerColumnEntryList();

                // collate log entry indexes for export
                Set<LogEntryKey> logEntrySet = new TreeSet<>();

                if (isEmptyLoggerColumnEntryList) {
                    // TODO
                } else {
                    List<CheckBoxMenuItemPopupEntry<LogEntryKey>> selectedLoggerColumnEntryList;
                    selectedLoggerColumnEntryList = logMessagesLoggerSelectPanel.getSelectedLoggerColumnEntryList();

                    for (CheckBoxMenuItemPopupEntry<LogEntryKey> loggerColumnEntry : selectedLoggerColumnEntryList) {

                        List<LogEntryKey> rowIndexList = loggerColumnEntry.getRowIndexList();

                        logEntrySet.addAll(rowIndexList);
                    }
                }

                final int rowCount = logEntrySet.size();

                JComboBox<ParseOption> parseOptionComboBox = getParseOptionComboBox();
                ParseOption parseOption = (ParseOption) parseOptionComboBox.getSelectedItem();

                String delimiter = getDelimiterTextField().getText();

                if (!delimiter.startsWith("\\")) {
                    delimiter = "\\" + delimiter;
                }

                logMessagesExportTask = new LogMessagesExportTask(logTableModel, exportFile, logEntrySet, parseOption,
                        delimiter) {

                    @Override
                    protected void process(List<Integer> chunks) {

                        if ((isCancelled()) || (chunks == null) || (chunks.size() == 0)) {
                            return;
                        }

                        Collections.sort(chunks);

                        int row = chunks.get(chunks.size() - 1);

                        int progress = (int) ((row * 100) / rowCount);

                        JProgressBar logExportJProgressBar = getLogExportProgressBar();
                        logExportJProgressBar.setValue(progress);

                        String message = String.format("Exported %d log entries (%d%%)", row, progress);

                        JLabel logExportJLabel = getLogExportLabel();

                        logExportJLabel.setText(message);
                    }

                    @Override
                    protected void done() {
                        try {
                            List<LogEntryKey> splitFailedList = get();
                            int splitFailedListSize = splitFailedList.size();

                            String message = null;

                            if (splitFailedListSize > 0) {
                                LOG.info("Log entry failed to split " + splitFailedList);
                                message = String.format("%d log entries failed to parse (%d)", splitFailedListSize,
                                        rowCount);
                            } else {
                                message = String.format("Exported %d log entries (%d%%)", rowCount, 100);
                            }

                            JLabel logExportJLabel = getLogExportLabel();
                            logExportJLabel.setText(message);

                        } catch (CancellationException ce) {
                            JLabel logExportJLabel = getLogExportLabel();
                            logExportJLabel.setText("Export cancelled.");
                            LOG.error("LogMessagesExportTask cancelled.", ce);
                        } catch (Exception e) {
                            LOG.error("Error in LogMessagesExportTask.", e);
                        } finally {

                            populateGeneratedExportFilePath(exportFile);

                            JButton logExportJButton = getLogExportButton();
                            logExportJButton.setEnabled(true);

                            JButton logExportCancelJButton = getLogExportCancelButton();
                            logExportCancelJButton.setEnabled(false);

                            JProgressBar logExportJProgressBar = getLogExportProgressBar();
                            logExportJProgressBar.setEnabled(false);
                        }
                    }
                };

                logMessagesExportTask.execute();

            } else {

                JLabel logExportJLabel = getLogExportLabel();
                logExportJLabel.setText("Export cancelled.");

                JProgressBar logExportJProgressBar = getLogExportProgressBar();
                logExportJProgressBar.setEnabled(false);
                logExportJProgressBar.setVisible(false);
            }
        } else {

            JLabel logExportJLabel = getLogExportLabel();
            logExportJLabel.setText("Export cancelled.");

            JProgressBar logExportJProgressBar = getLogExportProgressBar();
            logExportJProgressBar.setEnabled(false);
            logExportJProgressBar.setVisible(false);
        }
    }

    private void populateGeneratedExportFilePath(File exportFile) {

        ClickableFilePathPanel clickableFilePathPanel = getClickableFilePathPanel();

        clickableFilePathPanel.setFile(exportFile);
    }

    private String getDefaultMessagesFileName() {

        String fileName = FileUtilities.getFileBaseName(logFile);

        StringBuilder sb = new StringBuilder();
        sb.append(fileName);
        sb.append("-");
        sb.append("messages");
        sb.append(".tsv");

        String defaultXMLFileName = sb.toString();

        return defaultXMLFileName;
    }

    private void cancelTask() {

        if ((logMessagesExportTask != null)
                && ((!logMessagesExportTask.isCancelled()) || (!logMessagesExportTask.isDone()))) {
            logMessagesExportTask.cancel(true);
        }
    }

    private boolean isValidSelections() {

        boolean validSelections = true;

        String message = null;

        JComboBox<ParseOption> parseOptionComboBox = getParseOptionComboBox();
        ParseOption parseOption = (ParseOption) parseOptionComboBox.getSelectedItem();

        if (parseOption.equals(ParseOption.DELIMITER)) {

            String delimiter = getDelimiterTextField().getText();

            if ((delimiter == null) || ("".equals(delimiter))) {
                validSelections = false;
                message = "Empty delimiter.";
            }
        }

        if (validSelections) {

            LogMessagesLoggerSelectPanel logMessagesLoggerSelectPanel = getLogMessagesLoggerSelectPanel();

            List<CheckBoxMenuItemPopupEntry<LogEntryKey>> selectedLoggerColumnEntryList;
            selectedLoggerColumnEntryList = logMessagesLoggerSelectPanel.getSelectedLoggerColumnEntryList();

            if ((selectedLoggerColumnEntryList == null) || (selectedLoggerColumnEntryList.size() == 0)) {

                validSelections = false;

                message = "No logger selected.";
            }
        }

        JLabel logExportJLabel = getLogExportLabel();
        logExportJLabel.setText(message);

        return validSelections;
    }

    private JPanel getHeaderLabelPanel(String labelText) {

        JPanel headerLabelPanel = new JPanel();

        LayoutManager layout = new BoxLayout(headerLabelPanel, BoxLayout.X_AXIS);
        headerLabelPanel.setLayout(layout);

        JLabel headerLabel = LogViewerUtil.getHeaderLabel(labelText, -1);

        Dimension dim = new Dimension(10, 40);

        headerLabelPanel.add(Box.createHorizontalGlue());
        headerLabelPanel.add(Box.createRigidArea(dim));
        headerLabelPanel.add(headerLabel);
        headerLabelPanel.add(Box.createRigidArea(dim));
        headerLabelPanel.add(Box.createHorizontalGlue());

        headerLabelPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        return headerLabelPanel;
    }
}
