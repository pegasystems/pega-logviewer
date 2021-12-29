
package com.pega.gcs.logviewer.systemstate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuerySettings {

    @JsonProperty("fuzzySearchEnabled")
    private Boolean fuzzySearchEnabled;

    @JsonProperty("degreeOfFuzziness")
    private String degreeOfFuzziness;

    @JsonProperty("fuzzyPrefixLength")
    private Integer fuzzyPrefixLength;

    @JsonProperty("maxExpansionTerms")
    private Integer maxExpansionTerms;

    public QuerySettings() {
    }

    public Boolean getFuzzySearchEnabled() {
        return fuzzySearchEnabled;
    }

    public String getDegreeOfFuzziness() {
        return degreeOfFuzziness;
    }

    public Integer getFuzzyPrefixLength() {
        return fuzzyPrefixLength;
    }

    public Integer getMaxExpansionTerms() {
        return maxExpansionTerms;
    }

}
