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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.pega.gcs.fringecommon.guiutilities.Message;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkModel;
import com.pega.gcs.fringecommon.guiutilities.markerbar.MarkerBar;
import com.pega.gcs.fringecommon.guiutilities.search.SearchMarkerModel;
import com.pega.gcs.logviewer.systemscan.SystemScanTableModel;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntryKey;

public abstract class SystemScanView extends JPanel implements TableModelListener {

	private static final long serialVersionUID = 8968434918075553315L;

	private SystemScanTableModel systemScanTableModel;

	private JPanel supplementUtilityJPanel;

	private NavigationTableController<ScanResultHotfixEntryKey> navigationTableController;

	protected abstract void updateSupplementUtilityJPanel();

	public SystemScanView(SystemScanTableModel systemScanTableModel, JPanel supplementUtilityJPanel,
			NavigationTableController<ScanResultHotfixEntryKey> navigationTableController) {
		super();
		this.systemScanTableModel = systemScanTableModel;
		this.supplementUtilityJPanel = supplementUtilityJPanel;
		this.navigationTableController = navigationTableController;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.
	 * TableModelEvent)
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.UPDATE) {
			revalidate();
			repaint();
		}
	}

	protected SystemScanTableModel getSystemScanTableModel() {
		return systemScanTableModel;
	}

	protected JPanel getSupplementUtilityJPanel() {
		return supplementUtilityJPanel;
	}

	protected NavigationTableController<ScanResultHotfixEntryKey> getNavigationTableController() {
		return navigationTableController;
	}

	public void switchToFront() {
		updateSupplementUtilityJPanel();
	}

	protected void setMessage(JTextField statusBar, Message message) {

		if (message != null) {

			Color color = Color.BLUE;

			if (message.getMessageType().equals(Message.MessageType.ERROR)) {
				color = Color.RED;
			}

			String text = message.getText();

			statusBar.setForeground(color);
			statusBar.setText(text);
		}
	}

	protected JPanel getMarkerBarPanel(SystemScanTableModel systemScanTableModel) {

		JPanel markerBarPanel = new JPanel();
		markerBarPanel.setLayout(new BorderLayout());

		Dimension topDimension = new Dimension(16, 28);

		JLabel topSpacer = new JLabel();
		topSpacer.setPreferredSize(topDimension);
		topSpacer.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

		Dimension bottomDimension = new Dimension(16, 17);

		JLabel bottomSpacer = new JLabel();
		bottomSpacer.setPreferredSize(bottomDimension);
		bottomSpacer.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

		MarkerBar<ScanResultHotfixEntryKey> markerBar = getMarkerBar(systemScanTableModel);

		markerBarPanel.add(topSpacer, BorderLayout.NORTH);
		markerBarPanel.add(markerBar, BorderLayout.CENTER);
		markerBarPanel.add(bottomSpacer, BorderLayout.SOUTH);

		return markerBarPanel;
	}

	private MarkerBar<ScanResultHotfixEntryKey> getMarkerBar(SystemScanTableModel systemScanTableModel) {

		NavigationTableController<ScanResultHotfixEntryKey> navigationTableController = getNavigationTableController();

		SearchMarkerModel<ScanResultHotfixEntryKey> searchMarkerModel = new SearchMarkerModel<ScanResultHotfixEntryKey>(
				systemScanTableModel);

		MarkerBar<ScanResultHotfixEntryKey> markerBar = new MarkerBar<ScanResultHotfixEntryKey>(
				navigationTableController, searchMarkerModel);

		BookmarkModel<ScanResultHotfixEntryKey> bookmarkModel;
		bookmarkModel = systemScanTableModel.getBookmarkModel();

		markerBar.addMarkerModel(bookmarkModel);

		return markerBar;
	}

}
