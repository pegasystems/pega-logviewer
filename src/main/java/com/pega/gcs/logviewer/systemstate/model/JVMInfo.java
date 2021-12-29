
package com.pega.gcs.logviewer.systemstate.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pega.gcs.fringecommon.utilities.KeyValuePair;

public class JVMInfo {

    private static final String JVM_NODE_TYPE = "-DNodeType";

    private static final String JVM_HEAP_YOUNG_GEN = "-Xmn";

    private static final String JVM_HEAP_INITIAL_SIZE = "-Xms";

    private static final String JVM_HEAP_MAXIMUM_SIZE = "-Xmx";

    private static final String JVM_THREAD_STACK_SIZE = "-Xss";

    private static final String JVM_SECURITY_EGD = "-Djava.security.egd";

    private static final String JVM_PERM_SIZE = "-XX:PermSize";

    private static final String JVM_MAX_PERM_SIZE = "-XX:MaxPermSize";

    private static final String JVM_WLS_OIF_SERIALFILTER = "-Dweblogic.oif.serialFilter";

    private static final String JVM_WLS_OIF_SERIALFILTERMODE = "-Dweblogic.oif.serialFilterMode";

    @JsonProperty("VMName")
    private String vmName;

    @JsonProperty("VMVendor")
    private String vmVendor;

    @JsonProperty("VMVersion")
    private String vmVersion;

    @JsonProperty("TempDir")
    private String tempDir;

    @JsonProperty("VMArguments")
    private List<String> vmArguments;

    @JsonProperty("PZ_ERROR")
    private String pzError;

    private HashMap<String, String> vmOptionNameMap;

    private AppServer appServer;

    public JVMInfo() {
        super();
    }

    public String getVmName() {
        return vmName;
    }

    public String getVmVendor() {
        return vmVendor;
    }

    public String getVmVersion() {
        return vmVersion;
    }

    public String getTempDir() {
        return tempDir;
    }

    public List<String> getVmArguments() {
        return (vmArguments != null) ? Collections.unmodifiableList(vmArguments) : null;
    }

    public String getPzError() {
        return pzError;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tempDir, vmArguments, vmName, vmVendor, vmVersion);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof JVMInfo)) {
            return false;
        }

        JVMInfo other = (JVMInfo) obj;
        return Objects.equals(tempDir, other.tempDir) && Objects.equals(vmArguments, other.vmArguments)
                && Objects.equals(vmName, other.vmName) && Objects.equals(vmVendor, other.vmVendor)
                && Objects.equals(vmVersion, other.vmVersion);
    }

    public void postProcess(String nodeId, AnalysisMarkerListNodeMap analysisMarkerListNodeMap) {

        if (vmArguments != null) {

            // sort the vm args
            Collections.sort(vmArguments);

            vmOptionNameMap = new HashMap<>();

            for (String vmArg : vmArguments) {

                if (vmArg.startsWith("-D")) {

                    int splitIndex = vmArg.indexOf("=");

                    String optionName;
                    String value;

                    if (splitIndex != -1) {
                        optionName = vmArg.substring(0, splitIndex);
                        value = vmArg.substring(splitIndex + 1);

                    } else {
                        optionName = vmArg;
                        value = null;
                    }

                    String key = optionName.toLowerCase();

                    addToMap(optionName, key, value, nodeId, analysisMarkerListNodeMap);

                    if (appServer == null) {
                        appServer = AppServer.getAppServer(optionName);
                    }

                } else if (vmArg.startsWith("-XX:")) {

                    String optionStr = vmArg.substring(4);
                    String optionName;
                    String key;
                    String value;

                    if (optionStr.startsWith("+")) {

                        optionName = vmArg;
                        key = optionStr.substring(1);
                        value = Boolean.TRUE.toString();

                    } else if (optionStr.startsWith("-")) {

                        optionName = vmArg;
                        key = optionStr.substring(1);
                        value = Boolean.FALSE.toString();

                    } else {

                        int splitIndex = vmArg.indexOf("=");

                        if (splitIndex != -1) {
                            optionName = vmArg.substring(0, splitIndex);
                            key = optionName;
                            value = vmArg.substring(splitIndex + 1);
                        } else {
                            optionName = vmArg;
                            key = vmArg;
                            value = null;
                        }
                    }

                    addToMap(optionName, key, value, nodeId, analysisMarkerListNodeMap);

                } else if (vmArg.startsWith("-X")) {

                    String optionName;
                    String value = null;

                    if ((vmArg.startsWith("-Xmn")) || (vmArg.startsWith("-Xms")) || (vmArg.startsWith("-Xmx"))
                            || (vmArg.startsWith("-Xss"))) {

                        optionName = vmArg.substring(0, 4);
                        value = vmArg.substring(4);

                    } else {

                        int splitIndex = vmArg.indexOf(":");

                        if (splitIndex != -1) {
                            optionName = vmArg.substring(0, splitIndex);
                            value = vmArg.substring(splitIndex + 1);

                        } else {
                            optionName = vmArg;
                            value = null;
                        }
                    }

                    addToMap(optionName, optionName, value, nodeId, analysisMarkerListNodeMap);

                }
            }

            analyseJVMInfo(nodeId, analysisMarkerListNodeMap);
        }
    }

    private void addToMap(String optionName, String key, String value, String nodeId,
            AnalysisMarkerListNodeMap analysisMarkerListNodeMap) {

        // check duplicate
        String duplicateValue = vmOptionNameMap.get(optionName);

        if (duplicateValue != null) {

            String message = MessageFormat.format(AnalysisMarkerMessage.DUPLICATE_JVMOPTION, optionName, duplicateValue,
                    value);

            AnalysisMarker analysisMarker = new AnalysisMarker(nodeId, AnalysisMarkerCategory.JVMARG, message);

            analysisMarkerListNodeMap.addAnalysisMarker(analysisMarker);
        }

        vmOptionNameMap.put(key, value);
    }

    public KeyValuePair<String, String> getJvmOption(String optionName) {

        KeyValuePair<String, String> jvmOption = null;

        String key;

        if (optionName.startsWith("-D")) {
            key = optionName.toLowerCase();
        } else {
            key = optionName;
        }

        if (vmOptionNameMap != null) {

            if (vmOptionNameMap.containsKey(key)) {

                String value = vmOptionNameMap.get(key);

                jvmOption = new KeyValuePair<String, String>(key, value);
            }
        }

        return jvmOption;
    }

    public List<String> getNodeTypeList() {

        List<String> nodeTypeList = null;

        if (vmOptionNameMap != null) {

            String nodeTypeString = vmOptionNameMap.get(JVM_NODE_TYPE.toLowerCase());

            if (nodeTypeString != null) {

                nodeTypeList = new ArrayList<>();

                String[] nodeTypes = nodeTypeString.split(",");

                nodeTypeList.addAll(Arrays.asList(nodeTypes));
            }
        }

        return nodeTypeList;
    }

    public AppServer getAppServer() {
        return appServer;
    }

    public String getJvmHeapYoungGen() {
        return (vmOptionNameMap != null) ? vmOptionNameMap.get(JVM_HEAP_YOUNG_GEN) : null;
    }

    public String getJvmHeapInitialSize() {
        return (vmOptionNameMap != null) ? vmOptionNameMap.get(JVM_HEAP_INITIAL_SIZE) : null;
    }

    public String getJvmHeapMaximumSize() {
        return (vmOptionNameMap != null) ? vmOptionNameMap.get(JVM_HEAP_MAXIMUM_SIZE) : null;
    }

    public String getJvmThreadStackSize() {
        return (vmOptionNameMap != null) ? vmOptionNameMap.get(JVM_THREAD_STACK_SIZE) : null;
    }

    private void analyseJVMInfo(String nodeId, AnalysisMarkerListNodeMap analysisMarkerListNodeMap) {

        KeyValuePair<String, String> jvmOption;

        jvmOption = getJvmOption(JVMInfo.JVM_SECURITY_EGD);

        if (jvmOption == null) {

            AnalysisMarker analysisMarker = new AnalysisMarker(nodeId, AnalysisMarkerCategory.JVMARG_PERF,
                    AnalysisMarkerMessage.JVM_SECURITY_EGD);

            analysisMarkerListNodeMap.addAnalysisMarker(analysisMarker);
        }

        jvmOption = getJvmOption(JVMInfo.JVM_PERM_SIZE);

        if (jvmOption != null) {

            AnalysisMarker analysisMarker = new AnalysisMarker(nodeId, AnalysisMarkerCategory.JVMARG,
                    AnalysisMarkerMessage.JVM_PERM_SIZE);

            analysisMarkerListNodeMap.addAnalysisMarker(analysisMarker);
        }

        jvmOption = getJvmOption(JVMInfo.JVM_MAX_PERM_SIZE);

        if (jvmOption != null) {

            AnalysisMarker analysisMarker = new AnalysisMarker(nodeId, AnalysisMarkerCategory.JVMARG,
                    AnalysisMarkerMessage.JVM_MAX_PERM_SIZE);

            analysisMarkerListNodeMap.addAnalysisMarker(analysisMarker);
        }

        AppServer appServer = getAppServer();

        if (appServer == null) {

            String message = "Unable to detect Appserver.";

            AnalysisMarker analysisMarker = new AnalysisMarker(nodeId, AnalysisMarkerCategory.JVMARG_APPSERVER,
                    message);

            analysisMarkerListNodeMap.addAnalysisMarker(analysisMarker);

        } else {
            switch (appServer) {
            case JBOSS:
                analyseJBoss(nodeId, analysisMarkerListNodeMap);
                break;
            case TOMCAT:
                analyseTomcat(nodeId, analysisMarkerListNodeMap);
                break;
            case WEBLOGIC:
                analyseWebLogic(nodeId, analysisMarkerListNodeMap);
                break;
            case WEBSPHERE:
                break;
            case WEBSPHERE_LIBERTY:
                break;
            case WILDFLY:
                break;
            default:
                break;

            }
        }

    }

    private void analyseTomcat(String nodeId, AnalysisMarkerListNodeMap analysisMarkerListNodeMap) {

    }

    private void analyseWebLogic(String nodeId, AnalysisMarkerListNodeMap analysisMarkerListNodeMap) {

        KeyValuePair<String, String> jvmOption;

        jvmOption = getJvmOption(JVMInfo.JVM_WLS_OIF_SERIALFILTER);

        if (jvmOption == null) {

            AnalysisMarker analysisMarker = new AnalysisMarker(nodeId, AnalysisMarkerCategory.JVMARG,
                    AnalysisMarkerMessage.JVM_WLS_OIF_SERIALFILTER);

            analysisMarkerListNodeMap.addAnalysisMarker(analysisMarker);
        }

        jvmOption = getJvmOption(JVMInfo.JVM_WLS_OIF_SERIALFILTERMODE);

        if (jvmOption == null) {

            AnalysisMarker analysisMarker = new AnalysisMarker(nodeId, AnalysisMarkerCategory.JVMARG,
                    AnalysisMarkerMessage.JVM_WLS_OIF_SERIALFILTERMODE);

            analysisMarkerListNodeMap.addAnalysisMarker(analysisMarker);
        }

    }

    private void analyseJBoss(String nodeId, AnalysisMarkerListNodeMap analysisMarkerListNodeMap) {

    }

}
