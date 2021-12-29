/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.hotfixscan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.pega.gcs.fringecommon.guiutilities.FilterTableModelNavigation;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.Searchable.SelectedRowPosition;
import com.pega.gcs.fringecommon.guiutilities.TableCompareEntry;
import com.pega.gcs.fringecommon.guiutilities.search.SearchData;
import com.pega.gcs.logviewer.catalog.model.HotfixColumn;
import com.pega.gcs.logviewer.catalog.model.HotfixEntryKey;

public class HotfixScanTableCompareModel extends HotfixScanTableModel {

    private static final long serialVersionUID = -4305538471185269517L;

    private int compareNavIndex;

    private HotfixEntryKey compareNavKey;

    private boolean compareResultsWrap;

    private Map<CompareHotfixEntryKey, List<CompareHotfixEntryKey>> compareNavIndexMap;

    private List<CompareHotfixEntryKey> compareMarkerList;

    public HotfixScanTableCompareModel(RecentFile recentFile, SearchData<HotfixEntryKey> searchData,
            List<HotfixColumn> visibleColumnList) {
        super(recentFile, searchData, visibleColumnList);
    }

    @Override
    public void resetModel() {

        super.resetModel();

        compareNavIndex = 0;
        compareNavKey = null;
        compareResultsWrap = false;
        compareNavIndexMap = new HashMap<>();
        compareMarkerList = null;

    }

    @Override
    public boolean isColumnFilterEnabled(int column) {
        return false;
    }

    public boolean isCompareResultsWrap() {
        return compareResultsWrap;
    }

    public void setCompareResultsWrap(boolean compareResultsWrap) {
        this.compareResultsWrap = compareResultsWrap;
    }

    public int getCompareNavIndex() {
        return compareNavIndex;
    }

    // public Map<CompareHotfixEntryKey, List<CompareHotfixEntryKey>>
    // getCompareNavIndexMap() {
    // return compareNavIndexMap;
    // }

    public void setCompareNavIndexMap(TreeMap<CompareHotfixEntryKey, List<CompareHotfixEntryKey>> compareNavIndexMap) {
        this.compareNavIndexMap = compareNavIndexMap;
    }

    public List<CompareHotfixEntryKey> getCompareMarkerList() {
        return compareMarkerList;
    }

    public void setCompareMarkerList(List<CompareHotfixEntryKey> compareMarkerList) {
        this.compareMarkerList = compareMarkerList;
    }

    public int getCompareCount() {
        int compareCount = 0;

        compareCount = (compareNavIndexMap != null) ? compareNavIndexMap.size() : 0;

        return compareCount;
    }

    private int getCompareRowIndex(int currSelectedRowIndex, boolean forward, boolean first, boolean last) {

        List<HotfixEntryKey> compareNavIndexList = new ArrayList<HotfixEntryKey>(compareNavIndexMap.keySet());

        FilterTableModelNavigation<HotfixEntryKey> ttmn = getNavigationRowIndex(compareNavIndexList,
                currSelectedRowIndex, forward, first, last, compareResultsWrap);

        compareNavIndex = ttmn.getNavigationIndex();
        int compareRowIndex = ttmn.getNavigationRowIndex();
        compareNavKey = ttmn.getNavigationKey();

        return compareRowIndex;
    }

    public TableCompareEntry compareFirst() {

        TableCompareEntry tableCompareEntry;

        int startEntry = getCompareRowIndex(0, false, true, false);
        int endEntry = startEntry;

        if (compareNavKey != null) {

            List<CompareHotfixEntryKey> compareNavIndexList = compareNavIndexMap.get(compareNavKey);

            int size = compareNavIndexList.size();

            HotfixEntryKey endKey = compareNavIndexList.get(size - 1);

            endEntry = getIndexOfKey(endKey);
        }

        tableCompareEntry = new TableCompareEntry(startEntry, endEntry);

        return tableCompareEntry;
    }

    public TableCompareEntry comparePrevious(int currSelectedRow) {

        TableCompareEntry tableCompareEntry;

        int startEntry = getCompareRowIndex(currSelectedRow, false, false, false);
        int endEntry = startEntry;

        if (compareNavKey != null) {

            List<CompareHotfixEntryKey> compareNavIndexList = compareNavIndexMap.get(compareNavKey);

            int size = compareNavIndexList.size();

            HotfixEntryKey endKey = compareNavIndexList.get(size - 1);

            endEntry = getIndexOfKey(endKey);
        }

        tableCompareEntry = new TableCompareEntry(startEntry, endEntry);

        return tableCompareEntry;
    }

    public TableCompareEntry compareNext(int currSelectedRow) {

        TableCompareEntry tableCompareEntry;

        int startEntry = getCompareRowIndex(currSelectedRow, true, false, false);
        int endEntry = startEntry;

        if (compareNavKey != null) {

            List<CompareHotfixEntryKey> compareNavIndexList = compareNavIndexMap.get(compareNavKey);

            int size = compareNavIndexList.size();

            HotfixEntryKey endKey = compareNavIndexList.get(size - 1);

            endEntry = getIndexOfKey(endKey);
        }

        tableCompareEntry = new TableCompareEntry(startEntry, endEntry);

        return tableCompareEntry;
    }

    public TableCompareEntry compareLast() {

        TableCompareEntry tableCompareEntry;

        int startEntry = getCompareRowIndex(0, false, false, true);
        int endEntry = startEntry;

        if (compareNavKey != null) {

            List<CompareHotfixEntryKey> compareNavIndexList = compareNavIndexMap.get(compareNavKey);

            int size = compareNavIndexList.size();

            HotfixEntryKey endKey = compareNavIndexList.get(size - 1);

            endEntry = getIndexOfKey(endKey);
        }

        tableCompareEntry = new TableCompareEntry(startEntry, endEntry);

        return tableCompareEntry;
    }

    public SelectedRowPosition getCompareSelectedRowPosition(int selectedRow) {

        SelectedRowPosition selectedRowPosition = SelectedRowPosition.NONE;

        List<HotfixEntryKey> compareNavIndexList = new ArrayList<HotfixEntryKey>(compareNavIndexMap.keySet());

        if ((compareNavIndexList != null) && (compareNavIndexList.size() > 0)) {

            List<HotfixEntryKey> ftmEntryKeyList = getFtmEntryKeyList();

            HotfixEntryKey hotfixEntryKey = ftmEntryKeyList.get(selectedRow);

            int selectedRowIndex = hotfixEntryKey.getId();

            int size = compareNavIndexList.size();

            int firstIndex = compareNavIndexList.get(0).getId();
            int lastIndex = compareNavIndexList.get(size - 1).getId();

            if ((selectedRowIndex > firstIndex) && (selectedRowIndex < lastIndex)) {
                selectedRowPosition = SelectedRowPosition.BETWEEN;
            } else if (selectedRowIndex == firstIndex) {
                selectedRowPosition = SelectedRowPosition.FIRST;
            } else if (selectedRowIndex == lastIndex) {
                selectedRowPosition = SelectedRowPosition.LAST;
            } else {
                selectedRowPosition = SelectedRowPosition.NONE;
            }
        }

        return selectedRowPosition;
    }
}
