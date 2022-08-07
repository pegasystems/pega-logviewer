
package com.pega.gcs.logviewer.parser.cloudkjson;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClusterMetadata {

    @JsonProperty("clusterName")
    private String clusterName;

    @JsonProperty("environmentType")
    private String environmentType;

    @JsonProperty("region")
    private String region;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("clusterName")
    public String getClusterName() {
        return clusterName;
    }

    @JsonProperty("clusterName")
    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    @JsonProperty("environmentType")
    public String getEnvironmentType() {
        return environmentType;
    }

    @JsonProperty("environmentType")
    public void setEnvironmentType(String environmentType) {
        this.environmentType = environmentType;
    }

    @JsonProperty("region")
    public String getRegion() {
        return region;
    }

    @JsonProperty("region")
    public void setRegion(String region) {
        this.region = region;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
