
package com.pega.gcs.logviewer.report;

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

public class LogReportExportDialog extends JDialog {

    private static final long serialVersionUID = 1563645482258326358L;

    private static final Log4j2Helper LOG = new Log4j2Helper(LogReportExportDialog.class);

    private LogReportTableModel logReportTableModel;

    private String context;

    private JButton exportButton;

    private JButton exportCancelButton;

    private JProgressBar exportProgressBar;

    private JFileChooser fileChooser;

    private ClickableFilePathPanel clickableFilePathPanel;

    private JLabel exportLabel;

    private ReportTableExportTask reportTableExportTask;

    private File logFile;

    public LogReportExportDialog(LogTableModel logTableModel, LogReportTableModel logReportTableModel, String context,
            ImageIcon appIcon, Component parent) {

        super();

        this.logReportTableModel = logReportTableModel;
        this.context = context;

        this.reportTableExportTask = null;

        String filePath = logTableModel.getFilePath();

        logFile = new File(filePath);

        // check if the default output file is available
        String fileName = getDefaultTSVFileName();
        File currentDirectory = logFile.getParentFile();

        File proposedFile = new File(currentDirectory, fileName);

        if (proposedFile.exists() && proposedFile.isFile()) {
            populateGeneratedTsvPath(proposedFile);
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

    private JButton getExportButton() {

        if (exportButton == null) {

            exportButton = new JButton("Export as TSV");

            Dimension size = new Dimension(120, 26);
            exportButton.setPreferredSize(size);
            exportButton.setMinimumSize(size);
            exportButton.setMaximumSize(size);

            exportButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    performPALExport();
                }
            });
        }

        return exportButton;
    }

    private JButton getExportCancelButton() {

        if (exportCancelButton == null) {
            exportCancelButton = new JButton("Cancel");

            Dimension size = new Dimension(120, 26);
            exportCancelButton.setPreferredSize(size);
            exportCancelButton.setMinimumSize(size);
            exportCancelButton.setMaximumSize(size);

            exportCancelButton.setEnabled(false);

            exportCancelButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    cancelTask();
                }
            });
        }

        return exportCancelButton;
    }

    private JProgressBar getExportProgressBar() {

        if (exportProgressBar == null) {

            exportProgressBar = new JProgressBar(0, 100);
            exportProgressBar.setValue(0);
            exportProgressBar.setStringPainted(true);

        }

        return exportProgressBar;
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

    private ClickableFilePathPanel getClickableFilePathPanel() {

        if (clickableFilePathPanel == null) {
            clickableFilePathPanel = new ClickableFilePathPanel(true);
        }

        return clickableFilePathPanel;
    }

    private JLabel getExportLabel() {

        if (exportLabel == null) {
            exportLabel = new JLabel(" ");
        }

        return exportLabel;
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
        JProgressBar exportProgressBar = getExportProgressBar();
        ClickableFilePathPanel clickableFilePathPanel = getClickableFilePathPanel();
        JLabel exportLabel = getExportLabel();

        mainPanel.add(buttonsPanel, gbc1);
        mainPanel.add(exportProgressBar, gbc2);
        mainPanel.add(clickableFilePathPanel, gbc3);
        mainPanel.add(exportLabel, gbc4);

        return mainPanel;
    }

    private JPanel getButtonsPanel() {

        JPanel buttonsPanel = new JPanel();

        LayoutManager layout = new BoxLayout(buttonsPanel, BoxLayout.X_AXIS);
        buttonsPanel.setLayout(layout);

        JButton exportButton = getExportButton();
        JButton exportCancelButton = getExportCancelButton();

        Dimension dim = new Dimension(40, 40);
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(Box.createRigidArea(dim));
        buttonsPanel.add(exportButton);
        buttonsPanel.add(Box.createRigidArea(dim));
        buttonsPanel.add(exportCancelButton);
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

                populateGeneratedTsvPath(null);

                JButton exportButton = getExportButton();
                exportButton.setEnabled(false);

                JButton exportCancelButton = getExportCancelButton();
                exportCancelButton.setEnabled(true);

                JProgressBar exportProgressBar = getExportProgressBar();
                exportProgressBar.setEnabled(true);
                exportProgressBar.setVisible(true);

                final int rowCount = logReportTableModel.getRowCount();

                reportTableExportTask = new ReportTableExportTask(logReportTableModel, exportFile) {

                    @Override
                    protected void process(List<Integer> chunks) {

                        if ((isCancelled()) || (chunks == null) || (chunks.size() == 0)) {
                            return;
                        }

                        Collections.sort(chunks);

                        int row = chunks.get(chunks.size() - 1);

                        int progress = (int) ((row * 100) / rowCount);

                        JProgressBar exportProgressBar = getExportProgressBar();
                        exportProgressBar.setValue(progress);

                        String message = String.format("Exported %d entries (%d%%)", row, progress);

                        JLabel exportLabel = getExportLabel();

                        exportLabel.setText(message);
                    }

                    @Override
                    protected void done() {
                        try {
                            get();

                            String message = String.format("Exported %d entries (%d%%)", rowCount, 100);
                            JLabel exportLabel = getExportLabel();
                            exportLabel.setText(message);

                        } catch (Exception e) {
                            LOG.error("Error in ReportTableExportTask", e);
                        } finally {

                            populateGeneratedTsvPath(exportFile);

                            JButton exportButton = getExportButton();
                            exportButton.setEnabled(true);

                            JButton exportCancelButton = getExportCancelButton();
                            exportCancelButton.setEnabled(false);

                            JProgressBar exportProgressBar = getExportProgressBar();
                            exportProgressBar.setEnabled(false);
                        }

                    }

                };

                reportTableExportTask.execute();

            } else {

                JLabel exportLabel = getExportLabel();
                exportLabel.setText("Export cancelled.");

                JProgressBar exportProgressBar = getExportProgressBar();
                exportProgressBar.setEnabled(false);
                exportProgressBar.setVisible(false);
            }
        } else {

            JLabel exportLabel = getExportLabel();
            exportLabel.setText("Export cancelled.");

            JProgressBar exportProgressBar = getExportProgressBar();
            exportProgressBar.setEnabled(false);
            exportProgressBar.setVisible(false);
        }
    }

    private void populateGeneratedTsvPath(File reportFile) {

        ClickableFilePathPanel clickableFilePathPanel = getClickableFilePathPanel();

        clickableFilePathPanel.setFile(reportFile);
    }

    private String getDefaultTSVFileName() {

        String fileName = FileUtilities.getNameWithoutExtension(logFile);

        StringBuilder sb = new StringBuilder();
        sb.append(fileName);
        sb.append("-");
        sb.append(context);
        sb.append(".tsv");

        String defaultXMLFileName = sb.toString();

        return defaultXMLFileName;
    }

    private void cancelTask() {

        if ((reportTableExportTask != null)
                && ((!reportTableExportTask.isCancelled()) || (!reportTableExportTask.isDone()))) {
            reportTableExportTask.cancel(true);
        }
    }
}
