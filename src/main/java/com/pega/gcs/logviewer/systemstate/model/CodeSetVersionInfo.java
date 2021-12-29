
package com.pega.gcs.logviewer.systemstate.model;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CodeSetVersionInfo {

    @JsonProperty("CodeSetVersions")
    private TreeSet<CodeSetVersion> codeSetVersionSet;

    @JsonProperty("PZ_ERROR")
    private String pzError;

    public CodeSetVersionInfo() {
    }

    public Set<CodeSetVersion> getCodeSetVersionSet() {
        return (codeSetVersionSet != null) ? Collections.unmodifiableSet(codeSetVersionSet) : null;
    }

    public String getPzError() {
        return pzError;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("CodeSetVersionInfo [");

        boolean first = true;

        for (CodeSetVersion codeSetVersion : codeSetVersionSet) {

            if (!first) {
                stringBuilder.append(",");
            }

            first = false;

            stringBuilder.append(codeSetVersion);

        }

        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    public void postProcess() {

        if (codeSetVersionSet != null) {

            AtomicInteger index = new AtomicInteger(0);

            for (CodeSetVersion codeSetVersion : codeSetVersionSet) {

                codeSetVersion.setIndex(index.incrementAndGet());
            }
        }
    }
}
