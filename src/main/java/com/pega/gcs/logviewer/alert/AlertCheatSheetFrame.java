/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.alert;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class AlertCheatSheetFrame extends JFrame {

	private static final long serialVersionUID = -5658833974131181820L;

	public AlertCheatSheetFrame(String title, ImageIcon appIcon, Component parent) {

		setTitle(title + " - Alert Cheat Sheet");

		setIconImage(appIcon.getImage());

		// setPreferredSize(new Dimension(1150, 600));

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		setContentPane(getMainJPanel());

		pack();

		setExtendedState(Frame.MAXIMIZED_BOTH);
	}

	private JPanel getMainJPanel() {

		JPanel mainJPanel = new JPanel();
		mainJPanel.setLayout(new BorderLayout());

		Dimension dim = new Dimension(Integer.MAX_VALUE, 30);
		JPanel topJPanel = new JPanel();
		topJPanel.setPreferredSize(dim);
		topJPanel.setSize(dim);

		JPanel bottomJPanel = new JPanel();
		bottomJPanel.setPreferredSize(dim);
		bottomJPanel.setSize(dim);

		JTable alertCheatSheetTable = getAlertCheatSheetTable();

		JScrollPane alertCheatSheetTableScrollPane = new JScrollPane(alertCheatSheetTable);

		mainJPanel.add(topJPanel, BorderLayout.NORTH);
		mainJPanel.add(alertCheatSheetTableScrollPane, BorderLayout.CENTER);
		mainJPanel.add(bottomJPanel, BorderLayout.SOUTH);

		return mainJPanel;
	}

	private AlertCheatSheetTable getAlertCheatSheetTable() {

		AlertCheatSheetTableModel alertCheatSheetTableModel = new AlertCheatSheetTableModel();
		AlertCheatSheetTable alertCheatSheetTable = new AlertCheatSheetTable(alertCheatSheetTableModel);

		return alertCheatSheetTable;
	}
}
