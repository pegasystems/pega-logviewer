/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.report.alert;

import java.awt.Color;
import java.awt.Component;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.AlertLogEntry;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.alert.AlertMessageList.AlertMessage;

public abstract class AlertMessageReportModel extends FilterTableModel<AlertMessageReportEntry> {

    private static final long serialVersionUID = -8895320811393447615L;

    private static final Log4j2Helper LOG = new Log4j2Helper(AlertMessageReportModel.class);

    private AlertMessage alertMessage;

    private long thresholdKPI;

    private AlertLogEntryModel alertLogEntryModel;

    private Locale locale;

    private double minRangeValue;

    private double maxRangeValue;

    // for each alert, collate them based on the reason and list the alert keys.
    private List<AlertMessageReportEntry> alertMessageReportEntryList;

    private HashMap<AlertMessageReportEntry, Integer> keyIndexMap;

    private Map<String, AlertMessageReportEntry> alertMessageReportEntryMap;

    private AlertBoxAndWhiskerReportColumn keyAlertMessageReportColumn;

    private Pattern inClausePattern;

    protected abstract List<AlertBoxAndWhiskerReportColumn> getAlertMessageReportColumnList();

    protected abstract String getAlertMessageReportEntryKey(ArrayList<String> logEntryValueList);

    public abstract String getAlertMessageReportEntryKey(String dataText);

    public AlertMessageReportModel(AlertMessage alertMessage, long thresholdKPI, AlertLogEntryModel alertLogEntryModel,
            Locale locale) {

        super(null);

        this.alertMessage = alertMessage;
        this.thresholdKPI = thresholdKPI;
        this.alertLogEntryModel = alertLogEntryModel;
        this.locale = locale;
        this.minRangeValue = -1;
        this.maxRangeValue = -1;

        String inClauseStr = "(\\sIN\\s\\((.*?)\\))+";
        inClausePattern = Pattern.compile(inClauseStr, Pattern.CASE_INSENSITIVE);

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

        return alertMessageReportEntry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.guiutilities.CustomJTableModel#getColumnValue(java. lang.Object, int)
     */
    @Override
    public String getColumnValue(Object valueAtObject, int columnIndex) {

        AlertMessageReportEntry alertMessageReportEntry = (AlertMessageReportEntry) valueAtObject;

        String columnValue = null;

        if (alertMessageReportEntry != null) {

            List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList = getAlertMessageReportColumnList();
            AlertBoxAndWhiskerReportColumn alertBoxAndWhiskerReportColumn = alertMessageReportColumnList
                    .get(columnIndex);

            NumberFormat numberFormat = NumberFormat.getInstance(); // TODO

            Object columnObject = alertMessageReportEntry.getColumnValue(alertBoxAndWhiskerReportColumn, numberFormat);

            if (columnObject != null) {
                columnValue = columnObject.toString();
            }
        }

        return columnValue;
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
    protected HashMap<AlertMessageReportEntry, Integer> getKeyIndexMap() {

        if (keyIndexMap == null) {
            keyIndexMap = new HashMap<>();
        }

        return keyIndexMap;
    }

    @Override
    public void resetModel() {

        Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<AlertMessageReportEntry>>> columnFilterMap;
        columnFilterMap = getColumnFilterMap();
        columnFilterMap.clear();

        List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList = getAlertMessageReportColumnList();

        for (int columnIndex = 0; columnIndex < alertMessageReportColumnList.size(); columnIndex++) {

            AlertBoxAndWhiskerReportColumn alertBoxAndWhiskerReportColumn = alertMessageReportColumnList
                    .get(columnIndex);

            // preventing unnecessary buildup of filter map
            if (alertBoxAndWhiskerReportColumn.isFilterable()) {
                FilterColumn filterColumn = new FilterColumn(columnIndex);
                // deferring the setColumnFilterEnabled to updateColumnFilterMap if >1 data is available
                // filterColumn.setColumnFilterEnabled(true);
                columnFilterMap.put(filterColumn, null);
            }

            if (AlertBoxAndWhiskerReportColumn.KEY.equals(alertBoxAndWhiskerReportColumn.getColumnId())) {
                keyAlertMessageReportColumn = alertBoxAndWhiskerReportColumn;
            }
        }
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
        String alertMessageID = alertMessage.getMessageID();
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
            alertMessageReportEntryMap = new TreeMap<String, AlertMessageReportEntry>(String.CASE_INSENSITIVE_ORDER);
        }

        return alertMessageReportEntryMap;
    }

    public double getMinRangeValue() {
        return minRangeValue;
    }

    public double getMaxRangeValue() {
        return maxRangeValue;
    }

    public void processAlertLogEntry(AlertLogEntry alertLogEntry, ArrayList<String> logEntryValueList) {

        double observedKPI = alertLogEntry.getObservedKPI();

        minRangeValue = ((minRangeValue == -1) || (minRangeValue > observedKPI)) ? observedKPI : minRangeValue;

        maxRangeValue = (maxRangeValue < observedKPI) ? observedKPI : maxRangeValue;

        String alertMessageReportEntryKey = null;

        try {

            alertMessageReportEntryKey = getAlertMessageReportEntryKey(logEntryValueList);

        } catch (Exception e) {
            LOG.error("Error getting AlertMessageReportEntryKey: " + alertLogEntry.getKey(), e);

        }

        if (alertMessageReportEntryKey == null) {
            alertMessageReportEntryKey = "<UNKNOWN>";
        }

        AlertMessageReportEntry alertMessageReportEntry = getAlertMessageReportEntry(alertMessageReportEntryKey);

        if (alertMessageReportEntry == null) {

            String chartColor = alertMessage.getChartColor();
            Color color = null;

            if (chartColor == null) {
                color = Color.BLACK;
            } else {
                color = MyColor.getColor(chartColor);
            }

            String kpiUnit = alertMessage.getDssValueUnit();

            alertMessageReportEntry = new AlertMessageReportEntry(thresholdKPI, kpiUnit, alertMessageReportEntryKey,
                    color);

            addAlertMessageReportEntry(alertMessageReportEntry);
        }

        AlertLogEntryModel alertLogEntryModel = getAlertLogEntryModel();

        DateFormat modelDateFormat = alertLogEntryModel.getModelDateFormat();
        TimeZone timezone = modelDateFormat.getTimeZone();

        alertMessageReportEntry.addAlertLogEntry(alertLogEntry, timezone, locale);

        // Performance issue: moving out to postProcess to process on every parseFinal calls.
        // sort to rearrange based on 5point summary
        // List<AlertMessageReportEntry> alertMessageReportEntryList = getFtmEntryKeyList();
        // Collections.sort(alertMessageReportEntryList);
        //
        // updateKeyIndexMap();
    }

    public void postProcess() {
        // sort to rearrange based on 5point summary
        List<AlertMessageReportEntry> alertMessageReportEntryList = getFtmEntryKeyList();
        Collections.sort(alertMessageReportEntryList);

        updateKeyIndexMap();
    }

    private AlertMessageReportEntry getAlertMessageReportEntry(int rowIndex) {
        List<AlertMessageReportEntry> alertMessageReportEntryList = getFtmEntryKeyList();
        AlertMessageReportEntry alertMessageReportEntry = alertMessageReportEntryList.get(rowIndex);

        return alertMessageReportEntry;
    }

    private AlertMessageReportEntry getAlertMessageReportEntry(String alertMessageReportDataKey) {

        Map<String, AlertMessageReportEntry> alertMessageReportEntryMap = getAlertMessageReportEntryMap();

        return alertMessageReportEntryMap.get(alertMessageReportDataKey);

    }

    private void addAlertMessageReportEntry(AlertMessageReportEntry alertMessageReportEntry) {

        List<AlertMessageReportEntry> alertMessageReportEntryList = getFtmEntryKeyList();

        alertMessageReportEntryList.add(alertMessageReportEntry);

        Map<String, AlertMessageReportEntry> alertMessageReportEntryMap = getAlertMessageReportEntryMap();

        String alertMessageReportDataKey = alertMessageReportEntry.getAlertMessageReportEntryKey();

        alertMessageReportEntryMap.put(alertMessageReportDataKey, alertMessageReportEntry);

        updateColumnFilterMap(alertMessageReportEntry);

        // sorting is done after data is added to AlertMessageReportEntry item, in processAlertLogEntry().
        // Collections.sort(alertMessageReportEntryList);
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

                if (columnFilterEntryList.size() > 1) {
                    filterColumn.setColumnFilterEnabled(true);
                }

            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.guiutilities.CustomJTableModel#getTableColumnModel( )
     */
    @Override
    public TableColumnModel getTableColumnModel() {

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
             * @see javax.swing.table.DefaultTableCellRenderer# getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean,
             * boolean, int, int)
             */
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                AlertMessageReportEntry alertMessageReportEntry = (AlertMessageReportEntry) value;

                if (alertMessageReportEntry != null) {
                    AlertMessageReportModel alertMessageReportModel = (AlertMessageReportModel) table.getModel();

                    String text = alertMessageReportModel.getColumnValue(alertMessageReportEntry, column);

                    super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);

                    if (!isSelected) {
                        setBackground(MyColor.LIGHTEST_LIGHT_GRAY);
                    }

                    setBorder(new EmptyBorder(1, 8, 1, 10));

                } else {
                    setBackground(Color.WHITE);
                    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }

                return this;
            }
        };

        return dtcr;
    }

    protected Map<String, String> getAlertLogEntryDataValueMap(String dataValueString) {

        Map<String, String> alertLogEntryDataValueMap = new HashMap<>();

        if ((dataValueString != null) && (!"".equals(dataValueString)) && (!"NA".equals(dataValueString))) {

            String[] dataArray = dataValueString.split(";", 0);

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

    protected String getInClauseGeneralisedKey(String alertMessageReportEntryKey) {

        TreeSet<String> capturedGroupSet = new TreeSet<>();

        Matcher inClauseMatcher = inClausePattern.matcher(alertMessageReportEntryKey);

        while (inClauseMatcher.find()) {
            String matchedSubGroup = Pattern.quote(inClauseMatcher.group(2));
            capturedGroupSet.add(matchedSubGroup);
        }

        for (String capturedGroup : capturedGroupSet) {
            alertMessageReportEntryKey = alertMessageReportEntryKey.replaceAll(capturedGroup, "...");
        }

        return alertMessageReportEntryKey;
    }
}
