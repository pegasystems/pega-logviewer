/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.table.DefaultTableCellRenderer;
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
import com.pega.gcs.logviewer.systemscan.model.ScanResult;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixChangeEntry;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntry;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntryKey;

public class SystemScanTableModel extends FilterTableModel<ScanResultHotfixEntryKey> {

	private static final long serialVersionUID = 4368404105488278206L;

	private static final String SYSTEM_SCAN_FILE_NAME_REGEX_V7 = "ScanResults_(.*?)\\.zip";

	private static final String SYSTEM_SCAN_FILE_NAME_REGEX_V6 = "INVENTORY(.*?)";

	private static final Log4j2Helper LOG = new Log4j2Helper(SystemScanTableModel.class);

	private List<SystemScanColumn> systemScanColumnList;

	private List<ScanResultHotfixEntryKey> ftmEntryKeyList;

	private ScanResult scanResult;

	// search
	private SearchData<ScanResultHotfixEntryKey> searchData;
	private SearchModel<ScanResultHotfixEntryKey> searchModel;

	public SystemScanTableModel(RecentFile recentFile, SearchData<ScanResultHotfixEntryKey> searchData) {

		super(recentFile);

		this.searchData = searchData;

		systemScanColumnList = SystemScanColumn.getSystemScanColumnList();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return systemScanColumnList.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		ScanResultHotfixEntryKey scanResultHotfixEntryKey = getFtmEntryKeyList().get(rowIndex);

		ScanResultHotfixEntry scanResultHotfixEntry = getEventForKey(scanResultHotfixEntryKey);

		return scanResultHotfixEntry;
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

		for (SystemScanColumn systemScanColumn : systemScanColumnList) {

			DefaultTableCellRenderer dtcr = new SystemScanTableCellRenderer();

			int horizontalAlignment = systemScanColumn.getHorizontalAlignment();

			// if (horizontalAlignment == SwingConstants.LEFT) {
			// dtcr = getDefaultLeftAlignTableCellRenderer();
			// } else {
			// dtcr = getDefaultTableCellRenderer();
			// }

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
	protected boolean search(ScanResultHotfixEntryKey key, Object searchStrObj) {

		ScanResultHotfixEntry scanResultHotfixEntry = getEventForKey(key);

		boolean found = scanResultHotfixEntry.search(searchStrObj.toString());

		return found;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getNavigationRowIndex
	 * (java.util.List, int, boolean, boolean, boolean, boolean)
	 */
	@Override
	protected FilterTableModelNavigation<ScanResultHotfixEntryKey> getNavigationRowIndex(
			List<ScanResultHotfixEntryKey> resultList, int selectedRowIndex, boolean forward, boolean first,
			boolean last, boolean wrap) {

		int currSelectedRowIndex = selectedRowIndex;

		int navigationIndex = 0;
		int navigationRowIndex = 0;

		if ((resultList != null) && (resultList.size() > 0)) {

			int resultListSize = resultList.size();

			List<ScanResultHotfixEntryKey> entryKeyList = getFtmEntryKeyList();

			int logEntryIndexListSize = entryKeyList.size();

			ScanResultHotfixEntryKey entryKey = null;

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

					if (currSelectedRowIndex < (logEntryIndexListSize - 1)) {
						currSelectedRowIndex++;
					} else {
						if (wrap) {
							currSelectedRowIndex = 0;
						}
					}
				} else {
					currSelectedRowIndex = 0;
				}

				ScanResultHotfixEntryKey currSelectedEntryKey = entryKeyList.get(currSelectedRowIndex);

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
							currSelectedRowIndex = logEntryIndexListSize - 1;
						}
					}
				} else {
					currSelectedRowIndex = 0;
				}

				ScanResultHotfixEntryKey currSelectedEntryKey = entryKeyList.get(currSelectedRowIndex);

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

		FilterTableModelNavigation<ScanResultHotfixEntryKey> ttmn = new FilterTableModelNavigation<ScanResultHotfixEntryKey>();
		ttmn.setNavigationIndex(navigationIndex);
		ttmn.setNavigationRowIndex(navigationRowIndex);

		return ttmn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getFtmEntryKeyList()
	 */
	@Override
	public List<ScanResultHotfixEntryKey> getFtmEntryKeyList() {

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

		List<ScanResultHotfixEntryKey> ftmEntryKeyList = getFtmEntryKeyList();
		ftmEntryKeyList.clear();

		Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<ScanResultHotfixEntryKey>>> columnFilterMap;
		columnFilterMap = getColumnFilterMap();
		columnFilterMap.clear();

		for (int columnIndex = 0; columnIndex < systemScanColumnList.size(); columnIndex++) {

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
	public int getIndexOfKey(ScanResultHotfixEntryKey key) {
		List<ScanResultHotfixEntryKey> ftmEntryKeyList = getFtmEntryKeyList();

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
	public ScanResultHotfixEntry getEventForKey(ScanResultHotfixEntryKey scanResultHotfixEntryKey) {

		ScanResultHotfixEntry scanResultHotfixEntry = null;

		if (scanResultHotfixEntryKey != null) {
			scanResultHotfixEntry = scanResult.getScanResultHotfixEntry(scanResultHotfixEntryKey);
		}

		return scanResultHotfixEntry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getTreeNodeForKey(
	 * java.lang.Comparable)
	 */
	@Override
	public AbstractTreeTableNode getTreeNodeForKey(ScanResultHotfixEntryKey key) {
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
	public SearchModel<ScanResultHotfixEntryKey> getSearchModel() {

		if (searchModel == null) {

			if (searchData == null) {
				searchData = new SearchData<>(null);
			}

			searchModel = new SearchModel<ScanResultHotfixEntryKey>(searchData) {

				@Override
				public void searchInEvents(Object searchStrObj, ModalProgressMonitor mProgressMonitor) {

					if ((searchStrObj != null) && (!"".equals(searchStrObj.toString()))) {

						SystemScanSearchTask ttst = new SystemScanSearchTask(mProgressMonitor,
								SystemScanTableModel.this, searchStrObj) {

							/*
							 * (non-Javadoc)
							 * 
							 * @see javax.swing.SwingWorker#done()
							 */
							@Override
							protected void done() {

								try {
									List<ScanResultHotfixEntryKey> searchResultList = get();

									if (searchResultList != null) {
										setSearchResultList(searchStrObj, searchResultList);
									}

								} catch (CancellationException ce) {
									LOG.error("SystemScanSearchTask cancelled: ", ce);
								} catch (ExecutionException ee) {
									LOG.error("ExecutionException in SystemScanSearchTask.", ee);
								} catch (Exception e) {
									LOG.error("Exception in SystemScanSearchTask.", e);
								} finally {

									fireTableDataChanged();

									mProgressMonitor.close();
								}
							}
						};

						ttst.execute();

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

	public ScanResult getScanResult() {
		return scanResult;
	}

	public SystemScanColumn getColumn(int columnIndex) {
		SystemScanColumn systemScanColumn = systemScanColumnList.get(columnIndex);

		return systemScanColumn;
	}

	public void setScanResult(ScanResult scanResult) {

		this.scanResult = scanResult;

		resetModel();

		List<ScanResultHotfixEntryKey> ftmEntryKeyList = getFtmEntryKeyList();

		Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<ScanResultHotfixEntryKey>>> columnFilterMap = getColumnFilterMap();

		Set<ScanResultHotfixEntryKey> scanResultHotfixEntryKeySet = scanResult.getScanResultHotfixEntryKeySet();

		for (ScanResultHotfixEntryKey scanResultHotfixEntryKey : scanResultHotfixEntryKeySet) {

			ftmEntryKeyList.add(scanResultHotfixEntryKey);

			ScanResultHotfixEntry scanResultHotfixEntry = scanResult.getScanResultHotfixEntry(scanResultHotfixEntryKey);

			Iterator<FilterColumn> fcIterator = columnFilterMap.keySet().iterator();

			while (fcIterator.hasNext()) {

				FilterColumn filterColumn = fcIterator.next();

				List<CheckBoxMenuItemPopupEntry<ScanResultHotfixEntryKey>> columnFilterEntryList;
				columnFilterEntryList = columnFilterMap.get(filterColumn);

				if (columnFilterEntryList == null) {
					columnFilterEntryList = new ArrayList<CheckBoxMenuItemPopupEntry<ScanResultHotfixEntryKey>>();
					columnFilterMap.put(filterColumn, columnFilterEntryList);
				}

				int columnIndex = filterColumn.getIndex();
				SystemScanColumn systemScanColumn = systemScanColumnList.get(columnIndex);

				String value = scanResult.getScanResultHotfixEntryData(scanResultHotfixEntry, systemScanColumn);

				String columnValueStr = (value != null) ? value.toString() : null;

				if (columnValueStr == null) {
					columnValueStr = FilterTableModel.NULL_STR;
				} else if ("".equals(columnValueStr)) {
					columnValueStr = FilterTableModel.EMPTY_STR;
				}

				CheckBoxMenuItemPopupEntry<ScanResultHotfixEntryKey> columnFilterEntry;

				CheckBoxMenuItemPopupEntry<ScanResultHotfixEntryKey> searchKey;
				searchKey = new CheckBoxMenuItemPopupEntry<ScanResultHotfixEntryKey>(columnValueStr);

				int index = columnFilterEntryList.indexOf(searchKey);

				if (index == -1) {
					columnFilterEntry = new CheckBoxMenuItemPopupEntry<ScanResultHotfixEntryKey>(columnValueStr);
					columnFilterEntryList.add(columnFilterEntry);
				} else {
					columnFilterEntry = columnFilterEntryList.get(index);
				}

				columnFilterEntry.addRowIndex(scanResultHotfixEntryKey);

				boolean filterable = systemScanColumn.isFilterable();

				if ((filterable) && (columnFilterEntryList.size() > 1)) {
					filterColumn.setColumnFilterEnabled(true);
				}
			}
		}

		Collections.sort(ftmEntryKeyList);

		fireTableStructureChanged();
	}

	public SearchData<ScanResultHotfixEntryKey> getSearchData() {
		return searchData;
	}

	public String getScanResultHotfixEntryValue(ScanResultHotfixEntry scanResultHotfixEntry, int column) {

		ScanResultHotfixEntryKey scanResultHotfixEntryKey = scanResultHotfixEntry.getKey();

		String text = null;

		if (column > 0) {

			try {

				List<ScanResultHotfixChangeEntry> scanResultHotfixChangeEntryList;
				scanResultHotfixChangeEntryList = scanResultHotfixEntry.getScanResultHotfixChangeEntryList();

				ScanResultHotfixChangeEntry scanResultHotfixChangeEntry = scanResultHotfixChangeEntryList.get(0);

				List<String> recordDataList = scanResultHotfixChangeEntry.getRecordDataList();

				text = recordDataList.get(column - 1);

			} catch (Exception e) {
				LOG.error("Error in getting Hotfix entry value for index: " + scanResultHotfixEntryKey, e);
			}

		}

		return text;
	}

	protected void clearEntrySearchResults() {

		List<ScanResultHotfixEntryKey> filteredList = getFtmEntryKeyList();

		if (filteredList != null) {

			Iterator<ScanResultHotfixEntryKey> fListIterator = filteredList.iterator();

			while (fListIterator.hasNext()) {

				ScanResultHotfixEntryKey key = fListIterator.next();

				ScanResultHotfixEntry scanResultHotfixEntry = getEventForKey(key);
				scanResultHotfixEntry.setSearchFound(false);
			}
		}
	}

	public String getSelectedRowsData(int[] selectedRows) {

		StringBuffer selectedRowsDataSB = new StringBuffer();

		ScanResult scanResult = getScanResult();

		List<String> hotfixColumnList = scanResult.getHotfixColumnList();

		String hotfixColumnListCSV = GeneralUtilities.getListAsSeperatedValues(hotfixColumnList, null);

		selectedRowsDataSB.append(hotfixColumnListCSV);
		selectedRowsDataSB.append(System.getProperty("line.separator"));

		for (int selectedRow : selectedRows) {

			ScanResultHotfixEntryKey scanResultHotfixEntryKey;
			scanResultHotfixEntryKey = getFtmEntryKeyList().get(selectedRow);

			ScanResultHotfixEntry scanResultHotfixEntry;
			scanResultHotfixEntry = scanResult.getScanResultHotfixEntry(scanResultHotfixEntryKey);

			List<ScanResultHotfixChangeEntry> scanResultHotfixChangeEntryList;
			scanResultHotfixChangeEntryList = scanResultHotfixEntry.getScanResultHotfixChangeEntryList();

			for (ScanResultHotfixChangeEntry scanResultHotfixChangeEntry : scanResultHotfixChangeEntryList) {

				List<String> recordDataList = scanResultHotfixChangeEntry.getRecordDataList();

				String recordDataListCSV = GeneralUtilities.getListAsSeperatedValues(recordDataList, null);

				selectedRowsDataSB.append(recordDataListCSV);
				selectedRowsDataSB.append(System.getProperty("line.separator"));
			}

			selectedRowsDataSB.append(System.getProperty("line.separator"));
		}

		return selectedRowsDataSB.toString();
	}

	public String getSelectedColumnsData(List<ScanResultHotfixEntryKey> scanResultHotfixEntryKeyList,
			List<SystemScanColumn> systemScanColumns) {

		StringBuffer selectedColumnsDataSB = new StringBuffer();

		ScanResult scanResult = getScanResult();

		if (scanResultHotfixEntryKeyList == null) {
			scanResultHotfixEntryKeyList = getFtmEntryKeyList();
		}

		List<String> scanResultHotfixInfoColumnList = new ArrayList<>();

		if ((systemScanColumns != null) && (systemScanColumns.size() > 0)) {

			for (SystemScanColumn systemScanColumn : systemScanColumns) {

				int index = scanResult.getHotfixColumnIndex(systemScanColumn);

				String column = scanResult.getHotfixColumnList().get(index);

				scanResultHotfixInfoColumnList.add(column);

			}
		} else {
			scanResultHotfixInfoColumnList = scanResult.getHotfixColumnList();
		}

		String scanResultHotfixInfoColumnListCSV = GeneralUtilities
				.getListAsSeperatedValues(scanResultHotfixInfoColumnList, "\t");

		selectedColumnsDataSB.append(scanResultHotfixInfoColumnListCSV);
		selectedColumnsDataSB.append(System.getProperty("line.separator"));

		for (ScanResultHotfixEntryKey scanResultHotfixEntryKey : scanResultHotfixEntryKeyList) {

			ScanResultHotfixEntry scanResultHotfixEntry;
			scanResultHotfixEntry = scanResult.getScanResultHotfixEntry(scanResultHotfixEntryKey);

			if ((systemScanColumns != null) && (systemScanColumns.size() > 0)) {

				StringBuffer recordDataListCSVSB = new StringBuffer();
				boolean first = true;

				for (SystemScanColumn systemScanColumn : systemScanColumns) {

					if (!first) {
						recordDataListCSVSB.append("\t");
					}

					String text = getScanResult().getScanResultHotfixEntryData(scanResultHotfixEntry, systemScanColumn);

					recordDataListCSVSB.append(text);

					first = false;

				}

				selectedColumnsDataSB.append(recordDataListCSVSB);

			} else {

				List<ScanResultHotfixChangeEntry> scanResultHotfixChangeEntryList;
				scanResultHotfixChangeEntryList = scanResultHotfixEntry.getScanResultHotfixChangeEntryList();

				ScanResultHotfixChangeEntry scanResultHotfixChangeEntry = scanResultHotfixChangeEntryList.get(0);

				List<String> recordDataList = scanResultHotfixChangeEntry.getRecordDataList();

				selectedColumnsDataSB.append(GeneralUtilities.getListAsSeperatedValues(recordDataList, "\t"));

			}

			selectedColumnsDataSB.append(System.getProperty("line.separator"));

		}

		return selectedColumnsDataSB.toString();
	}

	public InventoryVersion getInventoryVersion() {

		InventoryVersion inventoryVersion = InventoryVersion.INVENTORY_VERSION_7;

		String inventoryFilePath = getFilePath();
		File inventoryFile = new File(inventoryFilePath);

		Pattern v7Pattern = Pattern.compile(SYSTEM_SCAN_FILE_NAME_REGEX_V7);
		Matcher v7PatternMatcher = v7Pattern.matcher(inventoryFile.getName());
		boolean v7 = v7PatternMatcher.find();

		if (v7) {
			inventoryVersion = InventoryVersion.INVENTORY_VERSION_7;
		} else {

			Pattern v6Pattern = Pattern.compile(SYSTEM_SCAN_FILE_NAME_REGEX_V6);
			Matcher v6PatternMatcher = v6Pattern.matcher(inventoryFile.getName());
			boolean v6 = v6PatternMatcher.find();

			if (v6) {
				inventoryVersion = InventoryVersion.INVENTORY_VERSION_6;
			}
		}

		return inventoryVersion;
	}
}
