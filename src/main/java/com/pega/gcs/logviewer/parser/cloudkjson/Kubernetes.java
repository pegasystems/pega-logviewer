
package com.pega.gcs.logviewer.parser.cloudkjson;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Kubernetes {

    @JsonProperty("container_image")
    private String containerImage;

    @JsonProperty("container_name")
    private String containerName;

    @JsonProperty("host")
    private String host;

    @JsonProperty("namespace_name")
    private String namespaceName;

    @JsonProperty("pod_id")
    private String podId;

    @JsonProperty("pod_name")
    private String podName;

    @JsonProperty("labels")
    private Labels labels;

    @JsonProperty("namespace_labels")
    private NamespaceLabels namespaceLabels;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("container_image")
    public String getContainerImage() {
        return containerImage;
    }

    @JsonProperty("container_image")
    public void setContainerImage(String containerImage) {
        this.containerImage = containerImage;
    }

    @JsonProperty("container_name")
    public String getContainerName() {
        return containerName;
    }

    @JsonProperty("container_name")
    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    @JsonProperty("host")
    public String getHost() {
        return host;
    }

    @JsonProperty("host")
    public void setHost(String host) {
        this.host = host;
    }

    @JsonProperty("namespace_name")
    public String getNamespaceName() {
        return namespaceName;
    }

    @JsonProperty("namespace_name")
    public void setNamespaceName(String namespaceName) {
        this.namespaceName = namespaceName;
    }

    @JsonProperty("pod_id")
    public String getPodId() {
        return podId;
    }

    @JsonProperty("pod_id")
    public void setPodId(String podId) {
        this.podId = podId;
    }

    @JsonProperty("pod_name")
    public String getPodName() {
        return podName;
    }

    @JsonProperty("pod_name")
    public void setPodName(String podName) {
        this.podName = podName;
    }

    @JsonProperty("labels")
    public Labels getLabels() {
        return labels;
    }

    @JsonProperty("labels")
    public void setLabels(Labels labels) {
        this.labels = labels;
    }

    @JsonProperty("namespace_labels")
    public NamespaceLabels getNamespaceLabels() {
        return namespaceLabels;
    }

    @JsonProperty("namespace_labels")
    public void setNamespaceLabels(NamespaceLabels namespaceLabels) {
        this.namespaceLabels = namespaceLabels;
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
