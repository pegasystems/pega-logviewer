
package com.pega.gcs.logviewer.systemstate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pega.gcs.fringecommon.guiutilities.datatable.IndexEntry;

public class DatastoreNodeMetadata extends IndexEntry implements Comparable<DatastoreNodeMetadata> {

    @JsonProperty("databaseSizeOnDisk")
    private String databaseSizeOnDisk;

    @JsonProperty("dbName")
    private String dbName;

    @JsonProperty("dbType")
    private String dbType;

    @JsonProperty("schemaMetadata")
    private SchemaMetadata schemaMetadata;

    @JsonProperty("tableCount")
    private Integer tableCount;

    public DatastoreNodeMetadata() {

    }

    public String getDatabaseSizeOnDisk() {
        return databaseSizeOnDisk;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbType() {
        return dbType;
    }

    public SchemaMetadata getSchemaMetadata() {
        return schemaMetadata;
    }

    public Integer getTableCount() {
        return tableCount;
    }

    @Override
    public int compareTo(DatastoreNodeMetadata other) {
        return getDbName().compareTo(other.getDbName());
    }

}
