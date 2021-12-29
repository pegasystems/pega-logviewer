/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer;

import java.util.List;

import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.pega.gcs.logviewer.model.Log4jLogRequestorLockEntry;

public class ThreadDumpRequestorLockTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 2195030765030098921L;

    // 5 for fields, and 1 for line number
    private static final int COLUMN_COUNT = 6;

    private TableColumnModel tableColumnModel;

    private List<Log4jLogRequestorLockEntry> log4jLogRequestorLockEntryList;

    public ThreadDumpRequestorLockTableModel(List<Log4jLogRequestorLockEntry> log4jLogRequestorLockEntryList) {
        super();

        this.log4jLogRequestorLockEntryList = log4jLogRequestorLockEntryList;

        initialise();
    }

    @Override
    public int getRowCount() {
        return log4jLogRequestorLockEntryList.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Log4jLogRequestorLockEntry log4jLogRequestorLockEntry;

        String value = null;

        log4jLogRequestorLockEntry = log4jLogRequestorLockEntryList.get(rowIndex);

        switch (columnIndex) {

        // line number
        case 0:
            value = String.valueOf(rowIndex + 1);
            break;

        // requestorId
        case 1:
            value = log4jLogRequestorLockEntry.getRequestorId();
            break;

        // thisThreadName
        case 2:
            value = log4jLogRequestorLockEntry.getThisThreadName();
            break;

        // originalLockThreadName
        case 3:
            value = log4jLogRequestorLockEntry.getOriginalLockThreadName();
            break;

        // finallyLockThreadName
        case 4:
            value = log4jLogRequestorLockEntry.getFinallyLockThreadName();
            break;

        // timeInterval
        case 5:
            value = log4jLogRequestorLockEntry.getTimeInterval().toString();
            break;
        default:
            break;
        }

        return value;
    }

    private void initialise() {
        // set up table columns
        tableColumnModel = new DefaultTableColumnModel();

        TableColumn tableColumn = null;
        DefaultTableCellRenderer dtcr = null;

        // line number
        tableColumn = new TableColumn(0);
        tableColumn.setHeaderValue("Line");
        dtcr = new DefaultTableCellRenderer();
        dtcr.setHorizontalAlignment(SwingConstants.CENTER);
        tableColumn.setCellRenderer(dtcr);
        tableColumn.setPreferredWidth(40);
        tableColumn.setWidth(50);
        tableColumnModel.addColumn(tableColumn);

        // requestorId
        tableColumn = new TableColumn(1);
        tableColumn.setHeaderValue("Requestor Id");
        dtcr = new DefaultTableCellRenderer();
        dtcr.setHorizontalAlignment(SwingConstants.CENTER);
        tableColumn.setCellRenderer(dtcr);
        tableColumn.setPreferredWidth(230);
        tableColumn.setWidth(50);
        tableColumnModel.addColumn(tableColumn);

        // thisThreadName
        tableColumn = new TableColumn(2);
        tableColumn.setHeaderValue("This Thread Name");
        dtcr = new DefaultTableCellRenderer();
        dtcr.setHorizontalAlignment(SwingConstants.CENTER);
        tableColumn.setCellRenderer(dtcr);
        tableColumn.setPreferredWidth(350);
        tableColumn.setWidth(50);
        tableColumnModel.addColumn(tableColumn);

        // originalLockThreadName
        tableColumn = new TableColumn(3);
        tableColumn.setHeaderValue("Original Lock Thread Name");
        dtcr = new DefaultTableCellRenderer();
        dtcr.setHorizontalAlignment(SwingConstants.CENTER);
        tableColumn.setCellRenderer(dtcr);
        tableColumn.setPreferredWidth(350);
        tableColumn.setWidth(50);
        tableColumnModel.addColumn(tableColumn);

        // finallyLockThreadName
        tableColumn = new TableColumn(4);
        tableColumn.setHeaderValue("Finally Lock Thread Name");
        dtcr = new DefaultTableCellRenderer();
        dtcr.setHorizontalAlignment(SwingConstants.CENTER);
        tableColumn.setCellRenderer(dtcr);
        tableColumn.setPreferredWidth(350);
        tableColumn.setWidth(50);
        tableColumnModel.addColumn(tableColumn);

        // timeInterval
        tableColumn = new TableColumn(5);
        tableColumn.setHeaderValue("Time Interval");
        dtcr = new DefaultTableCellRenderer();
        dtcr.setHorizontalAlignment(SwingConstants.CENTER);
        tableColumn.setCellRenderer(dtcr);
        tableColumn.setPreferredWidth(90);
        tableColumn.setWidth(50);
        tableColumnModel.addColumn(tableColumn);
    }

    public TableColumnModel getTableColumnModel() {
        return tableColumnModel;
    }

}
