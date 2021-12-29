
package com.pega.gcs.logviewer.systemstate.model;

import java.util.List;

import com.pega.gcs.fringecommon.guiutilities.datatable.IndexEntry;

public class CsvData extends IndexEntry {

    private List<String> dataList;

    public CsvData(List<String> dataList) {
        super();
        this.dataList = dataList;
    }

    public List<String> getDataList() {
        return dataList;
    }

}
