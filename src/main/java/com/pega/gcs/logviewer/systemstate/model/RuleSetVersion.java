
package com.pega.gcs.logviewer.systemstate.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pega.gcs.fringecommon.guiutilities.datatable.IndexEntry;

public class RuleSetVersion extends IndexEntry implements Comparable<RuleSetVersion> {

    @JsonProperty("RuleSetName")
    private String ruleSetName;

    @JsonProperty("RuleSetVersion")
    private String ruleSetVersion;

    @JsonProperty("IsRuleSetVersionLocked")
    private Boolean isRuleSetVersionLocked;

    @JsonProperty("RulesCount")
    private Integer rulesCount;

    @JsonProperty("LastModified")
    private String lastModified;

    public RuleSetVersion() {
        super();
    }

    public String getRuleSetName() {
        return ruleSetName;
    }

    public String getRuleSetVersion() {
        return ruleSetVersion;
    }

    public Boolean getIsRuleSetVersionLocked() {
        return isRuleSetVersionLocked;
    }

    public Integer getRulesCount() {
        return rulesCount;
    }

    public String getLastModified() {
        return lastModified;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isRuleSetVersionLocked, lastModified, ruleSetName, ruleSetVersion, rulesCount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RuleSetVersion)) {
            return false;
        }
        RuleSetVersion other = (RuleSetVersion) obj;
        return Objects.equals(isRuleSetVersionLocked, other.isRuleSetVersionLocked)
                && Objects.equals(lastModified, other.lastModified) && Objects.equals(ruleSetName, other.ruleSetName)
                && Objects.equals(ruleSetVersion, other.ruleSetVersion) && Objects.equals(rulesCount, other.rulesCount);
    }

    @Override
    public String toString() {
        return "RuleSetVersion [ruleSetName=" + ruleSetName + ", ruleSetVersion=" + ruleSetVersion
                + ", isRuleSetVersionLocked=" + isRuleSetVersionLocked + ", rulesCount=" + rulesCount
                + ", lastModified=" + lastModified + "]";
    }

    @Override
    public int compareTo(RuleSetVersion other) {

        int compare = this.ruleSetName.compareTo(other.ruleSetName);

        if (compare == 0) {
            compare = this.ruleSetVersion.compareTo(other.ruleSetVersion);
        }

        if (compare == 0) {
            compare = this.isRuleSetVersionLocked.compareTo(other.isRuleSetVersionLocked);
        }

        if (compare == 0) {
            compare = this.rulesCount.compareTo(other.rulesCount);
        }

        if (compare == 0) {
            compare = this.lastModified.compareTo(other.lastModified);
        }
        return compare;
    }

}
