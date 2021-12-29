
package com.pega.gcs.logviewer.systemstate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClusterState implements NodeObject {

    private static final String CLUSTERSTATE_NODE_NAME = "Cluster State";

    @JsonProperty("RuleSetVersionInfo")
    private RuleSetVersionInfo ruleSetVersionInfo;

    @JsonProperty("CodeSetVersionInfo")
    private CodeSetVersionInfo codeSetVersionInfo;

    @JsonProperty("DASSInfo")
    private DSSInfo dassInfo;

    public ClusterState() {
        super();
    }

    @Override
    public String getDisplayName() {
        return CLUSTERSTATE_NODE_NAME;
    }

    public RuleSetVersionInfo getRuleSetVersionInfo() {
        return ruleSetVersionInfo;
    }

    public CodeSetVersionInfo getCodeSetVersionInfo() {
        return codeSetVersionInfo;
    }

    public DSSInfo getDassInfo() {
        return dassInfo;
    }

    public void postProcess() {

        if (ruleSetVersionInfo != null) {
            ruleSetVersionInfo.postProcess();
        }

        if (codeSetVersionInfo != null) {
            codeSetVersionInfo.postProcess();
        }

        if (dassInfo != null) {
            dassInfo.postProcess();
        }
    }

    public boolean hasError() {

        boolean error = false;

        RuleSetVersionInfo ruleSetVersionInfo = getRuleSetVersionInfo();
        CodeSetVersionInfo codeSetVersionInfo = getCodeSetVersionInfo();
        DSSInfo dassInfo = getDassInfo();

        boolean ruleSetVersionInfoError = ruleSetVersionInfo.getPzError() != null;
        boolean codeSetVersionInfoError = codeSetVersionInfo.getPzError() != null;
        boolean dassInfoError = dassInfo.getPzError() != null;

        error = ruleSetVersionInfoError || codeSetVersionInfoError || dassInfoError;

        return error;
    }
}
