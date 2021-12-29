
package com.pega.gcs.logviewer.catalog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.pega.gcs.fringecommon.guiutilities.CheckBoxMenuItemPopupEntry;
import com.pega.gcs.fringecommon.guiutilities.FilterColumn;
import com.pega.gcs.fringecommon.guiutilities.FilterTableModel;
import com.pega.gcs.fringecommon.guiutilities.FilterTableModelNavigation;
import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.search.SearchData;
import com.pega.gcs.fringecommon.guiutilities.search.SearchModel;
import com.pega.gcs.fringecommon.guiutilities.treetable.AbstractTreeTableNode;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.GeneralUtilities;
import com.pega.gcs.logviewer.catalog.model.HotfixColumn;
import com.pega.gcs.logviewer.catalog.model.HotfixEntry;
import com.pega.gcs.logviewer.catalog.model.HotfixEntryKey;
import com.pega.gcs.logviewer.catalog.model.HotfixRecordEntry;

public class HotfixTableModel extends FilterTableModel<HotfixEntryKey> {

    private static final long serialVersionUID = 9177595805319794967L;

    private static final Log4j2Helper LOG = new Log4j2Helper(HotfixTableModel.class);

    private List<HotfixColumn> visibleColumnList;

    private SearchData<HotfixEntryKey> searchData;

    private List<HotfixEntryKey> ftmEntryKeyList;

    private HashMap<HotfixEntryKey, Integer> keyIndexMap;

    private SearchModel<HotfixEntryKey> searchModel;

    // main data - only for this model
    private List<HotfixColumn> hotfixColumnList;

    private TreeMap<HotfixEntryKey, HotfixEntry> hotfixEntryMap;

    // map to work with bookmarks. Bookmarks are on hofixId string
    private Map<String, HotfixEntryKey> hotfixIdEntryKeyMap;

    /*
     * this constructor is used by scanresult which get hotfix columns at runtime
     */
    public HotfixTableModel(RecentFile recentFile, SearchData<HotfixEntryKey> searchData,
            List<HotfixColumn> visibleColumnList) {

        super(recentFile);

        this.searchData = searchData;

        if (this.searchData == null) {
            this.searchData = new SearchData<>(null);
        }

        this.visibleColumnList = visibleColumnList;
        this.hotfixColumnList = null;
        this.hotfixEntryMap = null;
        this.hotfixIdEntryKeyMap = null;

        resetModel();
    }

    /*
     * This constructor is used by catalog scan plugin
     */
    public HotfixTableModel(List<HotfixColumn> hotfixColumnList, TreeMap<HotfixEntryKey, HotfixEntry> hotfixEntryMap,
            List<HotfixColumn> visibleColumnList) {

        super(null);

        this.searchData = new SearchData<>(null);

        this.visibleColumnList = visibleColumnList;
        this.hotfixColumnList = hotfixColumnList;
        this.hotfixEntryMap = hotfixEntryMap;
        this.hotfixIdEntryKeyMap = null;

        resetModel();

        updateColumnFilterMap();
    }

    @Override
    public int getColumnCount() {

        List<HotfixColumn> visibleColumnList = getVisibleColumnList();

        return visibleColumnList.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        HotfixEntryKey hotfixEntryKey = getFtmEntryKeyList().get(rowIndex);

        HotfixEntry hotfixEntry = getEventForKey(hotfixEntryKey);

        return hotfixEntry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.guiutilities.CustomJTableModel#getColumnValue(java. lang.Object, int)
     */
    @Override
    public String getColumnValue(Object valueAtObject, int columnIndex) {

        HotfixEntry hotfixEntry = (HotfixEntry) valueAtObject;

        HotfixColumn hotfixColumn = getColumn(columnIndex);

        String columnValue = null;

        if ((hotfixEntry != null) && (hotfixColumn != null)) {

            List<HotfixColumn> hotfixColumnList = getHotfixColumnList();

            int hfixColumnIndex = hotfixColumnList.indexOf(hotfixColumn);

            columnValue = hotfixEntry.getHotfixEntryData(hotfixColumn, hfixColumnIndex);
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

            int horizontalAlignment = hotfixColumn.getHorizontalAlignment();
            int prefColumnWidth = hotfixColumn.getPrefColumnWidth();

            TableColumn tableColumn = new TableColumn(index);
            tableColumn.setHeaderValue(hotfixColumn.getDisplayName());
            tableColumn.setPreferredWidth(prefColumnWidth);
            tableColumn.setWidth(prefColumnWidth);

            HotfixTableCellRenderer hotfixTableCellRenderer;
            hotfixTableCellRenderer = new HotfixTableCellRenderer();

            hotfixTableCellRenderer.setHorizontalAlignment(horizontalAlignment);

            tableColumn.setCellRenderer(hotfixTableCellRenderer);
            tableColumn.setResizable(true);

            tableColumnModel.addColumn(tableColumn);
        }

        return tableColumnModel;
    }

    @Override
    protected int getModelColumnIndex(int column) {
        return column;
    }

    @Override
    protected boolean search(HotfixEntryKey key, Object searchStrObj) {

        HotfixEntry hotfixEntry = getEventForKey(key);

        boolean casesensitive = false;
        boolean saveResults = true;

        boolean found = hotfixEntry.search(searchStrObj.toString(), casesensitive, saveResults);

        return found;
    }

    @Override
    protected FilterTableModelNavigation<HotfixEntryKey> getNavigationRowIndex(List<HotfixEntryKey> resultList,
            int currSelectedRowIndex, boolean forward, boolean first, boolean last, boolean wrap) {

        int navigationIndex = 0;
        int navigationRowIndex = 0;

        if ((resultList != null) && (resultList.size() > 0)) {

            int resultListSize = resultList.size();

            List<HotfixEntryKey> entryKeyList = getFtmEntryKeyList();

            int entryIndexListSize = entryKeyList.size();

            HotfixEntryKey entryKey = null;

            if (first) {

                entryKey = resultList.get(0);
                navigationIndex = 1;

            } else if (last) {

                int lastIndex = resultListSize - 1;
                entryKey = resultList.get(lastIndex);
                navigationIndex = resultListSize;

            } else if (forward) {
                // NEXT
                if (currSelectedRowIndex >= 0) {

                    if (currSelectedRowIndex < (entryIndexListSize - 1)) {
                        currSelectedRowIndex++;
                    } else {
                        if (wrap) {
                            currSelectedRowIndex = 0;
                        }
                    }
                } else {
                    currSelectedRowIndex = 0;
                }

                HotfixEntryKey currSelectedEntryKey = entryKeyList.get(currSelectedRowIndex);

                int searchIndex = Collections.binarySearch(resultList, currSelectedEntryKey);

                if (searchIndex >= 0) {
                    // exact search found
                    entryKey = resultList.get(searchIndex);
                } else {

                    searchIndex = (searchIndex * -1) - 1;

                    if (searchIndex == resultListSize) {

                        if (wrap) {
                            searchIndex = 0;
                        } else {
                            searchIndex = resultListSize - 1;
                        }
                    }

                    entryKey = resultList.get(searchIndex);
                }

                navigationIndex = resultList.indexOf(entryKey) + 1;

            } else {
                // PREVIOUS
                if (currSelectedRowIndex >= 0) {

                    if (currSelectedRowIndex > 0) {
                        currSelectedRowIndex--;
                    } else {
                        if (wrap) {
                            currSelectedRowIndex = entryIndexListSize - 1;
                        }
                    }
                } else {
                    currSelectedRowIndex = 0;
                }

                HotfixEntryKey currSelectedEntryKey = entryKeyList.get(currSelectedRowIndex);

                int searchIndex = Collections.binarySearch(resultList, currSelectedEntryKey);

                if (searchIndex >= 0) {
                    // exact search found
                    entryKey = resultList.get(searchIndex);
                } else {

                    searchIndex = (searchIndex * -1) - 1;

                    if (searchIndex == 0) {

                        if (wrap) {
                            searchIndex = resultListSize - 1;
                        } else {
                            searchIndex = 0;
                        }
                    } else {
                        searchIndex--;
                    }

                    entryKey = resultList.get(searchIndex);
                }

                navigationIndex = resultList.indexOf(entryKey) + 1;
            }

            if (entryKey != null) {

                navigationRowIndex = entryKeyList.indexOf(entryKey);

            } else {
                navigationRowIndex = currSelectedRowIndex;
            }

        }

        FilterTableModelNavigation<HotfixEntryKey> ttmn = new FilterTableModelNavigation<>();
        ttmn.setNavigationIndex(navigationIndex);
        ttmn.setNavigationRowIndex(navigationRowIndex);

        return ttmn;
    }

    @Override
    public List<HotfixEntryKey> getFtmEntryKeyList() {

        if (ftmEntryKeyList == null) {
            ftmEntryKeyList = new ArrayList<>();
        }

        return ftmEntryKeyList;
    }

    @Override
    protected HashMap<HotfixEntryKey, Integer> getKeyIndexMap() {

        if (keyIndexMap == null) {
            keyIndexMap = new HashMap<>();
        }

        return keyIndexMap;
    }

    @Override
    public void resetModel() {

        Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<HotfixEntryKey>>> columnFilterMap;
        columnFilterMap = getColumnFilterMap();
        columnFilterMap.clear();

        List<HotfixColumn> hotfixColumnList = getHotfixColumnList();

        if (hotfixColumnList != null) {

            List<HotfixColumn> visibleColumnList = getVisibleColumnList();

            for (int columnIndex = 0; columnIndex < visibleColumnList.size(); columnIndex++) {

                HotfixColumn hotfixColumn = visibleColumnList.get(columnIndex);

                // preventing unnecessary buildup of filter map
                if (hotfixColumn.isFilterable()) {
                    FilterColumn filterColumn = new FilterColumn(columnIndex);
                    // deferring the setColumnFilterEnabled to updateColumnFilterMap if >1 data is available
                    // filterColumn.setColumnFilterEnabled(true);
                    columnFilterMap.put(filterColumn, null);
                }
            }
        }

        clearSearchResults(true);

        fireTableDataChanged();
    }

    @Override
    public HotfixEntry getEventForKey(HotfixEntryKey key) {

        HotfixEntry hotfixEntry = null;

        TreeMap<HotfixEntryKey, HotfixEntry> hotfixEntryMap = getHotfixEntryMap();

        if (key != null) {
            hotfixEntry = hotfixEntryMap.get(key);
        }

        return hotfixEntry;
    }

    @Override
    public AbstractTreeTableNode getTreeNodeForKey(HotfixEntryKey key) {
        return null;
    }

    @Override
    public void clearSearchResults(boolean clearResults) {

        SearchModel<HotfixEntryKey> searchModel = getSearchModel();

        searchModel.resetResults(clearResults);

        clearEntrySearchResults();

    }

    private void clearEntrySearchResults() {

        TreeMap<HotfixEntryKey, HotfixEntry> hotfixEntryMap = getHotfixEntryMap();

        if (hotfixEntryMap != null) {
            for (Map.Entry<HotfixEntryKey, HotfixEntry> entry : hotfixEntryMap.entrySet()) {
                HotfixEntry hotfixEntry = entry.getValue();
                hotfixEntry.clearSearch();
            }
        }
    }

    @Override
    public SearchModel<HotfixEntryKey> getSearchModel() {

        if (searchModel == null) {

            SearchData<HotfixEntryKey> searchData = getSearchData();

            searchModel = new SearchModel<HotfixEntryKey>(searchData) {

                @Override
                public void searchInEvents(Object searchStrObj, ModalProgressMonitor modalProgressMonitor) {

                    if ((searchStrObj != null) && (!"".equals(searchStrObj.toString()))) {

                        HotfixSearchTask hotfixScanSearchTask = new HotfixSearchTask(modalProgressMonitor,
                                HotfixTableModel.this, searchStrObj) {

                            /*
                             * (non-Javadoc)
                             * 
                             * @see javax.swing.SwingWorker#done()
                             */
                            @Override
                            protected void done() {

                                try {
                                    List<HotfixEntryKey> searchResultList = get();

                                    if (searchResultList != null) {
                                        setSearchResultList(searchStrObj, searchResultList);
                                    }

                                } catch (CancellationException ce) {
                                    LOG.error("HotfixSearchTask cancelled: ", ce);
                                } catch (ExecutionException ee) {
                                    LOG.error("ExecutionException in HotfixSearchTask.", ee);
                                } catch (Exception e) {
                                    LOG.error("Exception in HotfixSearchTask.", e);
                                } finally {

                                    fireTableDataChanged();

                                    modalProgressMonitor.close();
                                }
                            }
                        };

                        hotfixScanSearchTask.execute();

                    }
                }

                @Override
                public void resetResults(boolean clearResults) {
                    // clears search result on search model and reset the search
                    // panel
                    resetSearchResults(clearResults);

                    // clear search results from within entries
                    clearEntrySearchResults();

                    fireTableDataChanged();
                }
            };
        }
        return searchModel;
    }

    public List<HotfixColumn> getVisibleColumnList() {
        return visibleColumnList;
    }

    public List<HotfixColumn> getHotfixColumnList() {
        return hotfixColumnList;
    }

    /*
     * overridden in HotfixScanTableModel
     */
    protected TreeMap<HotfixEntryKey, HotfixEntry> getHotfixEntryMap() {
        return hotfixEntryMap;
    }

    private Map<String, HotfixEntryKey> getHotfixIdEntryKeyMap() {

        if (hotfixIdEntryKeyMap == null) {
            hotfixIdEntryKeyMap = new HashMap<>();
        }
        return hotfixIdEntryKeyMap;
    }

    public SearchData<HotfixEntryKey> getSearchData() {
        return searchData;
    }

    public HotfixColumn getColumn(int columnIndex) {

        HotfixColumn hotfixColumn = getVisibleColumnList().get(columnIndex);

        return hotfixColumn;
    }

    protected void updateColumnFilterMap() {

        TreeMap<HotfixEntryKey, HotfixEntry> hotfixEntryMap = getHotfixEntryMap();

        List<HotfixEntryKey> ftmEntryKeyList = getFtmEntryKeyList();
        ftmEntryKeyList.clear();

        Map<String, HotfixEntryKey> hotfixIdEntryKeyMap = getHotfixIdEntryKeyMap();

        Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<HotfixEntryKey>>> columnFilterMap = getColumnFilterMap();

        for (HotfixEntryKey hotfixEntryKey : hotfixEntryMap.keySet()) {

            ftmEntryKeyList.add(hotfixEntryKey);

            HotfixEntry hotfixEntry = hotfixEntryMap.get(hotfixEntryKey);

            String hotfixId = hotfixEntry.getHotfixId();

            hotfixIdEntryKeyMap.put(hotfixId, hotfixEntryKey);

            Iterator<FilterColumn> fcIterator = columnFilterMap.keySet().iterator();

            while (fcIterator.hasNext()) {

                FilterColumn filterColumn = fcIterator.next();

                int columnIndex = filterColumn.getIndex();

                HotfixColumn hotfixColumn = getColumn(columnIndex);

                boolean filterable = hotfixColumn.isFilterable();

                if (filterable) {

                    List<CheckBoxMenuItemPopupEntry<HotfixEntryKey>> columnFilterEntryList;
                    columnFilterEntryList = columnFilterMap.get(filterColumn);

                    if (columnFilterEntryList == null) {
                        columnFilterEntryList = new ArrayList<CheckBoxMenuItemPopupEntry<HotfixEntryKey>>();
                        columnFilterMap.put(filterColumn, columnFilterEntryList);
                    }

                    String value = getColumnValue(hotfixEntry, columnIndex);

                    String columnValueStr = (value != null) ? value.toString() : null;

                    if (columnValueStr == null) {
                        columnValueStr = FilterTableModel.NULL_STR;
                    } else if ("".equals(columnValueStr)) {
                        columnValueStr = FilterTableModel.EMPTY_STR;
                    }

                    CheckBoxMenuItemPopupEntry<HotfixEntryKey> columnFilterEntry;

                    CheckBoxMenuItemPopupEntry<HotfixEntryKey> searchKey;
                    searchKey = new CheckBoxMenuItemPopupEntry<HotfixEntryKey>(columnValueStr);

                    int index = columnFilterEntryList.indexOf(searchKey);

                    if (index == -1) {
                        columnFilterEntry = new CheckBoxMenuItemPopupEntry<HotfixEntryKey>(columnValueStr);
                        columnFilterEntryList.add(columnFilterEntry);
                    } else {
                        columnFilterEntry = columnFilterEntryList.get(index);
                    }

                    columnFilterEntry.addRowIndex(hotfixEntryKey);

                    if (columnFilterEntryList.size() > 1) {
                        filterColumn.setColumnFilterEnabled(true);
                    }
                }
            }
        }

        Collections.sort(ftmEntryKeyList);

        updateKeyIndexMap();
    }

    public void applyFilter(String filterText, boolean caseSensitiveFilter) {

        List<HotfixEntryKey> ftmEntryKeyList = getFtmEntryKeyList();
        ftmEntryKeyList.clear();

        TreeMap<HotfixEntryKey, HotfixEntry> hotfixEntryMap = getHotfixEntryMap();

        boolean saveResults = false;

        for (Map.Entry<HotfixEntryKey, HotfixEntry> hotfixEntryEntrySet : hotfixEntryMap.entrySet()) {

            HotfixEntryKey hotfixEntryKey = hotfixEntryEntrySet.getKey();

            HotfixEntry hotfixEntry = hotfixEntryEntrySet.getValue();

            boolean search = hotfixEntry.search(filterText, caseSensitiveFilter, saveResults);

            if (search) {
                ftmEntryKeyList.add(hotfixEntryKey);
            }
        }

        Collections.sort(ftmEntryKeyList);

        fireTableDataChanged();
    }

    public String getSelectedRowsData(int[] selectedRows) {

        StringBuilder selectedRowsDataSB = new StringBuilder();

        List<HotfixColumn> hotfixColumnList = getHotfixColumnList();

        List<String> hotfixColumnNameList = HotfixColumn.getColumnNameList(hotfixColumnList);

        String hotfixColumnNameListCSV = GeneralUtilities.getCollectionAsSeperatedValues(hotfixColumnNameList, null,
                true);

        selectedRowsDataSB.append(hotfixColumnNameListCSV);
        selectedRowsDataSB.append(System.getProperty("line.separator"));

        for (int selectedRow : selectedRows) {

            HotfixEntryKey hotfixEntryKey;
            hotfixEntryKey = getFtmEntryKeyList().get(selectedRow);

            HotfixEntry hotfixEntry = getEventForKey(hotfixEntryKey);

            List<HotfixRecordEntry> hotfixRecordEntryList;
            hotfixRecordEntryList = hotfixEntry.getHotfixRecordEntryList();

            for (HotfixRecordEntry hotfixRecordEntry : hotfixRecordEntryList) {

                List<String> recordDataList = hotfixRecordEntry.getRecordDataList();

                String recordDataListCSV = GeneralUtilities.getCollectionAsSeperatedValues(recordDataList, null, true);

                selectedRowsDataSB.append(recordDataListCSV);
                selectedRowsDataSB.append(System.getProperty("line.separator"));
            }

            selectedRowsDataSB.append(System.getProperty("line.separator"));
        }

        return selectedRowsDataSB.toString();
    }

    /**
     * Get selected column data.
     * 
     * @param hotfixEntryKeyList - null for entire list
     * @param hotfixColumns      - null for all columns
     * @return a string of column data for hotfix entries
     */
    public String getSelectedColumnsData(List<HotfixEntryKey> hotfixEntryKeyList, List<HotfixColumn> hotfixColumns) {

        StringBuilder selectedColumnsDataSB = new StringBuilder();

        if (hotfixEntryKeyList == null) {
            hotfixEntryKeyList = getFtmEntryKeyList();
        }

        List<HotfixColumn> hotfixColumnList = null;

        if ((hotfixColumns != null) && (hotfixColumns.size() > 0)) {
            hotfixColumnList = hotfixColumns;
        } else {
            hotfixColumnList = getHotfixColumnList();
        }

        // dont add header when there is just one column
        if (hotfixColumnList.size() > 1) {
            List<String> hotfixColumnNameList = HotfixColumn.getColumnNameList(hotfixColumnList);

            String hotfixColumnNameListCSV = GeneralUtilities.getCollectionAsSeperatedValues(hotfixColumnNameList, "\t",
                    true);

            selectedColumnsDataSB.append(hotfixColumnNameListCSV);
            selectedColumnsDataSB.append(System.getProperty("line.separator"));
        }

        for (HotfixEntryKey hotfixEntryKey : hotfixEntryKeyList) {

            HotfixEntry hotfixEntry = getEventForKey(hotfixEntryKey);

            if ((hotfixColumns != null) && (hotfixColumns.size() > 0)) {

                StringBuilder recordDataListCsvSB = new StringBuilder();
                boolean first = true;

                for (HotfixColumn hotfixColumn : hotfixColumns) {

                    if (!first) {
                        recordDataListCsvSB.append("\t");
                    }

                    int columnIndex = getHotfixColumnList().indexOf(hotfixColumn);

                    String text = hotfixEntry.getHotfixEntryData(hotfixColumn, columnIndex);

                    recordDataListCsvSB.append(text);

                    first = false;

                }

                selectedColumnsDataSB.append(recordDataListCsvSB);

            } else {

                List<HotfixRecordEntry> hotfixRecordEntryList;
                hotfixRecordEntryList = hotfixEntry.getHotfixRecordEntryList();

                HotfixRecordEntry hotfixRecordEntry = hotfixRecordEntryList.get(0);

                List<String> recordDataList = hotfixRecordEntry.getRecordDataList();

                selectedColumnsDataSB
                        .append(GeneralUtilities.getCollectionAsSeperatedValues(recordDataList, "\t", true));

            }

            selectedColumnsDataSB.append(System.getProperty("line.separator"));

        }

        return selectedColumnsDataSB.toString();
    }
}
