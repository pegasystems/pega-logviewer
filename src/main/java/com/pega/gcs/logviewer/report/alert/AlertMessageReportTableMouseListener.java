/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.report.alert;

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
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import com.pega.gcs.fringecommon.guiutilities.BaseFrame;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.guiutilities.RightClickMenuItem;
import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.model.LogEntryKey;

public class AlertMessageReportTableMouseListener extends MouseAdapter {

    private LogTableModel logTableModel;

    private NavigationTableController<LogEntryKey> navigationTableController;

    private Component mainWindow;

    private Map<String, JFrame> alertMessageReportEntryFrameMap;

    public AlertMessageReportTableMouseListener(LogTableModel logTableModel,
            NavigationTableController<LogEntryKey> navigationTableController, Component mainWindow) {

        this.logTableModel = logTableModel;
        this.navigationTableController = navigationTableController;

        this.mainWindow = mainWindow;

        alertMessageReportEntryFrameMap = new HashMap<String, JFrame>();
    }

    private Map<String, JFrame> getAlertMessageReportEntryFrameMap() {
        return alertMessageReportEntryFrameMap;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

        if (SwingUtilities.isRightMouseButton(mouseEvent)) {

            Point point = mouseEvent.getPoint();

            final List<Integer> selectedRowList = new ArrayList<Integer>();

            final AlertMessageReportTable source = (AlertMessageReportTable) mouseEvent.getSource();

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

            if (selectedRowList.size() > 0) {

                final JPopupMenu popupMenu = new JPopupMenu();

                final RightClickMenuItem copyMenuItem = new RightClickMenuItem("Copy");

                copyMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

                        AlertMessageReportModel alertMessageReportModel = (AlertMessageReportModel) source.getModel();

                        int columnCount = alertMessageReportModel.getColumnCount();

                        StringBuilder alertMessageReportEntrySB = new StringBuilder();

                        // get the header row
                        for (int column = 0; column < columnCount; column++) {

                            TableColumn tableColumn = source.getTableHeader().getColumnModel().getColumn(column);
                            String columnValue = tableColumn.getHeaderValue().toString();

                            alertMessageReportEntrySB.append(columnValue);
                            alertMessageReportEntrySB.append("\t");
                        }

                        alertMessageReportEntrySB.append(System.getProperty("line.separator"));

                        for (int selectedRow : selectedRowList) {

                            for (int column = 0; column < columnCount; column++) {

                                String columnValue = (String) alertMessageReportModel.getValueAt(selectedRow, column);

                                alertMessageReportEntrySB.append(columnValue);
                                alertMessageReportEntrySB.append("\t");
                            }

                            alertMessageReportEntrySB.append(System.getProperty("line.separator"));
                        }

                        clipboard.setContents(new StringSelection(alertMessageReportEntrySB.toString()), copyMenuItem);

                        popupMenu.setVisible(false);

                    }
                });

                popupMenu.add(copyMenuItem);

                if (selectedRowList.size() == 1) {

                    RightClickMenuItem addBookmarkMenuItem = new RightClickMenuItem("Open Entry");

                    addBookmarkMenuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {

                            invokeAlertMessageReportEntryFrame(source);

                        }
                    });

                    popupMenu.add(addBookmarkMenuItem);

                }

                popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
            }

        } else if (mouseEvent.getClickCount() == 2) {

            AlertMessageReportTable source = (AlertMessageReportTable) mouseEvent.getSource();

            invokeAlertMessageReportEntryFrame(source);
        } else {
            super.mouseClicked(mouseEvent);
        }
    }

    private void invokeAlertMessageReportEntryFrame(AlertMessageReportTable alertMessageReportTable) {

        int selectedRow = alertMessageReportTable.getSelectedRow();

        AlertMessageReportModel alertMessageReportModel = (AlertMessageReportModel) alertMessageReportTable.getModel();

        AlertMessageReportEntry alertMessageReportEntry = (AlertMessageReportEntry) alertMessageReportModel
                .getValueAt(selectedRow, 0);

        String alertMessageReportEntryKey = alertMessageReportEntry.getAlertMessageReportEntryKey();

        JFrame alertMessageReportEntryFrame = alertMessageReportEntryFrameMap.get(alertMessageReportEntryKey);

        if (alertMessageReportEntryFrame == null) {

            alertMessageReportEntryFrame = new AlertMessageReportEntryFrame(selectedRow + 1, alertMessageReportEntry,
                    alertMessageReportModel, logTableModel, navigationTableController, BaseFrame.getAppIcon(),
                    mainWindow);

            alertMessageReportEntryFrame.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosed(WindowEvent windowEvent) {
                    Map<String, JFrame> alertMessageReportEntryFrameMap = getAlertMessageReportEntryFrameMap();
                    alertMessageReportEntryFrameMap.remove(alertMessageReportEntryKey);
                }

            });

            alertMessageReportEntryFrameMap.put(alertMessageReportEntryKey, alertMessageReportEntryFrame);
        } else {
            alertMessageReportEntryFrame.toFront();
        }
    }

    public void clearFrameMap() {

        for (JFrame alertMessageReportEntryFrame : alertMessageReportEntryFrameMap.values()) {
            alertMessageReportEntryFrame.dispose();
        }

        alertMessageReportEntryFrameMap.clear();

    }
}
