/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.catalog;

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

import com.pega.gcs.fringecommon.guiutilities.BaseFrame;
import com.pega.gcs.fringecommon.guiutilities.RightClickMenuItem;
import com.pega.gcs.logviewer.catalog.model.HotfixColumn;
import com.pega.gcs.logviewer.catalog.model.HotfixEntryKey;

public class HotfixTableMouseListener extends MouseAdapter {

    private Map<HotfixEntryKey, JFrame> hotfixEntryDetailDialogMap;

    private Component mainWindow;

    public HotfixTableMouseListener(Component mainWindow) {

        this.mainWindow = mainWindow;

        hotfixEntryDetailDialogMap = new HashMap<HotfixEntryKey, JFrame>();
    }

    private Component getMainWindow() {
        return mainWindow;
    }

    private Map<HotfixEntryKey, JFrame> getHotfixEntryDetailDialogMap() {
        return hotfixEntryDetailDialogMap;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

        HotfixTable source = (HotfixTable) mouseEvent.getSource();
        HotfixTableModel hotfixTableModel = (HotfixTableModel) source.getModel();

        if (SwingUtilities.isRightMouseButton(mouseEvent)) {

            Point point = mouseEvent.getPoint();

            final List<Integer> selectedRowList = new ArrayList<Integer>();

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

                RightClickMenuItem copyHfixDetailsMenuItem = null;
                RightClickMenuItem copyHfixIdMenuItem = null;
                RightClickMenuItem copyHfixIdDescMenuItem = null;
                RightClickMenuItem copyHfixIdListMenuItem = null;
                RightClickMenuItem copyHfixIdDescListMenuItem = null;

                copyHfixDetailsMenuItem = getCopyHfixDetailsMenuItem(popupMenu, selectedRowList, hotfixTableModel);

                copyHfixIdMenuItem = getCopyHfixIdMenuItem(popupMenu, selectedRowList, hotfixTableModel);

                copyHfixIdDescMenuItem = getCopyHfixIdDescMenuItem(popupMenu, selectedRowList, hotfixTableModel);

                copyHfixIdListMenuItem = getCopyHfixIdListMenuItem(popupMenu, hotfixTableModel);

                copyHfixIdDescListMenuItem = getCopyHfixIdDescListMenuItem(popupMenu, hotfixTableModel);

                // expected order
                if (copyHfixDetailsMenuItem != null) {
                    popupMenu.add(copyHfixDetailsMenuItem);
                }

                if (copyHfixIdMenuItem != null) {
                    popupMenu.add(copyHfixIdMenuItem);
                }

                if (copyHfixIdDescMenuItem != null) {
                    popupMenu.add(copyHfixIdDescMenuItem);
                }

                if (copyHfixIdListMenuItem != null) {
                    popupMenu.add(copyHfixIdListMenuItem);
                }

                if (copyHfixIdDescListMenuItem != null) {
                    popupMenu.add(copyHfixIdDescListMenuItem);
                }

                popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
            }

        } else if (mouseEvent.getClickCount() == 2) {

            int row = source.getSelectedRow();

            HotfixEntryKey hotfixEntryKey = hotfixTableModel.getFtmEntryKeyList().get(row);

            Map<HotfixEntryKey, JFrame> hotfixEntryDetailDialogMap;
            hotfixEntryDetailDialogMap = getHotfixEntryDetailDialogMap();

            JFrame hotfixEntryDetailDialog = hotfixEntryDetailDialogMap.get(hotfixEntryKey);

            if (hotfixEntryDetailDialog == null) {

                Component mainWindow = getMainWindow();

                hotfixEntryDetailDialog = new HotfixEntryDetailDialog(hotfixEntryKey, source, BaseFrame.getAppIcon(),
                        mainWindow);

                hotfixEntryDetailDialog.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosed(WindowEvent windowEvent) {
                        Map<HotfixEntryKey, JFrame> hotfixEntryDetailDialogMap = getHotfixEntryDetailDialogMap();
                        hotfixEntryDetailDialogMap.remove(hotfixEntryKey);
                    }

                });

                hotfixEntryDetailDialogMap.put(hotfixEntryKey, hotfixEntryDetailDialog);

                hotfixEntryDetailDialog.setVisible(true);

            } else {
                hotfixEntryDetailDialog.toFront();
            }
        } else {
            super.mouseClicked(mouseEvent);
        }
    }

    private RightClickMenuItem getCopyHfixDetailsMenuItem(JPopupMenu popupMenu, List<Integer> selectedRowList,
            HotfixTableModel hotfixTableModel) {

        String copyHfixDetailsMenuItemTitle = "Copy selected Hotfix entry details";
        RightClickMenuItem copyHfixDetailsMenuItem = new RightClickMenuItem(copyHfixDetailsMenuItemTitle);

        copyHfixDetailsMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

                int[] array = selectedRowList.stream().mapToInt(i -> i).toArray();
                String selectedRowsData = hotfixTableModel.getSelectedRowsData(array);

                clipboard.setContents(new StringSelection(selectedRowsData), copyHfixDetailsMenuItem);

                popupMenu.setVisible(false);

            }
        });

        return copyHfixDetailsMenuItem;
    }

    private RightClickMenuItem getCopyHfixIdMenuItem(JPopupMenu popupMenu, List<Integer> selectedRowList,
            HotfixTableModel hotfixTableModel) {

        String copyHfixIdMenuItemTitle = "Copy selected Hotfix Id";
        RightClickMenuItem copyHfixIdMenuItem = new RightClickMenuItem(copyHfixIdMenuItemTitle);

        copyHfixIdMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

                List<HotfixColumn> columns = new ArrayList<>();
                columns.add(HotfixColumn.HOTFIX_ID);

                int[] selectedRows = selectedRowList.stream().mapToInt(i -> i).toArray();

                List<HotfixEntryKey> hotfixEntryKeyList;
                hotfixEntryKeyList = hotfixTableModel.getEntryKeyForRowsList(selectedRows);

                String selectedRowsData = hotfixTableModel.getSelectedColumnsData(hotfixEntryKeyList, columns);

                clipboard.setContents(new StringSelection(selectedRowsData), copyHfixIdMenuItem);

                popupMenu.setVisible(false);

            }
        });

        return copyHfixIdMenuItem;
    }

    private RightClickMenuItem getCopyHfixIdDescMenuItem(JPopupMenu popupMenu, List<Integer> selectedRowList,
            HotfixTableModel hotfixTableModel) {

        String copyHfixIdDescMenuItemTitle = "Copy selected Hotfix Id And Description";
        RightClickMenuItem copyHfixIdDescMenuItem = new RightClickMenuItem(copyHfixIdDescMenuItemTitle);

        copyHfixIdDescMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

                List<HotfixColumn> columns = new ArrayList<>();
                columns.add(HotfixColumn.HOTFIX_ID);
                columns.add(HotfixColumn.HOTFIX_DESCRIPTION);

                int[] selectedRows = selectedRowList.stream().mapToInt(i -> i).toArray();

                List<HotfixEntryKey> hotfixEntryKeyList;
                hotfixEntryKeyList = hotfixTableModel.getEntryKeyForRowsList(selectedRows);

                String selectedRowsData = hotfixTableModel.getSelectedColumnsData(hotfixEntryKeyList, columns);

                clipboard.setContents(new StringSelection(selectedRowsData), copyHfixIdDescMenuItem);

                popupMenu.setVisible(false);

            }
        });

        return copyHfixIdDescMenuItem;
    }

    private RightClickMenuItem getCopyHfixIdListMenuItem(JPopupMenu popupMenu, HotfixTableModel hotfixTableModel) {

        String copyHfixIdListMenuItemTitle = "Copy all Hotfix Id";
        RightClickMenuItem copyHfixIdListMenuItem = new RightClickMenuItem(copyHfixIdListMenuItemTitle);

        copyHfixIdListMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

                List<HotfixColumn> columns = new ArrayList<>();
                columns.add(HotfixColumn.HOTFIX_ID);

                String selectedRowsData = hotfixTableModel.getSelectedColumnsData(null, columns);

                clipboard.setContents(new StringSelection(selectedRowsData), copyHfixIdListMenuItem);

                popupMenu.setVisible(false);

            }
        });

        return copyHfixIdListMenuItem;
    }

    private RightClickMenuItem getCopyHfixIdDescListMenuItem(JPopupMenu popupMenu, HotfixTableModel hotfixTableModel) {

        String copyHfixIdDescListMenuItemTitle = "Copy all Hotfix Id And Description";
        RightClickMenuItem copyHfixIdDescListMenuItem = new RightClickMenuItem(copyHfixIdDescListMenuItemTitle);

        copyHfixIdDescListMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

                List<HotfixColumn> columns = new ArrayList<>();
                columns.add(HotfixColumn.HOTFIX_ID);
                columns.add(HotfixColumn.HOTFIX_DESCRIPTION);

                String selectedRowsData = hotfixTableModel.getSelectedColumnsData(null, columns);

                clipboard.setContents(new StringSelection(selectedRowsData), copyHfixIdDescListMenuItem);

                popupMenu.setVisible(false);

            }
        });

        return copyHfixIdDescListMenuItem;
    }

    public void clearJDialogList() {

        for (JFrame hotfixEntryDetailDialog : hotfixEntryDetailDialogMap.values()) {
            hotfixEntryDetailDialog.dispose();
        }

        hotfixEntryDetailDialogMap.clear();
    }

}
