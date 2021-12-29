
package com.pega.gcs.logviewer.systemstate.model;

import com.pega.gcs.fringecommon.guiutilities.datatable.IndexEntry;

public class AnalysisMarker extends IndexEntry {

    private String nodeId;

    private AnalysisMarkerCategory analysisMarkerCategory;

    private String message;

    public AnalysisMarker(String nodeId, AnalysisMarkerCategory analysisMarkerCategory, String message) {

        super();

        this.nodeId = nodeId;
        this.analysisMarkerCategory = analysisMarkerCategory;
        this.message = message;
    }

    public String getNodeId() {
        return nodeId;
    }

    public AnalysisMarkerCategory getAnalysisMarkerCategory() {
        return analysisMarkerCategory;
    }

    public String getMessage() {
        return message;
    }

}
