
package com.pega.gcs.logviewer.systemstate.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OSInfo implements Comparable<OSInfo> {

    @JsonProperty("OSArchitecture")
    private String osArchitecture;

    @JsonProperty("OSName")
    private String osName;

    @JsonProperty("OSVersion")
    private String osVersion;

    @JsonProperty("FileSeparator")
    private String fileSeparator;

    @JsonProperty("FileEncoding")
    private String fileEncoding;

    @JsonProperty("LineSeparator")
    private String lineSeparator;

    @JsonProperty("PathSeparator")
    private String pathSeparator;

    @JsonProperty("PZ_ERROR")
    private String pzError;

    public OSInfo() {
        super();
    }

    public String getOsArchitecture() {
        return osArchitecture;
    }

    public String getOsName() {
        return osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getFileSeparator() {
        return fileSeparator;
    }

    public String getFileEncoding() {
        return fileEncoding;
    }

    public String getLineSeparator() {
        return lineSeparator;
    }

    public String getPathSeparator() {
        return pathSeparator;
    }

    public String getPzError() {
        return pzError;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileEncoding, fileSeparator, lineSeparator, osArchitecture, osName, osVersion,
                pathSeparator);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OSInfo)) {
            return false;
        }
        OSInfo other = (OSInfo) obj;
        return Objects.equals(fileEncoding, other.fileEncoding) && Objects.equals(fileSeparator, other.fileSeparator)
                && Objects.equals(lineSeparator, other.lineSeparator)
                && Objects.equals(osArchitecture, other.osArchitecture) && Objects.equals(osName, other.osName)
                && Objects.equals(osVersion, other.osVersion) && Objects.equals(pathSeparator, other.pathSeparator);
    }

    @Override
    public String toString() {
        return "OSInfo [osArchitecture=" + osArchitecture + ", osName=" + osName + ", osVersion=" + osVersion
                + ", fileSeparator=" + fileSeparator + ", fileEncoding=" + fileEncoding + ", lineSeparator="
                + lineSeparator + ", pathSeparator=" + pathSeparator + "]";
    }

    @Override
    public int compareTo(OSInfo other) {

        int compare = this.osArchitecture.compareTo(other.osArchitecture);

        if (compare == 0) {
            compare = this.osName.compareTo(other.osName);
        }

        if (compare == 0) {
            compare = this.osVersion.compareTo(other.osVersion);
        }

        if (compare == 0) {
            compare = this.fileSeparator.compareTo(other.fileSeparator);
        }

        if (compare == 0) {
            compare = this.fileEncoding.compareTo(other.fileEncoding);
        }

        if (compare == 0) {
            compare = this.lineSeparator.compareTo(other.lineSeparator);
        }

        if (compare == 0) {
            compare = this.pathSeparator.compareTo(other.pathSeparator);
        }

        return compare;
    }

    public void postProcess() {

    }
}
