
package com.pega.gcs.logviewer.systemstate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AutomatedSearchAlertsSettings {

    @JsonProperty("automaticallyMonitorFiles")
    private Boolean automaticallyMonitorFiles;

    @JsonProperty("monitoringFrequency")
    private String monitoringFrequency;

    public AutomatedSearchAlertsSettings() {
    }

    public Boolean getAutomaticallyMonitorFiles() {
        return automaticallyMonitorFiles;
    }

    public String getMonitoringFrequency() {
        return monitoringFrequency;
    }

}
