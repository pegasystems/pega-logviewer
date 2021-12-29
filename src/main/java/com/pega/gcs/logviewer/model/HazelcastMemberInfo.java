
package com.pega.gcs.logviewer.model;

import java.util.Objects;

import com.pega.gcs.fringecommon.guiutilities.datatable.IndexEntry;

public class HazelcastMemberInfo extends IndexEntry implements Comparable<HazelcastMemberInfo> {

    private String name;

    private String hostname;

    private String clusterAddress;

    private String uuid;

    private String operatingMode;

    private boolean errorMarker;

    public HazelcastMemberInfo(String name, String hostname, String clusterAddress, String uuid, String operatingMode) {
        super();
        this.name = name;
        this.hostname = hostname;
        this.clusterAddress = clusterAddress;
        this.uuid = uuid;
        this.operatingMode = operatingMode;
    }

    public String getName() {
        return name;
    }

    public String getHostname() {
        return hostname;
    }

    public String getClusterAddress() {
        return clusterAddress;
    }

    public String getUuid() {
        return uuid;
    }

    public String getOperatingMode() {
        return operatingMode;
    }

    public boolean isErrorMarker() {
        return errorMarker;
    }

    public void setErrorMarker(boolean errorMarker) {
        this.errorMarker = errorMarker;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HazelcastMemberInfo)) {
            return false;
        }
        HazelcastMemberInfo other = (HazelcastMemberInfo) obj;
        return Objects.equals(uuid, other.uuid);
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append("Member: [");

        if (null == getUuid()) {
            builder.append("]");
            return builder.toString();
        }

        builder.append("name=").append(getName()).append(", ");

        builder.append("address=");
        builder.append(hostname);
        builder.append("/");

        builder.append(getClusterAddress()).append(", ");

        builder.append("uuid=").append(getUuid()).append(", ");
        builder.append("mode=").append(getOperatingMode());

        builder.append("]");

        return builder.toString();
    }

    @Override
    public int compareTo(HazelcastMemberInfo other) {
        return getName().compareTo(other.getName());
    }
}
