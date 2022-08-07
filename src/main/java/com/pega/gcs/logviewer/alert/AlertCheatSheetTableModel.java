/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.alert;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.pega.gcs.fringecommon.guiutilities.CheckBoxMenuItemPopupEntry;
import com.pega.gcs.fringecommon.guiutilities.FilterColumn;
import com.pega.gcs.fringecommon.guiutilities.FilterTableModel;
import com.pega.gcs.fringecommon.guiutilities.FilterTableModelNavigation;
import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.fringecommon.guiutilities.search.SearchModel;
import com.pega.gcs.fringecommon.guiutilities.treetable.AbstractTreeTableNode;
import com.pega.gcs.logviewer.model.alert.AlertMessageList.AlertMessage;
import com.pega.gcs.logviewer.model.alert.AlertMessageListProvider;
import com.pega.gcs.logviewer.model.alert.Severity;

public class AlertCheatSheetTableModel extends FilterTableModel<Integer> {

    private static final long serialVersionUID = -2723048955819135389L;

    private List<Integer> alertMessageKeyList;

    private HashMap<Integer, Integer> keyIndexMap;

    List<AlertCheatSheetTableColumn> alertCheatSheetTableColumnList;

    public AlertCheatSheetTableModel() {

        super(null);

        this.alertCheatSheetTableColumnList = AlertCheatSheetTableColumn.getTableColumnList();

        resetModel();

        initialise();
    }

    @Override
    public int getColumnCount() {
        return alertCheatSheetTableColumnList.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        List<Integer> alertIdList = getFtmEntryKeyList();

        Integer alertId = alertIdList.get(rowIndex);

        AlertMessageListProvider alertMessageListProvider = AlertMessageListProvider.getInstance();

        AlertMessage alertMessage = alertMessageListProvider.getAlertMessage(alertId);

        String value = getColumnValue(alertMessage, columnIndex);

        return value;
    }

    @Override
    public String getColumnValue(Object valueAtObject, int columnIndex) {

        AlertMessage alertMessage = (AlertMessage) valueAtObject;

        String columnValue = null;

        if (alertMessage != null) {

            AlertCheatSheetTableColumn alertCheatSheetTableColumn;
            alertCheatSheetTableColumn = alertCheatSheetTableColumnList.get(columnIndex);

            if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.ID)) {
                columnValue = String.valueOf(alertMessage.getId());
            } else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.MESSAGEID)) {
                columnValue = alertMessage.getMessageID();
            } else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.CATEGORY)) {
                columnValue = alertMessage.getCategory();
            } else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.SUBCATEGORY)) {
                columnValue = alertMessage.getSubcategory();
            } else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.TITLE)) {
                columnValue = alertMessage.getTitle();
            } else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.SEVERITY)) {
                Severity severity = alertMessage.getSeverity();
                columnValue = (severity != null) ? severity.name() : null;
            } else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.PDNURL)) {
                columnValue = alertMessage.getPegaUrl();
            } else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.DESCRIPTION)) {
                columnValue = alertMessage.getDescription();
            } else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.DSSENABLECONFIG)) {
                columnValue = alertMessage.getDssEnableConfig();
            } else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.DSSENABLED)) {
                columnValue = alertMessage.getDssEnabled();
            } else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.DSSTHRESHOLDCONFIG)) {
                columnValue = alertMessage.getDssThresholdConfig();
            } else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.DSSVALUETYPE)) {
                columnValue = alertMessage.getDssValueType();
            } else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.DSSVALUEUNIT)) {
                columnValue = alertMessage.getDssValueUnit();
            } else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.DSSDEFAULTVALUE)) {
                columnValue = alertMessage.getDssDefaultValue();
            }

        }

        return columnValue;
    }

    @Override
    protected int getModelColumnIndex(int column) {
        return column;
    }

    @Override
    protected boolean search(Integer key, Object searchStrObj) {
        return false;
    }

    @Override
    protected FilterTableModelNavigation<Integer> getNavigationRowIndex(List<Integer> resultList,
            int currSelectedRowIndex, boolean forward, boolean first, boolean last, boolean wrap) {
        return null;
    }

    @Override
    public List<Integer> getFtmEntryKeyList() {

        if (alertMessageKeyList == null) {
            alertMessageKeyList = new ArrayList<Integer>();
        }

        return alertMessageKeyList;
    }

    @Override
    protected HashMap<Integer, Integer> getKeyIndexMap() {

        if (keyIndexMap == null) {
            keyIndexMap = new HashMap<>();
        }

        return keyIndexMap;
    }

    @Override
    public void resetModel() {

        Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<Integer>>> columnFilterMap;
        columnFilterMap = getColumnFilterMap();
        columnFilterMap.clear();

        for (int columnIndex = 0; columnIndex < alertCheatSheetTableColumnList.size(); columnIndex++) {

            AlertCheatSheetTableColumn alertCheatSheetTableColumn = alertCheatSheetTableColumnList.get(columnIndex);

            // preventing unnecessary buildup of filter map
            if (alertCheatSheetTableColumn.isFilterable()) {
                FilterColumn filterColumn = new FilterColumn(columnIndex);
                filterColumn.setColumnFilterEnabled(true);
                columnFilterMap.put(filterColumn, null);
            }
        }
    }

    @Override
    public Object getEventForKey(Integer key) {
        return null;
    }

    @Override
    public AbstractTreeTableNode getTreeNodeForKey(Integer key) {
        return null;
    }

    @Override
    public void clearSearchResults(boolean clearResults) {

    }

    @Override
    public SearchModel<Integer> getSearchModel() {
        return null;
    }

    private void initialise() {

        List<Integer> alertIdList = getFtmEntryKeyList();

        AlertMessageListProvider alertMessageListProvider = AlertMessageListProvider.getInstance();

        for (String messageId : alertMessageListProvider.getMessageIdList()) {

            AlertMessage alertMessage = alertMessageListProvider.getAlertMessage(messageId);
            Integer alertId = alertMessage.getId();

            alertIdList.add(alertId);

            updateColumnFilterMap(alertId, alertMessage);
        }

        updateKeyIndexMap();

    }

    // clearing the columnFilterMap will skip the below loop
    private void updateColumnFilterMap(Integer alertMessageKey, AlertMessage alertMessage) {

        if (alertMessage != null) {

            Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<Integer>>> columnFilterMap = getColumnFilterMap();

            Iterator<FilterColumn> fcIterator = columnFilterMap.keySet().iterator();

            while (fcIterator.hasNext()) {

                FilterColumn filterColumn = fcIterator.next();

                List<CheckBoxMenuItemPopupEntry<Integer>> columnFilterEntryList;
                columnFilterEntryList = columnFilterMap.get(filterColumn);

                if (columnFilterEntryList == null) {
                    columnFilterEntryList = new ArrayList<CheckBoxMenuItemPopupEntry<Integer>>();
                    columnFilterMap.put(filterColumn, columnFilterEntryList);
                }

                int columnIndex = filterColumn.getIndex();

                String columnValue = getColumnValue(alertMessage, columnIndex);

                if (columnValue == null) {
                    columnValue = FilterTableModel.NULL_STR;
                } else if ("".equals(columnValue)) {
                    columnValue = FilterTableModel.EMPTY_STR;
                }

                CheckBoxMenuItemPopupEntry<Integer> columnFilterEntry;

                CheckBoxMenuItemPopupEntry<Integer> searchKey;
                searchKey = new CheckBoxMenuItemPopupEntry<Integer>(columnValue);

                int index = columnFilterEntryList.indexOf(searchKey);

                if (index == -1) {
                    columnFilterEntry = new CheckBoxMenuItemPopupEntry<Integer>(columnValue);
                    columnFilterEntryList.add(columnFilterEntry);
                } else {
                    columnFilterEntry = columnFilterEntryList.get(index);
                }

                columnFilterEntry.addRowIndex(alertMessageKey);

            }
        }
    }

    @Override
    public TableColumnModel getTableColumnModel() {

        TableColumnModel tableColumnModel = new DefaultTableColumnModel();

        TableColumn tableColumn = null;
        int columnIndex = 0;

        for (AlertCheatSheetTableColumn alertCheatSheetTableColumn : alertCheatSheetTableColumnList) {

            DefaultTableCellRenderer dtcr = getDefaultTableCellRenderer();
            dtcr.setHorizontalAlignment(alertCheatSheetTableColumn.getHorizontalAlignment());
            TableCellRenderer tcr = dtcr;

            int prefColumnWidth = alertCheatSheetTableColumn.getPrefColumnWidth();

            tableColumn = new TableColumn(columnIndex++);
            tableColumn.setHeaderValue(alertCheatSheetTableColumn.getDisplayName());
            tableColumn.setCellRenderer(tcr);
            tableColumn.setPreferredWidth(prefColumnWidth);
            tableColumn.setWidth(prefColumnWidth);

            tableColumnModel.addColumn(tableColumn);
        }

        return tableColumnModel;
    }

    private DefaultTableCellRenderer getDefaultTableCellRenderer() {

        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {

            private static final long serialVersionUID = 1504347306097747771L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setBorder(new EmptyBorder(1, 4, 1, 4));

                if (!isSelected) {
                    setBackground(MyColor.LIGHTEST_LIGHT_GRAY);
                }
                return this;
            }

        };

        return dtcr;
    }
}
