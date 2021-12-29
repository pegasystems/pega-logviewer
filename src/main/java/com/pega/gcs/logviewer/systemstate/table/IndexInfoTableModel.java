
package com.pega.gcs.logviewer.systemstate.table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.pega.gcs.fringecommon.guiutilities.datatable.AbstractDataTableModel;
import com.pega.gcs.logviewer.systemstate.model.IndexInfo;

public class IndexInfoTableModel extends AbstractDataTableModel<IndexInfo, IndexInfoTableColumn> {

    private static final long serialVersionUID = -2043727343347541824L;

    private Map<Integer, IndexInfo> dataMap;

    private List<IndexInfoTableColumn> columnList;

    public IndexInfoTableModel(TreeSet<IndexInfo> indexeInfoSet) {

        columnList = IndexInfoTableColumn.getColumnList();

        dataMap = new HashMap<>();

        if (indexeInfoSet != null) {
            for (IndexInfo data : indexeInfoSet) {
                dataMap.put(data.getIndex(), data);
            }
        }

        reset();

    }

    @Override
    protected Map<Integer, IndexInfo> getDataMap() {
        return dataMap;
    }

    @Override
    protected List<IndexInfoTableColumn> getColumnList() {
        return columnList;
    }

    @Override
    protected String getColumnData(IndexInfo data, IndexInfoTableColumn dataTableColumn) {

        String columndata = null;

        if (dataTableColumn.equals(IndexInfoTableColumn.SNO)) {
            columndata = Integer.toString(data.getIndex());
        } else if (dataTableColumn.equals(IndexInfoTableColumn.NAME)) {
            columndata = data.getName();
        } else if (dataTableColumn.equals(IndexInfoTableColumn.INDEXEXISTS)) {
            Boolean indexExists = data.getIndexExists();
            columndata = (indexExists != null) ? Boolean.toString(indexExists) : null;
        } else if (dataTableColumn.equals(IndexInfoTableColumn.INDEXSTATUS)) {
            columndata = data.getIndexStatus();
        } else if (dataTableColumn.equals(IndexInfoTableColumn.INDEXSTATE)) {
            columndata = data.getIndexState();
        } else if (dataTableColumn.equals(IndexInfoTableColumn.PRIMARYSIZE)) {
            columndata = data.getPrimarySize();
        } else if (dataTableColumn.equals(IndexInfoTableColumn.TOTALSIZE)) {
            columndata = data.getTotalSize();
        } else if (dataTableColumn.equals(IndexInfoTableColumn.NUMOFDOCUMENTS)) {
            Long numOfDocuments = data.getNumOfDocuments();
            columndata = (numOfDocuments != null) ? Long.toString(numOfDocuments) : null;
        }

        return columndata;
    }
}
