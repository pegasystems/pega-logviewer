/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.pega.gcs.fringecommon.guiutilities.RightClickMenuItem;

public class ThreadDumpRequestorLockTableMouseListener extends MouseAdapter {

    private ThreadDumpTable threadDumpTable;

    private JTabbedPane threadDumpTabbedPane;

    private List<Integer> threadColumnList;

    public ThreadDumpRequestorLockTableMouseListener(ThreadDumpTable threadDumpTable, JTabbedPane threadDumpTabbedPane,
            List<Integer> threadColumnList) {

        super();

        this.threadDumpTable = threadDumpTable;
        this.threadDumpTabbedPane = threadDumpTabbedPane;
        this.threadColumnList = threadColumnList;
    }

    private JTabbedPane getThreadDumpTabbedPane() {
        return threadDumpTabbedPane;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

        if (SwingUtilities.isRightMouseButton(mouseEvent)) {

            Point point = mouseEvent.getPoint();

            final JTable source = (JTable) mouseEvent.getSource();

            final int selectedRow = source.rowAtPoint(point);
            final int selectedColumn = source.columnAtPoint(point);

            if (selectedRow != -1) {

                // select the row first
                source.setRowSelectionInterval(selectedRow, selectedRow);

                final JPopupMenu popupMenu = new JPopupMenu();

                final RightClickMenuItem copyCellMenuItem = new RightClickMenuItem("Copy Cell");

                copyCellMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

                        String cellValue = (String) source.getValueAt(selectedRow, selectedColumn);

                        clipboard.setContents(new StringSelection(cellValue), copyCellMenuItem);

                        popupMenu.setVisible(false);

                    }
                });

                popupMenu.add(copyCellMenuItem);

                if (threadColumnList.indexOf(selectedColumn) != -1) {

                    final RightClickMenuItem openThreadMenuItem = new RightClickMenuItem("Open Thread");

                    openThreadMenuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {

                            String cellValue = (String) source.getValueAt(selectedRow, selectedColumn);

                            ThreadDumpTableModel threadDumpTableModel;
                            threadDumpTableModel = (ThreadDumpTableModel) threadDumpTable.getModel();

                            List<String> threadNameList = threadDumpTableModel.getFtmEntryKeyList();

                            int rowIndex = threadNameList.indexOf(cellValue);

                            if (rowIndex != -1) {

                                threadDumpTable.setRowSelectionInterval(rowIndex, rowIndex);
                                threadDumpTable.scrollRowToVisible(rowIndex);

                                getThreadDumpTabbedPane().setSelectedIndex(0);
                            }

                            popupMenu.setVisible(false);

                        }
                    });

                    popupMenu.add(openThreadMenuItem);
                }
                popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
            }

        } else {
            super.mouseClicked(mouseEvent);
        }
    }
}
