
package com.pega.gcs.logviewer.systemstate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.SwingWorker;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.ProgressTaskInfo;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.fringecommon.utilities.GeneralUtilities;
import com.pega.gcs.logviewer.systemstate.model.ArchiveEntryByteData;
import com.pega.gcs.logviewer.systemstate.model.ClusterState;
import com.pega.gcs.logviewer.systemstate.model.CsvDataMap;
import com.pega.gcs.logviewer.systemstate.model.CsvDataSortComparator;
import com.pega.gcs.logviewer.systemstate.model.DatastoreMetadata;
import com.pega.gcs.logviewer.systemstate.model.Link;
import com.pega.gcs.logviewer.systemstate.model.NodeState;
import com.pega.gcs.logviewer.systemstate.model.RequestorPool;
import com.pega.gcs.logviewer.systemstate.model.RequestorsResult;
import com.pega.gcs.logviewer.systemstate.model.SearchState;
import com.pega.gcs.logviewer.systemstate.model.SystemState;
import com.pega.gcs.logviewer.systemstate.model.SystemStateByteDataModel;
import com.pega.gcs.logviewer.systemstate.model.SystemStateError;

public class SystemStateFileLoadTask extends SwingWorker<SystemState, ProgressTaskInfo> {

    private static final Log4j2Helper LOG = new Log4j2Helper(SystemStateFileLoadTask.class);

    private static final String SYSTEM_STATE_JSON_REGEX = "SystemState-(.*?)_(.*?)";

    private static final String NODE_FOLDER_REGEX = "(?:node-|reports)(.*?)";

    private static final String REPORTS_CSV_FILE_REGEX = "(.*?)_(.*?)";

    private static final String DATABASE_CLASS_REPORT = "DatabaseClassReport";

    private static final String DATABASE_INDICES_REPORT = "DatabaseIndicesReport";

    private static final String DATABASE_TABLE_REPORT = "DatabaseTableReport";

    private static final String DATABASE_NAME_REPORT = "DatabaseNameReport";

    private RecentFile recentFile;

    private ModalProgressMonitor progressMonitor;

    private Pattern systemStateJsonPattern;

    private Pattern nodeFolderPattern;

    private Pattern reportsCsvFilePattern;

    public SystemStateFileLoadTask(RecentFile recentFile, ModalProgressMonitor progressMonitor) {

        super();

        this.recentFile = recentFile;
        this.progressMonitor = progressMonitor;

        systemStateJsonPattern = Pattern.compile(SYSTEM_STATE_JSON_REGEX, Pattern.CASE_INSENSITIVE);
        nodeFolderPattern = Pattern.compile(NODE_FOLDER_REGEX, Pattern.CASE_INSENSITIVE);
        reportsCsvFilePattern = Pattern.compile(REPORTS_CSV_FILE_REGEX, Pattern.CASE_INSENSITIVE);

    }

    public ModalProgressMonitor getProgressMonitor() {
        return progressMonitor;
    }

    @Override
    protected SystemState doInBackground() throws Exception {

        SystemState systemState = null;

        // ModalProgressMonitor progressMonitor = getProgressMonitor();

        ProgressTaskInfo progressTaskInfo = new ProgressTaskInfo(0, 0);

        publish(progressTaskInfo);

        String filePath = recentFile.getPath();

        String fileExt = FileUtilities.getExtension(filePath);

        if ("zip".equalsIgnoreCase(fileExt)) {

            // collect zipEntries
            try (ZipFile zipFile = new ZipFile(filePath)) {

                SystemStateByteDataModel systemStateByteDataModel = null;

                // check if the zip contains 7zip package or not
                byte[] sevenzByteArray = null;

                Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();

                while (zipEntries.hasMoreElements()) {

                    ZipEntry zipEntry = zipEntries.nextElement();

                    String zipEntryName = zipEntry.getName();

                    String zipEntryExt = FileUtilities.getExtension(zipEntryName);

                    if ("7z".equalsIgnoreCase(zipEntryExt)) {

                        sevenzByteArray = getByteArrayFromZipEntry(zipFile, zipEntry);
                        break;
                    }
                }

                if (sevenzByteArray != null) {
                    systemStateByteDataModel = get7ZipSystemStateByteDataModel(sevenzByteArray);
                } else {
                    systemStateByteDataModel = getZipSystemStateByteDataModel(zipFile);
                }

                if (systemStateByteDataModel != null) {

                    systemState = processSystemStateByteDataModel(systemStateByteDataModel);
                }
            }

        } else if ("json".equalsIgnoreCase(fileExt)) {

            try (FileInputStream fis = new FileInputStream(filePath)) {

                File file = new File(filePath);
                String filename = FileUtilities.getNameWithoutExtension(file);

                byte[] byteArray = new byte[fis.available()];
                fis.read(byteArray);

                ArchiveEntryByteData archiveEntryByteData;
                archiveEntryByteData = new ArchiveEntryByteData(filename, byteArray);

                systemState = getSystemStateJson(archiveEntryByteData);
            }

        }

        if (systemState != null) {
            systemState.postProcess();
        }

        return systemState;
    }

    @Override
    protected void process(List<ProgressTaskInfo> chunks) {

        if ((isDone()) || (isCancelled()) || (chunks == null) || (chunks.size() == 0)) {
            return;
        }

        Collections.sort(chunks);

        ProgressTaskInfo progressTaskInfo = chunks.get(chunks.size() - 1);

        int count = (int) progressTaskInfo.getCount();
        int totalCount = (int) progressTaskInfo.getTotal();

        int progress = 0;

        if (totalCount > 0) {
            progress = (int) ((count * 100) / totalCount);
        }

        String note = String.format("Loaded %d node states (%d%%)", count, progress);

        if (progressMonitor != null) {
            progressMonitor.setProgress(progress);
            progressMonitor.setNote(note);
        }
    }

    private SystemStateByteDataModel getZipSystemStateByteDataModel(ZipFile zipFile) throws IOException {

        SystemStateByteDataModel systemStateByteDataModel = new SystemStateByteDataModel();

        Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();

        while (zipEntries.hasMoreElements()) {

            ZipEntry zipEntry = zipEntries.nextElement();

            String zipEntryName = zipEntry.getName();

            if (!zipEntry.isDirectory()) {

                File file = new File(zipEntryName);

                String filename = FileUtilities.getNameWithoutExtension(file);

                Matcher systemStateJsonMatcher = systemStateJsonPattern.matcher(filename);

                if (systemStateJsonMatcher.matches()) {

                    byte[] systemStateJsonBytes = getByteArrayFromZipEntry(zipFile, zipEntry);

                    ArchiveEntryByteData systemStateJsonByteData;
                    systemStateJsonByteData = new ArchiveEntryByteData(filename, systemStateJsonBytes);

                    systemStateByteDataModel.setSystemStateJsonByteData(systemStateJsonByteData);

                } else {

                    File parentFile = file.getParentFile();

                    if (parentFile == null) {

                        byte[] byteArray = getByteArrayFromZipEntry(zipFile, zipEntry);

                        systemStateByteDataModel.addOtherEntryByte(filename, byteArray);

                    } else {

                        String parentFilename = file.getParentFile().getName();

                        Matcher nodeFolderMatcher = nodeFolderPattern.matcher(parentFilename);

                        if (nodeFolderMatcher.matches()) {

                            String nodeId = nodeFolderMatcher.group(1);

                            if ((nodeId == null) || ("".equals(nodeId))) {
                                nodeId = "reports";
                            }

                            byte[] byteArray = getByteArrayFromZipEntry(zipFile, zipEntry);

                            ArchiveEntryByteData archiveEntryByteData;
                            archiveEntryByteData = new ArchiveEntryByteData(filename, byteArray);

                            systemStateByteDataModel.addNodeEntryByte(nodeId, archiveEntryByteData);

                        }
                    }
                }
            }
        }

        return systemStateByteDataModel;

    }

    private SystemStateByteDataModel get7ZipSystemStateByteDataModel(byte[] byteArray) throws IOException {

        SystemStateByteDataModel systemStateByteDataModel = new SystemStateByteDataModel();

        SeekableInMemoryByteChannel seekableInMemoryByteChannel = new SeekableInMemoryByteChannel(byteArray);

        try (SevenZFile sevenZFile = new SevenZFile(seekableInMemoryByteChannel)) {

            SevenZArchiveEntry sevenZArchiveEntry = sevenZFile.getNextEntry();

            while (sevenZArchiveEntry != null) {

                String zipEntryName = sevenZArchiveEntry.getName();

                if (!sevenZArchiveEntry.isDirectory()) {

                    int length = (int) sevenZArchiveEntry.getSize();

                    byte[] sevenZArchiveEntryBytes = getByteArrayFromSevenZArchiveEntry(sevenZFile, length);

                    File file = new File(zipEntryName);

                    String filename = FileUtilities.getNameWithoutExtension(file);

                    Matcher systemStateJsonMatcher = systemStateJsonPattern.matcher(filename);

                    if (systemStateJsonMatcher.matches()) {

                        ArchiveEntryByteData systemStateJsonByteData;
                        systemStateJsonByteData = new ArchiveEntryByteData(filename, sevenZArchiveEntryBytes);

                        systemStateByteDataModel.setSystemStateJsonByteData(systemStateJsonByteData);

                    } else {

                        File parentFile = file.getParentFile();

                        if (parentFile == null) {

                            systemStateByteDataModel.addOtherEntryByte(filename, sevenZArchiveEntryBytes);

                        } else {

                            String parentFilename = file.getParentFile().getName();

                            Matcher nodeFolderMatcher = nodeFolderPattern.matcher(parentFilename);

                            if (nodeFolderMatcher.matches()) {

                                String nodeId = nodeFolderMatcher.group(1);

                                if ((nodeId == null) || ("".equals(nodeId))) {
                                    nodeId = "reports";
                                }

                                ArchiveEntryByteData archiveEntryByteData;
                                archiveEntryByteData = new ArchiveEntryByteData(filename, sevenZArchiveEntryBytes);

                                systemStateByteDataModel.addNodeEntryByte(nodeId, archiveEntryByteData);

                            }
                        }
                    }
                }

                sevenZArchiveEntry = sevenZFile.getNextEntry();
            }
        }

        return systemStateByteDataModel;
    }

    private SystemState processSystemStateByteDataModel(SystemStateByteDataModel systemStateByteDataModel)
            throws Exception {

        SystemState systemState = null;

        if (systemStateByteDataModel != null) {

            ArchiveEntryByteData systemStateJsonByteData = systemStateByteDataModel.getSystemStateJsonByteData();

            systemState = getSystemStateJson(systemStateJsonByteData);

            if (systemState != null) {

                // root level artifacts
                List<ArchiveEntryByteData> otherEntryByteDataList;
                otherEntryByteDataList = systemStateByteDataModel.getOtherEntryByteDataList();

                for (ArchiveEntryByteData archiveEntryByteData : otherEntryByteDataList) {

                    String key = archiveEntryByteData.getName();
                    byte[] byteArray = archiveEntryByteData.getDatabytes();

                    if (key.equals("SearchState")) {

                        processSearchState(byteArray, systemState);

                    } else if (key.equals("ClusterInfo")) {

                        processClusterInfo(byteArray, systemState);

                    } else if (key.startsWith("Datasources-")) {
                        // in Node system state json these file are in root level
                        @SuppressWarnings("unused")
                        NodeState nodeState = systemState.getNodeStateCollection().iterator().next();
                        // TODO

                    } else if (key.startsWith("Requestors-")) {
                        // in Node system state json these file are in root level
                        NodeState nodeState = systemState.getNodeStateCollection().iterator().next();

                        processRequestorsResult(byteArray, nodeState);

                    } else if (key.startsWith("DatastoreNodeMetadata")) {

                        processDatastoreNodeMetadata(byteArray, systemState);

                    }
                }

                // Nodes and reports folder level artifacts
                Map<String, List<ArchiveEntryByteData>> nodeEntryByteDataMap;
                nodeEntryByteDataMap = systemStateByteDataModel.getnodeEntryByteDataMap();

                for (String nodeId : nodeEntryByteDataMap.keySet()) {

                    List<ArchiveEntryByteData> nodeEntryByteDataList = nodeEntryByteDataMap.get(nodeId);

                    if (nodeId.equals("reports")) {

                        processReportsArtifacts(nodeEntryByteDataList, systemState, progressMonitor);

                    } else {

                        NodeState nodeState = systemState.getNodeState(nodeId);

                        if (nodeState != null) {
                            processNodeArtifacts(nodeEntryByteDataList, nodeState, progressMonitor);
                        } else {
                            String message = "NodeState node found for nodeId:" + nodeId;

                            throw new Exception(message);
                        }
                    }
                }
            }
        }

        return systemState;
    }

    private byte[] getByteArrayFromZipEntry(ZipFile zipFile, ZipEntry zipEntry) throws IOException {

        byte[] byteArray = null;

        try (InputStream is = zipFile.getInputStream(zipEntry)) {
            byteArray = IOUtils.toByteArray(is);
        }

        return byteArray;
    }

    private byte[] getByteArrayFromSevenZArchiveEntry(SevenZFile sevenZFile, int length) throws IOException {

        byte[] byteArray = new byte[length];

        sevenZFile.read(byteArray);

        return byteArray;
    }

    private SystemState getSystemStateJson(ArchiveEntryByteData systemStateJsonByteData)
            throws JsonProcessingException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        JsonNode jsonNode = objectMapper.readTree(systemStateJsonByteData.getDatabytes());

        SystemState systemState = new SystemState();

        JsonNode statusJsonNode = jsonNode.get("status");
        JsonNode servedByJsonNode = jsonNode.get("servedBy");
        JsonNode errorJsonNode = jsonNode.get("error");
        JsonNode dataJsonNode = jsonNode.get("data");

        if (statusJsonNode == null) {
            // possibly 7.3.1. different data structure
            statusJsonNode = jsonNode.get("Status");

            if (statusJsonNode != null) {
                LOG.error("7.3.1 system state json file. not supported yet");
            }
        } else {

            systemState.setStatus(statusJsonNode.textValue());
            systemState.setServedBy(servedByJsonNode.textValue());

            JsonNode nodeStateJsonNode = dataJsonNode.get("nodeState");

            if (nodeStateJsonNode.isArray()) {

                // cluster level system state
                JsonNode clusterStateJsonNode = dataJsonNode.get("clusterState");

                ClusterState clusterState = objectMapper.treeToValue(clusterStateJsonNode, ClusterState.class);

                systemState.setClusterState(clusterState);

                Iterator<JsonNode> nodeStateIt = nodeStateJsonNode.elements();

                while (nodeStateIt.hasNext()) {

                    JsonNode nsJsonNode = nodeStateIt.next();

                    NodeState nodeState = objectMapper.treeToValue(nsJsonNode, NodeState.class);

                    systemState.addNodeState(nodeState);
                }
            } else {
                // node system state
                NodeState nodeState = objectMapper.treeToValue(nodeStateJsonNode, NodeState.class);

                systemState.addNodeState(nodeState);
            }

            if (errorJsonNode.isArray()) {

                Iterator<JsonNode> errorIt = errorJsonNode.elements();

                while (errorIt.hasNext()) {

                    JsonNode errJsonNode = errorIt.next();

                    SystemStateError error = objectMapper.treeToValue(errJsonNode, SystemStateError.class);

                    systemState.addError(error);
                }
            }
        }

        return systemState;
    }

    private void processClusterInfo(byte[] byteArray, SystemState systemState) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(byteArray);

        ClusterState clusterState = objectMapper.treeToValue(jsonNode, ClusterState.class);

        systemState.setClusterState(clusterState);
    }

    private void processSearchState(byte[] byteArray, SystemState systemState) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(byteArray);

        SearchState searchState = objectMapper.treeToValue(jsonNode, SearchState.class);

        systemState.setSearchState(searchState);
    }

    private void processRequestorsResult(byte[] byteArray, NodeState nodeState) throws IOException {

        RequestorsResult requestorsResult = new RequestorsResult();

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(byteArray);

        JsonNode dataNode = jsonNode.get("data");

        JsonNode resultArrayNode = dataNode.get("result");

        Iterator<JsonNode> resultIt = resultArrayNode.elements();

        while (resultIt.hasNext()) {

            JsonNode requestorPoolsListArrayJsonNode = resultIt.next();

            Iterator<JsonNode> requestorPoolsListIt = requestorPoolsListArrayJsonNode.elements();

            while (requestorPoolsListIt.hasNext()) {

                JsonNode requestorPoolArrayJsonNode = requestorPoolsListIt.next();

                Iterator<JsonNode> requestorPoolArrayIt = requestorPoolArrayJsonNode.elements();

                while (requestorPoolArrayIt.hasNext()) {

                    JsonNode requestorPoolJsonNode = requestorPoolArrayIt.next();

                    RequestorPool requestorPool = objectMapper.treeToValue(requestorPoolJsonNode, RequestorPool.class);

                    requestorsResult.addRequestorPool(requestorPool);
                }
            }
        }

        JsonNode linksNode = dataNode.get("_links");

        String name;
        Link link;

        name = "self";
        JsonNode selfNode = linksNode.get(name);
        link = objectMapper.treeToValue(selfNode, Link.class);
        link.setName("Self");
        requestorsResult.setSelfLink(link);

        name = "clearASinglePool";
        JsonNode clearASinglePoolNode = linksNode.get(name);
        link = objectMapper.treeToValue(clearASinglePoolNode, Link.class);
        link.setName("Clear A Single Pool");
        requestorsResult.setClearASinglePoolLink(link);

        name = "clearAllPools";
        JsonNode clearAllPoolsNode = linksNode.get(name);
        link = objectMapper.treeToValue(clearAllPoolsNode, Link.class);
        link.setName("Clear All Pools");
        requestorsResult.setClearAllPoolsLink(link);

        nodeState.setRequestorsResult(requestorsResult);
    }

    private void processNodeArtifacts(List<ArchiveEntryByteData> nodeEntryByteDataList, NodeState nodeState,
            ModalProgressMonitor progressMonitor) throws Exception {

        for (ArchiveEntryByteData archiveEntryByteData : nodeEntryByteDataList) {

            String name = archiveEntryByteData.getName();
            byte[] databytes = archiveEntryByteData.getDatabytes();

            File file = new File(name);

            String filename = FileUtilities.getNameWithoutExtension(file);

            if (filename.startsWith("Datasources-")) {
                // TODO finalised data structure for this json
            } else if (filename.startsWith("Requestors-")) {

                processRequestorsResult(databytes, nodeState);

            } else if (filename.startsWith(DATABASE_CLASS_REPORT)) {

                ByteArrayInputStream bais = new ByteArrayInputStream(databytes);

                InputStreamReader inputStreamReader = new InputStreamReader(bais, "UTF-8");

                Map<Integer, List<String>> dataMap;

                dataMap = GeneralUtilities.getDataMapFromCSV(inputStreamReader, progressMonitor);

                Integer headerKey = -1;
                List<String> headerList;

                headerList = dataMap.remove(headerKey);

                CsvDataSortComparator csvDataSortComparator;
                csvDataSortComparator = getCsvDataSortComparator(DATABASE_CLASS_REPORT, headerList);

                List<String> filterableHeaderList = getFilterableHeaderList(DATABASE_CLASS_REPORT);

                CsvDataMap databaseClassReport = new CsvDataMap(DATABASE_CLASS_REPORT, headerList, dataMap,
                        csvDataSortComparator, filterableHeaderList);

                nodeState.setDatabaseClassReport(databaseClassReport);

            }
        }
    }

    private void processReportsArtifacts(List<ArchiveEntryByteData> nodeEntryByteDataList, SystemState systemState,
            ModalProgressMonitor progressMonitor) throws Exception {

        for (ArchiveEntryByteData archiveEntryByteData : nodeEntryByteDataList) {

            String name = archiveEntryByteData.getName();
            byte[] databytes = archiveEntryByteData.getDatabytes();

            File file = new File(name);

            String filename = FileUtilities.getNameWithoutExtension(file);

            Matcher reportsCsvFileMatcher = reportsCsvFilePattern.matcher(filename);

            if (reportsCsvFileMatcher.matches()) {

                String reportCsvName = reportsCsvFileMatcher.group(1);
                ByteArrayInputStream bais = new ByteArrayInputStream(databytes);

                InputStreamReader inputStreamReader = new InputStreamReader(bais, "UTF-8");

                Map<Integer, List<String>> dataMap;

                dataMap = GeneralUtilities.getDataMapFromCSV(inputStreamReader, progressMonitor);

                Integer headerKey = -1;
                List<String> headerList;

                headerList = dataMap.remove(headerKey);

                CsvDataSortComparator csvDataSortComparator;
                csvDataSortComparator = getCsvDataSortComparator(reportCsvName, headerList);

                List<String> filterableHeaderList = getFilterableHeaderList(reportCsvName);

                CsvDataMap csvDataMap;
                csvDataMap = new CsvDataMap(reportCsvName, headerList, dataMap, csvDataSortComparator,
                        filterableHeaderList);

                systemState.addReportCsvDataMap(csvDataMap);

            }
        }
    }

    private void processDatastoreNodeMetadata(byte[] byteArray, SystemState systemState) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(byteArray);

        DatastoreMetadata datastoreMetadata = objectMapper.treeToValue(jsonNode, DatastoreMetadata.class);

        systemState.setDatastoreMetadata(datastoreMetadata);
    }

    private CsvDataSortComparator getCsvDataSortComparator(String reportCsvName, List<String> headerList) {

        CsvDataSortComparator csvDataSortComparator = null;
        List<Integer> compareIndexList = null;

        if (reportCsvName.equalsIgnoreCase(DATABASE_CLASS_REPORT)) {

            int index = headerList.indexOf("Name");

            if (index != -1) {
                compareIndexList = new ArrayList<>();
                compareIndexList.add(index);
            }
        } else if (reportCsvName.equalsIgnoreCase(DATABASE_INDICES_REPORT)) {

            int index = headerList.indexOf("Table Name");

            if (index != -1) {
                compareIndexList = new ArrayList<>();
                compareIndexList.add(index);

                index = headerList.indexOf("Index Name");

                if (index != -1) {
                    compareIndexList.add(index);
                }
            }
        } else if (reportCsvName.equalsIgnoreCase(DATABASE_TABLE_REPORT)) {

            int index = headerList.indexOf("Table");

            if (index != -1) {
                compareIndexList = new ArrayList<>();
                compareIndexList.add(index);

                index = headerList.indexOf("Column");

                if (index != -1) {
                    compareIndexList.add(index);
                }
            }
        } else if (reportCsvName.equalsIgnoreCase(DATABASE_NAME_REPORT)) {

            int index = headerList.indexOf("Database");

            if (index != -1) {
                compareIndexList = new ArrayList<>();
                compareIndexList.add(index);
            }
        }

        if (compareIndexList != null) {
            csvDataSortComparator = new CsvDataSortComparator(compareIndexList, true);
        }

        return csvDataSortComparator;
    }

    private List<String> getFilterableHeaderList(String reportCsvName) {

        List<String> filterableHeaderList = new ArrayList<>();

        if (reportCsvName.equalsIgnoreCase(DATABASE_CLASS_REPORT)) {

            filterableHeaderList.add("Name");
            filterableHeaderList.add("Label");
            filterableHeaderList.add("RuleSet Name");
            // filterableHeaderList.add("Directed Parent Specified in Rule-Obj-Class");
            filterableHeaderList.add("Actual Directed Parent");
            filterableHeaderList.add("Pattern Parent");
            filterableHeaderList.add("Base Class");
            filterableHeaderList.add("Uses Pattern Inheritance?");
            filterableHeaderList.add("Class Group");
            filterableHeaderList.add("Database Name");
            filterableHeaderList.add("Database Table");
        } else if (reportCsvName.equalsIgnoreCase(DATABASE_INDICES_REPORT)) {
            filterableHeaderList.add("Database Name");
            filterableHeaderList.add("Table Schema");
            filterableHeaderList.add("Table Name");
            filterableHeaderList.add("Index Name");
            filterableHeaderList.add("Column Name");
        } else if (reportCsvName.equalsIgnoreCase(DATABASE_TABLE_REPORT)) {
            filterableHeaderList.add("Database");
            filterableHeaderList.add("Catalog");
            filterableHeaderList.add("Schema");
            filterableHeaderList.add("Table");
            filterableHeaderList.add("Has pzPVStream?");
            filterableHeaderList.add("Column");
            filterableHeaderList.add("Column JDBC Type");
            filterableHeaderList.add("Column Vendor Type");
            filterableHeaderList.add("Column Length");
        } else if (reportCsvName.equalsIgnoreCase(DATABASE_NAME_REPORT)) {
            filterableHeaderList.add("Database");
            filterableHeaderList.add("Product Name");
            filterableHeaderList.add("Product Version");
            filterableHeaderList.add("Driver Name");
            filterableHeaderList.add("Driver Version");
        }
        return filterableHeaderList;
    }
}
