/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.alert;

import java.awt.Component;
import java.util.ArrayList;
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

public class AlertCheatSheetTableModel extends FilterTableModel<Integer> {

	private static final long serialVersionUID = -2723048955819135389L;

	private List<Integer> alertMessageKeyList;

	public AlertCheatSheetTableModel() {
		super(null);

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
		return AlertCheatSheetTableColumn.getTableColumnList().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		List<Integer> alertMessageKeyList = getFtmEntryKeyList();

		Integer key = alertMessageKeyList.get(rowIndex);

		AlertMessageListProvider alertMessageListProvider = AlertMessageListProvider.getInstance();

		Map<Integer, AlertMessage> alertMessageMap = alertMessageListProvider.getAlertMessageMap();

		AlertMessage alertMessage = alertMessageMap.get(key);

		String value = getColumnValue(alertMessage, columnIndex);

		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pega.gcs.fringecommon.guiutilities.FilterTableModel#
	 * getModelColumnIndex(int)
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
	 * @see com.pega.gcs.fringecommon.guiutilities.FilterTableModel#
	 * getNavigationRowIndex(java.util.List, int, boolean, boolean, boolean,
	 * boolean)
	 */
	@Override
	protected FilterTableModelNavigation<Integer> getNavigationRowIndex(List<Integer> resultList,
			int currSelectedRowIndex, boolean forward, boolean first, boolean last, boolean wrap) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pega.gcs.fringecommon.guiutilities.FilterTableModel#
	 * getFtmEntryKeyList()
	 */
	@Override
	public List<Integer> getFtmEntryKeyList() {

		if (alertMessageKeyList == null) {
			alertMessageKeyList = new ArrayList<Integer>();
		}

		return alertMessageKeyList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pega.gcs.fringecommon.guiutilities.FilterTableModel#resetModel()
	 */
	@Override
	public void resetModel() {

		Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<Integer>>> columnFilterMap;
		columnFilterMap = getColumnFilterMap();
		columnFilterMap.clear();

		int columnIndex = 0;

		for (AlertCheatSheetTableColumn alertCheatSheetTableColumn : AlertCheatSheetTableColumn.getTableColumnList()) {

			FilterColumn filterColumn = new FilterColumn(columnIndex);
			filterColumn.setColumnFilterEnabled(alertCheatSheetTableColumn.isFilterable());
			columnFilterMap.put(filterColumn, null);

			columnIndex++;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getIndexOfKey(
	 * java.lang.Comparable)
	 */
	@Override
	public int getIndexOfKey(Integer key) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getEventForKey(
	 * java.lang.Comparable)
	 */
	@Override
	public Object getEventForKey(Integer key) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getTreeNodeForKey
	 * (java.lang.Comparable)
	 */
	@Override
	public AbstractTreeTableNode getTreeNodeForKey(Integer key) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pega.gcs.fringecommon.guiutilities.FilterTableModel#
	 * clearSearchResults(boolean)
	 */
	@Override
	public void clearSearchResults(boolean clearResults) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pega.gcs.fringecommon.guiutilities.FilterTableModel#getSearchModel()
	 */
	@Override
	public SearchModel<Integer> getSearchModel() {
		// TODO Auto-generated method stub
		return null;
	}

	private void initialise() {

		List<Integer> rulesetInskeyEntryKeyList = getFtmEntryKeyList();

		AlertMessageListProvider alertMessageListProvider = AlertMessageListProvider.getInstance();

		Map<Integer, AlertMessage> alertMessageMap = alertMessageListProvider.getAlertMessageMap();

		if (alertMessageMap != null) {

			for (Map.Entry<Integer, AlertMessage> entry : alertMessageMap.entrySet()) {

				Integer alertMessageKey = entry.getKey();
				AlertMessage alertMessage = entry.getValue();

				rulesetInskeyEntryKeyList.add(alertMessageKey);

				updateColumnFilterMap(alertMessageKey, alertMessage);
			}
		}

	}

	private String getColumnValue(AlertMessage alertMessage, int columnIndex) {

		String columnValue = null;

		if (alertMessage != null) {

			AlertCheatSheetTableColumn alertCheatSheetTableColumn;
			alertCheatSheetTableColumn = AlertCheatSheetTableColumn.getTableColumnList().get(columnIndex);

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
				columnValue = alertMessage.getSeverity().name();
			} else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.PDNURL)) {
				columnValue = alertMessage.getPDNURL();
			} else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.DESCRIPTION)) {
				columnValue = alertMessage.getDescription();
			} else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.DSSENABLECONFIG)) {
				columnValue = alertMessage.getDSSEnableConfig();
			} else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.DSSENABLED)) {
				columnValue = alertMessage.getDSSEnabled();
			} else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.DSSTHRESHOLDCONFIG)) {
				columnValue = alertMessage.getDSSThresholdConfig();
			} else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.DSSVALUETYPE)) {
				columnValue = alertMessage.getDSSValueType();
			} else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.DSSVALUEUNIT)) {
				columnValue = alertMessage.getDSSValueUnit();
			} else if (alertCheatSheetTableColumn.equals(AlertCheatSheetTableColumn.DSSDEFAULTVALUE)) {
				columnValue = alertMessage.getDSSDefaultValue();
			}

		}

		return columnValue;
	}

	// clearing the columnFilterMap will skip the below loop
	private void updateColumnFilterMap(Integer rulesetInskeyEntryIndex, AlertMessage alertMessage) {

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

				String rulesetInsKeyStr = getColumnValue(alertMessage, columnIndex);

				if (rulesetInsKeyStr == null) {
					rulesetInsKeyStr = FilterTableModel.NULL_STR;
				} else if ("".equals(rulesetInsKeyStr)) {
					rulesetInsKeyStr = FilterTableModel.EMPTY_STR;
				}

				CheckBoxMenuItemPopupEntry<Integer> columnFilterEntry;

				CheckBoxMenuItemPopupEntry<Integer> searchKey;
				searchKey = new CheckBoxMenuItemPopupEntry<Integer>(rulesetInsKeyStr);

				int index = columnFilterEntryList.indexOf(searchKey);

				if (index == -1) {
					columnFilterEntry = new CheckBoxMenuItemPopupEntry<Integer>(rulesetInsKeyStr);
					columnFilterEntryList.add(columnFilterEntry);
				} else {
					columnFilterEntry = columnFilterEntryList.get(index);
				}

				columnFilterEntry.addRowIndex(rulesetInskeyEntryIndex);

			}
		}
	}

	@Override
	protected TableColumnModel getTableColumnModel() {

		TableColumnModel tableColumnModel = new DefaultTableColumnModel();

		TableColumn tableColumn = null;
		int columnIndex = 0;

		for (AlertCheatSheetTableColumn alertCheatSheetTableColumn : AlertCheatSheetTableColumn.getTableColumnList()) {

			TableCellRenderer tcr = null;

			DefaultTableCellRenderer dtcr = getDefaultTableCellRenderer();
			dtcr.setHorizontalAlignment(alertCheatSheetTableColumn.getHorizontalAlignment());
			tcr = dtcr;

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
