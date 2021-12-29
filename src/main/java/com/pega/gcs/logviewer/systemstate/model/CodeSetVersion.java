
package com.pega.gcs.logviewer.systemstate.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pega.gcs.fringecommon.guiutilities.datatable.IndexEntry;

public class CodeSetVersion extends IndexEntry implements Comparable<CodeSetVersion> {

    @JsonProperty("CodeSetName")
    private String codeSetName;

    @JsonProperty("CodesetVersion")
    private String codesetVersion;

    @JsonProperty("ClassCount")
    private Integer classCount;

    public CodeSetVersion() {
        super();
    }

    public String getCodeSetName() {
        return codeSetName;
    }

    public String getCodesetVersion() {
        return codesetVersion;
    }

    public Integer getClassCount() {
        return classCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(classCount, codeSetName, codesetVersion);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CodeSetVersion)) {
            return false;
        }
        CodeSetVersion other = (CodeSetVersion) obj;
        return Objects.equals(classCount, other.classCount) && Objects.equals(codeSetName, other.codeSetName)
                && Objects.equals(codesetVersion, other.codesetVersion);
    }

    @Override
    public String toString() {
        return "CodeSetVersion [codeSetName=" + codeSetName + ", codesetVersion=" + codesetVersion + ", classCount="
                + classCount + "]";
    }

    @Override
    public int compareTo(CodeSetVersion other) {

        int compare = this.codeSetName.compareTo(other.codeSetName);

        if (compare == 0) {
            compare = this.codesetVersion.compareTo(other.codesetVersion);
        }

        if (compare == 0) {
            compare = this.classCount.compareTo(other.classCount);
        }

        return compare;
    }

}
