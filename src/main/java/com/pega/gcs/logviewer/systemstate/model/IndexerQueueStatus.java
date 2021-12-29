
package com.pega.gcs.logviewer.systemstate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IndexerQueueStatus {

    @JsonProperty("queueName")
    private String queueName;

    @JsonProperty("queueStatus")
    private String queueStatus;

    @JsonProperty("queueSize")
    private String queueSize;

    @JsonProperty("numItemsProcessedLastHour")
    private String numItemsProcessedLastHour;

    public IndexerQueueStatus() {
    }

    public String getQueueName() {
        return queueName;
    }

    public String getQueueStatus() {
        return queueStatus;
    }

    public String getQueueSize() {
        return queueSize;
    }

    public String getNumItemsProcessedLastHour() {
        return numItemsProcessedLastHour;
    }

}
