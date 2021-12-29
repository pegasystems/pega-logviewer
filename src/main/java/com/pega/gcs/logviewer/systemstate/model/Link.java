
package com.pega.gcs.logviewer.systemstate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Link {

    private String name;

    @JsonProperty("templated")
    private String templated;

    @JsonProperty("method")
    private String method;

    @JsonProperty("href")
    private String href;

    public Link() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplated() {
        return templated;
    }

    public String getMethod() {
        return method;
    }

    public String getHref() {
        return href;
    }

}
