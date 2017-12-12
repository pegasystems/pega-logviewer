/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan;

import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import com.pega.gcs.fringecommon.guiutilities.BaseFrame;
import com.pega.gcs.fringecommon.guiutilities.RightClickMenuItem;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntryKey;

public class SystemScanTableMouseListener extends MouseAdapter {

	private Map<ScanResultHotfixEntryKey, JFrame> scanResultHotfixDialogMap;

	private Component mainWindow;

	/**
	 * @param logTableList
	 */
	public SystemScanTableMouseListener(Component mainWindow) {

		this.mainWindow = mainWindow;
		scanResultHotfixDialogMap = new HashMap<ScanResultHotfixEntryKey, JFrame>();
	}

	protected Component getMainWindow() {
		return mainWindow;
	}

	protected Map<ScanResultHotfixEntryKey, JFrame> getScanResultHotfixDialogMap() {
		return scanResultHotfixDialogMap;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		final SystemScanTable source = (SystemScanTable) e.getSource();
		final SystemScanTableModel systemScanTableModel = (SystemScanTableModel) source.getModel();

		if (SwingUtilities.isRightMouseButton(e)) {

			Point point = e.getPoint();

			final List<Integer> selectedRowList = new LinkedList<Integer>();

			int[] selectedRows = source.getSelectedRows();

			// in case the row was not selected when right clicking then based
			// on the point, select the row.
			if ((selectedRows != null) && (selectedRows.length <= 1)) {

				int selectedRow = source.rowAtPoint(point);

				if (selectedRow != -1) {
					// select the row first
					source.setRowSelectionInterval(selectedRow, selectedRow);
					selectedRows = new int[] { selectedRow };
				}
			}

			for (int selectedRow : selectedRows) {
				selectedRowList.add(selectedRow);
			}

			final int size = selectedRowList.size();

			if (size > 0) {

				final JPopupMenu popupMenu = new JPopupMenu();

				// ImageIcon appIcon = BaseFrame.getAppIcon();

				final RightClickMenuItem copyMenuItem = new RightClickMenuItem("Copy");

				copyMenuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

						int[] array = selectedRowList.stream().mapToInt(i -> i).toArray();
						String selectedRowsData = systemScanTableModel.getSelectedRowsData(array);

						clipboard.setContents(new StringSelection(selectedRowsData), copyMenuItem);

						popupMenu.setVisible(false);

					}
				});

				popupMenu.add(copyMenuItem);

				final RightClickMenuItem copyHotfixIDListMenuItem = new RightClickMenuItem("Copy Hotfix ID List");

				copyHotfixIDListMenuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

						List<SystemScanColumn> columns = new ArrayList<>();
						columns.add(SystemScanColumn.HOTFIX_ID);

						String selectedRowsData = systemScanTableModel.getSelectedColumnsData(null, columns);

						clipboard.setContents(new StringSelection(selectedRowsData), copyHotfixIDListMenuItem);

						popupMenu.setVisible(false);

					}
				});

				popupMenu.add(copyHotfixIDListMenuItem);

				final RightClickMenuItem copyHotfixIDDescListMenuItem = new RightClickMenuItem(
						"Copy Hotfix ID And Description List");

				copyHotfixIDDescListMenuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

						List<SystemScanColumn> columns = new ArrayList<>();
						columns.add(SystemScanColumn.HOTFIX_ID);
						columns.add(SystemScanColumn.HOTFIX_DESCRIPTION);

						String selectedRowsData = systemScanTableModel.getSelectedColumnsData(null, columns);

						clipboard.setContents(new StringSelection(selectedRowsData), copyHotfixIDDescListMenuItem);

						popupMenu.setVisible(false);

					}
				});

				popupMenu.add(copyHotfixIDDescListMenuItem);

				/*
				 * final BookmarkModel<ScanResultHotfixEntryKey> bookmarkModel =
				 * scanResultTableModel.getBookmarkModel();
				 * 
				 * // add bookmark
				 * 
				 * Map<ScanResultHotfixEntryKey, ScanResultHotfixEntry> entryKeyMap = new
				 * HashMap<>();
				 * 
				 * for (int selectedRow : selectedRowList) {
				 * 
				 * ScanResultHotfixEntry entry = (ScanResultHotfixEntry)
				 * scanResultTableModel.getValueAt(selectedRow, 0);
				 * 
				 * if (entry != null) {
				 * 
				 * entryKeyMap.put(entry.getKey(), entry); } }
				 * 
				 * RightClickMenuItem addBookmarkMenuItem = new
				 * RightClickMenuItem("Add Bookmark");
				 * 
				 * addBookmarkMenuItem.addActionListener(new ActionListener() {
				 * 
				 * @Override public void actionPerformed(ActionEvent e) {
				 * 
				 * Component mainWindow = getMainWindow();
				 * 
				 * BookmarkAddDialog<ScanResultHotfixEntryKey> bookmarkAddDialog;
				 * bookmarkAddDialog = new BookmarkAddDialog<ScanResultHotfixEntryKey>(appIcon,
				 * mainWindow, bookmarkModel, new
				 * ArrayList<ScanResultHotfixEntryKey>(entryKeyMap.keySet())) {
				 * 
				 * private static final long serialVersionUID = 9033469590975413111L;
				 * 
				 * @Override public List<Marker<ScanResultHotfixEntryKey>> getMarkerList(
				 * List<ScanResultHotfixEntryKey> keyList, String text) {
				 * 
				 * List<Marker<ScanResultHotfixEntryKey>> markerList = new ArrayList<>();
				 * 
				 * for (ScanResultHotfixEntryKey key : keyList) {
				 * 
				 * Marker<ScanResultHotfixEntryKey> marker = new
				 * Marker<ScanResultHotfixEntryKey>(key, text);
				 * 
				 * markerList.add(marker); }
				 * 
				 * return markerList; }
				 * 
				 * };
				 * 
				 * bookmarkAddDialog.setVisible(true); popupMenu.setVisible(false);
				 * 
				 * } });
				 * 
				 * popupMenu.add(addBookmarkMenuItem);
				 * 
				 * // show open and delete if (size == 1) {
				 * 
				 * ScanResultHotfixEntry entry = (ScanResultHotfixEntry) scanResultTableModel
				 * .getValueAt(selectedRowList.get(0), 0);
				 * 
				 * if (entry != null) {
				 * 
				 * ScanResultHotfixEntryKey key = entry.getKey();
				 * 
				 * List<Marker<ScanResultHotfixEntryKey>> bookmarkList =
				 * bookmarkModel.getMarkers(key);
				 * 
				 * if ((bookmarkList != null) && (bookmarkList.size() > 0)) {
				 * 
				 * RightClickMenuItem openBookmarkMenuItem = new
				 * RightClickMenuItem("Open Bookmark");
				 * 
				 * openBookmarkMenuItem.addActionListener(new ActionListener() {
				 * 
				 * @Override public void actionPerformed(ActionEvent e) {
				 * 
				 * Component mainWindow = getMainWindow();
				 * 
				 * Map<ScanResultHotfixEntryKey, List<Marker<ScanResultHotfixEntryKey>>>
				 * bookmarkListMap = new HashMap<>(); bookmarkListMap.put(key, bookmarkList);
				 * 
				 * BookmarkOpenDialog<ScanResultHotfixEntryKey> bookmarkOpenDialog;
				 * bookmarkOpenDialog = new
				 * BookmarkOpenDialog<ScanResultHotfixEntryKey>(appIcon, mainWindow,
				 * bookmarkListMap);
				 * 
				 * bookmarkOpenDialog.setVisible(true);
				 * 
				 * popupMenu.setVisible(false);
				 * 
				 * } });
				 * 
				 * RightClickMenuItem deleteBookmarkMenuItem = new
				 * RightClickMenuItem("Delete Bookmark");
				 * 
				 * deleteBookmarkMenuItem.addActionListener(new ActionListener() {
				 * 
				 * @Override public void actionPerformed(ActionEvent e) {
				 * 
				 * Component mainWindow = getMainWindow();
				 * 
				 * List<ScanResultHotfixEntryKey> keyList = new ArrayList<>(); keyList.add(key);
				 * BookmarkDeleteDialog<ScanResultHotfixEntryKey> bookmarkDeleteDialog;
				 * 
				 * bookmarkDeleteDialog = new
				 * BookmarkDeleteDialog<ScanResultHotfixEntryKey>(appIcon, mainWindow,
				 * bookmarkModel, keyList);
				 * 
				 * bookmarkDeleteDialog.setVisible(true);
				 * 
				 * popupMenu.setVisible(false);
				 * 
				 * } });
				 * 
				 * popupMenu.add(openBookmarkMenuItem); popupMenu.add(deleteBookmarkMenuItem); }
				 * } }
				 */
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}

		} else if (e.getClickCount() == 2)

		{

			int row = source.getSelectedRow();

			ScanResultHotfixEntryKey scanResultHotfixEntryKey = systemScanTableModel.getFtmEntryKeyList().get(row);

			JFrame scanResultHotfixDialog = scanResultHotfixDialogMap.get(scanResultHotfixEntryKey);

			if (scanResultHotfixDialog == null) {

				scanResultHotfixDialog = new ScanResultHotfixDialog(scanResultHotfixEntryKey, source,
						BaseFrame.getAppIcon());

				scanResultHotfixDialog.addWindowListener(new WindowAdapter() {

					@Override
					public void windowClosed(WindowEvent e) {
						Map<ScanResultHotfixEntryKey, JFrame> scanResultHotfixDialogMap = getScanResultHotfixDialogMap();
						scanResultHotfixDialogMap.remove(scanResultHotfixEntryKey);
					}

				});

				scanResultHotfixDialogMap.put(scanResultHotfixEntryKey, scanResultHotfixDialog);
			} else {
				scanResultHotfixDialog.toFront();
			}
		} else {
			super.mouseClicked(e);
		}
	}

	public void clearJDialogList() {

		for (JFrame logEntryModelDialog : scanResultHotfixDialogMap.values()) {
			logEntryModelDialog.dispose();
		}

		scanResultHotfixDialogMap.clear();
	}

}
