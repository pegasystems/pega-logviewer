
package com.pega.gcs.logviewer.parser.cloudkjson;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CloudKMessage {

    @JsonProperty("message")
    private String message;

    @JsonProperty("stream")
    private String stream;

    @JsonProperty("kubernetes")
    private Kubernetes kubernetes;

    @JsonProperty("docker")
    private Docker docker;

    @JsonProperty("container_info")
    private String containerInfo;

    @JsonProperty("clusterMetadata")
    private ClusterMetadata clusterMetadata;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("stream")
    public String getStream() {
        return stream;
    }

    @JsonProperty("stream")
    public void setStream(String stream) {
        this.stream = stream;
    }

    @JsonProperty("kubernetes")
    public Kubernetes getKubernetes() {
        return kubernetes;
    }

    @JsonProperty("kubernetes")
    public void setKubernetes(Kubernetes kubernetes) {
        this.kubernetes = kubernetes;
    }

    @JsonProperty("docker")
    public Docker getDocker() {
        return docker;
    }

    @JsonProperty("docker")
    public void setDocker(Docker docker) {
        this.docker = docker;
    }

    @JsonProperty("container_info")
    public String getContainerInfo() {
        return containerInfo;
    }

    @JsonProperty("container_info")
    public void setContainerInfo(String containerInfo) {
        this.containerInfo = containerInfo;
    }

    @JsonProperty("clusterMetadata")
    public ClusterMetadata getClusterMetadata() {
        return clusterMetadata;
    }

    @JsonProperty("clusterMetadata")
    public void setClusterMetadata(ClusterMetadata clusterMetadata) {
        this.clusterMetadata = clusterMetadata;
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
