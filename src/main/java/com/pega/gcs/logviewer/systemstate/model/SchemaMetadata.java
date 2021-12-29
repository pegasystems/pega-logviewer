
package com.pega.gcs.logviewer.systemstate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SchemaMetadata {

    @JsonProperty("schemaName")
    private String schemaName;

    @JsonProperty("schemaSize")
    private String schemaSize;

    public SchemaMetadata() {
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getSchemaSize() {
        return schemaSize;
    }

}
