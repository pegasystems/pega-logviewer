
package com.pega.gcs.logviewer.systemstate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NodeState implements NodeObject, Comparable<NodeState> {

    @JsonProperty("nodeId")
    private String nodeId;

    @JsonProperty("PRLogging")
    private PRLogging prLogging;

    @JsonProperty("PRConfig")
    private PRConfig prConfig;

    @JsonProperty("JVMInfo")
    private JVMInfo jvmInfo;

    @JsonProperty("OSInfo")
    private OSInfo osInfo;

    @JsonProperty("DatabaseInfo")
    private DatabaseInfo databaseInfo;

    private RequestorsResult requestorsResult;

    private CsvDataMap databaseClassReport;

    public NodeState() {
        super();
    }

    @Override
    public String getDisplayName() {
        return getNodeId();
    }

    public String getNodeId() {
        return nodeId;
    }

    public PRLogging getPrLogging() {
        return prLogging;
    }

    public PRConfig getPrConfig() {
        return prConfig;
    }

    public JVMInfo getJvmInfo() {
        return jvmInfo;
    }

    public OSInfo getOsInfo() {
        return osInfo;
    }

    public DatabaseInfo getDatabaseInfo() {
        return databaseInfo;
    }

    public RequestorsResult getRequestorsResult() {
        return requestorsResult;
    }

    public void setRequestorsResult(RequestorsResult requestorsResult) {
        this.requestorsResult = requestorsResult;
    }

    public CsvDataMap getDatabaseClassReport() {
        return databaseClassReport;
    }

    public void setDatabaseClassReport(CsvDataMap databaseClassReport) {
        this.databaseClassReport = databaseClassReport;
    }

    @Override
    public int compareTo(NodeState other) {
        return this.nodeId.compareTo(other.nodeId);
    }

    public boolean hasError() {

        boolean error;

        PRLogging prLogging = getPrLogging();
        PRConfig prConfig = getPrConfig();
        JVMInfo jvmInfo = getJvmInfo();
        OSInfo osInfo = getOsInfo();
        DatabaseInfo databaseInfo = getDatabaseInfo();

        boolean prLoggingError = prLogging.getPzError() != null;
        boolean prConfigError = prConfig.getPzError() != null;
        boolean jvmInfoError = jvmInfo.getPzError() != null;
        boolean osInfoError = osInfo.getPzError() != null;
        boolean databaseInfoError = databaseInfo.getPzError() != null;

        error = prLoggingError || prConfigError || jvmInfoError || osInfoError || databaseInfoError;

        return error;
    }

}
