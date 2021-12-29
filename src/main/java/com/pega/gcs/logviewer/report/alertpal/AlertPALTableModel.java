/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.report.alertpal;

import java.awt.Color;
import java.awt.Component;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.pega.gcs.fringecommon.guiutilities.CustomJTableModel;
import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.model.AlertLogEntry;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.PALStatisticName;

public class AlertPALTableModel extends CustomJTableModel {

    private static final long serialVersionUID = -5969212700097525759L;

    private static final Log4j2Helper LOG = new Log4j2Helper(AlertPALTableModel.class);

    private static final int alertTableColumnCount = 7;

    private LogTableModel logTableModel;

    // stores table column index and name as map
    private Map<Integer, String> tableModelColumnIndexMap;

    // stores column name and model value index
    private Map<String, Integer> logEntryColumnNameMap;

    private TableColumnModel tableColumnModel;

    public AlertPALTableModel(LogTableModel logTableModel) {
        super();
        this.logTableModel = logTableModel;

        initialise();
    }

    @Override
    public int getRowCount() {
        return logTableModel.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return tableModelColumnIndexMap.keySet().size();
    }

    @Override
    public String getColumnName(int column) {
        return tableModelColumnIndexMap.get(column);
    }

    // return either String, Long or Double types
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        AlertLogEntry alertLogEntry = null;

        alertLogEntry = (AlertLogEntry) logTableModel.getValueAt(rowIndex, columnIndex);

        return alertLogEntry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.guiutilities.CustomJTableModel#getTableColumnModel( )
     */
    @Override
    public TableColumnModel getTableColumnModel() {
        return tableColumnModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.guiutilities.CustomJTableModel#getColumnValue(java. lang.Object, int)
     */
    @Override
    public String getColumnValue(Object valueAtObject, int columnIndex) {

        AlertLogEntry alertLogEntry = (AlertLogEntry) valueAtObject;

        String columnValue = null;

        String columnName = tableModelColumnIndexMap.get(columnIndex);

        int logEntryColumnIndex = logEntryColumnNameMap.get(columnName);

        AlertLogEntryModel alem = (AlertLogEntryModel) logTableModel.getLogEntryModel();

        if (columnIndex < alertTableColumnCount) {

            columnValue = alem.getFormattedLogEntryValue(alertLogEntry, logEntryColumnIndex);
        } else {

            Number palDataValue = alertLogEntry.getPALDataValue(logEntryColumnIndex);

            if (palDataValue != null) {

                Locale locale = logTableModel.getLocale();

                NumberFormat nf = NumberFormat.getInstance(locale);

                columnValue = nf.format(palDataValue);
            }
        }

        return columnValue;
    }

    private void initialise() {

        logTableModel.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent tableModelEvent) {
                fireTableDataChanged();
            }
        });

        tableModelColumnIndexMap = new HashMap<Integer, String>();
        logEntryColumnNameMap = new HashMap<String, Integer>();
        tableColumnModel = new DefaultTableColumnModel();

        AlertLogEntryModel alem = (AlertLogEntryModel) logTableModel.getLogEntryModel();

        ArrayList<String> logEntryColumnList = alem.getLogEntryColumnList();

        int modelColumnIndex = 0;
        String columnName = null;

        // add only following into the PAL column list
        // LINE
        columnName = LogEntryColumn.LINE.getColumnId();
        updateColumn(modelColumnIndex, columnName, logEntryColumnList);
        modelColumnIndex++;

        // TIMESTAMP
        columnName = LogEntryColumn.TIMESTAMP.getColumnId();
        updateColumn(modelColumnIndex, columnName, logEntryColumnList);
        modelColumnIndex++;

        // MESSAGEID
        columnName = LogEntryColumn.MESSAGEID.getColumnId();
        updateColumn(modelColumnIndex, columnName, logEntryColumnList);
        modelColumnIndex++;

        // REQUESTORID
        columnName = LogEntryColumn.REQUESTORID.getColumnId();
        updateColumn(modelColumnIndex, columnName, logEntryColumnList);
        modelColumnIndex++;

        // USERID
        columnName = LogEntryColumn.USERID.getColumnId();
        updateColumn(modelColumnIndex, columnName, logEntryColumnList);
        modelColumnIndex++;

        // INTERACTIONNUMBER
        columnName = LogEntryColumn.INTERACTIONNUMBER.getColumnId();
        updateColumn(modelColumnIndex, columnName, logEntryColumnList);
        modelColumnIndex++;

        // FIRSTACTIVITY
        columnName = LogEntryColumn.FIRSTACTIVITY.getColumnId();
        updateColumn(modelColumnIndex, columnName, logEntryColumnList);
        modelColumnIndex++;

        TreeSet<PALStatisticName> palStatisticColumnSet = alem.getPalStatisticColumnSet();

        for (PALStatisticName palStatisticName : palStatisticColumnSet) {
            columnName = palStatisticName.name();
            updateColumn(modelColumnIndex, columnName, logEntryColumnList);
            modelColumnIndex++;

        }

    }

    private void updateColumn(int modelColumnIndex, String columnName, ArrayList<String> logEntryColumnList) {

        int logEntryColumnIndex = 0;
        TableColumn tableColumn = null;

        int horizontalAlignment = SwingConstants.RIGHT;
        int colWidth = 70;

        tableModelColumnIndexMap.put(modelColumnIndex, columnName);

        tableColumn = new TableColumn(modelColumnIndex);
        tableColumn.setHeaderValue(columnName);

        if (modelColumnIndex < alertTableColumnCount) {

            try {

                LogEntryColumn logEntryColumn = LogEntryColumn.getTableColumnById(columnName);

                logEntryColumnIndex = logEntryColumnList.indexOf(columnName);
                logEntryColumnNameMap.put(columnName, logEntryColumnIndex);

                horizontalAlignment = logEntryColumn.getHorizontalAlignment();
                colWidth = logEntryColumn.getPrefColumnWidth();
            } catch (IllegalArgumentException iae) {
                LOG.error("Error getting LogEntryColumn: " + columnName, iae);
            }
        } else {

            try {

                PALStatisticName palStatisticName = PALStatisticName.valueOf(columnName);

                logEntryColumnIndex = palStatisticName.ordinal();
                logEntryColumnNameMap.put(columnName, logEntryColumnIndex);

                horizontalAlignment = palStatisticName.getHorizontalAlignment();
                colWidth = palStatisticName.getPrefColumnWidth();
            } catch (IllegalArgumentException iae) {
                LOG.error("Error getting PALStatisticName: " + columnName, iae);
            }
        }

        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {

            private static final long serialVersionUID = 4405105756857852010L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                AlertLogEntry ale = (AlertLogEntry) value;

                if (ale != null) {

                    String columnValue = getColumnValue(ale, column);

                    super.getTableCellRendererComponent(table, columnValue, isSelected, hasFocus, row, column);

                    if (!table.isRowSelected(row)) {

                        Color color;

                        boolean searchFound = ale.isSearchFound();

                        if (searchFound) {
                            color = MyColor.LIGHT_YELLOW;
                        } else {
                            color = ale.getBackgroundColor();
                        }

                        setBackground(color);
                    }

                    setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 5));

                } else {
                    setBackground(Color.WHITE);

                    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }

                return this;
            }

        };

        dtcr.setHorizontalAlignment(horizontalAlignment);

        tableColumn.setCellRenderer(dtcr);

        tableColumn.setPreferredWidth(colWidth);
        tableColumn.setWidth(colWidth);

        tableColumnModel.addColumn(tableColumn);
    }

    public Charset getCharset() {
        return logTableModel.getCharset();
    }
}
