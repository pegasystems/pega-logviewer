
package com.pega.gcs.logviewer.systemstate.model;

public class CsvHeader {

    private String header;

    private boolean filterable;

    public CsvHeader(String header, boolean filterable) {
        super();
        this.header = header;
        this.filterable = filterable;
    }

    public String getHeader() {
        return header;
    }

    public boolean isFilterable() {
        return filterable;
    }

}
