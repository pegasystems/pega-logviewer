/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.report.alertpal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Collections;
import java.util.List;

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
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.logviewer.LogTableModel;

public class AlertPALExportDialog extends JDialog {

	private static final long serialVersionUID = 1563645482258326358L;

	private static final Log4j2Helper LOG = new Log4j2Helper(AlertPALExportDialog.class);

	private AlertPALTableModel alertPALTableModel;

	private JButton alertPalExportJButton;

	private JButton alertPalExportCancelJButton;

	private JProgressBar alertPalExportJProgressBar;

	private JFileChooser fileChooser;

	private ClickableFilePathPanel alertPalClickableFilePathPanel;

	private JLabel alertPalExportJLabel;

	private AlertPALTableExportTask alertPALTableExportTask;

	private File logFile;

	public AlertPALExportDialog(LogTableModel logTableModel, AlertPALTableModel alertPALTableModel, ImageIcon appIcon,
			Component parent) {

		super();

		this.alertPALTableModel = alertPALTableModel;

		this.alertPALTableExportTask = null;

		RecentFile recentFile = logTableModel.getRecentFile();
		String recentFileStr = (String) recentFile.getAttribute(RecentFile.KEY_FILE);

		logFile = new File(recentFileStr);

		// check if the default output file is available
		String fileName = getDefaultTSVFileName();
		File currentDirectory = logFile.getParentFile();

		File proposedFile = new File(currentDirectory, fileName);

		if (proposedFile.exists() && proposedFile.isFile()) {
			populateGeneratedTSVPath(proposedFile);
		}

		setTitle("Export to TSV - " + logTableModel.getModelName());

		setIconImage(appIcon.getImage());

		setPreferredSize(new Dimension(500, 170));

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		setContentPane(getMainJPanel());

		pack();

		setLocationRelativeTo(parent);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);

				cancelTask();
			}
		});

	}

	private JButton getAlertPalExportJButton() {

		if (alertPalExportJButton == null) {
			alertPalExportJButton = new JButton("Export as TSV");

			alertPalExportJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					performPALExport();
				}
			});
		}

		return alertPalExportJButton;
	}

	private JButton getAlertPalExportCancelJButton() {

		if (alertPalExportCancelJButton == null) {
			alertPalExportCancelJButton = new JButton("Cancel");

			alertPalExportCancelJButton.setEnabled(false);

			alertPalExportCancelJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					cancelTask();
				}
			});
		}

		return alertPalExportCancelJButton;
	}

	private JProgressBar getAlertPalExportJProgressBar() {

		if (alertPalExportJProgressBar == null) {

			alertPalExportJProgressBar = new JProgressBar(0, 100);
			alertPalExportJProgressBar.setValue(0);
			alertPalExportJProgressBar.setStringPainted(true);

			Dimension dim = new Dimension(Integer.MAX_VALUE, 17);
			alertPalExportJProgressBar.setPreferredSize(dim);
			alertPalExportJProgressBar.setMinimumSize(dim);

			alertPalExportJProgressBar.setEnabled(false);
			alertPalExportJProgressBar.setVisible(false);

		}

		return alertPalExportJProgressBar;
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

	private JLabel getAlertPalExportJLabel() {

		if (alertPalExportJLabel == null) {

			alertPalExportJLabel = new JLabel();

			Dimension dim = new Dimension(Integer.MAX_VALUE, 20);
			alertPalExportJLabel.setPreferredSize(dim);
			alertPalExportJLabel.setMinimumSize(dim);

			alertPalExportJLabel.setAlignmentX(CENTER_ALIGNMENT);
		}

		return alertPalExportJLabel;
	}

	private JPanel getMainJPanel() {

		JPanel mainJPanel = new JPanel();

		mainJPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weightx = 1.0D;
		gbc1.weighty = 0.0D;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.insets = new Insets(10, 50, 5, 50);

		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 1;
		gbc2.gridy = 0;
		gbc2.weightx = 1.0D;
		gbc2.weighty = 0.0D;
		gbc2.fill = GridBagConstraints.BOTH;
		gbc2.anchor = GridBagConstraints.NORTHWEST;
		gbc2.insets = new Insets(10, 50, 5, 50);

		GridBagConstraints gbc3 = new GridBagConstraints();
		gbc3.gridx = 0;
		gbc3.gridy = 1;
		gbc3.weightx = 1.0D;
		gbc3.weighty = 1.0D;
		gbc3.fill = GridBagConstraints.BOTH;
		gbc3.anchor = GridBagConstraints.NORTHWEST;
		gbc3.insets = new Insets(5, 10, 5, 10);
		gbc3.gridwidth = GridBagConstraints.REMAINDER;

		GridBagConstraints gbc4 = new GridBagConstraints();
		gbc4.gridx = 0;
		gbc4.gridy = 2;
		gbc4.weightx = 1.0D;
		gbc4.weighty = 1.0D;
		gbc4.fill = GridBagConstraints.BOTH;
		gbc4.anchor = GridBagConstraints.NORTHWEST;
		gbc4.insets = new Insets(5, 10, 5, 10);
		gbc4.gridwidth = GridBagConstraints.REMAINDER;

		GridBagConstraints gbc5 = new GridBagConstraints();
		gbc5.gridx = 0;
		gbc5.gridy = 3;
		gbc5.weightx = 1.0D;
		gbc5.weighty = 1.0D;
		gbc5.fill = GridBagConstraints.BOTH;
		gbc5.anchor = GridBagConstraints.NORTHWEST;
		gbc5.insets = new Insets(5, 10, 10, 10);
		gbc5.gridwidth = GridBagConstraints.REMAINDER;

		JButton alertPalExportJButton = getAlertPalExportJButton();
		JButton alertPalExportCancelJButton = getAlertPalExportCancelJButton();
		JPanel progressBarJPanel = getProgressBarJPanel();
		ClickableFilePathPanel alertPalClickableFilePathPanel = getAlertPalClickableFilePathPanel();
		JLabel alertPalExportJLabel = getAlertPalExportJLabel();

		mainJPanel.add(alertPalExportJButton, gbc1);
		mainJPanel.add(alertPalExportCancelJButton, gbc2);
		mainJPanel.add(progressBarJPanel, gbc3);
		mainJPanel.add(alertPalClickableFilePathPanel, gbc4);
		mainJPanel.add(alertPalExportJLabel, gbc5);

		return mainJPanel;
	}

	private JPanel getProgressBarJPanel() {

		JPanel progressBarJPanel = new JPanel();
		progressBarJPanel.setLayout(new BorderLayout());

		Dimension dim = new Dimension(Integer.MAX_VALUE, 25);
		progressBarJPanel.setPreferredSize(dim);
		progressBarJPanel.setMinimumSize(dim);

		JProgressBar alertPalExportJProgressBar = getAlertPalExportJProgressBar();

		progressBarJPanel.add(alertPalExportJProgressBar, BorderLayout.CENTER);

		return progressBarJPanel;
	}

	protected void performPALExport() {

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

				JButton alertPalExportJButton = getAlertPalExportJButton();
				alertPalExportJButton.setEnabled(false);

				JButton alertPalExportCancelJButton = getAlertPalExportCancelJButton();
				alertPalExportCancelJButton.setEnabled(true);

				JProgressBar alertPalExportJProgressBar = getAlertPalExportJProgressBar();
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

						JProgressBar alertPalExportJProgressBar = getAlertPalExportJProgressBar();
						alertPalExportJProgressBar.setValue(progress);

						String message = String.format("Exported %d Alert PAL entries (%d%%)", row, progress);

						JLabel alertPalExportJLabel = getAlertPalExportJLabel();

						alertPalExportJLabel.setText(message);
					}

					@Override
					protected void done() {
						try {
							get();

							String message = String.format("Exported %d Alert PAL entries (%d%%)", rowCount, 100);
							JLabel alertPalExportJLabel = getAlertPalExportJLabel();
							alertPalExportJLabel.setText(message);

						} catch (Exception e) {
							LOG.error("Error in AlertPALTableExportTask", e);
						} finally {

							populateGeneratedTSVPath(exportFile);

							JButton alertPalExportJButton = getAlertPalExportJButton();
							alertPalExportJButton.setEnabled(true);

							JButton alertPalExportCancelJButton = getAlertPalExportCancelJButton();
							alertPalExportCancelJButton.setEnabled(false);

							JProgressBar alertPalExportJProgressBar = getAlertPalExportJProgressBar();
							alertPalExportJProgressBar.setEnabled(false);
						}

					}

				};

				alertPALTableExportTask.execute();

			} else {

				JLabel alertPalExportJLabel = getAlertPalExportJLabel();
				alertPalExportJLabel.setText("Export cancelled.");

				JProgressBar alertPalExportJProgressBar = getAlertPalExportJProgressBar();
				alertPalExportJProgressBar.setEnabled(false);
				alertPalExportJProgressBar.setVisible(false);
			}
		} else {

			JLabel alertPalExportJLabel = getAlertPalExportJLabel();
			alertPalExportJLabel.setText("Export cancelled.");

			JProgressBar alertPalExportJProgressBar = getAlertPalExportJProgressBar();
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

		// String outputFile =
		// filename.concat("-").concat(line).concat(".html");
		StringBuffer sb = new StringBuffer();
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
