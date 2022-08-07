
package com.pega.gcs.logviewer.systemstate.model;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.dom4j.DocumentException;

import com.pega.gcs.fringecommon.log4j2.AbstractLog4j2Helper;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.KeyValuePair;

public class SystemState implements NodeObject {

    private static final AbstractLog4j2Helper LOG = new Log4j2Helper(SystemState.class);

    public static final String SYSTEMSTATE_ROOT_NODE_NAME = "System State";

    private String status;

    private String servedBy;

    private ClusterState clusterState;

    private Map<String, NodeState> nodeStateMap;

    private Set<SystemStateError> errorSet;

    private SearchState searchState;

    private Set<CsvDataMap> reportCsvDataMapSet;

    private DatastoreMetadata datastoreMetadata;

    private AnalysisMarkerListNodeMap analysisMarkerListNodeMap;

    public SystemState() {

        nodeStateMap = new TreeMap<>();
        errorSet = new TreeSet<>();

        reportCsvDataMapSet = new TreeSet<>();

        analysisMarkerListNodeMap = new AnalysisMarkerListNodeMap();
    }

    @Override
    public String getDisplayName() {
        return SYSTEMSTATE_ROOT_NODE_NAME;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServedBy() {
        return servedBy;
    }

    public void setServedBy(String servedBy) {
        this.servedBy = servedBy;
    }

    public void setClusterState(ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    public ClusterState getClusterState() {
        return clusterState;
    }

    public Collection<NodeState> getNodeStateCollection() {
        return Collections.unmodifiableCollection(nodeStateMap.values());
    }

    public Set<SystemStateError> getErrorSet() {
        return Collections.unmodifiableSet(errorSet);
    }

    public SearchState getSearchState() {
        return searchState;
    }

    public void setSearchState(SearchState searchState) {
        this.searchState = searchState;
    }

    public AnalysisMarkerListNodeMap getAnalysisMarkerListNodeMap() {
        return analysisMarkerListNodeMap;
    }

    public Set<CsvDataMap> getReportCsvDataMapSet() {
        return Collections.unmodifiableSet(reportCsvDataMapSet);
    }

    public DatastoreMetadata getDatastoreMetadata() {
        return datastoreMetadata;
    }

    public void setDatastoreMetadata(DatastoreMetadata datastoreMetadata) {
        this.datastoreMetadata = datastoreMetadata;
    }

    public NodeState getNodeState(String nodeId) {

        // if the nodeid contains OS specific chars the zip folder entry name get altered with '_'.
        // xxx.xxx.xx.xxx:8200 => xxx.xxx.xxx.xxx_8200
        NodeState nodeState = nodeStateMap.get(nodeId);

        if (nodeState == null) {

            for (Map.Entry<String, NodeState> entry : nodeStateMap.entrySet()) {

                String key = entry.getKey();

                String sanitisedNodeId = key.replaceAll("[\\/:*?\"<>|]", "_");

                if (sanitisedNodeId.equals(nodeId)) {
                    nodeState = entry.getValue();
                    break;
                }
            }
        }

        return nodeState;
    }

    public void addNodeState(NodeState nodeState) {

        if (nodeState != null) {
            String nodeId = nodeState.getNodeId();
            nodeStateMap.put(nodeId, nodeState);
        }

    }

    public boolean addError(SystemStateError error) {

        boolean success = false;

        if (error != null) {
            success = errorSet.add(error);
        }

        return success;
    }

    public void addReportCsvDataMap(CsvDataMap csvDataMap) {
        reportCsvDataMapSet.add(csvDataMap);
    }

    public void postProcess() {

        LOG.info("postProcess - Start");

        if (clusterState != null) {
            clusterState.postProcess();
        }

        if (searchState != null) {
            searchState.postProcess();
        }

        if (datastoreMetadata != null) {
            datastoreMetadata.postProcess();
        }

        for (Map.Entry<String, NodeState> entry : nodeStateMap.entrySet()) {

            NodeState nodeState = entry.getValue();

            postProcessNodeState(nodeState, analysisMarkerListNodeMap);
        }

        // index the errorSet
        AtomicInteger index = new AtomicInteger(0);

        for (SystemStateError systemStateError : errorSet) {

            systemStateError.setIndex(index.incrementAndGet());
        }

        // analysis
        if (clusterState != null) {
            analysePRConfig();
        }

        analyseNodeStateJvmArg();

        // index the analysisMarkerList
        analysisMarkerListNodeMap.regenrateIndexes();

        LOG.info("postProcess - End");

    }

    // using a helper method instead of defining within pojo
    public void postProcessNodeState(NodeState nodeState, AnalysisMarkerListNodeMap analysisMarkerListNodeMap) {

        String nodeId = nodeState.getNodeId();
        LOG.info("postProcessNodeState - Start - NodeId: " + nodeId);

        PRLogging prLogging = nodeState.getPrLogging();
        PRConfig prConfig = nodeState.getPrConfig();
        JVMInfo jvmInfo = nodeState.getJvmInfo();
        OSInfo osInfo = nodeState.getOsInfo();
        DatabaseInfo databaseInfo = nodeState.getDatabaseInfo();
        RequestorsResult requestorsResult = nodeState.getRequestorsResult();

        if (prLogging != null) {
            prLogging.postProcess();
        }

        if (prConfig != null) {
            try {
                prConfig.postProcess();
            } catch (DocumentException e) {
                LOG.error("Error parsing prconfig data" + nodeId);
            }
        }

        if (jvmInfo != null) {
            jvmInfo.postProcess(nodeId, analysisMarkerListNodeMap);
        }

        if (osInfo != null) {
            osInfo.postProcess();
        }

        if (databaseInfo != null) {
            databaseInfo.postProcess();
        }

        if (requestorsResult != null) {
            requestorsResult.postProcess();
        }

        LOG.info("postProcessNodeState - End - NodeId: " + nodeId);

    }

    private void analysePRConfig() {

        List<KeyValuePair<String, String>> prConfigSettingList = clusterState.getDassInfo().getPRConfigSettingList();

        for (Map.Entry<String, NodeState> entry : nodeStateMap.entrySet()) {

            String nodeId = entry.getKey();
            NodeState nodeState = entry.getValue();

            PRConfig prConfig = nodeState.getPrConfig();

            if (prConfig != null) {
                List<KeyValuePair<String, String>> nodeSettingList = prConfig.getSettingList();

                for (KeyValuePair<String, String> nodeSetting : nodeSettingList) {

                    for (KeyValuePair<String, String> prConfigSetting : prConfigSettingList) {

                        String prConfigKey = prConfigSetting.getKey();
                        String key = nodeSetting.getKey();

                        if (prConfigKey.equalsIgnoreCase(key)) {

                            String prConfigValue = prConfigSetting.getValue();
                            String value = nodeSetting.getValue();
                            String message = MessageFormat.format(AnalysisMarkerMessage.OVERRIDE_PRCONFIG, prConfigKey,
                                    prConfigValue, value);

                            AnalysisMarker analysisMarker = new AnalysisMarker(nodeId, AnalysisMarkerCategory.PRCONFIG,
                                    message);

                            analysisMarkerListNodeMap.addAnalysisMarker(analysisMarker);
                        }
                    }
                }
            } else {

                String message = "PRConfig info not found.";

                AnalysisMarker analysisMarker = new AnalysisMarker(nodeId, AnalysisMarkerCategory.PRCONFIG, message);

                analysisMarkerListNodeMap.addAnalysisMarker(analysisMarker);
            }
        }
    }

    private void analyseNodeStateJvmArg() {

        for (Map.Entry<String, NodeState> entry : nodeStateMap.entrySet()) {

            String nodeId = entry.getKey();
            NodeState nodeState = entry.getValue();

            JVMInfo jvmInfo = nodeState.getJvmInfo();

            if (jvmInfo == null) {

                String message = "JVM info not found.";

                AnalysisMarker analysisMarker = new AnalysisMarker(nodeId, AnalysisMarkerCategory.JVMARG, message);

                analysisMarkerListNodeMap.addAnalysisMarker(analysisMarker);
            }

        }
    }
}
