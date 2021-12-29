
package com.pega.gcs.logviewer.hotfixscan.report;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.pega.gcs.fringecommon.guiutilities.CheckBoxMenuItemPopupEntry;
import com.pega.gcs.fringecommon.guiutilities.DefaultTableColumn;
import com.pega.gcs.fringecommon.guiutilities.FilterColumn;
import com.pega.gcs.fringecommon.guiutilities.FilterTableModel;
import com.pega.gcs.fringecommon.guiutilities.FilterTableModelNavigation;
import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.fringecommon.guiutilities.search.SearchModel;
import com.pega.gcs.fringecommon.guiutilities.treetable.AbstractTreeTableNode;
import com.pega.gcs.logviewer.catalog.model.HotfixColumn;

public class HotfixReportTableModel extends FilterTableModel<Integer> {

    private static final long serialVersionUID = 7484678416283627618L;

    private List<Integer> keyList;

    private HashMap<Integer, Integer> keyIndexMap;

    // ref map for key list and HotfixReportRecord map
    private Map<Integer, HotfixReportRecord> hotfixReportRecordMap;

    private List<HotfixColumn> tableColumnList;

    private TableColumnModel tableColumnModel;

    public HotfixReportTableModel(Map<Integer, HotfixReportRecord> hotfixReportRecordMap,
            List<HotfixColumn> tableColumnList) {

        super(null);

        this.hotfixReportRecordMap = hotfixReportRecordMap;
        this.tableColumnList = tableColumnList;

        resetModel();

        initialise();

    }

    @Override
    public int getColumnCount() {
        return tableColumnList.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        List<Integer> ftmEntryKeyList = getFtmEntryKeyList();

        Integer ftmEntryKey = ftmEntryKeyList.get(rowIndex);

        HotfixReportRecord value = hotfixReportRecordMap.get(ftmEntryKey);

        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.guiutilities.CustomJTableModel#getColumnValue(java. lang.Object, int)
     */
    @Override
    public String getColumnValue(Object valueAtObject, int columnIndex) {

        HotfixReportRecord hotfixReportRecord = (HotfixReportRecord) valueAtObject;

        String columnValue = null;

        if (hotfixReportRecord != null) {

            columnValue = hotfixReportRecord.getValue(columnIndex);
        }

        return columnValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.guiutilities.CustomJTableModel#getTableColumnModel( )
     */
    @Override
    public TableColumnModel getTableColumnModel() {

        if (tableColumnModel == null) {

            tableColumnModel = new DefaultTableColumnModel();

            TableColumn tableColumn = null;

            AtomicInteger columnIndex = new AtomicInteger(0);

            for (DefaultTableColumn defaultTableColumn : tableColumnList) {

                DefaultTableCellRenderer dtcr = getDefaultTableCellRenderer();

                dtcr.setHorizontalAlignment(defaultTableColumn.getHorizontalAlignment());

                int prefColumnWidth = defaultTableColumn.getPrefColumnWidth();

                tableColumn = new TableColumn(columnIndex.getAndIncrement());
                tableColumn.setHeaderValue(defaultTableColumn.getDisplayName());
                tableColumn.setCellRenderer(dtcr);
                tableColumn.setPreferredWidth(prefColumnWidth);
                tableColumn.setWidth(prefColumnWidth);

                tableColumnModel.addColumn(tableColumn);
            }
        }

        return tableColumnModel;
    }

    @Override
    protected int getModelColumnIndex(int column) {
        return column;
    }

    @Override
    protected boolean search(Integer key, Object searchStrObj) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected FilterTableModelNavigation<Integer> getNavigationRowIndex(List<Integer> resultList,
            int currSelectedRowIndex, boolean forward, boolean first, boolean last, boolean wrap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Integer> getFtmEntryKeyList() {

        if (keyList == null) {
            keyList = new ArrayList<>();
        }

        return keyList;
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

        for (int columnIndex = 0; columnIndex < tableColumnList.size(); columnIndex++) {

            DefaultTableColumn defaultTableColumn = tableColumnList.get(columnIndex);

            // preventing unnecessary buildup of filter map
            if (defaultTableColumn.isFilterable()) {
                FilterColumn filterColumn = new FilterColumn(columnIndex);
                filterColumn.setColumnFilterEnabled(true);
                columnFilterMap.put(filterColumn, null);
            }
        }
    }

    @Override
    public Object getEventForKey(Integer key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AbstractTreeTableNode getTreeNodeForKey(Integer key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void clearSearchResults(boolean clearResults) {
        // TODO Auto-generated method stub

    }

    @Override
    public SearchModel<Integer> getSearchModel() {
        // TODO Auto-generated method stub
        return null;
    }

    private void initialise() {

        List<Integer> ftmEntryKeyList = getFtmEntryKeyList();

        ftmEntryKeyList.clear();

        for (Map.Entry<Integer, HotfixReportRecord> entry : hotfixReportRecordMap.entrySet()) {

            Integer ftmEntryKey = entry.getKey();
            HotfixReportRecord hotfixReportRecord = entry.getValue();

            ftmEntryKeyList.add(ftmEntryKey);

            updateColumnFilterMap(ftmEntryKey, hotfixReportRecord);
        }

        Collections.sort(ftmEntryKeyList);

        updateKeyIndexMap();
    }

    // clearing the columnFilterMap will skip the below loop
    private void updateColumnFilterMap(Integer ftmEntryKey, HotfixReportRecord hotfixReportRecord) {

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

            String columnValue = hotfixReportRecord.getValue(columnIndex);

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

            columnFilterEntry.addRowIndex(ftmEntryKey);

        }

    }

    private DefaultTableCellRenderer getDefaultTableCellRenderer() {

        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {

            private static final long serialVersionUID = 5071579118636978454L;

            /*
             * (non-Javadoc)
             * 
             * @see javax.swing.table.DefaultTableCellRenderer# getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean,
             * boolean, int, int)
             */
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                HotfixReportRecord hotfixReportRecord = (HotfixReportRecord) value;

                String columnValue = hotfixReportRecord.getValue(column);

                super.getTableCellRendererComponent(table, columnValue, isSelected, hasFocus, row, column);

                setBorder(new EmptyBorder(1, 8, 1, 10));

                if (!isSelected) {
                    setBackground(MyColor.LIGHTEST_LIGHT_GRAY);
                }

                return this;
            }

        };

        return dtcr;
    }

}
