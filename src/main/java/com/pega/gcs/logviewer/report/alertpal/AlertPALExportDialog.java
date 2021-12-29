/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.report.alertpal;

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
import com.pega.gcs.logviewer.LogTableModel;

public class AlertPALExportDialog extends JDialog {

    private static final long serialVersionUID = 1563645482258326358L;

    private static final Log4j2Helper LOG = new Log4j2Helper(AlertPALExportDialog.class);

    private AlertPALTableModel alertPALTableModel;

    private JButton alertPalExportButton;

    private JButton alertPalExportCancelButton;

    private JProgressBar alertPalExportProgressBar;

    private JFileChooser fileChooser;

    private ClickableFilePathPanel alertPalClickableFilePathPanel;

    private JLabel alertPalExportLabel;

    private AlertPALTableExportTask alertPALTableExportTask;

    private File logFile;

    public AlertPALExportDialog(LogTableModel logTableModel, AlertPALTableModel alertPALTableModel, ImageIcon appIcon,
            Component parent) {

        super();

        this.alertPALTableModel = alertPALTableModel;

        this.alertPALTableExportTask = null;

        String filePath = logTableModel.getFilePath();

        logFile = new File(filePath);

        // check if the default output file is available
        String fileName = getDefaultTSVFileName();
        File currentDirectory = logFile.getParentFile();

        File proposedFile = new File(currentDirectory, fileName);

        if (proposedFile.exists() && proposedFile.isFile()) {
            populateGeneratedTSVPath(proposedFile);
        }

        setPreferredSize(new Dimension(500, 200));

        setIconImage(appIcon.getImage());

        setTitle("Export to TSV - " + logTableModel.getModelName());

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

    private JButton getAlertPalExportButton() {

        if (alertPalExportButton == null) {

            alertPalExportButton = new JButton("Export as TSV");

            Dimension size = new Dimension(120, 26);
            alertPalExportButton.setPreferredSize(size);
            alertPalExportButton.setMinimumSize(size);
            alertPalExportButton.setMaximumSize(size);

            alertPalExportButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    performPALExport();
                }
            });
        }

        return alertPalExportButton;
    }

    private JButton getAlertPalExportCancelButton() {

        if (alertPalExportCancelButton == null) {
            alertPalExportCancelButton = new JButton("Cancel");

            Dimension size = new Dimension(120, 26);
            alertPalExportCancelButton.setPreferredSize(size);
            alertPalExportCancelButton.setMinimumSize(size);
            alertPalExportCancelButton.setMaximumSize(size);

            alertPalExportCancelButton.setEnabled(false);

            alertPalExportCancelButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    cancelTask();
                }
            });
        }

        return alertPalExportCancelButton;
    }

    private JProgressBar getAlertPalExportProgressBar() {

        if (alertPalExportProgressBar == null) {

            alertPalExportProgressBar = new JProgressBar(0, 100);
            alertPalExportProgressBar.setValue(0);
            alertPalExportProgressBar.setStringPainted(true);

        }

        return alertPalExportProgressBar;
    }

    private JFileChooser getFileChooser() {

        if (fileChooser == null) {

            String fileName = getDefaultTSVFileName();

            File currentDirectory = logFile.getParentFile();

            File proposedFile = new File(currentDirectory, fileName);

            fileChooser = new JFileChooser(currentDirectory);

            fileChooser.setDialogTitle("Save Tab-Seperated Value(.TSV) File");
            fileChooser.setSelectedFile(proposedFile);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            FileNameExtensionFilter filter = new FileNameExtensionFilter("Tab-Seperated Value Format (TSV)", "tsv");

            fileChooser.setFileFilter(filter);
        }

        return fileChooser;
    }

    private ClickableFilePathPanel getAlertPalClickableFilePathPanel() {

        if (alertPalClickableFilePathPanel == null) {
            alertPalClickableFilePathPanel = new ClickableFilePathPanel(true);
        }

        return alertPalClickableFilePathPanel;
    }

    private JLabel getAlertPalExportLabel() {

        if (alertPalExportLabel == null) {
            alertPalExportLabel = new JLabel(" ");
        }

        return alertPalExportLabel;
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
        JProgressBar alertPalExportJProgressBar = getAlertPalExportProgressBar();
        ClickableFilePathPanel alertPalClickableFilePathPanel = getAlertPalClickableFilePathPanel();
        JLabel alertPalExportJLabel = getAlertPalExportLabel();

        mainPanel.add(buttonsPanel, gbc1);
        mainPanel.add(alertPalExportJProgressBar, gbc2);
        mainPanel.add(alertPalClickableFilePathPanel, gbc3);
        mainPanel.add(alertPalExportJLabel, gbc4);

        return mainPanel;
    }

    private JPanel getButtonsPanel() {

        JPanel buttonsPanel = new JPanel();

        LayoutManager layout = new BoxLayout(buttonsPanel, BoxLayout.X_AXIS);
        buttonsPanel.setLayout(layout);

        JButton alertPalExportJButton = getAlertPalExportButton();
        JButton alertPalExportCancelJButton = getAlertPalExportCancelButton();

        Dimension dim = new Dimension(40, 40);
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(Box.createRigidArea(dim));
        buttonsPanel.add(alertPalExportJButton);
        buttonsPanel.add(Box.createRigidArea(dim));
        buttonsPanel.add(alertPalExportCancelJButton);
        buttonsPanel.add(Box.createRigidArea(dim));
        buttonsPanel.add(Box.createHorizontalGlue());

        return buttonsPanel;
    }

    private void performPALExport() {

        JFileChooser fileChooser = getFileChooser();

        int returnValue = fileChooser.showSaveDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {

            File exportFile = fileChooser.getSelectedFile();

            returnValue = JOptionPane.YES_OPTION;

            if (exportFile.exists()) {

                returnValue = JOptionPane.showConfirmDialog(this, "Replace Existing File?", "File Exists",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            }

            if (returnValue == JOptionPane.YES_OPTION) {

                populateGeneratedTSVPath(null);

                JButton alertPalExportJButton = getAlertPalExportButton();
                alertPalExportJButton.setEnabled(false);

                JButton alertPalExportCancelJButton = getAlertPalExportCancelButton();
                alertPalExportCancelJButton.setEnabled(true);

                JProgressBar alertPalExportJProgressBar = getAlertPalExportProgressBar();
                alertPalExportJProgressBar.setEnabled(true);
                alertPalExportJProgressBar.setVisible(true);

                final int rowCount = alertPALTableModel.getRowCount();

                alertPALTableExportTask = new AlertPALTableExportTask(alertPALTableModel, exportFile) {

                    @Override
                    protected void process(List<Integer> chunks) {

                        if ((isCancelled()) || (chunks == null) || (chunks.size() == 0)) {
                            return;
                        }

                        Collections.sort(chunks);

                        int row = chunks.get(chunks.size() - 1);

                        int progress = (int) ((row * 100) / rowCount);

                        JProgressBar alertPalExportJProgressBar = getAlertPalExportProgressBar();
                        alertPalExportJProgressBar.setValue(progress);

                        String message = String.format("Exported %d Alert PAL entries (%d%%)", row, progress);

                        JLabel alertPalExportJLabel = getAlertPalExportLabel();

                        alertPalExportJLabel.setText(message);
                    }

                    @Override
                    protected void done() {
                        try {
                            get();

                            String message = String.format("Exported %d Alert PAL entries (%d%%)", rowCount, 100);
                            JLabel alertPalExportJLabel = getAlertPalExportLabel();
                            alertPalExportJLabel.setText(message);

                        } catch (Exception e) {
                            LOG.error("Error in ReportTableExportTask", e);
                        } finally {

                            populateGeneratedTSVPath(exportFile);

                            JButton alertPalExportJButton = getAlertPalExportButton();
                            alertPalExportJButton.setEnabled(true);

                            JButton alertPalExportCancelJButton = getAlertPalExportCancelButton();
                            alertPalExportCancelJButton.setEnabled(false);

                            JProgressBar alertPalExportJProgressBar = getAlertPalExportProgressBar();
                            alertPalExportJProgressBar.setEnabled(false);
                        }

                    }

                };

                alertPALTableExportTask.execute();

            } else {

                JLabel alertPalExportJLabel = getAlertPalExportLabel();
                alertPalExportJLabel.setText("Export cancelled.");

                JProgressBar alertPalExportJProgressBar = getAlertPalExportProgressBar();
                alertPalExportJProgressBar.setEnabled(false);
                alertPalExportJProgressBar.setVisible(false);
            }
        } else {

            JLabel alertPalExportJLabel = getAlertPalExportLabel();
            alertPalExportJLabel.setText("Export cancelled.");

            JProgressBar alertPalExportJProgressBar = getAlertPalExportProgressBar();
            alertPalExportJProgressBar.setEnabled(false);
            alertPalExportJProgressBar.setVisible(false);
        }
    }

    private void populateGeneratedTSVPath(File alertPalReportFile) {

        ClickableFilePathPanel alertPalClickableFilePathPanel = getAlertPalClickableFilePathPanel();

        alertPalClickableFilePathPanel.setFile(alertPalReportFile);
    }

    private String getDefaultTSVFileName() {

        String fileName = FileUtilities.getNameWithoutExtension(logFile);

        StringBuilder sb = new StringBuilder();
        sb.append(fileName);
        sb.append("-PALData.tsv");

        String defaultXMLFileName = sb.toString();

        return defaultXMLFileName;
    }

    private void cancelTask() {

        if ((alertPALTableExportTask != null)
                && ((!alertPALTableExportTask.isCancelled()) || (!alertPALTableExportTask.isDone()))) {
            alertPALTableExportTask.cancel(true);
        }
    }
}
