
package com.pega.gcs.logviewer.systemstate.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pega.gcs.fringecommon.guiutilities.datatable.IndexEntry;

public class Setting extends IndexEntry implements Comparable<Setting> {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Value")
    private String value;

    private String rulesetName;

    private String settingName;

    public Setting() {
        super();
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getRulesetName() {
        return rulesetName;
    }

    public String getSettingName() {
        return settingName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Setting)) {
            return false;
        }
        Setting other = (Setting) obj;
        return Objects.equals(name, other.name) && Objects.equals(value, other.value);
    }

    @Override
    public String toString() {
        return "Setting [name=" + name + ", value=" + value + "]";
    }

    @Override
    public int compareTo(Setting other) {

        int compare = this.name.compareTo(other.name);

        if (compare == 0) {
            compare = this.value.compareTo(other.value);
        }

        return compare;
    }

    public void postProcess() {

        int splitIndex = name.indexOf("!");

        rulesetName = name.substring(0, splitIndex);
        settingName = name.substring(splitIndex + 1);
    }
}
