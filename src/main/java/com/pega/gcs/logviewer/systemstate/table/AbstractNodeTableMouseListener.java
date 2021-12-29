
package com.pega.gcs.logviewer.systemstate.table;

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
import javax.swing.SwingUtilities;

import com.pega.gcs.fringecommon.guiutilities.CustomJTable;
import com.pega.gcs.fringecommon.guiutilities.RightClickMenuItem;
import com.pega.gcs.fringecommon.guiutilities.datatable.AbstractDataTableModel;
import com.pega.gcs.logviewer.systemstate.SystemStateTreeNavigationController;

public abstract class AbstractNodeTableMouseListener extends MouseAdapter {

    private SystemStateTreeNavigationController systemStateTreeNavigationController;

    protected abstract String getNodeId(CustomJTable customJTable, int selectedRow);

    public AbstractNodeTableMouseListener(SystemStateTreeNavigationController systemStateTreeNavigationController) {
        this.systemStateTreeNavigationController = systemStateTreeNavigationController;
    }

    private SystemStateTreeNavigationController getSystemStateTreeNavigationController() {
        return systemStateTreeNavigationController;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

        if (SwingUtilities.isRightMouseButton(mouseEvent)) {

            Point point = mouseEvent.getPoint();

            final List<Integer> selectedRowList = new ArrayList<Integer>();

            final CustomJTable customJTable = (CustomJTable) mouseEvent.getSource();

            int[] selectedRows = customJTable.getSelectedRows();

            // in case the row was not selected when right clicking then based
            // on the point, select the row.
            if ((selectedRows != null) && (selectedRows.length <= 1)) {

                int selectedRow = customJTable.rowAtPoint(point);

                if (selectedRow != -1) {
                    // select the row first
                    customJTable.setRowSelectionInterval(selectedRow, selectedRow);
                    selectedRows = new int[] { selectedRow };
                }
            }

            for (int selectedRow : selectedRows) {
                selectedRowList.add(selectedRow);
            }

            final int size = selectedRowList.size();

            if (size > 0) {

                final JPopupMenu popupMenu = new JPopupMenu();

                RightClickMenuItem copyRightClickMenuItem = null;
                RightClickMenuItem openNodeRightClickMenuItem = null;

                copyRightClickMenuItem = getCopyEntryRightClickMenuItem(popupMenu, customJTable, selectedRowList);

                if (selectedRowList.size() == 1) {
                    openNodeRightClickMenuItem = getOpenNodeRightClickMenuItem(popupMenu, customJTable,
                            selectedRowList);
                }

                // expected order
                if (copyRightClickMenuItem != null) {
                    addPopupMenu(popupMenu, copyRightClickMenuItem);
                }

                if (openNodeRightClickMenuItem != null) {
                    addPopupMenu(popupMenu, openNodeRightClickMenuItem);
                }

                popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
            }

        } else if (mouseEvent.getClickCount() == 2) {

            final CustomJTable customJTable = (CustomJTable) mouseEvent.getSource();
            int selectedRow = customJTable.getSelectedRow();

            invokeOpenNode(customJTable, selectedRow);

        } else {
            super.mouseClicked(mouseEvent);
        }
    }

    private void addPopupMenu(JPopupMenu popupMenu, RightClickMenuItem rightClickMenuItem) {

        if (rightClickMenuItem != null) {
            popupMenu.add(rightClickMenuItem);
        }
    }

    private RightClickMenuItem getCopyEntryRightClickMenuItem(JPopupMenu popupMenu, CustomJTable customJTable,
            List<Integer> selectedRowList) {

        final RightClickMenuItem copyRightClickMenuItem = new RightClickMenuItem("Copy");

        copyRightClickMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                AbstractDataTableModel<?, ?> abstractDataTableModel;
                abstractDataTableModel = (AbstractDataTableModel<?, ?>) customJTable.getModel();

                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

                int[] array = selectedRowList.stream().mapToInt(i -> i).toArray();
                String selectedRowsData = abstractDataTableModel.getSelectedRowsData(array);

                clipboard.setContents(new StringSelection(selectedRowsData), copyRightClickMenuItem);

                popupMenu.setVisible(false);

            }
        });

        return copyRightClickMenuItem;
    }

    private RightClickMenuItem getOpenNodeRightClickMenuItem(JPopupMenu popupMenu, CustomJTable customJTable,
            List<Integer> selectedRowList) {

        final RightClickMenuItem openNodeRightClickMenuItem = new RightClickMenuItem("Open Node");

        openNodeRightClickMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                popupMenu.setVisible(false);

                int selectedRow = selectedRowList.get(0);
                invokeOpenNode(customJTable, selectedRow);

            }
        });

        return openNodeRightClickMenuItem;
    }

    private void invokeOpenNode(CustomJTable customJTable, int selectedRow) {

        SystemStateTreeNavigationController systemStateTreeNavigationController;
        systemStateTreeNavigationController = getSystemStateTreeNavigationController();

        if (systemStateTreeNavigationController != null) {
            String nodeId = getNodeId(customJTable, selectedRow);
            systemStateTreeNavigationController.scrollToKey(nodeId);
        }
    }
}
