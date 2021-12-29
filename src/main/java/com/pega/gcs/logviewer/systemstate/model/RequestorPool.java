
package com.pega.gcs.logviewer.systemstate.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pega.gcs.fringecommon.guiutilities.datatable.IndexEntry;

public class RequestorPool extends IndexEntry implements Comparable<RequestorPool> {

    @JsonProperty("timeoutCount")
    private Long timeoutCount;

    @JsonProperty("idleCount")
    private Integer idleCount;

    @JsonProperty("maxIdleCount")
    private Integer maxIdleCount;

    @JsonProperty("mostIdleCount")
    private Integer mostIdleCount;

    @JsonProperty("maxWaitTime")
    private Long maxWaitTime;

    @JsonProperty("servicePackageInsName")
    private String servicePackageInsName;

    @JsonProperty("accessGroupName")
    private String accessGroupName;

    @JsonProperty("applicationInfo")
    private String applicationInfo;

    @JsonProperty("longestWaitTime")
    private Long longestWaitTime;

    @JsonProperty("maxActiveCount")
    private Integer maxActiveCount;

    @JsonProperty("mostActiveCount")
    private Integer mostActiveCount;

    @JsonProperty("activeCount")
    private Integer activeCount;

    @JsonProperty("lastAccessTime")
    private String lastAccessTime;

    @JsonProperty("servicePackageName")
    private String servicePackageName;

    @JsonProperty("accessGroups")
    private TreeSet<AccessGroupNameObject> accessGroups;

    private List<String> accessGroupList;

    public RequestorPool() {
    }

    public Long getTimeoutCount() {
        return timeoutCount;
    }

    public Integer getIdleCount() {
        return idleCount;
    }

    public Integer getMaxIdleCount() {
        return maxIdleCount;
    }

    public Integer getMostIdleCount() {
        return mostIdleCount;
    }

    public Long getMaxWaitTime() {
        return maxWaitTime;
    }

    public String getServicePackageInsName() {
        return servicePackageInsName;
    }

    public String getAccessGroupName() {
        return accessGroupName;
    }

    public String getApplicationInfo() {
        return applicationInfo;
    }

    public Long getLongestWaitTime() {
        return longestWaitTime;
    }

    public Integer getMaxActiveCount() {
        return maxActiveCount;
    }

    public Integer getMostActiveCount() {
        return mostActiveCount;
    }

    public Integer getActiveCount() {
        return activeCount;
    }

    public String getLastAccessTime() {
        return lastAccessTime;
    }

    public String getServicePackageName() {
        return servicePackageName;
    }

    @Override
    public int compareTo(RequestorPool other) {
        return getServicePackageInsName().compareTo(other.getServicePackageInsName());
    }

    public List<String> getAccessGroupList() {

        if (accessGroupList == null) {

            accessGroupList = new ArrayList<>();

            for (AccessGroupNameObject accessGroupNameObject : accessGroups) {
                accessGroupList.add(accessGroupNameObject.getName());
            }

        }

        return accessGroupList;
    }

}
