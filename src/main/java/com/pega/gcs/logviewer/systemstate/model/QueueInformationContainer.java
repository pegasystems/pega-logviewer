
package com.pega.gcs.logviewer.systemstate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueueInformationContainer {

    @JsonProperty("pyFTSIncrementalIndexer")
    private IndexerQueueStatus ftsIncrementalIndexerQueueStatus;

    @JsonProperty("pyBatchIndexProcessor")
    private IndexerQueueStatus batchIndexProcessorQueueStatus;

    public QueueInformationContainer() {
    }

    public IndexerQueueStatus getFtsIncrementalIndexerQueueStatus() {
        return ftsIncrementalIndexerQueueStatus;
    }

    public IndexerQueueStatus getBatchIndexProcessorQueueStatus() {
        return batchIndexProcessorQueueStatus;
    }

}
