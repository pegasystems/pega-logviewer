
package com.pega.gcs.logviewer.systemstate.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.guiutilities.DefaultTableColumn;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.datatable.AbstractDataTableModel;
import com.pega.gcs.logviewer.systemstate.model.CsvData;
import com.pega.gcs.logviewer.systemstate.model.FTSSettings;

public class FTSSettingsTableModel extends AbstractDataTableModel<CsvData, DefaultTableColumn> {

    private static final long serialVersionUID = 9044208647082744370L;

    private Map<Integer, CsvData> dataMap;

    private List<DefaultTableColumn> columnList;

    private DefaultTableColumn snoTableColumn;

    private DefaultTableColumn keyTableColumn;

    private DefaultTableColumn valueTableColumn;

    public FTSSettingsTableModel(FTSSettings ftsSettings, RecentFile recentFile) {

        super(recentFile);

        columnList = new ArrayList<>();

        int prefColumnWidth = 150;
        int horizontalAlignment = SwingConstants.LEFT;

        String displayName = "S.NO";

        snoTableColumn = new DefaultTableColumn(displayName, prefColumnWidth, horizontalAlignment);

        prefColumnWidth = 400;
        displayName = "Key";
        keyTableColumn = new DefaultTableColumn(displayName, prefColumnWidth, horizontalAlignment);

        displayName = "Value";
        valueTableColumn = new DefaultTableColumn(displayName, prefColumnWidth, horizontalAlignment);

        columnList.add(snoTableColumn);
        columnList.add(keyTableColumn);
        columnList.add(valueTableColumn);

        List<CsvData> csvDataList = buildCsvDataList(ftsSettings);

        AtomicInteger indexAi = new AtomicInteger(0);
        dataMap = new HashMap<>();

        for (CsvData csvData : csvDataList) {
            Integer index = indexAi.incrementAndGet();

            csvData.setIndex(index);

            dataMap.put(csvData.getIndex(), csvData);
        }

        reset();

    }

    @Override
    protected Map<Integer, CsvData> getDataMap() {
        return dataMap;
    }

    @Override
    protected List<DefaultTableColumn> getColumnList() {
        return columnList;
    }

    @Override
    protected String getColumnData(CsvData data, DefaultTableColumn dataTableColumn) {

        String columndata = null;

        if (dataTableColumn.getDisplayName().equals("S.NO")) {
            columndata = Integer.toString(data.getIndex());
        } else {
            int colIndex = columnList.indexOf(dataTableColumn) - 1;
            List<String> colDataList = data.getDataList();

            if (colIndex < colDataList.size()) {
                columndata = data.getDataList().get(colIndex);
            }
        }

        return columndata;
    }

    private List<CsvData> buildCsvDataList(FTSSettings ftsSettings) {

        List<CsvData> csvDataList = new ArrayList<>();

        Map<String, String> fieldValueMap = ftsSettings.getFieldValueMap();

        for (Map.Entry<String, String> entry : fieldValueMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            CsvData csvData = new CsvData(Arrays.asList(key, value));
            csvDataList.add(csvData);

        }

        // CsvDataSortComparator csvSortComparator = new CsvDataSortComparator(Arrays.asList(0), true);
        // Collections.sort(csvDataList, csvSortComparator);

        return csvDataList;
    }
}
