
package com.pega.gcs.logviewer.systemstate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pega.gcs.fringecommon.guiutilities.datatable.IndexEntry;

public class HostNodeSetting extends IndexEntry implements Comparable<HostNodeSetting> {

    @JsonProperty("hostNodeID")
    private String hostNodeID;

    @JsonProperty("fileDirectory")
    private String fileDirectory;

    @JsonProperty("hostNodeStatus")
    private String hostNodeStatus;

    @JsonProperty("hostNodeIndexerStatus")
    private String hostNodeIndexerStatus;

    public HostNodeSetting() {
    }

    public String getHostNodeID() {
        return hostNodeID;
    }

    public String getFileDirectory() {
        return fileDirectory;
    }

    public String getHostNodeStatus() {
        return hostNodeStatus;
    }

    public String getHostNodeIndexerStatus() {
        return hostNodeIndexerStatus;
    }

    @Override
    public int compareTo(HostNodeSetting other) {
        return getHostNodeID().compareTo(other.getHostNodeID());
    }

}
