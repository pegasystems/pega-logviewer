/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer;

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
import java.util.concurrent.CancellationException;

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

	private JButton logExportJButton;

	private JButton logExportCancelJButton;

	private JProgressBar logExportJProgressBar;

	private JFileChooser fileChooser;

	private ClickableFilePathPanel clickableFilePathPanel;

	private JLabel logExportJLabel;

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

		setTitle("Export to XML - " + logTableModel.getModelName());

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

	private JButton getLogExportJButton() {

		if (logExportJButton == null) {
			logExportJButton = new JButton("Export as XML");

			logExportJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					performXMLExport();
				}
			});
		}

		return logExportJButton;
	}

	private JButton getLogExportCancelJButton() {

		if (logExportCancelJButton == null) {
			logExportCancelJButton = new JButton("Cancel");

			logExportCancelJButton.setEnabled(false);

			logExportCancelJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					cancelTask();
				}
			});
		}

		return logExportCancelJButton;
	}

	private JProgressBar getLogExportJProgressBar() {

		if (logExportJProgressBar == null) {

			logExportJProgressBar = new JProgressBar(0, 100);
			logExportJProgressBar.setValue(0);
			logExportJProgressBar.setStringPainted(true);

			Dimension dim = new Dimension(Integer.MAX_VALUE, 17);
			logExportJProgressBar.setPreferredSize(dim);
			logExportJProgressBar.setMinimumSize(dim);

			logExportJProgressBar.setEnabled(false);
			logExportJProgressBar.setVisible(false);
		}

		return logExportJProgressBar;
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

	private JLabel getLogExportJLabel() {

		if (logExportJLabel == null) {

			logExportJLabel = new JLabel();

			Dimension dim = new Dimension(Integer.MAX_VALUE, 20);
			logExportJLabel.setPreferredSize(dim);
			logExportJLabel.setMinimumSize(dim);

			logExportJLabel.setAlignmentX(CENTER_ALIGNMENT);

			// logExportJLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,
			// 1));
		}

		return logExportJLabel;
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

		JButton logExportJButton = getLogExportJButton();
		JButton logExportCancelJButton = getLogExportCancelJButton();
		JPanel progressBarJPanel = getProgressBarJPanel();
		ClickableFilePathPanel clickableFilePathPanel = getClickableFilePathPanel();
		JLabel logExportJLabel = getLogExportJLabel();

		mainJPanel.add(logExportJButton, gbc1);
		mainJPanel.add(logExportCancelJButton, gbc2);
		mainJPanel.add(progressBarJPanel, gbc3);
		mainJPanel.add(clickableFilePathPanel, gbc4);
		mainJPanel.add(logExportJLabel, gbc5);

		return mainJPanel;
	}

	private JPanel getProgressBarJPanel() {

		JPanel progressBarJPanel = new JPanel();
		progressBarJPanel.setLayout(new BorderLayout());

		Dimension dim = new Dimension(Integer.MAX_VALUE, 25);
		progressBarJPanel.setPreferredSize(dim);
		progressBarJPanel.setMinimumSize(dim);

		JProgressBar logExportJProgressBar = getLogExportJProgressBar();

		progressBarJPanel.add(logExportJProgressBar, BorderLayout.CENTER);

		return progressBarJPanel;
	}

	protected void performXMLExport() {

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

				populateGeneratedXMLPath(null);

				JButton logExportJButton = getLogExportJButton();
				logExportJButton.setEnabled(false);

				JButton logExportCancelJButton = getLogExportCancelJButton();
				logExportCancelJButton.setEnabled(true);

				JProgressBar logExportJProgressBar = getLogExportJProgressBar();
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

						JProgressBar logExportJProgressBar = getLogExportJProgressBar();
						logExportJProgressBar.setValue(progress);

						String message = String.format("Exported %d log entries (%d%%)", row, progress);

						JLabel logExportJLabel = getLogExportJLabel();

						logExportJLabel.setText(message);
					}

					@Override
					protected void done() {
						try {
							get();

							String message = String.format("Exported %d log entries (%d%%)", rowCount, 100);
							JLabel logExportJLabel = getLogExportJLabel();
							logExportJLabel.setText(message);

						} catch (CancellationException ce) {
							JLabel logExportJLabel = getLogExportJLabel();
							logExportJLabel.setText("Export cancelled.");
							LOG.error("LogXMLExportTask cancelled.", ce);
						} catch (Exception e) {
							LOG.error("Error in LogXMLExportTask.", e);
						} finally {

							populateGeneratedXMLPath(exportFile);

							JButton logExportJButton = getLogExportJButton();
							logExportJButton.setEnabled(true);

							JButton logExportCancelJButton = getLogExportCancelJButton();
							logExportCancelJButton.setEnabled(false);

							JProgressBar logExportJProgressBar = getLogExportJProgressBar();
							logExportJProgressBar.setEnabled(false);
						}
					}
				};

				logXMLExportTask.execute();

			} else {

				JLabel logExportJLabel = getLogExportJLabel();
				logExportJLabel.setText("Export cancelled.");

				JProgressBar logExportJProgressBar = getLogExportJProgressBar();
				logExportJProgressBar.setEnabled(false);
				logExportJProgressBar.setVisible(false);
			}
		} else {

			JLabel logExportJLabel = getLogExportJLabel();
			logExportJLabel.setText("Export cancelled.");

			JProgressBar logExportJProgressBar = getLogExportJProgressBar();
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

		// String outputFile =
		// filename.concat("-").concat(line).concat(".html");
		StringBuffer sb = new StringBuffer();
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
