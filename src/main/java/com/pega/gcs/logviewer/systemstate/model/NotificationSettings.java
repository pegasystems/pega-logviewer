
package com.pega.gcs.logviewer.systemstate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationSettings {

    @JsonProperty("notifyWhenProcessesAreDone")
    private Boolean notifyWhenProcessesAreDone;

    @JsonProperty("notifyOnChangeOfSearchHostNodes")
    private Boolean notifyOnChangeOfSearchHostNodes;

    public NotificationSettings() {
    }

    public Boolean getNotifyWhenProcessesAreDone() {
        return notifyWhenProcessesAreDone;
    }

    public Boolean getNotifyOnChangeOfSearchHostNodes() {
        return notifyOnChangeOfSearchHostNodes;
    }

}
