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
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.pega.gcs.fringecommon.guiutilities.BaseFrame;
import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.RightClickMenuItem;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkAddDialog;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkDeleteDialog;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkModel;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkOpenDialog;
import com.pega.gcs.fringecommon.guiutilities.markerbar.Marker;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.logviewer.model.LogEntry;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogEntryModel;

public class LogTableMouseListener extends MouseAdapter {

    private static final Log4j2Helper LOG = new Log4j2Helper(LogTableMouseListener.class);

    private Map<LogEntryKey, JFrame> logEntryModelFrameMap;

    private Component mainWindow;

    public LogTableMouseListener(Component mainWindow) {

        this.mainWindow = mainWindow;

        logEntryModelFrameMap = new HashMap<>();
    }

    private Component getMainWindow() {
        return mainWindow;
    }

    private Map<LogEntryKey, JFrame> getLogEntryModelFrameMap() {
        return logEntryModelFrameMap;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

        if (SwingUtilities.isRightMouseButton(mouseEvent)) {

            Point point = mouseEvent.getPoint();

            final List<Integer> selectedRowList = new ArrayList<Integer>();

            final LogTable logTable = (LogTable) mouseEvent.getSource();

            int[] selectedRows = logTable.getSelectedRows();

            // in case the row was not selected when right clicking then based
            // on the point, select the row.
            if ((selectedRows != null) && (selectedRows.length <= 1)) {

                int selectedRow = logTable.rowAtPoint(point);

                if (selectedRow != -1) {
                    // select the row first
                    logTable.setRowSelectionInterval(selectedRow, selectedRow);
                    selectedRows = new int[] { selectedRow };
                }
            }

            for (int selectedRow : selectedRows) {
                selectedRowList.add(selectedRow);
            }

            final int size = selectedRowList.size();

            if (size > 0) {

                final JPopupMenu popupMenu = new JPopupMenu();

                RightClickMenuItem copyLogEntryMenuItem = null;
                RightClickMenuItem exportLogEntryLogMenuItem = null;
                RightClickMenuItem addBookmarkMenuItem = null;
                RightClickMenuItem openBookmarkMenuItem = null;
                RightClickMenuItem deleteBookmarkMenuItem = null;

                copyLogEntryMenuItem = getCopyLogEntryRightClickMenuItem(popupMenu, logTable, selectedRowList);
                exportLogEntryLogMenuItem = getExportLogEventRightClickMenuItem(popupMenu, logTable, selectedRowList);
                addBookmarkMenuItem = getAddBookmarkRightClickMenuItem(popupMenu, selectedRowList, logTable);

                // show open and delete bookmark
                if (size == 1) {

                    Integer selectedRow = selectedRowList.get(0);

                    LogTableModel logTableModel = (LogTableModel) logTable.getModel();

                    LogEntry logEntry = (LogEntry) logTableModel.getValueAt(selectedRow, 0);

                    if (logEntry != null) {

                        LogEntryKey logEntryKey = logEntry.getKey();

                        BookmarkModel<LogEntryKey> bookmarkModel = logTableModel.getBookmarkModel();

                        List<Marker<LogEntryKey>> bookmarkList = bookmarkModel.getMarkers(logEntryKey);

                        if ((bookmarkList != null) && (bookmarkList.size() > 0)) {

                            openBookmarkMenuItem = getOpenBookmarkRightClickMenuItem(popupMenu, logEntryKey,
                                    bookmarkModel);

                            deleteBookmarkMenuItem = getDeleteBookmarkRightClickMenuItem(popupMenu, logEntryKey,
                                    bookmarkModel);
                        }
                    }

                }
                // expected order
                if (copyLogEntryMenuItem != null) {
                    addPopupMenu(popupMenu, copyLogEntryMenuItem);
                }

                if (exportLogEntryLogMenuItem != null) {
                    addPopupMenu(popupMenu, exportLogEntryLogMenuItem);
                }

                if (addBookmarkMenuItem != null) {
                    addPopupMenu(popupMenu, addBookmarkMenuItem);
                }

                if (openBookmarkMenuItem != null) {
                    addPopupMenu(popupMenu, openBookmarkMenuItem);
                }

                if (deleteBookmarkMenuItem != null) {
                    addPopupMenu(popupMenu, deleteBookmarkMenuItem);
                }

                popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
            }

        } else if (mouseEvent.getClickCount() == 2) {

            LogTable logTable = (LogTable) mouseEvent.getSource();

            performDoubleClick(logTable);

        } else {
            super.mouseClicked(mouseEvent);
        }
    }

    private void addPopupMenu(JPopupMenu popupMenu, RightClickMenuItem rightClickMenuItem) {

        if (rightClickMenuItem != null) {
            popupMenu.add(rightClickMenuItem);
        }
    }

    public void clearJDialogList() {

        Map<LogEntryKey, JFrame> logEntryModelFrameMap = getLogEntryModelFrameMap();

        for (JFrame logEntryModelDialog : logEntryModelFrameMap.values()) {
            logEntryModelDialog.dispose();
        }

        logEntryModelFrameMap.clear();

    }

    private RightClickMenuItem getCopyLogEntryRightClickMenuItem(JPopupMenu popupMenu, LogTable logTable,
            List<Integer> selectedRowList) {

        final RightClickMenuItem copyLogEntryMenuItem = new RightClickMenuItem("Copy");

        copyLogEntryMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                LogTableModel logTableModel = (LogTableModel) logTable.getModel();

                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

                int[] array = selectedRowList.stream().mapToInt(i -> i).toArray();
                String selectedRowsData = logTableModel.getSelectedRowsData(array);

                clipboard.setContents(new StringSelection(selectedRowsData), copyLogEntryMenuItem);

                popupMenu.setVisible(false);

            }
        });

        return copyLogEntryMenuItem;
    }

    private RightClickMenuItem getExportLogEventRightClickMenuItem(JPopupMenu popupMenu, LogTable logTable,
            List<Integer> selectedRowList) {

        RightClickMenuItem exportLogEntryLogMenuItem = new RightClickMenuItem("Export selected log event(s)");

        exportLogEntryLogMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                try {

                    LogTableModel logTableModel = (LogTableModel) logTable.getModel();

                    LogEntry logEntry = (LogEntry) logTableModel.getValueAt(selectedRowList.get(0), 0);

                    String postfix = String.valueOf(logEntry.getKey().getLineNo());

                    String filePath = logTableModel.getFilePath();
                    File logFile = new File(filePath);

                    String fileName = FileUtilities.getNameWithoutExtension(logFile);
                    fileName = fileName + "-" + postfix + ".log";
                    File currentDirectory = logFile.getParentFile();

                    File proposedFile = new File(currentDirectory, fileName);

                    JFileChooser fileChooser = new JFileChooser(currentDirectory);

                    fileChooser.setDialogTitle("Save LOG(.log) File");
                    fileChooser.setSelectedFile(proposedFile);
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    FileNameExtensionFilter filter = new FileNameExtensionFilter("LOG Format", "log");

                    fileChooser.setFileFilter(filter);

                    Component mainWindow = getMainWindow();

                    int returnValue = fileChooser.showSaveDialog(mainWindow);

                    if (returnValue == JFileChooser.APPROVE_OPTION) {

                        File exportFile = fileChooser.getSelectedFile();

                        returnValue = JOptionPane.YES_OPTION;

                        if (exportFile.exists()) {

                            returnValue = JOptionPane.showConfirmDialog(mainWindow,
                                    "Replace existing file '" + exportFile.getAbsolutePath() + "' ?", "File Exists",
                                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        }

                        if (returnValue == JOptionPane.YES_OPTION) {

                            UIManager.put("ModalProgressMonitor.progressText", "Exporting selected log entries");

                            ModalProgressMonitor modalProgressMonitor = new ModalProgressMonitor(mainWindow, "",
                                    "Exporting selected log entries (0%)                                   ", 0, 100);
                            modalProgressMonitor.setMillisToDecideToPopup(0);
                            modalProgressMonitor.setMillisToPopup(0);

                            LogEventLogExportTask logEventLogExportTask;

                            logEventLogExportTask = new LogEventLogExportTask(logTableModel, selectedRowList,
                                    exportFile, modalProgressMonitor) {

                                @Override
                                protected void done() {

                                    Boolean success = Boolean.FALSE;
                                    try {

                                        success = get();

                                    } catch (Exception e) {
                                        LOG.error("LogEventLogExportTask erorr: ", e);
                                    } finally {

                                        modalProgressMonitor.close();
                                        System.gc();

                                        if ((success != null) && (success)) {

                                            String message = "Exported Log entries to '" + exportFile.getAbsolutePath()
                                                    + "'.\nOpen in new tab?";

                                            int nextAction = JOptionPane.showConfirmDialog(mainWindow, message,
                                                    "Open Exported Log file?", JOptionPane.YES_NO_OPTION,
                                                    JOptionPane.INFORMATION_MESSAGE);

                                            if (nextAction == JOptionPane.YES_OPTION) {
                                                LogViewer logViewer = LogViewer.getInstance();
                                                logViewer.loadLogFile(exportFile);
                                            }

                                        }
                                    }
                                }

                            };

                            logEventLogExportTask.execute();

                            modalProgressMonitor.show();
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Error in Export Log entries", ex);
                } finally {
                    popupMenu.setVisible(false);
                }

            }
        });

        return exportLogEntryLogMenuItem;
    }

    private RightClickMenuItem getAddBookmarkRightClickMenuItem(JPopupMenu popupMenu, List<Integer> selectedRowList,
            LogTable logTable) {

        LogTableModel logTableModel = (LogTableModel) logTable.getModel();

        Map<LogEntryKey, LogEntry> leKeyMap = new HashMap<>();

        for (int selectedRow : selectedRowList) {

            LogEntry logEntry = (LogEntry) logTableModel.getValueAt(selectedRow, 0);

            if (logEntry != null) {

                leKeyMap.put(logEntry.getKey(), logEntry);
            }
        }

        RightClickMenuItem addBookmarkMenuItem = new RightClickMenuItem("Add Bookmark");

        addBookmarkMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                ImageIcon appIcon = BaseFrame.getAppIcon();
                Component mainWindow = getMainWindow();

                BookmarkModel<LogEntryKey> bookmarkModel = logTableModel.getBookmarkModel();

                BookmarkAddDialog<LogEntryKey> bookmarkAddDialog;
                bookmarkAddDialog = new BookmarkAddDialog<LogEntryKey>(null, bookmarkModel,
                        new ArrayList<>(leKeyMap.keySet()), appIcon, mainWindow) {

                    private static final long serialVersionUID = 9033469590975413111L;

                    @Override
                    public List<Marker<LogEntryKey>> getMarkerList(List<LogEntryKey> keyList, String text) {

                        LogEntryModel logEntryModel = logTableModel.getLogEntryModel();

                        List<Marker<LogEntryKey>> markerList = new ArrayList<>();

                        for (LogEntryKey key : keyList) {

                            LogEntry logEntry = leKeyMap.get(key);

                            String logEntryTimeText = logEntryModel.getFormattedLogEntryValue(logEntry,
                                    LogEntryColumn.TIMESTAMP);

                            LogEntryMarker logEntryMarker = new LogEntryMarker(key, logEntryTimeText, text);

                            markerList.add(logEntryMarker);
                        }

                        return markerList;
                    }

                };

                bookmarkAddDialog.setVisible(true);
                popupMenu.setVisible(false);

            }
        });

        return addBookmarkMenuItem;
    }

    private RightClickMenuItem getOpenBookmarkRightClickMenuItem(JPopupMenu popupMenu, LogEntryKey key,
            BookmarkModel<LogEntryKey> bookmarkModel) {

        RightClickMenuItem openBookmark = new RightClickMenuItem("Open Bookmark");

        openBookmark.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                ImageIcon appIcon = BaseFrame.getAppIcon();
                Component mainWindow = getMainWindow();

                BookmarkOpenDialog<LogEntryKey> bookmarkOpenDialog;
                bookmarkOpenDialog = new BookmarkOpenDialog<>(bookmarkModel, key, appIcon, mainWindow);

                bookmarkOpenDialog.setVisible(true);

                popupMenu.setVisible(false);

            }
        });

        return openBookmark;
    }

    private RightClickMenuItem getDeleteBookmarkRightClickMenuItem(JPopupMenu popupMenu, LogEntryKey key,
            BookmarkModel<LogEntryKey> bookmarkModel) {

        RightClickMenuItem deleteBookmark = new RightClickMenuItem("Delete Bookmark");

        deleteBookmark.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                ImageIcon appIcon = BaseFrame.getAppIcon();
                Component mainWindow = getMainWindow();

                BookmarkDeleteDialog<LogEntryKey> bookmarkDeleteDialog;
                bookmarkDeleteDialog = new BookmarkDeleteDialog<>(bookmarkModel, key, appIcon, mainWindow);

                bookmarkDeleteDialog.setVisible(true);

                popupMenu.setVisible(false);

            }
        });

        return deleteBookmark;
    }

    private void performDoubleClick(LogTable logTable) {

        int row = logTable.getSelectedRow();

        LogTableModel logTableModel = (LogTableModel) logTable.getModel();

        LogEntry logEntry = (LogEntry) logTableModel.getValueAt(row, 0);

        final LogEntryKey logEntryKey = logEntry.getKey();

        Map<LogEntryKey, JFrame> logEntryModelFrameMap = getLogEntryModelFrameMap();

        JFrame logEntryModelDialog = logEntryModelFrameMap.get(logEntryKey);

        if (logEntryModelDialog == null) {

            Component mainWindow = getMainWindow();

            logEntryModelDialog = new LogNavigationDialog(logEntry, logTable, BaseFrame.getAppIcon(), mainWindow);

            logEntryModelDialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosed(WindowEvent windowEvent) {
                    Map<LogEntryKey, JFrame> logEntryModelDialogMap = getLogEntryModelFrameMap();
                    logEntryModelDialogMap.remove(logEntryKey);
                }

            });

            logEntryModelFrameMap.put(logEntryKey, logEntryModelDialog);

            logEntryModelDialog.setVisible(true);

        } else {
            logEntryModelDialog.toFront();
        }
    }
}
