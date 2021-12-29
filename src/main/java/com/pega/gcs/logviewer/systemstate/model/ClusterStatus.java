
package com.pega.gcs.logviewer.systemstate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClusterStatus {

    @JsonProperty("isIndexingEnabled")
    private boolean isIndexingEnabled;

    public ClusterStatus() {
        // TODO Auto-generated constructor stub
    }

    public boolean isIndexingEnabled() {
        return isIndexingEnabled;
    }

}
