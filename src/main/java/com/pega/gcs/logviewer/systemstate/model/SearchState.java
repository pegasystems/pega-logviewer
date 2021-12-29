
package com.pega.gcs.logviewer.systemstate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchState implements NodeObject {

    private static final String SEARCHSTATE_NODE_NAME = "Search State";

    @JsonProperty("clusterStatus")
    private ClusterStatus clusterStatus;

    @JsonProperty("settings")
    private CleanedFTSSettings cleanedFTSSettings;

    @JsonProperty("indexInfo")
    private IndexesInfo indexesInfo;

    @JsonProperty("queueInformation")
    private QueueInformationContainer queueInformationContainer;

    public SearchState() {
    }

    @Override
    public String getDisplayName() {
        return SEARCHSTATE_NODE_NAME;
    }

    public ClusterStatus getClusterStatus() {
        return clusterStatus;
    }

    public CleanedFTSSettings getCleanedFTSSettings() {
        return cleanedFTSSettings;
    }

    public IndexesInfo getIndexesInfo() {
        return indexesInfo;
    }

    public QueueInformationContainer getQueueInformationContainer() {
        return queueInformationContainer;
    }

    public void postProcess() {

        if (indexesInfo != null) {
            indexesInfo.postProcess();
        }

        if (cleanedFTSSettings != null) {
            cleanedFTSSettings.postProcess();
        }

    }
}
