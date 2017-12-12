/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import com.pega.gcs.logviewer.systemscan.model.ScanResult;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixChangeEntry;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntry;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntryKey;

public class ScanResultHotfixTableModel extends FilterTableModel<Integer> {

	private static final long serialVersionUID = -7040463283091877211L;

	private ScanResultHotfixEntryKey scanResultHotfixEntryKey;

	private ScanResult scanResult;

	private List<SystemScanColumn> hotfixInfoColumnList;

	private List<Integer> ftmEntryKeyList;

	private TreeMap<Integer, ScanResultHotfixChangeEntry> scanResultHotfixChangeEntryMap;

	public ScanResultHotfixTableModel(ScanResultHotfixEntryKey scanResultHotfixEntryKey, ScanResult scanResult) {

		super(null);

		this.scanResultHotfixEntryKey = scanResultHotfixEntryKey;

		this.scanResult = scanResult;

		hotfixInfoColumnList = SystemScanColumn.getHotfixInfoColumnList();

		resetModel();

		List<Integer> ftmEntryKeyList = getFtmEntryKeyList();

		Map<Integer, ScanResultHotfixChangeEntry> scanResultHotfixChangeEntryMap = getScanResultHotfixChangeEntryMap();

		Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<Integer>>> columnFilterMap = getColumnFilterMap();

		ScanResultHotfixEntry scanResultHotfixEntry = scanResult.getScanResultHotfixEntry(scanResultHotfixEntryKey);

		for (ScanResultHotfixChangeEntry scanResultHotfixChangeEntry : scanResultHotfixEntry
				.getScanResultHotfixChangeEntryList()) {

			Integer entryId = scanResultHotfixChangeEntry.getEntryId();

			ftmEntryKeyList.add(entryId);

			scanResultHotfixChangeEntryMap.put(entryId, scanResultHotfixChangeEntry);

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
				SystemScanColumn systemScanColumn = hotfixInfoColumnList.get(columnIndex);

				String value = scanResult.getScanResultHotfixChangeEntryData(scanResultHotfixChangeEntry,
						systemScanColumn);

				String columnValueStr = (value != null) ? value.toString() : null;

				if (columnValueStr == null) {
					columnValueStr = FilterTableModel.NULL_STR;
				} else if ("".equals(columnValueStr)) {
					columnValueStr = FilterTableModel.EMPTY_STR;
				}

				CheckBoxMenuItemPopupEntry<Integer> columnFilterEntry;

				CheckBoxMenuItemPopupEntry<Integer> searchKey;
				searchKey = new CheckBoxMenuItemPopupEntry<Integer>(columnValueStr);

				int index = columnFilterEntryList.indexOf(searchKey);

				if (index == -1) {
					columnFilterEntry = new CheckBoxMenuItemPopupEntry<Integer>(columnValueStr);
					columnFilterEntryList.add(columnFilterEntry);
				} else {
					columnFilterEntry = columnFilterEntryList.get(index);
				}

				columnFilterEntry.addRowIndex(entryId);

				boolean filterable = systemScanColumn.isFilterable();

				if ((filterable) && (columnFilterEntryList.size() > 1)) {
					filterColumn.setColumnFilterEnabled(true);
				}
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return hotfixInfoColumnList.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		Integer rowKey = getFtmEntryKeyList().get(rowIndex);

		ScanResultHotfixChangeEntry scanResultHotfixChangeEntry = getEventForKey(rowKey);

		return scanResultHotfixChangeEntry;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getTableColumnModel()
	 */
	@Override
	protected TableColumnModel getTableColumnModel() {

		TableColumnModel tableColumnModel = new DefaultTableColumnModel();

		TableColumn tableColumn = null;
		int columnIndex = 0;

		for (SystemScanColumn systemScanColumn : hotfixInfoColumnList) {

			DefaultTableCellRenderer dtcr = new ScanResultHotfixTableCellRenderer();

			int horizontalAlignment = systemScanColumn.getHorizontalAlignment();

			dtcr.setHorizontalAlignment(horizontalAlignment);

			int prefColumnWidth = systemScanColumn.getPrefColumnWidth();

			tableColumn = new TableColumn(columnIndex++);
			tableColumn.setHeaderValue(systemScanColumn.getDisplayName());
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
	 * @see
	 * com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getModelColumnIndex(
	 * int)
	 */
	@Override
	protected int getModelColumnIndex(int column) {
		return column;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pega.gcs.fringecommon.guiutilities.FilterTableModel#search(java.lang.
	 * Comparable, java.lang.Object)
	 */
	@Override
	protected boolean search(Integer key, Object searchStrObj) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getNavigationRowIndex
	 * (java.util.List, int, boolean, boolean, boolean, boolean)
	 */
	@Override
	protected FilterTableModelNavigation<Integer> getNavigationRowIndex(List<Integer> resultList,
			int currSelectedRowIndex, boolean forward, boolean first, boolean last, boolean wrap) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getFtmEntryKeyList()
	 */
	@Override
	public List<Integer> getFtmEntryKeyList() {

		if (ftmEntryKeyList == null) {
			ftmEntryKeyList = new ArrayList<>();
		}

		return ftmEntryKeyList;
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

		Map<Integer, ScanResultHotfixChangeEntry> scanResultHotfixChangeEntryMap = getScanResultHotfixChangeEntryMap();
		scanResultHotfixChangeEntryMap.clear();

		Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<Integer>>> columnFilterMap;
		columnFilterMap = getColumnFilterMap();
		columnFilterMap.clear();

		for (int columnIndex = 0; columnIndex < hotfixInfoColumnList.size(); columnIndex++) {

			FilterColumn filterColumn = new FilterColumn(columnIndex);

			filterColumn.setColumnFilterEnabled(false);

			columnFilterMap.put(filterColumn, null);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getIndexOfKey(java.
	 * lang.Comparable)
	 */
	@Override
	public int getIndexOfKey(Integer key) {
		List<Integer> ftmEntryKeyList = getFtmEntryKeyList();

		int index = -1;

		if (ftmEntryKeyList != null) {
			index = ftmEntryKeyList.indexOf(key);
		}

		return index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getEventForKey(java.
	 * lang.Comparable)
	 */
	@Override
	public ScanResultHotfixChangeEntry getEventForKey(Integer key) {

		ScanResultHotfixChangeEntry scanResultHotfixChangeEntry = null;

		if (key != null) {
			Map<Integer, ScanResultHotfixChangeEntry> scanResultHotfixChangeEntryMap;
			scanResultHotfixChangeEntryMap = getScanResultHotfixChangeEntryMap();
			scanResultHotfixChangeEntry = scanResultHotfixChangeEntryMap.get(key);
		}

		return scanResultHotfixChangeEntry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getTreeNodeForKey(
	 * java.lang.Comparable)
	 */
	@Override
	public AbstractTreeTableNode getTreeNodeForKey(Integer key) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pega.gcs.fringecommon.guiutilities.FilterTableModel#clearSearchResults(
	 * boolean)
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

	private TreeMap<Integer, ScanResultHotfixChangeEntry> getScanResultHotfixChangeEntryMap() {

		if (scanResultHotfixChangeEntryMap == null) {
			scanResultHotfixChangeEntryMap = new TreeMap<>();
		}

		return scanResultHotfixChangeEntryMap;
	}

	public ScanResultHotfixEntryKey getScanResultHotfixEntryKey() {
		return scanResultHotfixEntryKey;
	}

	public ScanResult getScanResult() {
		return scanResult;
	}

	public SystemScanColumn getColumn(int columnIndex) {
		SystemScanColumn systemScanColumn = hotfixInfoColumnList.get(columnIndex);

		return systemScanColumn;
	}
}
