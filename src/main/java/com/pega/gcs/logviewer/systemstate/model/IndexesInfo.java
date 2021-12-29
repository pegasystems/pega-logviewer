
package com.pega.gcs.logviewer.systemstate.model;

import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IndexesInfo {

    @JsonProperty("defaultIndexes")
    private TreeSet<IndexInfo> defaultIndexInfoSet;

    @JsonProperty("dedicatedIndexes")
    private TreeSet<IndexInfo> dedicatedIndexInfoSet;

    @JsonProperty("customIndexes")
    private TreeSet<IndexInfo> customIndexInfoSet;

    public IndexesInfo() {
    }

    public TreeSet<IndexInfo> getDefaultIndexInfoSet() {
        return defaultIndexInfoSet;
    }

    public TreeSet<IndexInfo> getDedicatedIndexInfoSet() {
        return dedicatedIndexInfoSet;
    }

    public TreeSet<IndexInfo> getCustomIndexInfoSet() {
        return customIndexInfoSet;
    }

    public void postProcess() {

        if (defaultIndexInfoSet != null) {

            AtomicInteger index = new AtomicInteger(0);

            for (IndexInfo indexInfo : defaultIndexInfoSet) {

                indexInfo.setIndex(index.incrementAndGet());
            }
        }

        if (dedicatedIndexInfoSet != null) {

            AtomicInteger index = new AtomicInteger(0);

            for (IndexInfo indexInfo : dedicatedIndexInfoSet) {

                indexInfo.setIndex(index.incrementAndGet());
            }
        }

        if (customIndexInfoSet != null) {

            AtomicInteger index = new AtomicInteger(0);

            for (IndexInfo indexInfo : customIndexInfoSet) {

                indexInfo.setIndex(index.incrementAndGet());
            }
        }
    }

}
