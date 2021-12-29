
package com.pega.gcs.logviewer.systemstate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SecuritySettings {

    @JsonProperty("encryptionSearchInterNodeCommunications")
    private Boolean encryptionSearchInterNodeCommunications;

    @JsonProperty("displayPropertiesWithAccessControlPolicies")
    private Boolean displayPropertiesWithAccessControlPolicies;

    @JsonProperty("customizeFullTextSearch")
    private Boolean customizeFullTextSearch;

    public SecuritySettings() {
    }

    public Boolean getEncryptionSearchInterNodeCommunications() {
        return encryptionSearchInterNodeCommunications;
    }

    public Boolean getDisplayPropertiesWithAccessControlPolicies() {
        return displayPropertiesWithAccessControlPolicies;
    }

    public Boolean getCustomizeFullTextSearch() {
        return customizeFullTextSearch;
    }

}
