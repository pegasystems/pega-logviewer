
package com.pega.gcs.logviewer.systemstate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessGroupNameObject implements Comparable<AccessGroupNameObject> {

    @JsonProperty("name")
    private String name;

    public AccessGroupNameObject() {
        super();
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(AccessGroupNameObject other) {
        return getName().compareTo(other.getName());
    }

}