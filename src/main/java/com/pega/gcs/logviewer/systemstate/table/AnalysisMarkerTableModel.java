/*******************************************************************************
 * Copyright (c) 2022 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.systemstate.table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.pega.gcs.fringecommon.guiutilities.datatable.AbstractDataTableModel;
import com.pega.gcs.logviewer.systemstate.model.AnalysisMarker;

public class AnalysisMarkerTableModel extends AbstractDataTableModel<AnalysisMarker, AnalysisMarkerTableColumn> {

    private static final long serialVersionUID = 9044208647082744370L;

    private Map<Integer, AnalysisMarker> dataMap;

    private List<AnalysisMarkerTableColumn> columnList;

    private Map<Integer, Integer> indexLookupMap;

    public AnalysisMarkerTableModel(List<AnalysisMarker> analysisMarkerList) {

        super(null);

        columnList = AnalysisMarkerTableColumn.getColumnList();

        dataMap = new HashMap<>();
        indexLookupMap = new HashMap<>();

        if (analysisMarkerList != null) {

            AtomicInteger indexAi = new AtomicInteger(0);

            for (AnalysisMarker data : analysisMarkerList) {

                Integer analysisMarkerIndex = data.getIndex();

                Integer dataIndex = indexAi.incrementAndGet();

                dataMap.put(dataIndex, data);

                indexLookupMap.put(analysisMarkerIndex, dataIndex);
            }
        }

        reset();

    }

    @Override
    protected Map<Integer, AnalysisMarker> getDataMap() {
        return dataMap;
    }

    @Override
    protected List<AnalysisMarkerTableColumn> getColumnList() {
        return columnList;
    }

    @Override
    protected String getColumnData(AnalysisMarker data, AnalysisMarkerTableColumn dataTableColumn) {

        String columndata = null;

        if (dataTableColumn.equals(AnalysisMarkerTableColumn.SNO)) {
            columndata = Integer.toString(indexLookupMap.get(data.getIndex()));
        } else if (dataTableColumn.equals(AnalysisMarkerTableColumn.NODEID)) {
            columndata = data.getNodeId();
        } else if (dataTableColumn.equals(AnalysisMarkerTableColumn.CATEGORY)) {
            columndata = data.getAnalysisMarkerCategory().name();
        } else if (dataTableColumn.equals(AnalysisMarkerTableColumn.MESSAGE)) {
            columndata = data.getMessage();
        }

        return columndata;
    }

}
