
package com.pega.gcs.logviewer.systemstate.model;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RuleSetVersionInfo {

    @JsonProperty("RuleSetVersions")
    private TreeSet<RuleSetVersion> ruleSetVersionSet;

    @JsonProperty("PZ_ERROR")
    private String pzError;

    public RuleSetVersionInfo() {
    }

    public Set<RuleSetVersion> getRuleSetVersionSet() {
        return (ruleSetVersionSet != null) ? Collections.unmodifiableSet(ruleSetVersionSet) : null;
    }

    public String getPzError() {
        return pzError;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("RuleSetVersionInfo [");

        boolean first = true;

        for (RuleSetVersion ruleSetVersion : ruleSetVersionSet) {

            if (!first) {
                stringBuilder.append(",");
            }

            first = false;

            stringBuilder.append(ruleSetVersion);

        }

        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    public void postProcess() {

        if (ruleSetVersionSet != null) {

            AtomicInteger index = new AtomicInteger(0);

            for (RuleSetVersion ruleSetVersion : ruleSetVersionSet) {

                ruleSetVersion.setIndex(index.incrementAndGet());
            }
        }
    }
}
