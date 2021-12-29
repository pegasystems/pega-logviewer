
package com.pega.gcs.logviewer.systemstate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pega.gcs.fringecommon.guiutilities.datatable.IndexEntry;

public class IndexInfo extends IndexEntry implements Comparable<IndexInfo> {

    @JsonProperty("name")
    private String name;

    @JsonProperty("indexExists")
    private Boolean indexExists;

    @JsonProperty("indexStatus")
    private String indexStatus;

    @JsonProperty("indexState")
    private String indexState;

    @JsonProperty("primarySize")
    private String primarySize;

    @JsonProperty("totalSize")
    private String totalSize;

    @JsonProperty("numOfDocuments")
    private long numOfDocuments;

    public IndexInfo() {
    }

    public String getName() {
        return name;
    }

    public Boolean getIndexExists() {
        return indexExists;
    }

    public String getIndexStatus() {
        return indexStatus;
    }

    public String getIndexState() {
        return indexState;
    }

    public String getPrimarySize() {
        return primarySize;
    }

    public String getTotalSize() {
        return totalSize;
    }

    public long getNumOfDocuments() {
        return numOfDocuments;
    }

    @Override
    public int compareTo(IndexInfo other) {
        return getName().compareTo(other.getName());
    }

}
