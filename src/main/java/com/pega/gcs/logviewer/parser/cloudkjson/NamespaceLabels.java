
package com.pega.gcs.logviewer.parser.cloudkjson;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NamespaceLabels {

    @JsonProperty("created-by")
    private String createdBy;

    @JsonProperty("istio-injection")
    private String istioInjection;

    @JsonProperty("ps/resource-type")
    private String psResourceType;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("created-by")
    public String getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("created-by")
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("istio-injection")
    public String getIstioInjection() {
        return istioInjection;
    }

    @JsonProperty("istio-injection")
    public void setIstioInjection(String istioInjection) {
        this.istioInjection = istioInjection;
    }

    @JsonProperty("ps/resource-type")
    public String getPsResourceType() {
        return psResourceType;
    }

    @JsonProperty("ps/resource-type")
    public void setPsResourceType(String psResourceType) {
        this.psResourceType = psResourceType;
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
