/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer;

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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.pega.gcs.fringecommon.guiutilities.ClickableFilePathPanel;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.FileUtilities;

public class LogXMLExportDialog extends JDialog {

    private static final long serialVersionUID = 1563645482258326358L;

    private static final Log4j2Helper LOG = new Log4j2Helper(LogXMLExportDialog.class);

    private LogTableModel logTableModel;

    private JButton logExportButton;

    private JButton logExportCancelButton;

    private JProgressBar logExportProgressBar;

    private JFileChooser fileChooser;

    private ClickableFilePathPanel clickableFilePathPanel;

    private JLabel logExportLabel;

    private LogXMLExportTask logXMLExportTask;

    private File logFile;

    public LogXMLExportDialog(LogTableModel logTableModel, ImageIcon appIcon, Component parent) {

        super();

        this.logTableModel = logTableModel;

        this.logXMLExportTask = null;

        String filePath = logTableModel.getFilePath();

        logFile = new File(filePath);

        // check if the default output file is available
        String fileName = getDefaultXMLFileName();
        File currentDirectory = logFile.getParentFile();

        File proposedFile = new File(currentDirectory, fileName);

        if (proposedFile.exists() && proposedFile.isFile()) {
            populateGeneratedXMLPath(proposedFile);
        }

        setPreferredSize(new Dimension(500, 200));

        setIconImage(appIcon.getImage());

        setTitle("Export to XML - " + logTableModel.getModelName());

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

    private JButton getLogExportButton() {

        if (logExportButton == null) {
            logExportButton = new JButton("Export as XML");

            logExportButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    performXMLExport();
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

            String fileName = getDefaultXMLFileName();

            File currentDirectory = logFile.getParentFile();

            File proposedFile = new File(currentDirectory, fileName);

            fileChooser = new JFileChooser(currentDirectory);

            fileChooser.setDialogTitle("Save XML(.xml) File");
            fileChooser.setSelectedFile(proposedFile);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Format", "xml");

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
        gbc1.weighty = 0.0D;
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
        gbc4.insets = new Insets(10, 5, 10, 5);

        JPanel buttonsPanel = getButtonsPanel();
        JProgressBar logExportJProgressBar = getLogExportProgressBar();
        ClickableFilePathPanel clickableFilePathPanel = getClickableFilePathPanel();
        JLabel logExportJLabel = getLogExportLabel();

        mainPanel.add(buttonsPanel, gbc1);
        mainPanel.add(logExportJProgressBar, gbc2);
        mainPanel.add(clickableFilePathPanel, gbc3);
        mainPanel.add(logExportJLabel, gbc4);

        return mainPanel;
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

    protected void performXMLExport() {

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

                populateGeneratedXMLPath(null);

                JButton logExportJButton = getLogExportButton();
                logExportJButton.setEnabled(false);

                JButton logExportCancelJButton = getLogExportCancelButton();
                logExportCancelJButton.setEnabled(true);

                JProgressBar logExportJProgressBar = getLogExportProgressBar();
                logExportJProgressBar.setEnabled(true);
                logExportJProgressBar.setVisible(true);

                final int rowCount = logTableModel.getLogEntryModel().getTotalRowCount();

                logXMLExportTask = new LogXMLExportTask(logTableModel, exportFile) {

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
                            get();

                            String message = String.format("Exported %d log entries (%d%%)", rowCount, 100);
                            JLabel logExportJLabel = getLogExportLabel();
                            logExportJLabel.setText(message);

                        } catch (CancellationException ce) {
                            JLabel logExportJLabel = getLogExportLabel();
                            logExportJLabel.setText("Export cancelled.");
                            LOG.error("LogXMLExportTask cancelled.", ce);
                        } catch (Exception e) {
                            LOG.error("Error in LogXMLExportTask.", e);
                        } finally {

                            populateGeneratedXMLPath(exportFile);

                            JButton logExportJButton = getLogExportButton();
                            logExportJButton.setEnabled(true);

                            JButton logExportCancelJButton = getLogExportCancelButton();
                            logExportCancelJButton.setEnabled(false);

                            JProgressBar logExportJProgressBar = getLogExportProgressBar();
                            logExportJProgressBar.setEnabled(false);
                        }
                    }
                };

                logXMLExportTask.execute();

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

    private void populateGeneratedXMLPath(File xmlFile) {

        ClickableFilePathPanel clickableFilePathPanel = getClickableFilePathPanel();

        clickableFilePathPanel.setFile(xmlFile);
    }

    private String getDefaultXMLFileName() {

        String fileName = FileUtilities.getNameWithoutExtension(logFile);

        StringBuilder sb = new StringBuilder();
        sb.append(fileName);
        sb.append(".xml");

        String defaultXMLFileName = sb.toString();

        return defaultXMLFileName;
    }

    private void cancelTask() {

        if ((logXMLExportTask != null) && ((!logXMLExportTask.isCancelled()) || (!logXMLExportTask.isDone()))) {
            logXMLExportTask.cancel(true);
        }
    }
}
