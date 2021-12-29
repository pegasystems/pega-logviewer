/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.pega.gcs.fringecommon.guiutilities.CheckBoxMenuItemPopupEntry;
import com.pega.gcs.fringecommon.guiutilities.FilterColumn;
import com.pega.gcs.fringecommon.guiutilities.FilterTableModel;
import com.pega.gcs.fringecommon.guiutilities.FilterTableModelNavigation;
import com.pega.gcs.fringecommon.guiutilities.search.SearchModel;
import com.pega.gcs.fringecommon.guiutilities.treetable.AbstractTreeTableNode;
import com.pega.gcs.logviewer.catalog.model.HotfixColumn;
import com.pega.gcs.logviewer.catalog.model.HotfixEntry;
import com.pega.gcs.logviewer.catalog.model.HotfixRecordEntry;

public class HotfixRecordEntryTableModel extends FilterTableModel<Integer> {

    private static final long serialVersionUID = -7040463283091877211L;

    private HotfixEntry hotfixEntry;

    private List<HotfixColumn> visibleColumnList;

    private List<HotfixColumn> hotfixColumnList;

    private List<Integer> ftmEntryKeyList;

    private HashMap<Integer, Integer> keyIndexMap;

    // main data map
    private Map<Integer, HotfixRecordEntry> hotfixRecordEntryMap;

    public HotfixRecordEntryTableModel(HotfixEntry hotfixEntry, List<HotfixColumn> hotfixColumnList) {

        super(null);

        this.hotfixEntry = hotfixEntry;
        this.hotfixColumnList = hotfixColumnList;

        this.visibleColumnList = new ArrayList<>();
        this.hotfixRecordEntryMap = new HashMap<>();

        resetModel();

        initialise();

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {

        List<HotfixColumn> visibleColumnList = getVisibleColumnList();

        return visibleColumnList.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Integer rowKey = getFtmEntryKeyList().get(rowIndex);

        HotfixRecordEntry hotfixRecordEntry = getEventForKey(rowKey);

        return hotfixRecordEntry;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.guiutilities.CustomJTableModel#getColumnValue(java. lang.Object, int)
     */
    @Override
    public String getColumnValue(Object valueAtObject, int columnIndex) {

        HotfixRecordEntry hotfixRecordEntry = (HotfixRecordEntry) valueAtObject;

        HotfixColumn hotfixColumn = getColumn(columnIndex);

        String columnValue = null;

        if ((hotfixRecordEntry != null) && (hotfixColumn != null)) {

            int hfixColumnIndex = getHotfixColumnList().indexOf(hotfixColumn);

            columnValue = hotfixRecordEntry.getHotfixRecordEntryData(hotfixColumn, hfixColumnIndex);
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

        TableColumnModel tableColumnModel = new DefaultTableColumnModel();

        List<HotfixColumn> visibleColumnList = getVisibleColumnList();

        for (int index = 0; index < getColumnCount(); index++) {

            HotfixColumn hotfixColumn = visibleColumnList.get(index);

            DefaultTableCellRenderer dtcr = new HotfixRecordEntryTableCellRenderer();

            int horizontalAlignment = hotfixColumn.getHorizontalAlignment();

            dtcr.setHorizontalAlignment(horizontalAlignment);

            int prefColumnWidth = hotfixColumn.getPrefColumnWidth();

            TableColumn tableColumn = new TableColumn(index);
            tableColumn.setHeaderValue(hotfixColumn.getDisplayName());
            tableColumn.setCellRenderer(dtcr);
            tableColumn.setPreferredWidth(prefColumnWidth);
            tableColumn.setWidth(prefColumnWidth);

            tableColumnModel.addColumn(tableColumn);
        }

        return tableColumnModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getModelColumnIndex( int)
     */
    @Override
    protected int getModelColumnIndex(int column) {
        return column;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.guiutilities.FilterTableModel#search(java.lang. Comparable, java.lang.Object)
     */
    @Override
    protected boolean search(Integer key, Object searchStrObj) {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getNavigationRowIndex (java.util.List, int, boolean, boolean,
     * boolean, boolean)
     */
    @Override
    protected FilterTableModelNavigation<Integer> getNavigationRowIndex(List<Integer> resultList,
            int currSelectedRowIndex, boolean forward, boolean first, boolean last, boolean wrap) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getFtmEntryKeyList()
     */
    @Override
    public List<Integer> getFtmEntryKeyList() {

        if (ftmEntryKeyList == null) {
            ftmEntryKeyList = new ArrayList<>();
        }

        return ftmEntryKeyList;
    }

    @Override
    protected HashMap<Integer, Integer> getKeyIndexMap() {

        if (keyIndexMap == null) {
            keyIndexMap = new HashMap<>();
        }

        return keyIndexMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.guiutilities.FilterTableModel#resetModel()
     */
    @Override
    public void resetModel() {

        List<Integer> ftmEntryKeyList = getFtmEntryKeyList();
        ftmEntryKeyList.clear();

        HashMap<Integer, Integer> keyIndexMap = getKeyIndexMap();
        keyIndexMap.clear();

        hotfixRecordEntryMap.clear();

        visibleColumnList.clear();

        Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<Integer>>> columnFilterMap;
        columnFilterMap = getColumnFilterMap();
        columnFilterMap.clear();

        List<HotfixColumn> hotfixColumns = new ArrayList<>();
        hotfixColumns.add(HotfixColumn.ID);
        hotfixColumns.addAll(hotfixColumnList);

        int columnIndex = 0;

        for (HotfixColumn hotfixColumn : hotfixColumns) {
            /*
            // @formatter:off
            // CHECKSTYLE:OFF
            if (hotfixColumn.equals(HotfixColumn.ID)
                    || hotfixColumn.equals(HotfixColumn.PXINSTALLSTATUS)
                    || hotfixColumn.equals(HotfixColumn.CODE_CHANGE_CLASSNAME)
                    || hotfixColumn.equals(HotfixColumn.CODE_CHANGE_JARNAME)
                    || hotfixColumn.equals(HotfixColumn.CODE_CHANGE_PACKAGENAME)
                    || hotfixColumn.equals(HotfixColumn.MODULE)
                    || hotfixColumn.equals(HotfixColumn.CODE_MODIFIED_DATE)
                    || hotfixColumn.equals(HotfixColumn.RULE_CHANGE_KEY)
                    || hotfixColumn.equals(HotfixColumn.RULE_CHANGE_RULESET)
                    || hotfixColumn.equals(HotfixColumn.SCHEMA_CHANGE_KEY)
                    || hotfixColumn.equals(HotfixColumn.SCHEMA_CHANGE_TABLE_NAME)
                    || hotfixColumn.equals(HotfixColumn.HASH)
                    || hotfixColumn.equals(HotfixColumn.CLASS_NAME)
                    || hotfixColumn.equals(HotfixColumn.JAR_NAME)
                    || hotfixColumn.equals(HotfixColumn.PACKAGE_NAME)
                    || hotfixColumn.equals(HotfixColumn.MODIFIED_DATE)) {
                // CHECKSTYLE:ON
                // @formatter:on
*/
            visibleColumnList.add(hotfixColumn);

            // preventing unnecessary buildup of filter map
            if (hotfixColumn.isFilterable()) {
                FilterColumn filterColumn = new FilterColumn(columnIndex);
                filterColumn.setColumnFilterEnabled(true);
                columnFilterMap.put(filterColumn, null);
            }

            columnIndex++;
            // }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getEventForKey(java. lang.Comparable)
     */
    @Override
    public HotfixRecordEntry getEventForKey(Integer key) {

        HotfixRecordEntry hotfixRecordEntry = null;

        if (key != null) {
            hotfixRecordEntry = hotfixRecordEntryMap.get(key);
        }

        return hotfixRecordEntry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getTreeNodeForKey( java.lang.Comparable)
     */
    @Override
    public AbstractTreeTableNode getTreeNodeForKey(Integer key) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.guiutilities.FilterTableModel#clearSearchResults( boolean)
     */
    @Override
    public void clearSearchResults(boolean clearResults) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getSearchModel()
     */
    @Override
    public SearchModel<Integer> getSearchModel() {
        return null;
    }

    private void initialise() {

        List<Integer> ftmEntryKeyList = getFtmEntryKeyList();

        ftmEntryKeyList.clear();

        hotfixRecordEntryMap.clear();

        for (HotfixRecordEntry hotfixRecordEntry : hotfixEntry.getHotfixRecordEntryList()) {

            Integer entryId = hotfixRecordEntry.getEntryId();

            ftmEntryKeyList.add(entryId);

            hotfixRecordEntryMap.put(entryId, hotfixRecordEntry);

            updateColumnFilterMap(entryId, hotfixRecordEntry);
        }

        updateKeyIndexMap();
    }

    // clearing the columnFilterMap will skip the below loop
    private void updateColumnFilterMap(Integer ftmEntryKey, HotfixRecordEntry hotfixRecordEntry) {

        Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<Integer>>> columnFilterMap = getColumnFilterMap();

        Iterator<FilterColumn> fcIterator = columnFilterMap.keySet().iterator();

        while (fcIterator.hasNext()) {

            FilterColumn filterColumn = fcIterator.next();

            int columnIndex = filterColumn.getIndex();

            HotfixColumn hotfixColumn = getColumn(columnIndex);

            boolean filterable = hotfixColumn.isFilterable();

            if (filterable) {

                List<CheckBoxMenuItemPopupEntry<Integer>> columnFilterEntryList;
                columnFilterEntryList = columnFilterMap.get(filterColumn);

                if (columnFilterEntryList == null) {
                    columnFilterEntryList = new ArrayList<CheckBoxMenuItemPopupEntry<Integer>>();
                    columnFilterMap.put(filterColumn, columnFilterEntryList);
                }

                String value = getColumnValue(hotfixRecordEntry, columnIndex);

                String columnValue = (value != null) ? value.toString() : null;

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
    }

    public List<HotfixColumn> getVisibleColumnList() {
        return visibleColumnList;
    }

    public List<HotfixColumn> getHotfixColumnList() {
        return hotfixColumnList;
    }

    private HotfixColumn getColumn(int columnIndex) {

        HotfixColumn hotfixColumn = getVisibleColumnList().get(columnIndex);

        return hotfixColumn;
    }
}
