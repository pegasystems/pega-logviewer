/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jfree.ui.RefineryUtilities;

import com.pega.gcs.fringecommon.guiutilities.Searchable.SelectedRowPosition;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.logviewer.model.LogEntry;

public class LogNavigationDialog extends JFrame {

	private static final long serialVersionUID = 1116053624131571491L;

	private LogEntry logEntry;

	private LogEntry currentLogEntry;

	private LogTable logTable;

	// private LogEntryModel logEntryModel;

	private JPanel mainLogEntryJPanel;

	private ImageIcon firstImageIcon;

	private ImageIcon lastImageIcon;

	private ImageIcon prevImageIcon;

	private ImageIcon nextImageIcon;

	private JButton navPrevJButton;

	private JButton navNextJButton;

	private JButton navFirstJButton;

	private JButton navLastJButton;

	private JButton navResetJButton;

	private JLabel navIndexJLabel;

	protected LogNavigationDialog(LogEntry logEntry, LogTable logTable, ImageIcon appIcon) throws HeadlessException {

		super();

		this.logEntry = logEntry;
		this.logTable = logTable;

		LogTableModel ltm = (LogTableModel) logTable.getModel();
		String modelName = ltm.getModelName();

		initialize();

		setIconImage(appIcon.getImage());

		setPreferredSize(new Dimension(1200, 600));

		setTitle(modelName + " - Log Entry Details [" + logEntry.getKey() + "]");

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		setContentPane(getOverallJPanel());

		pack();

		RefineryUtilities.centerFrameOnScreen(this);

		// visible should be the last step
		setVisible(true);

	}

	/**
	 * @return the navPrevJButton
	 */
	private JButton getNavPrevJButton() {

		if (navPrevJButton == null) {

			navPrevJButton = new JButton("Previous", prevImageIcon);

			Dimension size = new Dimension(80, 20);
			navPrevJButton.setPreferredSize(size);
			navPrevJButton.setMaximumSize(size);
			navPrevJButton.setBorder(BorderFactory.createEmptyBorder());
			navPrevJButton.setToolTipText("Previous entry");
			navPrevJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					navigatePrevious();
				}
			});
		}

		return navPrevJButton;
	}

	/**
	 * @return the navNextJButton
	 */
	private JButton getNavNextJButton() {

		if (navNextJButton == null) {

			navNextJButton = new JButton("Next", nextImageIcon);

			Dimension size = new Dimension(80, 20);
			navNextJButton.setPreferredSize(size);
			navNextJButton.setMaximumSize(size);
			navNextJButton.setBorder(BorderFactory.createEmptyBorder());
			navNextJButton.setToolTipText("Next entry");
			navNextJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					navigateNext();
				}
			});
		}

		return navNextJButton;
	}

	/**
	 * @return the navFirstJButton
	 */
	private JButton getNavFirstJButton() {

		if (navFirstJButton == null) {

			navFirstJButton = new JButton("First", firstImageIcon);

			Dimension size = new Dimension(80, 20);
			navFirstJButton.setPreferredSize(size);
			navFirstJButton.setMaximumSize(size);
			navFirstJButton.setBorder(BorderFactory.createEmptyBorder());
			navFirstJButton.setToolTipText("First entry");
			navFirstJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					navigateFirst();
				}
			});
		}

		return navFirstJButton;
	}

	/**
	 * @return the navLastJButton
	 */
	private JButton getNavLastJButton() {

		if (navLastJButton == null) {

			navLastJButton = new JButton("Last", lastImageIcon);

			Dimension size = new Dimension(80, 20);
			navLastJButton.setPreferredSize(size);
			navLastJButton.setMaximumSize(size);
			navLastJButton.setBorder(BorderFactory.createEmptyBorder());
			navLastJButton.setToolTipText("Last entry");
			navLastJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					navigateLast();
				}
			});
		}

		return navLastJButton;
	}

	/**
	 * @return the navIndexJLabel
	 */
	private JLabel getNavIndexJLabel() {
		if (navIndexJLabel == null) {
			navIndexJLabel = new JLabel();

			Dimension size = new Dimension(60, 20);
			navIndexJLabel.setPreferredSize(size);
			navIndexJLabel.setMaximumSize(size);
			navIndexJLabel.setForeground(Color.BLUE);
			navIndexJLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			navIndexJLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return navIndexJLabel;
	}

	private JButton getNavResetJButton() {
		if (navResetJButton == null) {

			navResetJButton = new JButton("Reset");

			Dimension size = new Dimension(80, 20);
			navResetJButton.setPreferredSize(size);
			navResetJButton.setMaximumSize(size);
			navResetJButton.setBorder(BorderFactory.createEmptyBorder());
			navResetJButton.setToolTipText("Reset to original log Entry");
			navResetJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					navigateReset();
				}
			});
		}

		return navResetJButton;
	}

	private void initialize() {

		firstImageIcon = FileUtilities.getImageIcon(this.getClass(), "first.png");

		lastImageIcon = FileUtilities.getImageIcon(this.getClass(), "last.png");

		prevImageIcon = FileUtilities.getImageIcon(this.getClass(), "prev.png");

		nextImageIcon = FileUtilities.getImageIcon(this.getClass(), "next.png");

		currentLogEntry = logEntry;

		populateMainLogEntryJPanel();
	}

	private JPanel getOverallJPanel() {

		JPanel overallJPanel = new JPanel();

		overallJPanel.setLayout(new BorderLayout());

		JPanel toolbarJPanel = getToolbarJPanel();
		JPanel mainLogEntryJPanel = getMainLogEntryJPanel();

		overallJPanel.add(toolbarJPanel, BorderLayout.NORTH);
		overallJPanel.add(mainLogEntryJPanel, BorderLayout.CENTER);

		return overallJPanel;
	}

	private JPanel getToolbarJPanel() {

		JPanel toolbarJPanel = new JPanel();

		LayoutManager layout = new BoxLayout(toolbarJPanel, BoxLayout.LINE_AXIS);
		toolbarJPanel.setLayout(layout);

		JButton navPrevJButton = getNavPrevJButton();
		JButton navNextJButton = getNavNextJButton();
		JLabel navIndexJLabel = getNavIndexJLabel();
		JButton navFirstJButton = getNavFirstJButton();
		JButton navLastJButton = getNavLastJButton();
		JButton navResetJButton = getNavResetJButton();

		Dimension spacer = new Dimension(10, 30);
		Dimension rigidArea = new Dimension(60, 30);

		toolbarJPanel.add(Box.createHorizontalGlue());
		toolbarJPanel.add(Box.createRigidArea(spacer));
		toolbarJPanel.add(navFirstJButton);
		toolbarJPanel.add(Box.createRigidArea(spacer));
		toolbarJPanel.add(navPrevJButton);
		toolbarJPanel.add(Box.createRigidArea(spacer));
		toolbarJPanel.add(navIndexJLabel);
		toolbarJPanel.add(Box.createRigidArea(spacer));
		toolbarJPanel.add(navNextJButton);
		toolbarJPanel.add(Box.createRigidArea(spacer));
		toolbarJPanel.add(navLastJButton);
		toolbarJPanel.add(Box.createRigidArea(rigidArea));
		// toolbarJPanel.add(Box.createHorizontalGlue());
		toolbarJPanel.add(navResetJButton);
		toolbarJPanel.add(Box.createHorizontalGlue());

		toolbarJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		return toolbarJPanel;
	}

	private JPanel getMainLogEntryJPanel() {

		if (mainLogEntryJPanel == null) {
			mainLogEntryJPanel = new JPanel();
			mainLogEntryJPanel.setLayout(new BorderLayout());

		}

		return mainLogEntryJPanel;
	}

	private void populateMainLogEntryJPanel() {

		JPanel mainLogEntryJPanel = getMainLogEntryJPanel();

		mainLogEntryJPanel.removeAll();

		LogTableModel ltm = (LogTableModel) logTable.getModel();

		JPanel logEntryPanel = currentLogEntry.getDetailsJPanel(ltm);

		Integer logEntryIndex = currentLogEntry.getKey();

		mainLogEntryJPanel.add(logEntryPanel, BorderLayout.CENTER);

		logTable.logTableScrollToIndex(logEntryIndex);

		updateNavButtonStates(logEntryIndex);
	}

	private void navigate(boolean forward, boolean first, boolean last) {

		int rowNumber = -1;

		LogTableModel ltm = (LogTableModel) logTable.getModel();

		List<Integer> ltmIndexList = ltm.getFtmEntryKeyList();
		int ltmLastIndex = ltmIndexList.size() - 1;

		if (ltmIndexList.size() > 0) {

			if (first) {
				rowNumber = 0;

			} else if (last) {
				rowNumber = ltmLastIndex;

			} else {

				Integer currentLogEntryIndex = currentLogEntry.getKey();

				rowNumber = Collections.binarySearch(ltmIndexList, currentLogEntryIndex);

				if (rowNumber < 0) {
					rowNumber = (rowNumber * -1) - 1;
				}

				if (forward) {
					rowNumber++;
				} else {
					rowNumber--;
				}

				if (rowNumber < 0) {
					rowNumber = 0;
				} else if (rowNumber > ltmLastIndex) {
					rowNumber = ltmLastIndex;
				}

			}

			currentLogEntry = (LogEntry) ltm.getValueAt(rowNumber, 0);

			populateMainLogEntryJPanel();

			logTable.setRowSelectionInterval(rowNumber, rowNumber);
			logTable.scrollRowToVisible(rowNumber);
		}
	}

	protected void navigatePrevious() {
		navigate(false, false, false);
	}

	protected void navigateNext() {
		navigate(true, false, false);
	}

	protected void navigateFirst() {
		navigate(false, true, false);
	}

	protected void navigateLast() {
		navigate(false, false, true);
	}

	protected void navigateReset() {

		currentLogEntry = logEntry;

		populateMainLogEntryJPanel();
	}

	private void updateNavButtonStates(Integer logEntryIndex) {

		LogTableModel logTableModel = (LogTableModel) logTable.getModel();

		JButton navFirstJButton = getNavFirstJButton();
		JButton navPrevJButton = getNavPrevJButton();
		JButton navNextJButton = getNavNextJButton();
		JButton navLastJButton = getNavLastJButton();
		JLabel navIndexJLabel = getNavIndexJLabel();

		SelectedRowPosition selectedRowPosition = SelectedRowPosition.NONE;

		List<Integer> logEntryIndexList = logTableModel.getFtmEntryKeyList();

		if (logEntryIndexList.size() > 0) {

			int size = logEntryIndexList.size();

			int firstIndex = logEntryIndexList.get(0);
			int lastIndex = logEntryIndexList.get(size - 1);

			if ((logEntryIndex > firstIndex) && (logEntryIndex < lastIndex)) {
				selectedRowPosition = SelectedRowPosition.BETWEEN;
			} else if (logEntryIndex <= firstIndex) {
				selectedRowPosition = SelectedRowPosition.FIRST;
			} else if (logEntryIndex >= lastIndex) {
				selectedRowPosition = SelectedRowPosition.LAST;
			} else {
				selectedRowPosition = SelectedRowPosition.NONE;
			}
		}

		switch (selectedRowPosition) {

		case FIRST:
			navFirstJButton.setEnabled(false);
			navPrevJButton.setEnabled(false);
			navNextJButton.setEnabled(true);
			navLastJButton.setEnabled(true);
			break;

		case LAST:
			navFirstJButton.setEnabled(true);
			navPrevJButton.setEnabled(true);
			navNextJButton.setEnabled(false);
			navLastJButton.setEnabled(false);
			break;

		case BETWEEN:
			navFirstJButton.setEnabled(true);
			navPrevJButton.setEnabled(true);
			navNextJButton.setEnabled(true);
			navLastJButton.setEnabled(true);
			break;

		case NONE:
			navFirstJButton.setEnabled(false);
			navPrevJButton.setEnabled(false);
			navNextJButton.setEnabled(false);
			navLastJButton.setEnabled(false);
			break;
		default:
			break;
		}

		navIndexJLabel.setText(logEntryIndex.toString());

	}
}
