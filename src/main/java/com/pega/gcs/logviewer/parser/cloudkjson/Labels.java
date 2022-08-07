
package com.pega.gcs.logviewer.parser.cloudkjson;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Labels {

    @JsonProperty("app")
    private String app;

    @JsonProperty("environmentguid")
    private String environmentguid;

    @JsonProperty("environmenttype")
    private String environmenttype;

    @JsonProperty("istio.io/rev")
    private String istioIoRev;

    @JsonProperty("pega-cluster-member")
    private String pegaClusterMember;

    @JsonProperty("pega-rtdg-member")
    private String pegaRtdgMember;

    @JsonProperty("pod-template-hash")
    private String podTemplateHash;

    @JsonProperty("security.istio.io/tlsMode")
    private String securityIstioIoTlsMode;

    @JsonProperty("service.istio.io/canonical-name")
    private String serviceIstioIoCanonicalName;

    @JsonProperty("service.istio.io/canonical-revision")
    private String serviceIstioIoCanonicalRevision;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("app")
    public String getApp() {
        return app;
    }

    @JsonProperty("app")
    public void setApp(String app) {
        this.app = app;
    }

    @JsonProperty("environmentguid")
    public String getEnvironmentguid() {
        return environmentguid;
    }

    @JsonProperty("environmentguid")
    public void setEnvironmentguid(String environmentguid) {
        this.environmentguid = environmentguid;
    }

    @JsonProperty("environmenttype")
    public String getEnvironmenttype() {
        return environmenttype;
    }

    @JsonProperty("environmenttype")
    public void setEnvironmenttype(String environmenttype) {
        this.environmenttype = environmenttype;
    }

    @JsonProperty("istio.io/rev")
    public String getIstioIoRev() {
        return istioIoRev;
    }

    @JsonProperty("istio.io/rev")
    public void setIstioIoRev(String istioIoRev) {
        this.istioIoRev = istioIoRev;
    }

    @JsonProperty("pega-cluster-member")
    public String getPegaClusterMember() {
        return pegaClusterMember;
    }

    @JsonProperty("pega-cluster-member")
    public void setPegaClusterMember(String pegaClusterMember) {
        this.pegaClusterMember = pegaClusterMember;
    }

    @JsonProperty("pega-rtdg-member")
    public String getPegaRtdgMember() {
        return pegaRtdgMember;
    }

    @JsonProperty("pega-rtdg-member")
    public void setPegaRtdgMember(String pegaRtdgMember) {
        this.pegaRtdgMember = pegaRtdgMember;
    }

    @JsonProperty("pod-template-hash")
    public String getPodTemplateHash() {
        return podTemplateHash;
    }

    @JsonProperty("pod-template-hash")
    public void setPodTemplateHash(String podTemplateHash) {
        this.podTemplateHash = podTemplateHash;
    }

    @JsonProperty("security.istio.io/tlsMode")
    public String getSecurityIstioIoTlsMode() {
        return securityIstioIoTlsMode;
    }

    @JsonProperty("security.istio.io/tlsMode")
    public void setSecurityIstioIoTlsMode(String securityIstioIoTlsMode) {
        this.securityIstioIoTlsMode = securityIstioIoTlsMode;
    }

    @JsonProperty("service.istio.io/canonical-name")
    public String getServiceIstioIoCanonicalName() {
        return serviceIstioIoCanonicalName;
    }

    @JsonProperty("service.istio.io/canonical-name")
    public void setServiceIstioIoCanonicalName(String serviceIstioIoCanonicalName) {
        this.serviceIstioIoCanonicalName = serviceIstioIoCanonicalName;
    }

    @JsonProperty("service.istio.io/canonical-revision")
    public String getServiceIstioIoCanonicalRevision() {
        return serviceIstioIoCanonicalRevision;
    }

    @JsonProperty("service.istio.io/canonical-revision")
    public void setServiceIstioIoCanonicalRevision(String serviceIstioIoCanonicalRevision) {
        this.serviceIstioIoCanonicalRevision = serviceIstioIoCanonicalRevision;
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
