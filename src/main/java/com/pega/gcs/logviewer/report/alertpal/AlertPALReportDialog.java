/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.report.alertpal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.guiutilities.BaseFrame;
import com.pega.gcs.fringecommon.guiutilities.MultipleTableSelectionListener;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.logviewer.LogTableModel;

public class AlertPALReportDialog extends JFrame {

	private static final long serialVersionUID = -4294293375116690122L;

	private LogTableModel logTableModel;

	private AlertPALTable alertPALTable;

	private JButton exportToTSVJButton;

	private MultipleTableSelectionListener multipleTableSelectionListener;

	public AlertPALReportDialog(LogTableModel logTableModel,
			NavigationTableController<Integer> navigationTableController, ImageIcon appIcon, Component parent) {

		super();

		this.logTableModel = logTableModel;

		setTitle("Alert PAL Overview - " + logTableModel.getModelName());

		multipleTableSelectionListener = new MultipleTableSelectionListener(logTableModel, navigationTableController);

		setIconImage(appIcon.getImage());

		setPreferredSize(new Dimension(1200, 600));

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		setContentPane(getMainJPanel());

		pack();

		setLocationRelativeTo(parent);

		// setVisible called by caller.
		// setVisible(true);

	}

	public void destroyJPanel() {
		multipleTableSelectionListener.clearCustomJTables();
	}

	private JComponent getMainJPanel() {

		JPanel mainJPanel = new JPanel();
		mainJPanel.setLayout(new BorderLayout());

		JPanel controlsJPanel = getControlsJPanel();

		JTable alertPALTable = getAlertPALTable();

		JScrollPane palJTableScrollpane = new JScrollPane(alertPALTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		palJTableScrollpane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

		mainJPanel.add(controlsJPanel, BorderLayout.NORTH);

		mainJPanel.add(palJTableScrollpane, BorderLayout.CENTER);

		return mainJPanel;
	}

	private JPanel getControlsJPanel() {

		JPanel controlsJPanel = new JPanel();

		LayoutManager layout = new BoxLayout(controlsJPanel, BoxLayout.X_AXIS);
		controlsJPanel.setLayout(layout);

		JButton exportToTSVJButton = getExportToTSVJButton();

		Dimension startDim = new Dimension(20, 40);

		controlsJPanel.add(Box.createHorizontalGlue());
		controlsJPanel.add(Box.createRigidArea(startDim));
		controlsJPanel.add(exportToTSVJButton);
		controlsJPanel.add(Box.createRigidArea(startDim));
		controlsJPanel.add(Box.createHorizontalGlue());
		controlsJPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

		return controlsJPanel;
	}

	private AlertPALTable getAlertPALTable() {

		if (alertPALTable == null) {

			AlertPALTableModel aptm = new AlertPALTableModel(logTableModel);

			alertPALTable = new AlertPALTable(aptm);

			multipleTableSelectionListener.addCustomJTable(alertPALTable);
		}

		return alertPALTable;
	}

	protected JButton getExportToTSVJButton() {

		if (exportToTSVJButton == null) {
			exportToTSVJButton = new JButton("Export table as TSV");

			Dimension size = new Dimension(200, 20);
			exportToTSVJButton.setPreferredSize(size);
			exportToTSVJButton.setMaximumSize(size);
			exportToTSVJButton.setHorizontalTextPosition(SwingConstants.LEADING);

			exportToTSVJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					JTable alertPALTable = getAlertPALTable();

					AlertPALTableModel aptm = (AlertPALTableModel) alertPALTable.getModel();

					AlertPALExportDialog apaled = new AlertPALExportDialog(logTableModel, aptm, BaseFrame.getAppIcon(),
							AlertPALReportDialog.this);
					apaled.setVisible(true);
				}
			});

		}

		return exportToTSVJButton;
	}

}
