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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.pega.gcs.fringecommon.guiutilities.FilterTable;
import com.pega.gcs.fringecommon.guiutilities.RightClickMenuItem;
import com.pega.gcs.logviewer.model.ThreadDumpThreadInfo;

public class ThreadDumpTable extends FilterTable<String> {

    private static final long serialVersionUID = -1999857450475975767L;

    public ThreadDumpTable(ThreadDumpTableModel threadDumpTableModel) {

        super(threadDumpTableModel);

        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

                if (SwingUtilities.isRightMouseButton(mouseEvent)) {

                    Point point = mouseEvent.getPoint();

                    final List<Integer> selectedRowList = new ArrayList<Integer>();

                    final ThreadDumpTable source = (ThreadDumpTable) mouseEvent.getSource();

                    int[] selectedRows = source.getSelectedRows();

                    // in case the row was not selected when right clicking then
                    // based
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
                        String menuItemStr = "Copy Thread Info";

                        final RightClickMenuItem copyThreadInfoMenuItem = new RightClickMenuItem(menuItemStr);

                        copyThreadInfoMenuItem.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

                                ThreadDumpTableModel threadDumpTableModel;
                                threadDumpTableModel = (ThreadDumpTableModel) source.getModel();

                                StringBuilder logEntryTextSB = new StringBuilder();

                                for (int selectedRow : selectedRowList) {

                                    ThreadDumpThreadInfo threadDumpThreadInfo = threadDumpTableModel
                                            .getThreadDumpThreadInfo(selectedRow);

                                    if (threadDumpThreadInfo != null) {
                                        logEntryTextSB.append(threadDumpThreadInfo.getThreadDumpString());
                                        logEntryTextSB.append(System.getProperty("line.separator"));

                                    }
                                }

                                clipboard.setContents(new StringSelection(logEntryTextSB.toString()),
                                        copyThreadInfoMenuItem);

                                popupMenu.setVisible(false);

                            }
                        });

                        popupMenu.add(copyThreadInfoMenuItem);

                        popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                    }

                } else {
                    super.mouseClicked(mouseEvent);
                }
            }
        });

    }
}
