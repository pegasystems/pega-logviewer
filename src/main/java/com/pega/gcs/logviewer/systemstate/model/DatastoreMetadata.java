
package com.pega.gcs.logviewer.systemstate.model;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DatastoreMetadata implements NodeObject {

    private static final String DATASTOREMETADATA_NODE_NAME = "Datastore Metadata";

    @JsonProperty("nodeMetadata")
    private TreeSet<DatastoreNodeMetadata> datastoreNodeMetadataSet;

    public DatastoreMetadata() {
    }

    @Override
    public String getDisplayName() {
        return DATASTOREMETADATA_NODE_NAME;
    }

    public Set<DatastoreNodeMetadata> getDatastoreNodeMetadataSet() {
        return Collections.unmodifiableSet(datastoreNodeMetadataSet);
    }

    public void postProcess() {

        if (datastoreNodeMetadataSet != null) {

            AtomicInteger index = new AtomicInteger(0);

            for (DatastoreNodeMetadata datastoreNodeMetadata : datastoreNodeMetadataSet) {
                datastoreNodeMetadata.setIndex(index.incrementAndGet());
            }
        }
    }

}
