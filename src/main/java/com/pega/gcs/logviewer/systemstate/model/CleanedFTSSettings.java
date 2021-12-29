
package com.pega.gcs.logviewer.systemstate.model;

import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CleanedFTSSettings {

    @JsonProperty("searchIndexHostNodeSettings")
    private TreeSet<HostNodeSetting> searchIndexHostNodeSettingSet;

    @JsonProperty("automatedSearchAlertsSettings")
    private AutomatedSearchAlertsSettings automatedSearchAlertsSettings;

    @JsonProperty("notificationSettings")
    private NotificationSettings notificationSettings;

    @JsonProperty("querySettings")
    private QuerySettings querySettings;

    @JsonProperty("securitySettings")
    private SecuritySettings securitySettings;

    @JsonProperty("fullSettings")
    private FTSSettings fullSettings;

    public CleanedFTSSettings() {
    }

    public TreeSet<HostNodeSetting> getSearchIndexHostNodeSettingSet() {
        return searchIndexHostNodeSettingSet;
    }

    public AutomatedSearchAlertsSettings getAutomatedSearchAlertsSettings() {
        return automatedSearchAlertsSettings;
    }

    public NotificationSettings getNotificationSettings() {
        return notificationSettings;
    }

    public QuerySettings getQuerySettings() {
        return querySettings;
    }

    public SecuritySettings getSecuritySettings() {
        return securitySettings;
    }

    public FTSSettings getFullSettings() {
        return fullSettings;
    }

    public void postProcess() {

        if (searchIndexHostNodeSettingSet != null) {

            AtomicInteger index = new AtomicInteger(0);

            for (HostNodeSetting hostNodeSetting : searchIndexHostNodeSettingSet) {
                hostNodeSetting.setIndex(index.incrementAndGet());
            }

        }
    }
}
