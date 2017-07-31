/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer;

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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import com.pega.gcs.fringecommon.guiutilities.BaseFrame;
import com.pega.gcs.fringecommon.guiutilities.RightClickMenuItem;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkAddDialog;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkDeleteDialog;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkModel;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkOpenDialog;
import com.pega.gcs.fringecommon.guiutilities.markerbar.Marker;
import com.pega.gcs.logviewer.model.LogEntry;

public class LogTableMouseListener extends MouseAdapter {

	private Map<Integer, JFrame> logEntryModelDialogMap;

	private Component mainWindow;

	/**
	 * @param logTableList
	 */
	public LogTableMouseListener(Component mainWindow) {

		this.mainWindow = mainWindow;
		logEntryModelDialogMap = new HashMap<Integer, JFrame>();
	}

	protected Component getMainWindow() {
		return mainWindow;
	}

	protected Map<Integer, JFrame> getLogEntryModelDialogMap() {
		return logEntryModelDialogMap;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		if (SwingUtilities.isRightMouseButton(e)) {

			Point point = e.getPoint();

			final List<Integer> selectedRowList = new LinkedList<Integer>();

			final LogTable source = (LogTable) e.getSource();

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

				ImageIcon appIcon = BaseFrame.getAppIcon();

				final RightClickMenuItem copyMenuItem = new RightClickMenuItem("Copy");

				copyMenuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

						LogTableModel logTableModel = (LogTableModel) source.getModel();

						StringBuffer logEntryTextSB = new StringBuffer();

						for (int selectedRow : selectedRowList) {

							LogEntry logEntry = (LogEntry) logTableModel.getValueAt(selectedRow, 0);

							if (logEntry != null) {
								logEntryTextSB.append(logEntry.getLogEntryText());
								logEntryTextSB.append("\n");

							}
						}

						clipboard.setContents(new StringSelection(logEntryTextSB.toString()), copyMenuItem);

						popupMenu.setVisible(false);

					}
				});

				popupMenu.add(copyMenuItem);

				LogTableModel logTableModel = (LogTableModel) source.getModel();

				final BookmarkModel<Integer> bookmarkModel = logTableModel.getBookmarkModel();

				// add bookmark
				Map<Integer, LogEntry> leKeyMap = new HashMap<>();

				for (int selectedRow : selectedRowList) {

					LogEntry logEntry = (LogEntry) logTableModel.getValueAt(selectedRow, 0);

					if (logEntry != null) {

						leKeyMap.put(logEntry.getKey(), logEntry);
					}
				}

				RightClickMenuItem addBookmarkMenuItem = new RightClickMenuItem("Add Bookmark");

				addBookmarkMenuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						Component mainWindow = getMainWindow();

						BookmarkAddDialog<Integer> bookmarkAddDialog;
						bookmarkAddDialog = new BookmarkAddDialog<Integer>(appIcon, mainWindow, bookmarkModel,
								new ArrayList<Integer>(leKeyMap.keySet())) {

							private static final long serialVersionUID = 9033469590975413111L;

							@Override
							public List<Marker<Integer>> getMarkerList(List<Integer> keyList, String text) {

								List<Marker<Integer>> markerList = new ArrayList<>();

								for (Integer key : keyList) {

									Marker<Integer> marker = new Marker<Integer>(key, text);

									markerList.add(marker);
								}

								return markerList;
							}

						};

						bookmarkAddDialog.setVisible(true);
						popupMenu.setVisible(false);

					}
				});

				popupMenu.add(addBookmarkMenuItem);

				// show open and delete
				if (size == 1) {

					LogEntry logEntry = (LogEntry) logTableModel.getValueAt(selectedRowList.get(0), 0);

					if (logEntry != null) {

						Integer key = logEntry.getKey();

						List<Marker<Integer>> bookmarkList = bookmarkModel.getMarkers(key);

						if ((bookmarkList != null) && (bookmarkList.size() > 0)) {

							RightClickMenuItem openBookmarkMenuItem = new RightClickMenuItem("Open Bookmark");

							openBookmarkMenuItem.addActionListener(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent e) {

									Component mainWindow = getMainWindow();

									Map<Integer, List<Marker<Integer>>> bookmarkListMap = new HashMap<>();
									bookmarkListMap.put(key, bookmarkList);

									BookmarkOpenDialog<Integer> bookmarkOpenDialog;
									bookmarkOpenDialog = new BookmarkOpenDialog<Integer>(appIcon, mainWindow,
											bookmarkListMap);

									bookmarkOpenDialog.setVisible(true);

									popupMenu.setVisible(false);

								}
							});

							RightClickMenuItem deleteBookmarkMenuItem = new RightClickMenuItem("Delete Bookmark");

							deleteBookmarkMenuItem.addActionListener(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent e) {

									Component mainWindow = getMainWindow();

									List<Integer> keyList = new ArrayList<>();
									keyList.add(key);
									BookmarkDeleteDialog<Integer> bookmarkDeleteDialog;

									bookmarkDeleteDialog = new BookmarkDeleteDialog<Integer>(appIcon, mainWindow,
											bookmarkModel, keyList);

									bookmarkDeleteDialog.setVisible(true);

									popupMenu.setVisible(false);

								}
							});

							popupMenu.add(openBookmarkMenuItem);
							popupMenu.add(deleteBookmarkMenuItem);
						}
					}
				}

				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}

		} else if (e.getClickCount() == 2) {

			LogTable logTable = (LogTable) e.getSource();

			int row = logTable.getSelectedRow();

			LogTableModel logTableModel = (LogTableModel) logTable.getModel();

			LogEntry logEntry = (LogEntry) logTableModel.getValueAt(row, 0);

			final Integer logEntryIndex = logEntry.getKey();

			JFrame logEntryModelDialog = logEntryModelDialogMap.get(logEntryIndex);

			if (logEntryModelDialog == null) {

				logEntryModelDialog = new LogNavigationDialog(logEntry, logTable, BaseFrame.getAppIcon());

				logEntryModelDialog.addWindowListener(new WindowAdapter() {

					@Override
					public void windowClosed(WindowEvent e) {
						Map<Integer, JFrame> logEntryModelDialogMap = getLogEntryModelDialogMap();
						logEntryModelDialogMap.remove(logEntryIndex);
					}

				});

				logEntryModelDialogMap.put(logEntryIndex, logEntryModelDialog);
			} else {
				logEntryModelDialog.toFront();
			}
		} else {
			super.mouseClicked(e);
		}
	}

	public void clearJDialogList() {

		for (JFrame logEntryModelDialog : logEntryModelDialogMap.values()) {
			logEntryModelDialog.dispose();
		}

		logEntryModelDialogMap.clear();

	}

}
