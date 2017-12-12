/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.pega.gcs.fringecommon.guiutilities.Message;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.guiutilities.search.SearchPanel;
import com.pega.gcs.logviewer.systemscan.SystemScanTable;
import com.pega.gcs.logviewer.systemscan.SystemScanTableModel;
import com.pega.gcs.logviewer.systemscan.SystemScanTableMouseListener;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntryKey;

public class SystemScanSingleTableView extends SystemScanView {

	private static final long serialVersionUID = 5124626447046521621L;

	private SystemScanTable systemScanTable;

	private SearchPanel<ScanResultHotfixEntryKey> searchPanel;

	private JTextField statusBar;

	public SystemScanSingleTableView(SystemScanTableModel systemScanTableModel, JPanel supplementUtilityJPanel,
			NavigationTableController<ScanResultHotfixEntryKey> navigationTableController) {

		super(systemScanTableModel, supplementUtilityJPanel, navigationTableController);

		SystemScanTable systemScanTable = getSystemScanTable();

		navigationTableController.addCustomJTable(systemScanTable);

		searchPanel = new SearchPanel<>(navigationTableController, systemScanTableModel.getSearchModel());

		setLayout(new BorderLayout());

		JPanel utilityJPanel = getUtilityJPanel();
		JPanel scanResultDataJPanel = getScanResultDataJPanel();
		JPanel statusBarJPanel = getStatusBarJPanel();

		add(utilityJPanel, BorderLayout.NORTH);
		add(scanResultDataJPanel, BorderLayout.CENTER);
		add(statusBarJPanel, BorderLayout.SOUTH);

		systemScanTableModel.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {

				String propertyName = evt.getPropertyName();

				if ("message".equals(propertyName)) {

					JTextField statusBar = getStatusBar();
					Message message = (Message) evt.getNewValue();
					setMessage(statusBar, message);
				}

			}
		});
	}

	@Override
	protected void updateSupplementUtilityJPanel() {
		JPanel supplementUtilityJPanel = getSupplementUtilityJPanel();

		supplementUtilityJPanel.removeAll();
		LayoutManager layout = new BoxLayout(supplementUtilityJPanel, BoxLayout.LINE_AXIS);
		supplementUtilityJPanel.setLayout(layout);
		supplementUtilityJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

		supplementUtilityJPanel.revalidate();
		supplementUtilityJPanel.repaint();
	}

	private SystemScanTable getSystemScanTable() {

		if (systemScanTable == null) {
			
			SystemScanTableModel systemScanTableModel = getSystemScanTableModel();
			systemScanTable = new SystemScanTable(systemScanTableModel);

			systemScanTable.setFillsViewportHeight(true);

			SystemScanTableMouseListener systemScanTableMouseListener = new SystemScanTableMouseListener(this);

			systemScanTable.addMouseListener(systemScanTableMouseListener);
		}

		return systemScanTable;
	}

	private JTextField getStatusBar() {

		if (statusBar == null) {
			statusBar = new JTextField();
			statusBar.setEditable(false);
			statusBar.setBackground(null);
			statusBar.setBorder(null);
		}

		return statusBar;
	}

	private JPanel getUtilityJPanel() {

		JPanel utilityJPanel = new JPanel();

		LayoutManager layout = new BoxLayout(utilityJPanel, BoxLayout.LINE_AXIS);
		utilityJPanel.setLayout(layout);

		utilityJPanel.add(searchPanel);

		return utilityJPanel;
	}

	private JPanel getScanResultDataJPanel() {

		SystemScanTable systemScanTable = getSystemScanTable();

		JPanel scanResultTableJPanel = new JPanel();
		scanResultTableJPanel.setLayout(new BorderLayout());

		JScrollPane traceTableScrollpane = new JScrollPane(systemScanTable,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		systemScanTable.setPreferredScrollableViewportSize(traceTableScrollpane.getPreferredSize());

		SystemScanTableModel systemScanTableModel = getSystemScanTableModel();

		JPanel markerBarPanel = getMarkerBarPanel(systemScanTableModel);

		scanResultTableJPanel.add(traceTableScrollpane, BorderLayout.CENTER);
		scanResultTableJPanel.add(markerBarPanel, BorderLayout.EAST);

		return scanResultTableJPanel;
	}

	private JPanel getStatusBarJPanel() {

		JPanel statusBarJPanel = new JPanel();

		LayoutManager layout = new BoxLayout(statusBarJPanel, BoxLayout.LINE_AXIS);
		statusBarJPanel.setLayout(layout);

		Dimension spacer = new Dimension(5, 16);

		JTextField statusBar = getStatusBar();

		statusBarJPanel.add(Box.createRigidArea(spacer));
		statusBarJPanel.add(statusBar);
		statusBarJPanel.add(Box.createRigidArea(spacer));

		statusBarJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

		return statusBarJPanel;

	}
}
