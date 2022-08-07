/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
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
import com.pega.gcs.logviewer.model.ThreadDumpColumn;
import com.pega.gcs.logviewer.model.ThreadDumpThreadInfo;
import com.pega.gcs.logviewer.model.ThreadDumpThreadInfoV7;

public class ThreadDumpTableModel extends FilterTableModel<String> {

    private static final long serialVersionUID = 5568889081583557545L;

    private List<String> threadNameList;

    private HashMap<String, Integer> keyIndexMap;

    private Map<String, ThreadDumpThreadInfo> threadDumpThreadInfoMap;

    private List<ThreadDumpColumn> threadDumpColumnList;

    private boolean v7ThreadDump;

    public ThreadDumpTableModel(List<ThreadDumpThreadInfo> threadDumpThreadInfoList) {

        super(null);

        v7ThreadDump = false;

        if (threadDumpThreadInfoList != null) {

            ThreadDumpThreadInfo threadDumpThreadInfo = threadDumpThreadInfoList.get(0);

            if (threadDumpThreadInfo instanceof ThreadDumpThreadInfoV7) {
                threadDumpColumnList = ThreadDumpColumn.getV7ThreadDumpColumnList();
                v7ThreadDump = true;
            } else {
                threadDumpColumnList = ThreadDumpColumn.getV6ThreadDumpColumnList();
            }

        } else {
            threadDumpColumnList = new ArrayList<>();
        }

        resetModel();

        threadNameList = getFtmEntryKeyList();
        threadDumpThreadInfoMap = new HashMap<>();

        if (threadDumpThreadInfoList != null) {

            for (ThreadDumpThreadInfo tdThreadInfo : threadDumpThreadInfoList) {

                String threadName = tdThreadInfo.getThreadName();

                threadNameList.add(threadName);

                threadDumpThreadInfoMap.put(threadName, tdThreadInfo);

                updateColumnFilterMap(tdThreadInfo);

            }

            Collections.sort(threadNameList);
        }

        updateKeyIndexMap();
    }

    private void updateColumnFilterMap(ThreadDumpThreadInfo threadDumpThreadInfo) {

        if (threadDumpThreadInfo != null) {

            String threadName = threadDumpThreadInfo.getThreadName();

            Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<String>>> columnFilterMap = getColumnFilterMap();

            Iterator<FilterColumn> fcIterator = columnFilterMap.keySet().iterator();

            while (fcIterator.hasNext()) {

                FilterColumn filterColumn = fcIterator.next();

                List<CheckBoxMenuItemPopupEntry<String>> columnFilterEntryList;
                columnFilterEntryList = columnFilterMap.get(filterColumn);

                if (columnFilterEntryList == null) {
                    columnFilterEntryList = new ArrayList<CheckBoxMenuItemPopupEntry<String>>();
                    columnFilterMap.put(filterColumn, columnFilterEntryList);
                }

                int columnIndex = filterColumn.getIndex();
                String columnName = threadDumpColumnList.get(columnIndex).getColumnId();

                Object value = threadDumpThreadInfo.getValue(columnName);

                String columnValueStr = (value != null) ? value.toString() : null;

                if (columnValueStr == null) {
                    columnValueStr = FilterTableModel.NULL_STR;
                } else if ("".equals(columnValueStr)) {
                    columnValueStr = FilterTableModel.EMPTY_STR;
                }

                CheckBoxMenuItemPopupEntry<String> columnFilterEntry;

                CheckBoxMenuItemPopupEntry<String> searchKey;
                searchKey = new CheckBoxMenuItemPopupEntry<String>(columnValueStr);

                int index = columnFilterEntryList.indexOf(searchKey);

                if (index == -1) {
                    columnFilterEntry = new CheckBoxMenuItemPopupEntry<String>(columnValueStr);
                    columnFilterEntryList.add(columnFilterEntry);
                } else {
                    columnFilterEntry = columnFilterEntryList.get(index);
                }

                columnFilterEntry.addRowIndex(threadName);

            }
        }
    }

    @Override
    public int getColumnCount() {
        return threadDumpColumnList.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        String columnName = threadDumpColumnList.get(columnIndex).getColumnId();

        ThreadDumpThreadInfo threadDumpThreadInfo = getThreadDumpThreadInfo(rowIndex);

        Object value = threadDumpThreadInfo.getValue(columnName);

        return value;
    }

    @Override
    public String getColumnValue(Object valueAtObject, int columnIndex) {

        String columnValue = null;

        if (valueAtObject != null) {
            columnValue = valueAtObject.toString();
        }

        return columnValue;
    }

    @Override
    protected int getModelColumnIndex(int column) {
        return column;
    }

    @Override
    protected boolean search(String key, Object searchStrObj) {
        return false;
    }

    @Override
    protected FilterTableModelNavigation<String> getNavigationRowIndex(List<String> resultList,
            int currSelectedRowIndex, boolean forward, boolean first, boolean last, boolean wrap) {
        return null;
    }

    @Override
    public List<String> getFtmEntryKeyList() {

        if (threadNameList == null) {
            threadNameList = new ArrayList<>();
        }

        return threadNameList;
    }

    @Override
    protected HashMap<String, Integer> getKeyIndexMap() {

        if (keyIndexMap == null) {
            keyIndexMap = new HashMap<>();
        }

        return keyIndexMap;
    }

    @Override
    public void resetModel() {

        Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<String>>> columnFilterMap;
        columnFilterMap = getColumnFilterMap();
        columnFilterMap.clear();

        for (int columnIndex = 0; columnIndex < threadDumpColumnList.size(); columnIndex++) {

            ThreadDumpColumn threadDumpColumn = threadDumpColumnList.get(columnIndex);

            // preventing unnecessary buildup of filter map
            if (threadDumpColumn.isFilterable()) {
                FilterColumn filterColumn = new FilterColumn(columnIndex);
                filterColumn.setColumnFilterEnabled(true);
                columnFilterMap.put(filterColumn, null);
            }
        }
    }

    @Override
    public Object getEventForKey(String key) {
        return null;
    }

    @Override
    public AbstractTreeTableNode getTreeNodeForKey(String key) {
        return null;
    }

    @Override
    public void clearSearchResults(boolean clearResults) {

    }

    @Override
    public SearchModel<String> getSearchModel() {
        return null;
    }

    public ThreadDumpThreadInfo getThreadDumpThreadInfo(int rowIndex) {
        List<String> threadNameList = getFtmEntryKeyList();

        String threadName = threadNameList.get(rowIndex);

        ThreadDumpThreadInfo threadDumpThreadInfo = threadDumpThreadInfoMap.get(threadName);

        return threadDumpThreadInfo;
    }

    @Override
    public TableColumnModel getTableColumnModel() {

        TableColumnModel tableColumnModel = new DefaultTableColumnModel();

        TableColumn tableColumn = null;
        int columnIndex = 0;

        for (ThreadDumpColumn threadDumpColumn : threadDumpColumnList) {

            TableCellRenderer tcr;

            DefaultTableCellRenderer dtcr = getDefaultTableCellRenderer();
            dtcr.setHorizontalAlignment(threadDumpColumn.getHorizontalAlignment());
            tcr = dtcr;

            int prefColumnWidth = threadDumpColumn.getPrefColumnWidth();

            tableColumn = new TableColumn(columnIndex++);
            tableColumn.setHeaderValue(threadDumpColumn.getDisplayName());
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

    public boolean isV7ThreadDump() {
        return v7ThreadDump;
    }

    public void applyFilter(String filterText, boolean caseSensitiveFilter, boolean excludeBenignFilter) {

        List<String> threadDumpThreadInfoList = getFtmEntryKeyList();
        threadDumpThreadInfoList.clear();

        for (Map.Entry<String, ThreadDumpThreadInfo> threadDumpThreadInfoEntrySet : threadDumpThreadInfoMap
                .entrySet()) {

            String threadName = threadDumpThreadInfoEntrySet.getKey();
            ThreadDumpThreadInfo threadDumpThreadInfo = threadDumpThreadInfoEntrySet.getValue();

            boolean search = threadDumpThreadInfo.search(filterText, caseSensitiveFilter);

            int stackDepth = threadDumpThreadInfo.getStackDepth();
            boolean benignThread = (excludeBenignFilter) ? (stackDepth > 5) : true;

            if (search && benignThread) {
                threadDumpThreadInfoList.add(threadName);
            }
        }

        Collections.sort(threadDumpThreadInfoList);

        fireTableDataChanged();
    }

}
