/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.pega.gcs.fringecommon.guiutilities.FilterTableModelNavigation;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.Searchable.SelectedRowPosition;
import com.pega.gcs.fringecommon.guiutilities.TableCompareEntry;
import com.pega.gcs.fringecommon.guiutilities.search.SearchData;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntryKey;

public class SystemScanTableCompareModel extends SystemScanTableModel {

	private static final long serialVersionUID = -4305538471185269517L;

	private int compareNavIndex;

	private ScanResultHotfixEntryKey compareNavKey;

	private boolean compareResultsWrap;

	private Map<ScanResultHotfixEntryKey, List<ScanResultHotfixEntryKey>> compareNavIndexMap;

	private List<ScanResultHotfixEntryKey> compareMarkerList;

	public SystemScanTableCompareModel(RecentFile recentFile, SearchData<ScanResultHotfixEntryKey> searchData) {
		super(recentFile, searchData);
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

	public Map<ScanResultHotfixEntryKey, List<ScanResultHotfixEntryKey>> getCompareNavIndexMap() {
		return compareNavIndexMap;
	}

	public void setCompareNavIndexMap(
			TreeMap<ScanResultHotfixEntryKey, List<ScanResultHotfixEntryKey>> compareNavIndexMap) {
		this.compareNavIndexMap = compareNavIndexMap;
	}

	public List<ScanResultHotfixEntryKey> getCompareMarkerList() {
		return compareMarkerList;
	}

	public void setCompareMarkerList(List<ScanResultHotfixEntryKey> compareMarkerList) {
		this.compareMarkerList = compareMarkerList;
	}

	public int getCompareCount() {
		int compareCount = 0;

		compareCount = (compareNavIndexMap != null) ? compareNavIndexMap.size() : 0;

		return compareCount;
	}

	private int getCompareRowIndex(int currSelectedRowIndex, boolean forward, boolean first, boolean last) {

		List<ScanResultHotfixEntryKey> compareNavIndexList = new LinkedList<ScanResultHotfixEntryKey>(
				compareNavIndexMap.keySet());

		FilterTableModelNavigation<ScanResultHotfixEntryKey> ttmn = getNavigationRowIndex(compareNavIndexList,
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

			List<ScanResultHotfixEntryKey> compareNavIndexList = compareNavIndexMap.get(compareNavKey);

			int size = compareNavIndexList.size();

			ScanResultHotfixEntryKey endKey = compareNavIndexList.get(size - 1);

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

			List<ScanResultHotfixEntryKey> compareNavIndexList = compareNavIndexMap.get(compareNavKey);

			int size = compareNavIndexList.size();

			ScanResultHotfixEntryKey endKey = compareNavIndexList.get(size - 1);

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

			List<ScanResultHotfixEntryKey> compareNavIndexList = compareNavIndexMap.get(compareNavKey);

			int size = compareNavIndexList.size();

			ScanResultHotfixEntryKey endKey = compareNavIndexList.get(size - 1);

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

			List<ScanResultHotfixEntryKey> compareNavIndexList = compareNavIndexMap.get(compareNavKey);

			int size = compareNavIndexList.size();

			ScanResultHotfixEntryKey endKey = compareNavIndexList.get(size - 1);

			endEntry = getIndexOfKey(endKey);
		}

		tableCompareEntry = new TableCompareEntry(startEntry, endEntry);

		return tableCompareEntry;
	}

	public SelectedRowPosition getCompareSelectedRowPosition(int selectedRow) {

		SelectedRowPosition selectedRowPosition = SelectedRowPosition.NONE;

		List<ScanResultHotfixEntryKey> compareNavIndexList = new LinkedList<ScanResultHotfixEntryKey>(
				compareNavIndexMap.keySet());

		if ((compareNavIndexList != null) && (compareNavIndexList.size() > 0)) {

			List<ScanResultHotfixEntryKey> ftmEntryKeyList = getFtmEntryKeyList();

			ScanResultHotfixEntryKey scanResultHotfixEntryKey = ftmEntryKeyList.get(selectedRow);

			int selectedRowIndex = scanResultHotfixEntryKey.getId();

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
