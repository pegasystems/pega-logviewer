
package com.pega.gcs.logviewer.systemstate.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pega.gcs.fringecommon.guiutilities.datatable.IndexEntry;

public class SystemStateError extends IndexEntry implements Comparable<SystemStateError> {

    @JsonProperty("nodeId")
    @JsonAlias("nodeID")
    private String nodeId;

    @JsonProperty("message")
    @JsonAlias("errorMessage")
    private String message;

    public SystemStateError() {
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, nodeId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SystemStateError)) {
            return false;
        }
        SystemStateError other = (SystemStateError) obj;
        return Objects.equals(message, other.message) && Objects.equals(nodeId, other.nodeId);
    }

    @Override
    public String toString() {
        return "SystemStateError [nodeId=" + nodeId + ", message=" + message + "]";
    }

    @Override
    public int compareTo(SystemStateError other) {
        int compare = this.nodeId.compareTo(other.nodeId);

        if (compare == 0) {
            compare = this.message.compareTo(other.message);
        }

        return compare;
    }

}
