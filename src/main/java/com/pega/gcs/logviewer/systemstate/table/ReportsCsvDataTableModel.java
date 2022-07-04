
package com.pega.gcs.logviewer.systemstate.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.guiutilities.DefaultTableColumn;
import com.pega.gcs.fringecommon.guiutilities.datatable.AbstractDataTableModel;
import com.pega.gcs.logviewer.systemstate.model.CsvData;
import com.pega.gcs.logviewer.systemstate.model.CsvDataMap;
import com.pega.gcs.logviewer.systemstate.model.CsvHeader;

public class ReportsCsvDataTableModel extends AbstractDataTableModel<CsvData, DefaultTableColumn> {

    private static final long serialVersionUID = 9044208647082744370L;

    private Map<Integer, CsvData> dataMap;

    private List<DefaultTableColumn> columnList;

    private DefaultTableColumn snoTableColumn;

    public ReportsCsvDataTableModel(CsvDataMap csvDataMap) {

        super(null);

        columnList = new ArrayList<>();

        int prefColumnWidth = 80;
        int horizontalAlignment = SwingConstants.LEFT;
        String displayName = "S.NO";

        snoTableColumn = new DefaultTableColumn(displayName, displayName, prefColumnWidth, horizontalAlignment, true,
                false);

        columnList.add(snoTableColumn);

        prefColumnWidth = 120;
        horizontalAlignment = SwingConstants.LEFT;

        List<CsvHeader> csvHeaderList = csvDataMap.getCsvHeaderList();

        for (CsvHeader csvHeader : csvHeaderList) {

            String header = csvHeader.getHeader();
            boolean filterable = csvHeader.isFilterable();

            DefaultTableColumn defaultTableColumn;
            defaultTableColumn = new DefaultTableColumn(header, header, prefColumnWidth, horizontalAlignment, true,
                    filterable);

            columnList.add(defaultTableColumn);
        }

        dataMap = new HashMap<>();

        if (csvDataMap != null) {
            for (CsvData data : csvDataMap.getCsvDataList()) {
                dataMap.put(data.getIndex(), data);
            }
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

            // in 8.3 header list size doesnt match with data record size.
            if (colIndex < colDataList.size()) {
                columndata = data.getDataList().get(colIndex);
            }
        }

        return columndata;
    }

}
