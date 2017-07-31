/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.report.alert;

import java.awt.Component;
import java.text.NumberFormat;
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
import com.pega.gcs.logviewer.model.AlertLogEntry;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;

public abstract class AlertMessageReportModel extends FilterTableModel<AlertMessageReportEntry> {

	private static final long serialVersionUID = -8895320811393447615L;

	private String alertMessageID;

	private long thresholdKPI;

	private String kpiUnit;

	private AlertLogEntryModel alertLogEntryModel;

	// for each alert, collate them based on the reason and list the alert keys.
	private List<AlertMessageReportEntry> alertMessageReportEntryList;

	private Map<String, AlertMessageReportEntry> alertMessageReportEntryMap;

	private AlertBoxAndWhiskerReportColumn keyAlertMessageReportColumn;

	protected abstract List<AlertBoxAndWhiskerReportColumn> getAlertMessageReportColumnList();

	protected abstract String getAlertMessageReportEntryKey(AlertLogEntry alertLogEntry,
			ArrayList<String> logEntryValueList);

	public AlertMessageReportModel(String alertMessageID, long thresholdKPI, String kpiUnit,
			AlertLogEntryModel alertLogEntryModel) {
		super(null);

		this.alertMessageID = alertMessageID;
		this.thresholdKPI = thresholdKPI;
		this.kpiUnit = kpiUnit;
		this.alertLogEntryModel = alertLogEntryModel;
		// resetModel also sets up keyAlertMessageReportColumn
		resetModel();
	}

	@Override
	public int getColumnCount() {
		return getAlertMessageReportColumnList().size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		AlertMessageReportEntry alertMessageReportEntry = getAlertMessageReportEntry(rowIndex);

		Object object = getColumnValue(alertMessageReportEntry, columnIndex);

		return object;
	}

	@Override
	protected int getModelColumnIndex(int column) {
		return column;
	}

	@Override
	protected boolean search(AlertMessageReportEntry key, Object searchStrObj) {
		return false;
	}

	@Override
	protected FilterTableModelNavigation<AlertMessageReportEntry> getNavigationRowIndex(
			List<AlertMessageReportEntry> resultList, int currSelectedRowIndex, boolean forward, boolean first,
			boolean last, boolean wrap) {
		return null;
	}

	@Override
	public List<AlertMessageReportEntry> getFtmEntryKeyList() {

		if (alertMessageReportEntryList == null) {
			alertMessageReportEntryList = new ArrayList<AlertMessageReportEntry>();
		}

		return alertMessageReportEntryList;
	}

	@Override
	public void resetModel() {

		Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<AlertMessageReportEntry>>> columnFilterMap;
		columnFilterMap = getColumnFilterMap();
		columnFilterMap.clear();

		int columnIndex = 0;

		for (AlertBoxAndWhiskerReportColumn alertBoxAndWhiskerReportColumn : getAlertMessageReportColumnList()) {

			FilterColumn filterColumn = new FilterColumn(columnIndex);
			filterColumn.setColumnFilterEnabled(alertBoxAndWhiskerReportColumn.isFilterable());
			columnFilterMap.put(filterColumn, null);

			columnIndex++;

			if (AlertBoxAndWhiskerReportColumn.KEY.equals(alertBoxAndWhiskerReportColumn.getColumnId())) {
				keyAlertMessageReportColumn = alertBoxAndWhiskerReportColumn;
			}
		}

	}

	@Override
	public int getIndexOfKey(AlertMessageReportEntry key) {
		return 0;
	}

	@Override
	public Object getEventForKey(AlertMessageReportEntry key) {
		return null;
	}

	@Override
	public AbstractTreeTableNode getTreeNodeForKey(AlertMessageReportEntry key) {
		return null;
	}

	@Override
	public void clearSearchResults(boolean clearResults) {
	}

	@Override
	public SearchModel<AlertMessageReportEntry> getSearchModel() {
		return null;
	}

	@Override
	public String toString() {
		return getAlertMessageID() + "[Key Column: " + getKeyAlertMessageReportColumn().getDisplayName() + "]";
	}

	public String getAlertMessageID() {
		return alertMessageID;
	}

	protected AlertLogEntryModel getAlertLogEntryModel() {
		return alertLogEntryModel;
	}

	public AlertBoxAndWhiskerReportColumn getKeyAlertMessageReportColumn() {
		return keyAlertMessageReportColumn;
	}

	private Map<String, AlertMessageReportEntry> getAlertMessageReportEntryMap() {

		if (alertMessageReportEntryMap == null) {
			alertMessageReportEntryMap = new HashMap<String, AlertMessageReportEntry>();
		}

		return alertMessageReportEntryMap;
	}

	/**
	 * @param alertLogEntry
	 * @param logEntryValueList
	 *            - uncompressed data
	 */
	public void processAlertLogEntry(AlertLogEntry alertLogEntry, ArrayList<String> logEntryValueList) {

		String alertMessageReportEntryKey = getAlertMessageReportEntryKey(alertLogEntry, logEntryValueList);

		AlertMessageReportEntry alertMessageReportEntry = getAlertMessageReportEntry(alertMessageReportEntryKey);

		if (alertMessageReportEntry == null) {
			alertMessageReportEntry = new AlertMessageReportEntry(thresholdKPI, kpiUnit, alertMessageReportEntryKey);

			addAlertMessageReportEntry(alertMessageReportEntry);
		}

		Integer alertLogEntryKey = alertLogEntry.getKey();
		double alertLogEntryValue = alertLogEntry.getObservedKPI();

		alertMessageReportEntry.addAlertLogEntryKey(alertLogEntryKey, alertLogEntryValue);
	}

	private String getColumnValue(AlertMessageReportEntry alertMessageReportEntry, int columnIndex) {

		String columnValue = null;

		if (alertMessageReportEntry != null) {

			List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList = getAlertMessageReportColumnList();
			AlertBoxAndWhiskerReportColumn alertBoxAndWhiskerReportColumn = alertMessageReportColumnList
					.get(columnIndex);

			NumberFormat numberFormat = alertLogEntryModel.getNumberFormat();

			Object columnObject = alertMessageReportEntry.getColumnValue(alertBoxAndWhiskerReportColumn, numberFormat);

			if (columnObject != null) {
				columnValue = columnObject.toString();
			}
		}

		return columnValue;
	}

	public AlertMessageReportEntry getAlertMessageReportEntry(int rowIndex) {
		List<AlertMessageReportEntry> alertMessageReportEntryList = getFtmEntryKeyList();
		AlertMessageReportEntry alertMessageReportEntry = alertMessageReportEntryList.get(rowIndex);

		return alertMessageReportEntry;
	}

	protected AlertMessageReportEntry getAlertMessageReportEntry(String alertMessageReportDataKey) {

		Map<String, AlertMessageReportEntry> alertMessageReportEntryMap = getAlertMessageReportEntryMap();

		return alertMessageReportEntryMap.get(alertMessageReportDataKey);

	}

	protected void addAlertMessageReportEntry(AlertMessageReportEntry alertMessageReportEntry) {

		List<AlertMessageReportEntry> alertMessageReportEntryList = getFtmEntryKeyList();

		alertMessageReportEntryList.add(alertMessageReportEntry);

		Map<String, AlertMessageReportEntry> alertMessageReportEntryMap = getAlertMessageReportEntryMap();

		String alertMessageReportDataKey = alertMessageReportEntry.getAlertMessageReportEntryKey();

		alertMessageReportEntryMap.put(alertMessageReportDataKey, alertMessageReportEntry);

		updateColumnFilterMap(alertMessageReportEntry);

		Collections.sort(alertMessageReportEntryList);
	}

	// clearing the columnFilterMap will skip the below loop
	private void updateColumnFilterMap(AlertMessageReportEntry alertMessageReportEntry) {

		if (alertMessageReportEntry != null) {

			Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<AlertMessageReportEntry>>> columnFilterMap = getColumnFilterMap();

			Iterator<FilterColumn> fcIterator = columnFilterMap.keySet().iterator();

			while (fcIterator.hasNext()) {

				FilterColumn filterColumn = fcIterator.next();

				List<CheckBoxMenuItemPopupEntry<AlertMessageReportEntry>> columnFilterEntryList;
				columnFilterEntryList = columnFilterMap.get(filterColumn);

				if (columnFilterEntryList == null) {
					columnFilterEntryList = new ArrayList<CheckBoxMenuItemPopupEntry<AlertMessageReportEntry>>();
					columnFilterMap.put(filterColumn, columnFilterEntryList);
				}

				int columnIndex = filterColumn.getIndex();

				String columnValueStr = getColumnValue(alertMessageReportEntry, columnIndex);

				if (columnValueStr == null) {
					columnValueStr = FilterTableModel.NULL_STR;
				} else if ("".equals(columnValueStr)) {
					columnValueStr = FilterTableModel.EMPTY_STR;
				}

				CheckBoxMenuItemPopupEntry<AlertMessageReportEntry> columnFilterEntry;

				CheckBoxMenuItemPopupEntry<AlertMessageReportEntry> searchKey;
				searchKey = new CheckBoxMenuItemPopupEntry<AlertMessageReportEntry>(columnValueStr);

				int index = columnFilterEntryList.indexOf(searchKey);

				if (index == -1) {
					columnFilterEntry = new CheckBoxMenuItemPopupEntry<AlertMessageReportEntry>(columnValueStr);
					columnFilterEntryList.add(columnFilterEntry);
				} else {
					columnFilterEntry = columnFilterEntryList.get(index);
				}

				columnFilterEntry.addRowIndex(alertMessageReportEntry);

			}
		}
	}

	@Override
	protected TableColumnModel getTableColumnModel() {

		TableColumnModel tableColumnModel = new DefaultTableColumnModel();

		TableColumn tableColumn = null;
		int columnIndex = 0;

		for (AlertBoxAndWhiskerReportColumn alertBoxAndWhiskerReportColumn : getAlertMessageReportColumnList()) {

			TableCellRenderer tcr = null;

			DefaultTableCellRenderer dtcr = getDefaultTableCellRenderer();
			dtcr.setHorizontalAlignment(alertBoxAndWhiskerReportColumn.getHorizontalAlignment());
			tcr = dtcr;

			int prefColumnWidth = alertBoxAndWhiskerReportColumn.getPrefColumnWidth();

			tableColumn = new TableColumn(columnIndex++);
			tableColumn.setHeaderValue(alertBoxAndWhiskerReportColumn.getDisplayName());
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

			/*
			 * (non-Javadoc)
			 * 
			 * @see javax.swing.table.DefaultTableCellRenderer#
			 * getTableCellRendererComponent(javax.swing.JTable,
			 * java.lang.Object, boolean, boolean, int, int)
			 */
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				setBorder(new EmptyBorder(1, 8, 1, 10));

				if (!isSelected) {
					setBackground(MyColor.LIGHTEST_LIGHT_GRAY);
				}
				return this;
			}

		};

		return dtcr;
	}

	protected Map<String, String> getAlertLogEntryDataValueMap(String dataValueString) {

		Map<String, String> alertLogEntryDataValueMap = new HashMap<>();

		if ((dataValueString != null) && (!"".equals(dataValueString)) && (!"NA".equals(dataValueString))) {

			String[] dataArray = dataValueString.split(";");

			for (String data : dataArray) {

				String[] valueArray = data.split("=", 2);

				String name = valueArray[0].trim();
				String value = null;

				if (valueArray.length > 1) {
					value = valueArray[1];
				}

				alertLogEntryDataValueMap.put(name, value);
			}

		}

		return alertLogEntryDataValueMap;

	}
}
